package network.frostless.glacier.vote;

import network.frostless.glacierapi.user.GameUser;

import java.util.Set;

public interface Voter {

    void removeVote();

    void initVote(VoteCategory<? extends Votable> category, Set<GameUser> gameUsers);

    <T extends VoteCategory<? extends Votable>> int getHighestVote(T result, int requiredAmount);
}
