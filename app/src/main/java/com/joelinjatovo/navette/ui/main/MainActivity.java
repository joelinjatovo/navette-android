package com.joelinjatovo.navette.ui.main;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.ui.auth.login.LoginViewModel;
import com.joelinjatovo.navette.ui.auth.login.LoginViewModelFactory;
import com.joelinjatovo.navette.ui.main.auth.AuthViewModel;
import com.joelinjatovo.navette.ui.main.auth.AuthViewModelFactory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
import androidx.appcompat.widget.Toolbar;
=======
import androidx.lifecycle.ViewModelProvider;
>>>>>>> 44b858c183c8e37b0c20d4887d84f6927ebbee0c
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_maps,
            R.id.navigation_account
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
