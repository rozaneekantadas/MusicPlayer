package com.suptodas.diu.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class AudioPlayerActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private Runnable runnable;
    private Handler handler;
    private static MediaPlayer mediaPlayer;
    private TextView songTitleName;
    private static int position;
    private ArrayList<File> songs;
    private ArrayList<String> songName;
    private ImageButton btnPlay;
    private TextView durarionSong, currentPositionSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        durarionSong = findViewById(R.id.duration);
        currentPositionSong = findViewById(R.id.currentPosition);

        songTitleName = findViewById(R.id.songNameTitle);

        getSupportActionBar().setTitle("Music Player");

        Bundle bundle = getIntent().getExtras();

        songs = (ArrayList) bundle.getParcelableArrayList("list");
        position = bundle.getInt("position");
        songName = bundle.getStringArrayList("songNameList");

        Uri uri = Uri.parse(songs.get(position).toString());


        createMusicPlayer(uri, songName.get(position));

        seekBar = findViewById(R.id.seekBar);
        handler = new Handler();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                     mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changeSeekBar() {

        currentPositionSong.setText(createTimerLabel(mediaPlayer.getCurrentPosition()));
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekBar();
                }
            };

            handler.postDelayed(runnable, 0);
        }

    }

    public void createMusicPlayer(Uri songUri, String title){

        songTitleName.setText(title);

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();

        mediaPlayer = MediaPlayer.create(this, songUri);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                seekBar.setMax(mediaPlayer.getDuration());

                String toTime = createTimerLabel(mediaPlayer.getDuration());
                durarionSong.setText(toTime);

                mediaPlayer.start();
                changeSeekBar();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if(position == songName.size()-1){
                    position = 0;
                }
                else {
                    position++;
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                createMusicPlayer(uri, songName.get(position));

            }
        });

    }

    public void musicPlayerRun(View view){

        btnPlay = findViewById(R.id.playBtn);

        if(view.getId() == R.id.playBtn){

            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                btnPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
            }else {
                mediaPlayer.start();
                btnPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                changeSeekBar();
            }

        }

        if(view.getId() == R.id.playForward){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
        }

        if(view.getId() == R.id.playBack){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
        }

        if(view.getId() == R.id.nextSong){

            if(position == songName.size()-1){
                position = 0;
            }
            else {
                position++;
            }

            Uri uri = Uri.parse(songs.get(position).toString());
            createMusicPlayer(uri, songName.get(position));
        }

        if (view.getId() == R.id.previousSong){

            if(position == 0){
                position = songName.size()-1;
            }
            else {
                position--;
            }

            Uri uri = Uri.parse(songs.get(position).toString());
            createMusicPlayer(uri, songName.get(position));
        }
    }

    public String createTimerLabel(int duration){

        String timeLable = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLable += min + ":";

        if (sec < 10) timeLable += "0";
        timeLable += sec;

        return timeLable;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        mediaPlayer.pause();

    }
}
