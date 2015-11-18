package io.github.verizoncraft.boxelclient.example;


import io.github.verizoncraft.boxelclient.BoxelVideoClient;
import io.github.verizoncraft.boxelclient.BoxelWebClient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BoxelExamplePlugin extends JavaPlugin implements Listener {
    private Logger log = Bukkit.getLogger();
    private BoxelWebClient mWebClient;
    private BoxelVideoClient mVideoClient;
    private List<BukkitTask> mStreams;

    @Override
    public void onEnable() {
        super.onEnable();
        mStreams = new ArrayList<>();
        mWebClient = new BoxelWebClient(this);
        mVideoClient = new BoxelVideoClient(this);
        saveDefaultConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player;
        boolean success = false;
        try {
            player = (Player) sender;
        } catch (ClassCastException e) {
            sender.sendMessage(ChatColor.RED+"Only players can run "+cmd.getName());
            return success;
        }

        if (cmd.getName().equalsIgnoreCase("stream")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED+cmd.getName() + " requires one argument.");
            } else {
                BukkitTask task = mVideoClient.subscribe(player.getLocation().add(0, 50, 0), args[0], 30, 40);
                mStreams.add(task);
                success = true;
            }
        } else if (cmd.getName().equalsIgnoreCase("website")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED+cmd.getName() + " requires one argument.");
            } else {
                try {
                    mWebClient.load(player.getLocation().add(0, 50, 0), player, args[0], 30, 40);
                    success = true;
                    sender.sendMessage("Loading...");
                } catch (MalformedURLException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid URL " + args[0]);
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("stopstreams")) {
            stopStreams();
            success = true;
        }
        return success;
    }

    @Override
    public void onDisable() {
        stopStreams();
    }

    private void stopStreams() {
        for (BukkitTask stream : mStreams) {
            stream.cancel();
            mStreams.remove(stream);
        }
    }
}
