package network.frostless.glacier.vote.types;

import com.google.common.collect.ImmutableSet;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.vote.Votable;
import network.frostless.glacier.vote.VoteCategory;
import network.frostless.glacier.vote.VoteManager;
import network.frostless.glacierapi.user.GameUser;

import java.util.Set;

public class MapType implements VoteCategory<String> {

    final VoteManager voteManager = Glacier.get().getVoteManager();

    @Override
    public String getVoteCategoryType() {
        return null;
    }

    @Override
    public String getVoteCategoryDescription() {
        return null;
    }

    @Override
    public void onVote(VoteCategory<String> category, Set<GameUser> gameUsers) {

    }

    @Override
    public Set<String> getVoteCategoryElements() {
        return ImmutableSet.of("Map1", "Map2");
    }
}
