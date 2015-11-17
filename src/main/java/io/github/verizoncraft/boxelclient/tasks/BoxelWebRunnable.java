package io.github.verizoncraft.boxelclient.tasks;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.JedisPubSub;

public class BoxelWebRunnable {
    private int mWidth;
    private int mHeight;
    private Plugin mPlugin;
    private Location mLocation;
    private String mChannel;
    private JedisPubSub mListener;


}
