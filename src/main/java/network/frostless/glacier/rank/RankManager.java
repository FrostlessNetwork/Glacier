package network.frostless.glacier.rank;

import com.google.common.collect.Maps;
import network.frostless.glacier.Glacier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class RankManager {

    private static final Map<String, String> rankCache = Maps.newConcurrentMap();


    public static String getRank(String name) {
        if(name == null) return null;
        if(!rankCache.containsKey(name)) fetchRank(name);

        return rankCache.get(name);
    }

    private static void fetchRank(String name) {
        try(PreparedStatement statement = Glacier.get().getUserManager().getConnection().prepareStatement("SELECT regexp_replace(t.permission, 'prefix.\\d*.', '') FROM (SELECT permission from luckperms_group_permissions WHERE name = ? AND permission LIKE 'prefix.%') t")) {
            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) rankCache.put(name, rs.getString(1));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Fetches all Luckperm ranks from database and loads it into the cache
     */
    public static void fetchAllRanks() {
        try(PreparedStatement s = Glacier.get().getUserManager().getConnection().prepareStatement("SELECT t.name, regexp_replace(t.permission, 'prefix.\\d*.', '') FROM (SELECT * from luckperms_group_permissions WHERE permission LIKE 'prefix.%') t")) {
            ResultSet resultSet = s.executeQuery();

            while(resultSet.next()) {
                rankCache.put(resultSet.getString(1), resultSet.getString(2));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
