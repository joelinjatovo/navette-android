package com.navetteclub.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.navetteclub.R;
import com.navetteclub.database.entity.User;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.utils.Utils;
import com.navetteclub.views.AlertView;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;
import com.navetteclub.vm.MyViewModelFactory;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;

import static com.navetteclub.services.LocationUpdatesService.ACTION_BROADCAST;
import static com.navetteclub.services.LocationUpdatesService.ACTION_BROADCAST_PUSHER;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AuthViewModel authViewModel;

    private ClubViewModel clubViewModel;

    private NotificationManagerCompat mNotificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupUi();

        setupNotification();

        setupViewModel();

        initMapSdk();

        connectPush();

        registerNetworkBroadcastReceiver();
    }

    private void setupNotification() {
        mNotificationManagerCompat = NotificationManagerCompat.from(this);
    }

    public void showNotification(){
        boolean areNotificationsEnabled = mNotificationManagerCompat.areNotificationsEnabled();

        if (!areNotificationsEnabled) {
            // Because the user took an action to create a notification, we create a prompt to let
            // the user re-enable notifications for this application again.
            Snackbar snackbar = Snackbar
                    .make(
                            findViewById(R.id.nav_view),
                            "You need to enable notifications for this app",
                            Snackbar.LENGTH_LONG)
                    .setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Links to this app's notification settings
                            openNotificationSettingsForApp();
                        }
                    });
            snackbar.show();
            return;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "a")
                        .setSmallIcon(R.drawable.outline_add_24)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Much longer text that cannot fit one line..."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotificationManagerCompat.notify(1, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcastReceiver();
    }

    private void setupViewModel() {
        MyViewModelFactory factory = new MyViewModelFactory(getApplication());

        clubViewModel = new ViewModelProvider(this, factory).get(ClubViewModel.class);

        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        authViewModel.isAuthenticated(Preferences.Auth.getCurrentUser(this));

        authViewModel.getAuthenticationState().observe(this,
                authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            Log.d(TAG, "'AUTHENTICATED'");
                            if(authViewModel.getUser() != null){
                                connectPrivatePush(authViewModel.getUser());
                            }
                            break;
                        case INVALID_AUTHENTICATION:
                        case UNAUTHENTICATED:
                            Log.d(TAG, "'UNAUTHENTICATED'");
                            break;
                    }
                });

        // load clubs
        clubViewModel.load();
    }

    private void setupUi(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_maps,
                R.id.navigation_notification,
                R.id.navigation_account
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()){
                case R.id.order_fragment:
                    navView.setVisibility(View.GONE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //requestWindowFeature( Window.FEATURE_NO_TITLE );
                    break;
                default:
                    navView.setVisibility(View.VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE );
                    break;
            }
        });
    }

    private void initMapSdk() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

    }

    private void updateConnectionUi(Boolean connected) {
        AlertView alertView = findViewById(R.id.alertView);
        if(!connected){
            alertView.setVisibility(View.VISIBLE);
            alertView.setIcon(R.drawable.outline_wifi_off_black_18);
            alertView.setTitle(R.string.internet_error_title);
            alertView.setSubtitle(R.string.internet_error_subtitle);
        }else{
            alertView.setVisibility(View.GONE);
        }
    }

    private void registerNetworkBroadcastReceiver() {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkBroadcastReceiver() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void connectPush() {
        PusherOptions options = new PusherOptions();
        options.setCluster(Constants.getPusherAppCluster());

        Pusher pusher = new Pusher(Constants.getPusherAppKey(), options);
        pusher.connect();

        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG + "Pusher", "my-event " + event.getData());
                showNotification();
            }
        });

        channel.bind("user.point.created", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG + "Pusher", "user.point.created " + event.getData());

                JsonObject convertedObject = new Gson().fromJson(event.getData(), JsonObject.class);
                JsonObject point = convertedObject.get("point").getAsJsonObject();
                double lat = point.get("lat").getAsDouble();
                double lng = point.get("lng").getAsDouble();
                LatLng location = new LatLng(lat,lng);

                // Notify anyone listening for broadcasts about the new location.
                Intent intent = new Intent(LocationUpdatesService.ACTION_BROADCAST_PUSHER);
                intent.putExtra(LocationUpdatesService.EXTRA_LOCATION, location);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });
    }

    private void connectPrivatePush(User user) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", user.getAuthorizationToken());
        HttpAuthorizer authorizer = new HttpAuthorizer(Constants.getBaseUrl() + "broadcasting/auth");
        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        options.setCluster(Constants.getPusherAppCluster());
        Pusher pusher = new Pusher(Constants.getPusherAppKey(), options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i(TAG + "Pusher", "Connection State Change: " + change.toString());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i(TAG + "Pusher", String.format("Connection Error: [%s], exception was [%s]", message, e));
            }
        }, ConnectionState.ALL);

        pusher.subscribePrivate("private-App.User."+ user.getId(), new PrivateChannelEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG + "Pusher", "onEvent");
                Log.d(TAG + "Pusher", event.getEventName());
                Log.d(TAG + "Pusher", event.getData());
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d(TAG + "Pusher", "onSubscriptionSucceeded " + channelName);
            }

            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.d(TAG + "Pusher", String.format("Authentication failure due to [%s], exception was [%s]", message, e));
            }
        }, "user.point.created");
    }

    /**
     * Helper method for the SnackBar action, i.e., if the user has this application's notifications
     * disabled, this opens up the dialog to turn them back on after the user requests a
     * Notification launch.
     *
     * IMPORTANT NOTE: You should not do this action unless the user takes an action to see your
     * Notifications like this sample demonstrates. Spamming users to re-enable your notifications
     * is a bad idea.
     */
    private void openNotificationSettingsForApp() {
        // Links to this app's notification settings.
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG + "Broadcast", "mNetworkReceiver.onReceive");
            updateConnectionUi(Utils.haveNetworkConnection(MainActivity.this));
        }
    };
}