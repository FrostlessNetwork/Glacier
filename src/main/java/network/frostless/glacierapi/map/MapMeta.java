package network.frostless.glacierapi.map;

public interface MapMeta {

    String getName();
    void setName(String name);

    int getVersion();
    void setVersion(int version);

    long getLastModified();
    void setLastModified(long lastModified);
}
