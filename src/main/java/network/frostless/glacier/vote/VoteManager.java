package network.frostless.glacier.vote;

import com.google.common.collect.Maps;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;

import java.util.*;

public class VoteManager {
    ;
    private final Map<VoteCategory<?>, Set<GameUser>> voteMap = Collections.synchronizedMap(Maps.newLinkedHashMap());

    public void initVote(VoteCategory<?> category, Set<GameUser> gameUsers) {
        voteMap.put(category, gameUsers);
    }

    public void removeVote() {
        voteMap.values().forEach(gameUsers -> {
            gameUsers.removeIf(gameUser -> gameUser.getUserState().equals(UserGameState.LOBBY));
        });
    }

    public int getHighestVote(VoteCategory<?> category, int requiredAmount) {
        Set<GameUser> winningResult = voteMap.get(category);
        if (winningResult.size() >= requiredAmount) {
            return winningResult.size();
        }

        return -1;
    }

    public void destroy() {
        if (voteMap.isEmpty()) return;
        voteMap.clear();
    }
}
