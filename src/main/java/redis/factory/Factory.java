package redis.factory;

import redis.command.CommandProcessor;

public interface Factory {
    CommandProcessor getInstance();
}
