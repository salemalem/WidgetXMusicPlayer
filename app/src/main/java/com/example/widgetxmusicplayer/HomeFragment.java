package com.example.widgetxmusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<String> stringArrayList;
    //    ArrayList<String> locationsArrayList;
//    ArrayList<String> arrayList;
//    String[] locationsArray;
    String locationsString = "";
    String[] musicParts;
    Boolean musicIsPlaying = false;

    ListView musicListView;

    ArrayAdapter<String> stringArrayAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }

        } else {
            doStuff();
        }

    }

    public void doStuff() {
        musicListView = (ListView) view.findViewById(R.id.musicListView);
        stringArrayList = new ArrayList<>();
        getMusic();
        stringArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, stringArrayList);
        musicListView.setAdapter(stringArrayAdapter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open music player to play desired song
//                Log.i("debugparent", locationsString);
//                Log.i("debugparent", Integer.toString(position));
//                Log.i("debugparent", String.valueOf(position));
//                Log.i("debugparent", musicParts[(int) id]);
//                Log.i("debugparent", Array.toString(musicParts));
//                for (String foo: musicParts) {
//                    Log.i("debugs", foo);
//                }
//                playMusic(position);
//                if(musicIsPlaying) {
//                    finishActivity(1);
//                }
                musicIsPlaying = true;
//                Intent intent = new Intent(getContext(), music_player.class);
//                intent.putExtra("path", musicParts[position]);
//                intent.putExtra("")
//                startActivity(intent);
            }
        });
    }

    public void getMusic() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            int counter = 0;

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);

//                locationsArrayList.add(currentLocation);
                stringArrayList.add(currentTitle + "\n" + currentArtist);
                locationsString += currentLocation;
                locationsString += "|";
//                stringArrayList.add(currentLocation);
//                locationsArray[counter] = currentLocation;
//                Log.i("currentLocation", currentLocation.getClass().getName());
//                if(currentLocation instanceof String) {
//                    Log.i("Stringggggggggggggggg","messageeeeeeeeeeeeeeeeeeeeeee");
//                    arrayList.add(currentLocation);
//                }
                counter++;
            } while (songCursor.moveToNext());
            musicParts = locationsString.split("\\|");
        }


    }
}