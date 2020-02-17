package com.example.widgetxmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    SearchFragment searchFragment;
    HomeFragment homeFragment;
    PlayerFragment playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        searchFragment = new SearchFragment();
        homeFragment = new HomeFragment();
        playerFragment = new PlayerFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, new HomeFragment()).commit();
        }



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.search:
                        fragment = searchFragment;
                        break;
                    case R.id.home:
                        fragment = homeFragment;
                        break;
                    case R.id.player:
                        fragment = playerFragment;
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }
}

