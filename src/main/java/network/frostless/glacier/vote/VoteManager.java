package network.frostless.glacier.vote;

import com.google.common.collect.Maps;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class VoteManager implements Voter {

    private final Map<VoteCategory<? extends Votable>, Set<GameUser>> voteMap = Maps.newConcurrentMap();

    @Override
    public void initVote(VoteCategory<? extends Votable> category, Set<GameUser> gameUsers) {
        voteMap.putIfAbsent(category, gameUsers);
    }

    @Override
    public void removeVote() {
        voteMap.values().forEach(gameuser -> {
            Predicate<? super GameUser> removeAction = (user) -> user.getUserState() == UserGameState.LOBBY;
            gameuser.removeIf(removeAction);
        });
    }

    @Override
    public <T extends VoteCategory<? extends Votable>> int getHighestVote(T result, int requiredAmount) {
        Set<GameUser> winningResult = voteMap.get(result);
        if (winningResult.size() >= requiredAmount) {
            return winningResult.size();
        }

        return -1;
    }

    public void destroyVoteMap() {
        if (voteMap.isEmpty()) return;
        voteMap.clear();
    }
}
