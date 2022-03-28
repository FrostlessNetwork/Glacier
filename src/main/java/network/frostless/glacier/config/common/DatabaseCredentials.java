package network.frostless.glacier.config.common;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class DatabaseCredentials {

    private String url;

    private String username;

    private String password;



    public DatabaseCredentials() { }
}
