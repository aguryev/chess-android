package com.example.user.stchess;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;
import android.net.Uri;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.content.Context;


public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    // buttons
    ImageButton btBack;
    ImageButton btMute;
    Button btSelect;

    // audio manager
    AudioManager audioManager;

    //music player
    MediaPlayer playMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get the buttons
        btBack = (ImageButton)findViewById(R.id.buttonBack);
        btMute = (ImageButton)findViewById(R.id.buttonMute);
        btSelect = (Button)findViewById(R.id.buttonSelect);
        btBack.setOnClickListener(this);
        btSelect.setOnClickListener(this);
        btMute.setOnClickListener(this);

        // initialize audio manager
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // set mute icon
        if (audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
            btMute.setBackground(getDrawable(R.drawable.mute));
        }
        else btMute.setBackground(getDrawable(R.drawable.unmute));

    }

    public void onClick(View v) {
        int i = 0;
        if (v == btSelect) {
            // choose music file
            showFileChooser();
        }
        else if (v == btMute) {
            // toggle mute
            audioManager.adjustStreamVolume (
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_TOGGLE_MUTE,
                        AudioManager.FLAG_SHOW_UI);
            // set mute icon
            if (audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
                btMute.setBackground(getDrawable(R.drawable.mute));
            }
            else btMute.setBackground(getDrawable(R.drawable.unmute));
        }
        else if (v == btBack) {
            // return to main activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void showFileChooser() {
        // file chooser
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(
                Intent.createChooser(intent, "Select a Music File"),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // play music
                    playMusic = new MediaPlayer();
                    try {
                        playMusic.setDataSource(this, uri);
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Cannot play music!",
                                Toast.LENGTH_LONG).show();
                    }
                    try {
                        playMusic.prepare();
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Cannot play music!",
                                Toast.LENGTH_LONG).show();
                    }
                    playMusic.start();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
