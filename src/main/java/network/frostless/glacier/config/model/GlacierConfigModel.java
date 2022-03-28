package network.frostless.glacier.config.model;

import lombok.Data;
import network.frostless.glacier.config.common.DatabaseCredentials;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class GlacierConfigModel {

    private String test = "test";

    private DatabaseCredentials worldDatabase = new DatabaseCredentials();
}
