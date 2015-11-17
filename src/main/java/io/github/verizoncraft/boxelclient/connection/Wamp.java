package io.github.verizoncraft.boxelclient.connection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import java.util.concurrent.TimeUnit;

public class Wamp {

    private String Url;
    private String realm;
    private WampClient client;

    private static Wamp instance;

    private Wamp(String Url, String realm) {
        this.Url = Url;
        this.realm = realm;

        buildClient();
    }

    public static Wamp getInstance(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();

        if (instance == null) {
            instance = new Wamp(config.getString("boxel-host"), config.getString("boxel-realm"));
        }
        return instance;
    }

    public WampClient getClient() {
        return client;
    }

    public void buildClient() {
        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();
        try {
            builder.withConnectorProvider(connectorProvider)
                    .withUri(Url)
                    .withRealm(realm)
                    .withInfiniteReconnects()
                    .withCloseOnErrors(true)
                    .withReconnectInterval(5, TimeUnit.SECONDS);
            this.client = builder.build();
        } catch (WampError e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.open();
    }

    public void setClient(WampClient client) {
        this.client = client;
    }
}
