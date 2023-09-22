package me.zowpy.meetup.redis.internal;

import lombok.Getter;
import me.zowpy.meetup.redis.RedisCredentials;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.function.Consumer;

@Getter
public class JedisHandler {

    private JedisPool jedisPool;
    private Jedis jedis;

    private final RedisCredentials redisCredentials;

    public JedisHandler(RedisCredentials redisCredentials) {
        this.redisCredentials = redisCredentials;

        connect();
    }

    /**
     * connect to redis
     */

    private void connect() {
        try {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), redisCredentials.getHost(), redisCredentials.getPort());
            this.jedis = jedisPool.getResource();

            if (redisCredentials.isAuth()) jedis.auth(redisCredentials.getPassword());

            jedis.connect();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * authenticate the redis connection
     *
     * @param jedis redis connection
     */

    public void auth(Jedis jedis) {
        if (jedis != null) {
            if (redisCredentials.isAuth()) jedis.auth(redisCredentials.getPassword());
        }
    }

    /**
     * run a command using redis
     *
     * @param consumer function that will be executed
     */

    public void runCommand(Consumer<Jedis> consumer) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            auth(jedis);

            consumer.accept(jedis);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
