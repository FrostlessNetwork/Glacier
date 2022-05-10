package network.frostless.glacier.exceptions;


public class GameNotFoundException extends Exception {

    private final String name;

    public GameNotFoundException(String game) {
        super(game);
        this.name = game;
    }

    public String getName() {
        return name;
    }
}
