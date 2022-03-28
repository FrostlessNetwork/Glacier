package network.frostless.glacier.slime.loaders;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import network.frostless.glacier.config.common.DatabaseCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SlimePostgresLoader extends UpdatableLoader {

    private final Logger logger = LogManager.getLogger("SWM Postgres Loader");
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, new ThreadFactoryBuilder()
            .setNameFormat("SWM Postgres Glacier Lock Pool Thread #%1$d").build());

    private static final int CURRENT_DB_VERSION = 1;
    private static final long MAX_LOCK_TIME = 300000L; // Max time difference between current time millis and world lock
    private static final long LOCK_INTERVAL = 60000L;


    /* Database version handling queries */
    private static final String CREATE_VERSIONING_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS database_version (id serial PRIMARY KEY, version int8(11));";
    private static final String INSERT_VERSION_QUERY = "INSERT INTO database_version (id, version) VALUES(1, ?) ON CONFLICT DO UPDATE SET id = ?;";
    private static final String GET_VERSION_QUERY = "SELECT version FROM database_version WHERE id = 1;";

    // World handling queries
    private static final String CREATE_WORLDS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS worlds (id SERIAL NOT NULL PRIMARY KEY, name varchar(255) UNIQUE, world BYTEA, locked BIGINT);";
    private static final String SELECT_WORLD_QUERY = "SELECT world, locked FROM worlds WHERE name = ?;";
    private static final String UPDATE_WORLD_QUERY = "INSERT INTO worlds (name, world, locked) VALUES (?, ?, 1) ON CONFLICT DO UPDATE SET world = ?;";
    private static final String UPDATE_LOCK_QUERY = "UPDATE worlds SET locked = 0 WHERE name = ?;";
    private static final String DELETE_WORLD_QUERY = "DELETE FROM worlds WHERE name = ?;";
    private static final String LIST_WORLDS_QUERY = "SELECT name FROM worlds;";


    private final Map<String, ScheduledFuture<?>> lockedWorlds = new HashMap<>();
    private final HikariDataSource dataSource;

    public SlimePostgresLoader(DatabaseCredentials credentials) {

        dataSource = new HikariDataSource(generateHikariConfig(credentials));

        try (Connection con = dataSource.getConnection()) {
            // Create worlds table
            try (PreparedStatement statement = con.prepareStatement(CREATE_WORLDS_TABLE_QUERY)) {
                statement.execute();
            }

            // Create versioning table
            try (PreparedStatement statement = con.prepareStatement(CREATE_VERSIONING_TABLE_QUERY)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() throws NewerDatabaseException, IOException {
        // no update checkign kthx because if this shit
        // runs on the minigames server, it will legit fuck
        // everything up in the db due to all the servers running it.
    }


    @Override
    public byte[] loadWorld(String worldName, boolean readOnly) throws UnknownWorldException, WorldInUseException, IOException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_WORLD_QUERY)) {
            statement.setString(1, worldName);
            ResultSet set = statement.executeQuery();

            if (!set.next()) {
                throw new UnknownWorldException(worldName);
            }

            if (!readOnly) {
                long lockedMillis = set.getLong("locked");

                if (System.currentTimeMillis() - lockedMillis <= MAX_LOCK_TIME) {
                    throw new WorldInUseException(worldName);
                }

                updateLock(worldName, true);
            }

            return set.getBytes("world");
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean worldExists(String worldName) throws IOException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_WORLD_QUERY)) {
            statement.setString(1, worldName);
            ResultSet set = statement.executeQuery();

            return set.next();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public List<String> listWorlds() throws IOException {
        List<String> worldList = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(LIST_WORLDS_QUERY)) {
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                worldList.add(set.getString("name"));
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }

        return worldList;
    }

    @Override
    public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) throws IOException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_WORLD_QUERY)) {
            statement.setString(1, worldName);
            statement.setBytes(2, serializedWorld);
            statement.setBytes(3, serializedWorld);
            statement.executeUpdate();

            if (lock) {
                updateLock(worldName, true);
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void unlockWorld(String worldName) throws UnknownWorldException, IOException {
        ScheduledFuture<?> future = lockedWorlds.remove(worldName);

        if (future != null) {
            future.cancel(false);
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_LOCK_QUERY)) {
            statement.setLong(1, 0L);
            statement.setString(2, worldName);

            if (statement.executeUpdate() == 0) {
                throw new UnknownWorldException(worldName);
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }

    }

    @Override
    public boolean isWorldLocked(String worldName) throws UnknownWorldException, IOException {
        if (lockedWorlds.containsKey(worldName)) {
            return true;
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_WORLD_QUERY)) {
            statement.setString(1, worldName);
            ResultSet set = statement.executeQuery();

            if (!set.next()) {
                throw new UnknownWorldException(worldName);
            }

            return System.currentTimeMillis() - set.getLong("locked") <= MAX_LOCK_TIME;
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void deleteWorld(String worldName) throws UnknownWorldException, IOException {
        ScheduledFuture future = lockedWorlds.remove(worldName);

        if (future != null) {
            future.cancel(false);
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_WORLD_QUERY)) {
            statement.setString(1, worldName);

            if (statement.executeUpdate() == 0) {
                throw new UnknownWorldException(worldName);
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }

    }


    /**
     * Generates a HikariConfig object for HikariCP.
     *
     * @param credentials The credentials to use for the connection.
     * @return The HikariConfig object.
     */
    private HikariConfig generateHikariConfig(DatabaseCredentials credentials) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(credentials.getUrl());
        hikariConfig.setUsername(credentials.getUsername());
        hikariConfig.setPassword(credentials.getPassword());
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

        return hikariConfig;
    }

    private void updateLock(String worldName, boolean forceSchedule) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_LOCK_QUERY)) {
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, worldName);

            statement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Failed to update the lock for world " + worldName + ":");
            ex.printStackTrace();
        }

        if (forceSchedule || lockedWorlds.containsKey(worldName)) { // Only schedule another update if the world is still on the map
            lockedWorlds.put(worldName, executorService.schedule(() -> updateLock(worldName, false), LOCK_INTERVAL, TimeUnit.MILLISECONDS));
        }
    }

}
