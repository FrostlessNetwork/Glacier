package network.frostless.glacier.game.mechanics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class DefaultGameMechanicHandler<U extends GameUser> implements GameMechanicHandler<U> {

    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void onDeath(GameUser user) {
        final Player player = user.getPlayer();
        user.getPlayer().spigot().respawn();
        user.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.teleport(user.getGame().getWorldCenter());
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
        user.getPlayer().setFallDistance(0.0F);
        user.getPlayer().setGameMode(GameMode.SURVIVAL);
        user.getPlayer().setAllowFlight(true);
        user.getPlayer().setFlying(true);
        user.getPlayer().setCollidable(false);
        user.getPlayer().setInvulnerable(true);
        user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));
        user.setUserState(UserGameState.SPECTATING);
        Team team = Glacier.get().getGameBoard().getSpectator(user.getGame());

        if (team != null) {
            OffloadTask.offloadSync(() -> team.addPlayer(user.getPlayer()));
        } else {
            System.err.println("Spectators team is null!");
        }
    }
}
