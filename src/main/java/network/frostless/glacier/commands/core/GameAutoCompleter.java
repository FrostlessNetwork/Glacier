package network.frostless.glacier.commands.core;

import network.frostless.glacier.Glacier;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;

import java.util.Collection;
import java.util.List;

public class GameAutoCompleter implements SuggestionProvider {
    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull List<String> args, @NotNull CommandActor sender, @NotNull ExecutableCommand command) {
        return Glacier.get().getGameManager().getGames().keySet();
    }
}
