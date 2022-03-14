package network.frostless.glacierapi.user;

import java.util.UUID;

public interface DataGameUser {
    long getId();
    void setId(long id);

    UUID getUuid();
    void setUuid(UUID uuid);
}
