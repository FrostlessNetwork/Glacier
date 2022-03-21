package network.frostless.glacier.vote;

import network.frostless.glacierapi.user.GameUser;

import java.util.List;

public interface VoteCategory<T> {

    String getVoteType();

    String getVoteDescription();

    void onVote(GameUser user);

    List<T> getVotableItems();
}
