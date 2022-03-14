package network.frostless.glacier.user.impl;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacierapi.user.GameUser;

import java.util.UUID;

@Data
public class GameUserImpl implements GameUser {

    @DatabaseField(id = true)
    private long id;

    @DatabaseField(foreign = true,
            foreignColumnName = "id",
            canBeNull = false,
            foreignAutoRefresh = true,
            unique = true,
            foreignAutoCreate = true
    )
    private GlobalUser globalUser;

    @DatabaseField(unique = true)
    private UUID uuid;

    // Transient, no saves
    private transient String gameIdentifier;

    public GameUserImpl() {
    }

    public GameUserImpl(GlobalUser globalUser) {
        this.globalUser = globalUser;
        this.uuid = globalUser.getUuid();
    }

    public GameUserImpl(UUID uuid) {
        this.uuid = uuid;
    }


}
