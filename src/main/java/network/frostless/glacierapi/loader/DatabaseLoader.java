package network.frostless.glacierapi.loader;


import java.sql.Connection;

public interface DatabaseLoader {

    default Connection connection() {
        // TODO: later
        throw new RuntimeException("Not implemented yet");
    }
}
