package io.github.verizoncraft.boxelclient.tasks;

import io.github.verizoncraft.boxelclient.connection.Wamp;
import io.github.verizoncraft.boxelclient.models.Frame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import ws.wamp.jawampa.WampClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BoxelWebRunnable {
    private int mWidth;
    private int mHeight;
    private Location mLocation;
    private Plugin mPlugin;


    public BoxelWebRunnable(Plugin plugin, Location location, int width, int height) {
        mPlugin = plugin;
        mWidth = width;
        mHeight = height;
        mLocation = location;
    }


    public BukkitTask renderWebsite(final URL address, final Player player) {
        final String url = address.toString();
        final Wamp wamp = Wamp.getInstance(mPlugin);

        final ArrayList<String> rpcData = new ArrayList<>();
        rpcData.add(player.getUniqueId().toString());
        rpcData.add(url);

        return new BukkitRunnable() {
            @Override
            public void run() {
                wamp.getClient()
                        .statusChanged()
                        .subscribe(new Action1<WampClient.State>() {
                            @Override
                            public void call(WampClient.State t1) {
                                System.out.println("Session status changed to " + t1);

                                if (t1 instanceof WampClient.ConnectedState) {
                                    Observable<String> result = wamp.getClient()
                                            .call("com.boxel.website", String.class, rpcData);
                                    result.subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String t1) {
                                            System.out.println("Got Website from request");
                                            final Frame frame = Frame.fromJSON(t1);

                                            Bukkit.getServer().getScheduler().runTask(mPlugin, new Runnable() {
                                                @Override
                                                public void run() {
                                                    Bukkit.getScheduler().callSyncMethod(
                                                            JavaPlugin.getProvidingPlugin(this.getClass()), new Callable<Object>() {
                                                                public Object call() {
                                                                    frame.drawAtLocation(mLocation, mWidth, mHeight);
                                                                    return null;
                                                                }
                                                            });

                                                }
                                            });;
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable t1) {
                                            System.out.println("Completed website with error " + t1);
                                            // rebuild the client -- this one is borked
                                            wamp.buildClient();
                                        }
                                    });
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable t) {
                                System.out.println("Session ended with error " + t);
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                System.out.println("Session ended normally");
                            }
                        });
            }
        }.runTask(mPlugin);
    }

}
