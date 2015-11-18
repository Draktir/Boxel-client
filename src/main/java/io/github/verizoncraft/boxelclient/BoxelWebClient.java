package io.github.verizoncraft.boxelclient;

import io.github.verizoncraft.boxelclient.tasks.BoxelWebRunnable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.net.MalformedURLException;
import java.net.URL;

public class BoxelWebClient {
    private Plugin mPlugin;

    public BoxelWebClient(Plugin plugin) {
        mPlugin = plugin;
    }

    public BukkitTask load(Location location, Player player, String url, int width, int height) throws MalformedURLException{
        URL realURL = new URL(url);
        return new BoxelWebRunnable(mPlugin, location, width, height).renderWebsite(realURL, player);
    }
}
