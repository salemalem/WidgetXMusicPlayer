package com.example.widgetxmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    SearchFragment searchFragment = new SearchFragment();
    HomeFragment homeFragment = new HomeFragment();
    public PlayerFragment playerFragment = new PlayerFragment();
    Fragment fragment = null;

    MediaPlayer mp = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, new HomeFragment()).commit();
        }



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.search:
//                        fragment = searchFragment;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, searchFragment).commit();
                        break;
                    case R.id.home:
//                        fragment = homeFragment;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, homeFragment).commit();
                        break;
//                    case R.id.player:
////                        fragment = playerFragment;
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, playerFragment).commit();
//                        break;
                }

//                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }
}

