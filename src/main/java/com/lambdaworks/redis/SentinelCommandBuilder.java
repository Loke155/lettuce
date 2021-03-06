package com.lambdaworks.redis;

import static com.lambdaworks.redis.protocol.CommandKeyword.*;
import static com.lambdaworks.redis.protocol.CommandType.*;

import java.util.List;
import java.util.Map;

import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.output.*;
import com.lambdaworks.redis.protocol.Command;
import com.lambdaworks.redis.protocol.CommandArgs;
import com.lambdaworks.redis.protocol.CommandKeyword;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
class SentinelCommandBuilder<K, V> extends BaseRedisCommandBuilder<K, V> {
    public SentinelCommandBuilder(RedisCodec<K, V> codec) {
        super(codec);
    }

    public Command<K, V, List<V>> getMasterAddrByKey(K key) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add("get-master-addr-by-name").addKey(key);
        return createCommand(SENTINEL, new ValueListOutput<K, V>(codec), args);
    }

    public Command<K, V, List<Map<K, V>>> masters() {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add("masters");
        return createCommand(SENTINEL, new ListOfMapsOutput<K, V>(codec), args);
    }

    public Command<K, V, Map<K, V>> master(K key) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add("master").addKey(key);
        return createCommand(SENTINEL, new MapOutput<K, V>(codec), args);
    }

    public Command<K, V, List<Map<K, V>>> slaves(K key) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add(SLAVES).addKey(key);
        return createCommand(SENTINEL, new ListOfMapsOutput<K, V>(codec), args);
    }

    public Command<K, V, Long> reset(K key) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add(RESET).addKey(key);
        return createCommand(SENTINEL, new IntegerOutput<K, V>(codec), args);
    }

    public Command<K, V, String> failover(K key) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add(FAILOVER).addKey(key);
        return createCommand(SENTINEL, new StatusOutput<K, V>(codec), args);
    }

    public Command<K, V, String> monitor(K key, String ip, int port, int quorum) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add(MONITOR).addKey(key).add(ip).add(port).add(quorum);
        return createCommand(SENTINEL, new StatusOutput<K, V>(codec), args);
    }

    public Command<K, V, String> set(K key, String option, V value) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add(SET).addKey(key).add(option).addValue(value);
        return createCommand(SENTINEL, new StatusOutput<K, V>(codec), args);
    }

    public Command<K, V, String> ping() {
        return createCommand(PING, new StatusOutput<K, V>(codec));
    }

    public Command<K, V, String> remove(K key) {
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec).add(CommandKeyword.REMOVE).addKey(key);
        return createCommand(SENTINEL, new StatusOutput<K, V>(codec), args);
    }
}
