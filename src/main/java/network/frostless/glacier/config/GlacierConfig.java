package network.frostless.glacier.config;

import network.frostless.frostcore.config.impl.YamlConfiguration;
import network.frostless.glacier.config.model.GlacierConfigModel;

public class GlacierConfig extends YamlConfiguration<GlacierConfigModel> {

    public GlacierConfig() { }

    @Override
    protected Class<GlacierConfigModel> clazz() {
        return GlacierConfigModel.class;
    }
}
