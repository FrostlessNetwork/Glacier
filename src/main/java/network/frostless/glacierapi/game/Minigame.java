package network.frostless.glacierapi.game;

/**
 * Easy to use interface without specifying game type.
 * Used to get the game identifier
 */
public interface Minigame {

    /**
     * Gets the game identifier
     * @return the game identifier
     */
    String getIdentifier();

    /**
     * Sets the game identifier
     * @param identifier the game identifier
     */
    void setIdentifier(String identifier);
}
