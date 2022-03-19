package network.frostless.glacier.identify;

import io.lettuce.core.pubsub.RedisPubSubListener;
import network.frostless.glacier.Glacier;
import network.frostless.serverapi.RedisKeys;

import java.util.function.Consumer;

public record IdentifyListener(Consumer<String> call) implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String message) {
        if (channel.equals(RedisKeys.PS_SERVER_IDENTIFY(Glacier.getPlugin().getServerIdentifier()))) {
            call.accept(message);
        }
    }

    @Override
    public void message(String pattern, String channel, String message) {

    }

    @Override
    public void subscribed(String channel, long count) {

    }

    @Override
    public void psubscribed(String pattern, long count) {

    }

    @Override
    public void unsubscribed(String channel, long count) {

    }

    @Override
    public void punsubscribed(String pattern, long count) {

    }
}
