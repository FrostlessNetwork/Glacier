package network.frostless.glacier.vote;

import network.frostless.glacierapi.user.GameUser;

import java.util.Set;

public interface VoteCategory<T> {

    String getVoteCategoryType();

    String getVoteCategoryDescription();

    void onVote(VoteCategory<T> category, Set<GameUser> gameUsers);

    Set<T> getVoteCategoryElements();
}
