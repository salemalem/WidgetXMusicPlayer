package com.example.widgetxmusicplayer;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.widgetxmusicplayer.utils.DownloadTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    List<String> Singer;
    List<String> Song;
    List<String> Link;
    List<String> SongNameAndArtistListToShow;
    View view;

    private ListView downloadedMusicListView;
    private SearchView searchView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchView = view.findViewById(R.id.searchView);
        downloadedMusicListView = (ListView) view.findViewById(R.id.downloadedMusicListView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getWebsite(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.search_fragment);
//
//        searchView = view.findViewById(R.id.searchView);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                getWebsite(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//    }

    public String toNormalURL(String text) {

        String builder = "";

        for (String retval : text.split(" ")) {
            builder = builder + "%20" + retval;

        }

        StringBuilder otw = new StringBuilder(builder);

        otw.delete(0, 3);



        String url = "https://mp3-tut.net/search?query=" + otw;

        return url;
    }

    //---------------------------------------------------//

    //---------------------------------------------------//
    private void getWebsite(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {

                    Document doc = Jsoup.connect(toNormalURL(name)).get();
                    Singer = new ArrayList<>();
                    Song = new ArrayList<>();
                    SongNameAndArtistListToShow = new ArrayList<>();
                    Link = new ArrayList<>();

                    for (int data_key = 0; data_key < 50; data_key++) {

                        Elements smtg = doc.select("div[data-key=\"" + Integer.toString(data_key) + "\"]");

                        Elements singer = smtg.select("div.audio-list-entry-inner div.track-name-container div.track div.title a");
                        Elements composition = smtg.select("div.audio-list-entry-inner div.track-name-container div.track div.title:nth-child(2)");
                        Elements link_to_download = smtg.select("div.audio-list-entry-inner div.download-container a");

                        builder.append("\n").append(singer.text()).append("\n").append(composition.text()).append("\n").append(link_to_download.attr("href"));
                        Singer.add(singer.text());
                        Song.add(composition.text());
                        SongNameAndArtistListToShow.add(singer.text() + "\n" + composition.text());
                        Link.add(link_to_download.attr("href"));
                    }

                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Link.get(0)));
//                        startActivity(browserIntent);

                        //Write your code here...

                        //names of arrays = Singer, Song, Link
                        ArrayAdapter arrayAdapter;
                        arrayAdapter = new ArrayAdapter(
                                getContext(),
                                android.R.layout.simple_list_item_1,
                                SongNameAndArtistListToShow
                        );


                        downloadedMusicListView.setAdapter(arrayAdapter);

                        downloadedMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Link.get(position)));
//                                startActivity(browserIntent);
                                // Here, thisActivity is the current activity
                                if (ContextCompat.checkSelfPermission(getContext(),
                                        Manifest.permission.READ_CONTACTS)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    // Permission is not granted
                                    // Should we show an explanation?
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        // Show an explanation to the user *asynchronously* -- don't block
                                        // this thread waiting for the user's response! After the user
                                        // sees the explanation, try again to request the permission.
                                    } else {
                                        // No explanation needed; request the permission
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                0);

                                    }
                                } else {
                                    // Permission has already been granted
                                }

                                File file = new File(getActivity().getExternalFilesDir(null), "Dummy");

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Link.get(position)))
                                        .setTitle(Song.get(position))// Title of the Download Notification
                                        .setDescription(Singer.get(position))// Description of the Download Notification
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE)
                                        .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                                        .setRequiresCharging(false)// Set if charging is required to begin the download
                                        .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                                        .setAllowedOverRoaming(true)// Set if download is allowed on roaming network
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                SongNameAndArtistListToShow.get(position) + ".mp3");
                                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                long downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
                            }
                        });

                    }
                });
            }
        }).start();
    }
}


