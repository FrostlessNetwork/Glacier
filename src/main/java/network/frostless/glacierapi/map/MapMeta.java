package network.frostless.glacierapi.map;

public interface MapMeta {

    String getName();

    void setName(String name);

    String getFriendlyName();

    void setFriendlyName(String friendlyName);

    int getVersion();

    void setVersion(int version);

    long getLastModified();

    void setLastModified(long lastModified);

    @SuppressWarnings("unchecked")
    default <T extends MapMeta> T copy() {
        return (T) this;
    }
}
