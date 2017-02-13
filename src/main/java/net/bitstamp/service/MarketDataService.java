package net.bitstamp.service;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import net.bitstamp.cfg.BitstampConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


public class MarketDataService {

    private final BitstampConfig configuration;
    private Pusher client;
    private Map<String, Channel> channels;

    public MarketDataService(BitstampConfig configuration) {

        this.configuration = configuration;
        client = new Pusher(configuration.getPusherKey(), configuration.pusherOptions());
    }


    public void connect() {

        // Re-connect is handled by the base ReconnectService when it reads a closed
        // conn. state
        client.connect();
        channels.clear();
        for (String name : configuration.getChannels()) {
            Channel instance = client.subscribe(name);
            if (name.equals("order_book")) {
                bindOrderData(instance);
            } else if (name.equals("diff_order_book")) {
                bindOrderData(instance);
            } else if (name.equals("live_trades")) {
                bindOrderData(instance);
            } else {
                throw new IllegalArgumentException(name);
            }
            channels.put(name, instance);
        }
    }

    private void bindOrderData(Channel chan) {
        SubscriptionEventListener subscriptionEventListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, String data) {
                System.out.println(data);
            }

        };

        chan.bind("data", subscriptionEventListener);
    }
    public void disconnect() {

        client.disconnect();
        channels.clear();
    }


}
