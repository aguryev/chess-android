package com.example.user.stchess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // declare buttons
    private ImageButton btSingle;
    private ImageButton btTwo;
    private ImageButton btOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get the buttons
        btSingle = (ImageButton)findViewById(R.id.buttonSingle);
        btTwo = (ImageButton)findViewById(R.id.buttonTwo);
        btOptions = (ImageButton)findViewById(R.id.buttonOptions);

        // add click listener
        btSingle.setOnClickListener(this);
        btTwo.setOnClickListener(this);
        btOptions.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == btSingle) {
            // start single activity
            startActivity(new Intent(this, SingleActivity.class));
        }
        else if (v == btTwo) {
            // start two players activity
            startActivity(new Intent(this, TwoActivity.class));
        }
        else if (v == btOptions) {
            // show options
            startActivity(new Intent(this, OptionsActivity.class));
        }
    }
}
