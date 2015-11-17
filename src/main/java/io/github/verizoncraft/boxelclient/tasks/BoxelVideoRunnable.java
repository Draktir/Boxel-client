package io.github.verizoncraft.boxelclient.tasks;

import io.github.verizoncraft.boxelclient.connection.Redis;
import io.github.verizoncraft.boxelclient.models.Frame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.Callable;

public class BoxelVideoRunnable extends BukkitRunnable {
    private int mWidth;
    private int mHeight;
    private Plugin mPlugin;
    private Location mLocation;
    private String mChannel;
    private JedisPubSub mListener;

    public BoxelVideoRunnable(Plugin plugin, Location location, String channel, int width, int height) {
        super();
        mWidth = width;
        mHeight = height;
        mLocation = location;
        mPlugin = plugin;
        mChannel = channel;

        mListener = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                final Frame frame = Frame.fromJSON(message);
                Bukkit.getScheduler().callSyncMethod(mPlugin, new Callable<Void>() {
                    public Void call() {
                        Bukkit.getScheduler().runTask(mPlugin, new Runnable() {
                            public void run() {
                                frame.drawAtLocation(mLocation, mWidth, mHeight);
                            }
                        });
                        return null;
                    }
                });
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                super.onSubscribe(channel, subscribedChannels);
            }
        };
    }

    public void run() {
        Redis.getInstance(mPlugin).subscribe(mListener, mChannel);
    }

    @Override
    public void cancel() {
        try {
            if (mListener.isSubscribed()) {
                mListener.unsubscribe();
            }
            super.cancel();
        } catch (JedisConnectionException e) {
            //pass
        } catch (IllegalStateException e) {
            //pass
        }
    }
}
