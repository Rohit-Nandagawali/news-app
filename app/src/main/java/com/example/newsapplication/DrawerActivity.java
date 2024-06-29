package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.newsapplication.Fragments.Home;
import com.example.newsapplication.Fragments.Profile;
import com.example.newsapplication.Fragments.Publish;
import com.example.newsapplication.databinding.ActivityDrawerBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivityDrawerBinding binding;
    GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);


        binding = ActivityDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        to show dp of user logged in
        showdp();

//        drawer setup
        setupDrawer();

    }

    //    setting up drawer
    private void setupDrawer() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new Home()); //by default showing Home Fragment in drawer
        fragmentTransaction.commit();

//        show drawer when clicking on icon
        binding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawer.openDrawer(Gravity.LEFT);
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(this); //showing navigation in this drawer

    }

    private void showdp() {
        //        getting sign in account data
        account = GoogleSignIn.getLastSignedInAccount(this);

        //glide to show url from internet
        Glide.with(this).load(account.getPhotoUrl()).into(binding.profileIcon);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Check which menu item was selected and take action using if-else
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            // When the "Home" menu item is selected, display the Home fragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new Home());
            fragmentTransaction.commit();
        } else if (itemId == R.id.nav_publish) {
            // When the "Publish" menu item is selected, display the Publish fragment
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame_layout, new Publish());
            fragmentTransaction1.commit();
        } else if (itemId == R.id.nav_profile) {
            // When the "Profile" menu item is selected, display the Profile fragment
            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.frame_layout, new Profile());
            fragmentTransaction2.commit();
        }
        // Close the navigation drawer after an item is selected
        binding.drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}