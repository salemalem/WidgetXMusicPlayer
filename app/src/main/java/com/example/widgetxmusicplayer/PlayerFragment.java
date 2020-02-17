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
import java.util.Timer;
import java.util.TimerTask;

public class PlayerFragment extends Fragment {
    String location;

    private View parent_view;
    private SeekBar seek_song_progressbar;
    private FloatingActionButton btn_play;
    private TextView tv_song_current_duration, tv_song_total_duration;

    private MediaPlayer mp;
    private Handler mHandler = new Handler();

    private MusicUtils utils;

    public AssetFileDescriptor afd;

    public View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            location = this.getArguments().getString("location");
        } catch (Exception e) {
            
        }
        view = inflater.inflate(R.layout.player_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMusicPlayerComponents();
    }

    private void setMusicPlayerComponents() {
//        parent_view = view.findViewById(R.id.parent_view);
        seek_song_progressbar = view.findViewById(R.id.seek_song_progressbar);
        btn_play = view.findViewById(R.id.btn_play);

        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        tv_song_current_duration = view.findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = view.findViewById(R.id.total_duration);


        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btn_play.setImageResource(R.drawable.ic_pause);
            }
        });


        try {
            mp.stop();
        } catch (Exception e) {

        }

        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
//        mp.release();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        } else {
            Snackbar.make(parent_view, item.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
