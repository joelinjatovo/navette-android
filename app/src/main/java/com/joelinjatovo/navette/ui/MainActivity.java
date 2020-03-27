package com.joelinjatovo.navette.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.vm.AuthViewModel;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.utils.Constants;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.Preferences;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AuthViewModel authViewModel;

    private ClubViewModel clubViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyViewModelFactory factory = new MyViewModelFactory(getApplication());

        clubViewModel = new ViewModelProvider(this, factory).get(ClubViewModel.class);

        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        authViewModel.isAuthenticated(Preferences.Auth.getCurrentUser(this));

        authViewModel.getAuthenticationState().observe(this,
                authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            if(authViewModel.getUser() != null){
                                connectPrivatePush(authViewModel.getUser());
                            }
                            Log.d(TAG, "'AUTHENTICATED'");
                            break;
                        case INVALID_AUTHENTICATION:
                        case UNAUTHENTICATED:
                            Log.d(TAG, "'UNAUTHENTICATED'");
                            break;
                    }
                });

        connectPush();

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
                case R.id.profile_fragment:
                case R.id.login_fragment:
                case R.id.register_fragment:
                case R.id.verify_phone_fragment:
                case R.id.clubs_fragment:
                case R.id.order_fragment:
                    navView.setVisibility(View.GONE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //requestWindowFeature( Window.FEATURE_NO_TITLE );
                    break;
                case R.id.main_navigation:
                case R.id.navigation_home:
                    navView.setVisibility(View.VISIBLE);
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

        clubViewModel.load();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
    }

    private void connectPush() {
        PusherOptions options = new PusherOptions();
        options.setCluster(Constants.PUSHER_APP_CLUSTER);

        Pusher pusher = new Pusher(Constants.PUSHER_APP_KEY, options);
        pusher.connect();

        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG, "my-event " + event.getData());
            }
        });
        channel.bind("user.point.created", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG, "user.point.created " + event.getData());
            }
        });
    }

    private void connectPrivatePush(User user) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", user.getAuthorizationToken());
        HttpAuthorizer authorizer = new HttpAuthorizer(Constants.BASE_URL + "broadcasting/auth");
        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        options.setCluster(Constants.PUSHER_APP_CLUSTER);
        Pusher pusher = new Pusher(Constants.PUSHER_APP_KEY, options);
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
}