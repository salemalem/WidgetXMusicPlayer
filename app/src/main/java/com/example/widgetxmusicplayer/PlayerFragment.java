package com.example.widgetxmusicplayer;

import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.widgetxmusicplayer.utils.MusicUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerFragment extends Fragment {
    String location;

    private View parent_view;
    private SeekBar seek_song_progressbar;
    private FloatingActionButton btn_play;
    private TextView tv_song_current_duration, tv_song_total_duration;
    private ImageButton btn_next;
    private ImageButton btn_prev;

    MediaPlayer mp;
//    private MediaPlayer mp = new MediaPlayer();

    private Handler mHandler = new Handler();

    private MusicUtils utils;
    private Fragment fragment;
    private String[] locations;
    private Integer position;
//    public Fragment PlayerFragmentState;

    public AssetFileDescriptor afd;


    public View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            locations = this.getArguments().getStringArray("locations");
//            position = this.getArguments().getInt("position");
//            location = locations[position];
//        }
        try {
            locations = this.getArguments().getStringArray("locations");
            position = this.getArguments().getInt("position");
            location = locations[position];
//            PlayerFragmentState = getActivity().getSupportFragmentManager().getFragment(savedInstanceState, "PlayerFragmentState");
        } catch (Exception e) {

        }


        view = inflater.inflate(R.layout.player_fragment, container, false);
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
////        if (savedInstanceState != null) {
////            //Restore the fragment's state here
////            locations = savedInstanceState.getStringArray("locations");
////            position = savedInstanceState.getInt("position");
////            location = locations[position];
////        } else {
//         if (savedInstanceState != null) {
//            locations = savedInstanceState.getStringArray("locations");
//            position = savedInstanceState.getInt("position");
//            location = locations[position];
//        }
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (savedInstanceState != null) {
//            locations = savedInstanceState.getStringArray("locations");
//            position = savedInstanceState.getInt("position");
//            location = locations[position];
//        }
        setMusicPlayerComponents();
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
////        if (position != null) {
//        super.onSaveInstanceState(outState);
//        outState.putStringArray("locations", locations);
//        outState.putInt("position", position);
////        }
////        getActivity().getSupportFragmentManager().putFragment(outState, "PlayerFragmentKey", PlayerFragmentState);
//    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//
//        locations = savedInstanceState.getStringArray("locations");
//        position = savedInstanceState.getInt("position");
//        location = locations[position];
//        super.onViewStateRestored(savedInstanceState);
//    }

    private void setMusicPlayerComponents() {
//        parent_view = view.findViewById(R.id.parent_view);
        seek_song_progressbar = view.findViewById(R.id.seek_song_progressbar);
        btn_play = view.findViewById(R.id.btn_play);
        btn_next = view.findViewById(R.id.btn_next);
        btn_prev = view.findViewById(R.id.btn_prev);

        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        tv_song_current_duration = view.findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = view.findViewById(R.id.total_duration);

        try {
            mp.stop();
            mp.release();
            mp = null;
        } catch (Exception e) {

        }


//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
                btn_play.setImageResource(R.drawable.ic_play_arrow);
//            }
//        });


        try {
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        playNextSong(mediaPlayer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
//            afd = getActivity().getAssets().openFd(location);
            mp.setDataSource(location);
//            afd.close();
            mp.prepare();
            mp.start();
            btn_play.setImageResource(R.drawable.ic_pause);
            mHandler.post(mUpdateTimeTask);
        } catch (Exception e) {
            Snackbar.make(view, "Аудио файл табылмады.", Snackbar.LENGTH_SHORT).show();
        }

        utils = new MusicUtils();
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mp.seekTo(currentPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });

        buttonPlayerAction();

    }

    void playNextSong(MediaPlayer mp) throws Exception {
        mp.reset();

        if (position < locations.length) {
            position++;
        } else {
            position = 0;
        }

        mp.setDataSource(locations[position]);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    playNextSong(mp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mp.prepare();
        mp.start();
    }

    void playNextSong_button() throws IOException {
        mp.reset();

        if (position < locations.length) {
            position++;
        } else {
            position = 0;
        }

        mp.setDataSource(locations[position]);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    playNextSong(mp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mp.prepare();
        mp.start();
    }


    void playPreviousSong_button() throws IOException {
        mp.reset();

        if (position > 0) {
            position--;
        } else {
            position = locations.length - 1;
        }

        mp.setDataSource(locations[position]);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    playNextSong(mp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mp.prepare();
        mp.start();

    }



    private void buttonPlayerAction() {
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btn_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    mp.start();
                    btn_play.setImageResource(R.drawable.ic_pause);
                    mHandler.post(mUpdateTimeTask);
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playNextSong_button();
                    mHandler.post(mUpdateTimeTask);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playPreviousSong_button();
                    mHandler.post(mUpdateTimeTask);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
//    public void controlClick(View v) throws IOException {
//        int id = v.getId();
//        switch (id) {
//            case R.id.btn_repeat: {
//                toggleButtonColor((ImageButton) v);
//                Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
//                break;
//            }
//            case R.id.btn_shuffle: {
//                toggleButtonColor((ImageButton) v);
//                Snackbar.make(parent_view, "Shuffle", Snackbar.LENGTH_SHORT).show();
//                break;
//            }
//            case R.id.btn_prev: {
//                toggleButtonColor((ImageButton) v);
//                Snackbar.make(parent_view, "Previous", Snackbar.LENGTH_SHORT).show();
//                break;
//            }
//            case R.id.btn_next: {
//                toggleButtonColor((ImageButton) v);
//                Snackbar.make(parent_view, "Next", Snackbar.LENGTH_SHORT).show();
//
//                try {
////                    afd = getActivity().getAssets().openFd("off-deez.mp3");
////                    Log.i("tag:", afd.toString());
////                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                    mp.setDataSource("/storage/emulated/0/Download/Lil Morty - Choppa on me.mp3");
////                    afd.close();
//                    mp.prepare();
//                } catch (Exception e) {
//                    Snackbar.make(view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
//                }
//
//                break;
//            }
//        }
//    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            bt.setColorFilter(getResources().getColor(R.color.colorDarkOrange), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }

    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        int progress = utils.getProgressSeekBar(currentDuration, totalDuration);
        seek_song_progressbar.setProgress(progress);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mHandler.removeCallbacks(mUpdateTimeTask);
//        mp.stop();
//        mp.release();
//        mp = null;
//    }

}
