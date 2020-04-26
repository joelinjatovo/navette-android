package com.navetteclub.utils;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.util.HttpAuthorizer;

import java.util.HashMap;
import java.util.Map;

public class PusherOdk {

    private static PusherOdk ourInstance;

    private Pusher pusherPrivate = null;

    private String token;

    private PusherOdk(String token) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", token);
        final HttpAuthorizer authorizer = new HttpAuthorizer(Constants.getBaseUrl() + "broadcasting/auth");
        authorizer.setHeaders(headers);

        PusherOptions pusherOptions = new PusherOptions();
        pusherOptions.setAuthorizer(authorizer);
        pusherOptions.setCluster(Constants.getPusherAppCluster());
        pusherOptions.setEncrypted(true);
        pusherPrivate = new Pusher(Constants.getPusherAppKey(), pusherOptions);
    }

    public Pusher getPusher(){
        return pusherPrivate;
    }

    /**
     * Singleton Instance
     *
     * @return PusherOdk
     */
    public static synchronized PusherOdk getInstance(String token) {
        if (ourInstance == null || (ourInstance.token!=null && !ourInstance.token.equals(token))) {
            if (ourInstance != null){
                ourInstance.pusherPrivate.disconnect();
            }

            ourInstance = new PusherOdk(token);
        }
        return ourInstance;
    }

    public String getConnectionId(){
        return pusherPrivate.getConnection().getSocketId();
    }
}
