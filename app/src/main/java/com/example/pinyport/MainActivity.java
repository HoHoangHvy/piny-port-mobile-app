package com.example.pinyport;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pinyport.databinding.ActivityMainBinding;
import com.example.pinyport.network.SharedPrefsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefsManager = new SharedPrefsManager(this);
        boolean isLoggedIn = sharedPrefsManager.isLoggedIn();

        if (!isLoggedIn) {
            // If not logged in, navigate to the login activity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up BottomNavigationView and NavController
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // AppBarConfiguration should not include the OrderDetailFragment
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_orders, R.id.navigation_chat,
                R.id.navigation_home, R.id.navigation_customers, R.id.navigation_profile, R.id.orderDetailFragment, R.id.navigation_chat_detail, R.id.customer_detail_navigation, R.id.navigation_create_order, R.id.navigation_create_customer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Set custom ActionBar layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_bar);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("white")));

            // Set up button click listener for home button
            View customView = getSupportActionBar().getCustomView();
            ImageButton homeButton = customView.findViewById(R.id.homeButton);
            homeButton.setOnClickListener(v -> {
                if (navController.getCurrentDestination() != null &&
                        (navController.getCurrentDestination().getId() == R.id.orderDetailFragment) || (navController.getCurrentDestination().getId() == R.id.navigation_create_order)) {
                    // Navigate to OrdersFragment if currently on OrderDetailFragment
                    navController.navigate(R.id.navigation_orders);
                } else if (navController.getCurrentDestination() != null &&
                        (navController.getCurrentDestination().getId() == R.id.navigation_create_customer)) {
                    navController.navigate(R.id.navigation_customers);
                } else {
                    // Navigate to HomeFragment otherwise
                    navController.navigate(R.id.navigation_home);
                }
            });


            // Set up destination change listener to update title and visibility of home button
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.navigation_chat_detail ) {
                    // Hide ActionBar in specific fragments
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                } else {
                    // Show ActionBar in other fragments
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                }
                TextView customTitle = customView.findViewById(R.id.customTitle);
                // Update title based on destination
                if (destination.getId() == R.id.navigation_create_order) {
                    customTitle.setText("Create Orders");
                } else if (destination.getId() == R.id.navigation_create_customer) {
                    customTitle.setText("Create Customers");
                } else if (destination.getId() == R.id.navigation_orders) {
                    customTitle.setText("Orders");
                } else if (destination.getId() == R.id.navigation_chat) {
                    customTitle.setText("Chat");
                } else if (destination.getId() == R.id.navigation_home) {
                    customTitle.setText("Home");
                } else if (destination.getId() == R.id.navigation_customers) {
                    customTitle.setText("Customers");
                } else if (destination.getId() == R.id.navigation_profile) {
                    customTitle.setText("Profile");
                } else if (destination.getId() == R.id.orderDetailFragment) {
                    customTitle.setText("Order Detail");
                } else if (destination.getId() == R.id.customer_detail_navigation) {
                    customTitle.setText("Customer Detail");
                }

                // Show or hide the home button depending on the fragment
                if (destination.getId() == R.id.navigation_home) {
                    homeButton.setVisibility(View.GONE);
                } else {
                    homeButton.setVisibility(View.VISIBLE);
                }
            });

        }
    }
}
