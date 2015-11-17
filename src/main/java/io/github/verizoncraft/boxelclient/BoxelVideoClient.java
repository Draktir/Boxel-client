package io.github.verizoncraft.boxelclient;

import io.github.verizoncraft.boxelclient.tasks.BoxelVideoRunnable;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BoxelVideoClient {
    private Plugin mPlugin;

    public BoxelVideoClient(Plugin plugin) {
        mPlugin = plugin;
    }

    public BukkitTask subscribe(Location location, String channel, int width, int height) {
        return new BoxelVideoRunnable(mPlugin, location, channel, width, height).runTaskAsynchronously(mPlugin);
    }
}
