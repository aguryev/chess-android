package com.example.user.stchess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SingleActivity extends AppCompatActivity implements View.OnClickListener {

    // local variables
    private ImageButton btStart;
    private ImageButton btBack;
    private ImageButton btPawn;
    private ImageButton btRook;
    private ImageButton btQueen;
    private ImageButton btColor;
    private TextView txDifficulty;
    private EditText etPlayerName;
    private int difficulty_level = 1;
    private int player_color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        // set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get the buttons
        btStart = (ImageButton)findViewById(R.id.buttonStart);
        btBack = (ImageButton)findViewById(R.id.buttonBack);
        btColor = (ImageButton)findViewById(R.id.playerColor);
        btPawn = (ImageButton)findViewById(R.id.difPawn);
        btRook = (ImageButton)findViewById(R.id.difRook);
        btQueen = (ImageButton)findViewById(R.id.difQueen);

        //get textview
        txDifficulty = (TextView)findViewById(R.id.compDifficulty);

        //get edittext
        etPlayerName = (EditText)findViewById(R.id.playerName);

        // add click listener
        btStart.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btPawn.setOnClickListener(this);
        btRook.setOnClickListener(this);
        btQueen.setOnClickListener(this);
        btColor.setOnClickListener(this);
    }

    public void onClick(View v) {
        // change activity
        if (v == btStart) {
            // create game intent
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("DIFFICULTY_LEVEL", difficulty_level);
            intent.putExtra("PLAYER_COLOR", player_color);
            intent.putExtra("PLAYER_1_NAME", etPlayerName.getText().toString());
            intent.putExtra("PLAYER_2_NAME", getString(R.string.computer) + ": " +
                    txDifficulty.getText().toString());
            // start single activity
            startActivity(intent);
        }
        else if (v == btBack) {
            // return to main activity
            startActivity(new Intent(this, MainActivity.class));
        }
        // color selection
        if (v == btColor) {
            if (player_color == 0) {
                player_color = 1;
                btColor.setImageResource(R.drawable.b_rook);
            }
            else if (player_color == 1) {
                player_color = 0;
                btColor.setImageResource(R.drawable.w_rook);
            }
        }
        // difficulty level selection
        if (v == btPawn) {
            difficulty_level = 0;
            txDifficulty.setText(getString(R.string.easy));
            btPawn.setBackground(getDrawable(R.drawable.dif_border));
            btRook.setBackground(null);
            btQueen.setBackground(null);
        }
        else if (v == btRook) {
            difficulty_level = 1;
            txDifficulty.setText(getString(R.string.medium));
            btPawn.setBackground(null);
            btRook.setBackground(getDrawable(R.drawable.dif_border));
            btQueen.setBackground(null);
        }
        else if (v == btQueen) {
            difficulty_level = 2;
            txDifficulty.setText(getString(R.string.hard));
            btPawn.setBackground(null);
            btRook.setBackground(null);
            btQueen.setBackground(getDrawable(R.drawable.dif_border));
        }
    }

}
