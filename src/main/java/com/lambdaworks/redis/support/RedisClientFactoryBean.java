package com.lambdaworks.redis.support;

import static com.lambdaworks.redis.LettuceStrings.isNotEmpty;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;

/**
 * Factory Bean for {@link RedisClient} instances. Needs either a {@link java.net.URI} or a {@link RedisURI} as input. URI
 * Formats: {@code
 *     redis-sentinel://host[:port][,host2[:port2]][/databaseNumber]#sentinelMasterId
 * }
 *
 * {@code
 *     redis://host[:port][/databaseNumber]
 * }
 * 
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
public class RedisClientFactoryBean extends LettuceFactoryBeanSupport<RedisClient> {

    // todo: support for client resources

    @Override
    public void afterPropertiesSet() throws Exception {

        if (getRedisURI() == null) {
            RedisURI redisURI = RedisURI.create(getUri());

            if (isNotEmpty(getPassword())) {
                redisURI.setPassword(getPassword());
            }
            setRedisURI(redisURI);
        }

        super.afterPropertiesSet();
    }

    @Override
    protected void destroyInstance(RedisClient instance) throws Exception {
        instance.shutdown();
    }

    @Override
    public Class<?> getObjectType() {
        return RedisClient.class;
    }

    @Override
    protected RedisClient createInstance() throws Exception {
        return new RedisClient(getRedisURI());
    }
}
