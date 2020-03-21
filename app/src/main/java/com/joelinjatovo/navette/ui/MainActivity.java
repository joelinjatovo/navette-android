package com.joelinjatovo.navette.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.vm.AuthViewModel;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.utils.Constants;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.Preferences;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
            if(destination.getId() == R.id.navigation_maps) {
                navView.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //requestWindowFeature( Window.FEATURE_NO_TITLE );
            } else if(destination.getId() == R.id.navigation_home) {
                navView.setVisibility(View.VISIBLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //requestWindowFeature( Window.FEATURE_NO_TITLE );
            } else {
                navView.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE );
            }
        });

        connectPush();

        clubViewModel.load();
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
                Log.d(TAG, "my-event");
                Log.d(TAG, event.getData());
            }
        });
    }
}
