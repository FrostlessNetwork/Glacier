package network.frostless.glacier.game.mechanics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class DefaultGameMechanicHandler<U extends GameUser> implements GameMechanicHandler<U> {

    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void onDeath(GameUser user) {
        final Player player = user.getPlayer();
        user.getPlayer().spigot().respawn();
        user.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.teleport(user.getGame().getWorldCenter());
        user.setUserState(UserGameState.SPECTATING);
        spectate(user);

        user.getGame().executeUsers((u) -> {
            TagResolver tags = TagResolver.builder()
                    .tag("player", Tag.inserting(user.getTeamDisplayName()))
                    .tag("remaining", Tag.inserting(Component.text(user.getGame().getIngamePlayers())))
                    .build();

            u.sendMessage(mm.deserialize("<player> <reset><gray>died! There are <remaining> players left!", tags));
        });
    }

    @Override
    public void respawn(GameUser user) {
        user.getPlayer().setGameMode(org.bukkit.GameMode.SURVIVAL);
        user.getPlayer().teleport(user.getGame().getWorldCenter());
    }

    @Override
    public void spectate(GameUser user) {
        user.getPlayer().getInventory().clear();
        user.setUserState(UserGameState.SPECTATING);
        user.getGame().addToSpectators(user);

        user.getPlayer().setFallDistance(0.0F);
        user.getPlayer().setGameMode(GameMode.SURVIVAL);
        user.getPlayer().setAllowFlight(true);
        user.getPlayer().setFlying(true);
        user.getPlayer().setCollidable(false);
        user.getPlayer().setInvulnerable(true);
        user.getPlayer().setInvisible(true);

        giveSpectatorTools(user);
    }

    private void giveSpectatorTools(GameUser user) {
        final Player player = user.getPlayer();
    }
}
