package com.navetteclub.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.navetteclub.App;
import com.navetteclub.R;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.ItemPayload;
import com.navetteclub.models.OrderPayload;
import com.navetteclub.models.RidePayload;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.services.PusherService;
import com.navetteclub.ui.driver.RideMapFragment;
import com.navetteclub.ui.order.LiveFragment;
import com.navetteclub.ui.order.OrderViewFragment;
import com.navetteclub.utils.GlobalNotificationBuilder;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.navetteclub.services.PusherService.CHANNEL_ID;

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
        registerNetworkBroadcastReceiver();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.navetteclub.dev", //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                android.util.Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, GlobalNotificationBuilder.NETWORK_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_notification_32)
                        .setContentTitle(getString(R.string.internet_error_title))
                        .setContentText(getString(R.string.internet_error_subtitle))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.internet_error_description)))
                        .setContentIntent(pendingIntent) // will fire when the user taps the notification
                        .setAutoCancel(true) // automatically removes the notification when the user taps it.
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(mBuilder);

        // notificationId is a unique int for each notification that you must define
        mNotificationManagerCompat.notify(GlobalNotificationBuilder.NETWORK_NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcastReceiver();
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(getApplication());

        clubViewModel = new ViewModelProvider(this, factory).get(ClubViewModel.class);

        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        authViewModel.isAuthenticated(Preferences.Auth.getCurrentUser(this));

        authViewModel.getAuthenticationState().observe(this,
                authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            Log.d(TAG, "'AUTHENTICATED'");
                            if(authViewModel.getUser() != null){
                                if(App.isServiceRunning(PusherService.class)){
                                    Log.d(TAG, "'stopPushService'");
                                    App.stopPushService();
                                }
                                App.startPusherService(authViewModel.getUser().getAuthorizationToken(), authViewModel.getUser().getId());
                            }
                            break;
                        case INVALID_AUTHENTICATION:
                        case UNAUTHENTICATED:
                            Log.d(TAG, "'UNAUTHENTICATED'");
                            if(App.isServiceRunning(PusherService.class)){
                                Log.d(TAG, "'stopPushService'");
                                App.stopPushService();
                            }
                            break;
                    }
                });
    }

    private void setupUi(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_notification,
                R.id.navigation_account
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()){
                case R.id.login_fragment:
                case R.id.register_fragment:
                case R.id.forgot_fragment:
                case R.id.phone_fragment:
                case R.id.verify_phone_fragment:
                case R.id.search_fragment:
                case R.id.order_fragment:
                case R.id.navigation_order:
                    navView.setVisibility(View.GONE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
                    }
                    //requestWindowFeature( Window.FEATURE_NO_TITLE );
                    break;
                case R.id.thanks_fragment:
                case R.id.rides_fragment:
                case R.id.ride_point_fragment:
                case R.id.ride_points_fragment:
                case R.id.ride_map_fragment:
                case R.id.orders_fragment:
                case R.id.order_view_fragment:
                case R.id.live_fragment:
                    navView.setVisibility(View.GONE);
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

            showNotification();
        }else{
            alertView.setVisibility(View.GONE);
            mNotificationManagerCompat.cancel(GlobalNotificationBuilder.NETWORK_NOTIFICATION_ID);
        }
    }

    protected void registerNetworkBroadcastReceiver() {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(mPusherReceiver, new IntentFilter(PusherService.ACTION_BROADCAST));
    }

    protected void unregisterNetworkBroadcastReceiver() {
        try {
            unregisterReceiver(mNetworkReceiver);
            unregisterReceiver(mPusherReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
            updateConnectionUi(Utils.haveNetworkConnection(MainActivity.this));
        }
    };

    private BroadcastReceiver mPusherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) { return; }
            String event = bundle.getString(PusherService.EXTRA_EVENT_NAME);
            String payload = bundle.getString(PusherService.EXTRA_PAYLOAD);
            Log.d(TAG, "Event "  + event);
            Log.d(TAG, "Payload "  + payload);

            if(event!=null && payload!=null){
                Gson gson = new Gson();
                if ("item.date.changed".equals(event)) {
                    ItemPayload itemPayload = gson.fromJson(payload, ItemPayload.class);
                    if (itemPayload != null) {
                        if(itemPayload.getRideAt()!=null){
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText(getString(R.string.info))
                                    .setContentText(Utils.formatDateToString(itemPayload.getRideAt()))
                                    .show();
                        }
                    }
                }
            }
        }
    };
}