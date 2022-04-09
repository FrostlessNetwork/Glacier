package network.frostless.glacier.lobby;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.items.ImmutableItem;
import network.frostless.glacier.user.Users;
import network.frostless.glacier.utils.LazyLocation;
import network.frostless.glacierapi.events.game.LobbyJoinEvent;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public abstract class AbstractLobby implements Listener, Lobby {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected LazyLocation lazyLocation;

    private Location cachedLocation;

    /**
     * Constructs a new lobby with the provided world and location.
     *
     * @param spawn The location of the lobby.
     */
    public AbstractLobby(LazyLocation spawn) {
        this.lazyLocation = spawn;
        Glacier.getPlugin().registerListeners(this);
    }

    public AbstractLobby(Location spawn) {
        this.cachedLocation = spawn;

        Glacier.getPlugin().registerListeners(this);
    }


    protected abstract Map<Integer, Pair<ImmutableItem, Consumer<PlayerInteractEvent>>> getItems();

    protected void giveItems(Player player) {
        for (Map.Entry<Integer, Pair<ImmutableItem, Consumer<PlayerInteractEvent>>> itemset : getItems().entrySet()) {
            player.getInventory().setItem(itemset.getKey(), itemset.getValue().key().itemStack());
        }
    }

    protected abstract void onLobbyJoin(LobbyJoinEvent event);

    @EventHandler
    public void onPlayerJoin(LobbyJoinEvent event) {
        event.getGameUser().setUserState(UserGameState.LOBBY);
        event.getGameUser().getPlayer().teleportAsync(getRealLocation()).whenComplete((success, err) -> Bukkit.getScheduler().runTask(Glacier.getPlugin(), () -> {
            final Player player = event.getGameUser().getPlayer();
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);
            player.setHealth(20);
            onLobbyJoin(event);
        }));
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if (isLobbyUser(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player player && isLobbyUser(player)) {

            if (evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                evt.getEntity().teleport(getRealLocation());
                evt.getEntity().sendMessage(Component.text("No dying for you!").color(TextColor.color(0xAFFC09)));
            }

        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!isLobbyUser(event.getEntity())) return;
        event.setCancelled(true);
        event.getPlayer().teleport(getRealLocation());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (isLobbyUser(evt.getPlayer())) {
            if (evt.getItem() != null) {
                for (Pair<ImmutableItem, Consumer<PlayerInteractEvent>> value : getItems().values()) {

                    if(ImmutableItem.compare(evt.getItem(), value.key())) {
                        value.value().accept(evt);
                    }
                }
            }
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && isLobbyUser(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent evt) {
        if (isLobbyUser(evt.getPlayer())) evt.setCancelled(true);
    }

    private boolean isLobbyUser(Player player) {
        GameUser user = Users.getUser(player.getUniqueId(), GameUser.class);
        if (user == null) return false;

        return user.isInLobby();
    }

    private Location getRealLocation() {
        if (cachedLocation == null) cachedLocation = lazyLocation.location();

        if (lazyLocation != null && !lazyLocation.location().equals(cachedLocation)) {
            cachedLocation = lazyLocation.location();
        }

        return cachedLocation;
    }
}
