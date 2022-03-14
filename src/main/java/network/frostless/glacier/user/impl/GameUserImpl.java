package network.frostless.glacier.user.impl;

import lombok.Getter;
import lombok.Setter;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;

import java.util.UUID;

@Getter
@Setter
public abstract class GameUserImpl extends DataGameUserImpl implements GameUser {

    // Transient, no saves
    private transient String gameIdentifier;

    private transient UserGameState userState;


    public GameUserImpl() {}

    public GameUserImpl(UUID uuid) {
        super(uuid);
    }

}
