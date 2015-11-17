package io.github.verizoncraft.boxelclient.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.net.URI;
import java.util.*;


public class Redis {

    private Plugin plugin;

    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    private static HashMap instances = new HashMap<String, Redis>();

    private Redis(URI redisURI) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);

        objectMapper = new ObjectMapper();
        jedisPool = new JedisPool(poolConfig, redisURI);
    }

    public static Redis getInstance(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        String redisURI = config.getString("redis-uri");
        return getInstance(redisURI);
    }
    public static Redis getInstance(String redisURI) {
        URI uri =  URI.create(redisURI);
        return getInstance(uri);
    }

    public static Redis getInstance(URI redisURI) {
        Redis instance = (Redis) instances.get(redisURI.toString());
        if (instance == null) {
            instance = new Redis(redisURI);
            instances.put(redisURI.toString(), instance);
        }
        return instance;
    }

    public void psubscribe(final JedisPubSub listener, final String... patterns) {
        new Thread(new Runnable() {
            public void run() {
                jedisPool.getResource().psubscribe(listener, patterns);
            }
        }).start();
    }

    public void subscribe(final JedisPubSub listener, final String channel) {
        new Thread(new Runnable() {
            public void run() {
                jedisPool.getResource().subscribe(listener, channel);
            }
        }).start();
    }

    public long hset(String key, String field, Object value) {
        try {
            return this.hset(key, field, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException jpe) {
            //TODO: log jpe
            return 0L;
        }
    }

    public long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(key, field, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.setex(key, seconds, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(key, field);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Object hget(String key, String field, Class clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.hget(key, field);
            if(null == value) {
                return null;
            }
            return objectMapper.readValue(value, clazz);
        } catch(IOException ioe) {
            //TODO: log exception
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long hdel(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hdel(key, fields);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Map<String, String> hgetall(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T>List<T> hlist(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            ArrayList<T> items = new ArrayList<T>();
            for (String item : jedis.hgetAll(key).values())
                items.add(objectMapper.readValue(item, clazz));
            return items;
        } catch(IOException ioe) {
            //TODO: log exception
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long publish(String channel, String message) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.publish(channel, message);
        } finally {
            jedis.close();
        }
    }

    public Set<String> keys(String pattern) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.keys(pattern);
        } finally {
            jedis.close();
        }
    }

    public Long del(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    public Long del(Set<String> keys) {
        Jedis jedis = jedisPool.getResource();
        try {
            String[] strings = new String[keys.size()];
            return jedis.del(keys.toArray(strings));
        } finally {
            jedis.close();
        }
    }
}
