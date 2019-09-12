package com.example.user.stchess;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;

public class TwoActivity extends AppCompatActivity implements View.OnClickListener {

    // local variables
    private ImageButton btStart;
    private ImageButton btBack;
    private ImageButton btColor;
    private ImageView imPlayerOne;
    private ImageView imPlayerTwo;
    private TextView txPlayerOne;
    private TextView txPlayerTwo;
    private EditText etPlayerOne;
    private EditText etPlayerTwo;
    private int player_color = 0;

    // create the TextWatcher
    TextWatcher playerOneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void afterTextChanged(Editable s) {
            // set txPlayerOne.text to s
            txPlayerOne.setText(s.toString());
        }
    };

    TextWatcher playerTwoWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void afterTextChanged(Editable s) {
            // set txPlayerTwo.text to s
            txPlayerTwo.setText(s.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        // set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get the buttons
        btStart = (ImageButton) findViewById(R.id.buttonStart);
        btBack = (ImageButton) findViewById(R.id.buttonBack);
        btColor = (ImageButton) findViewById(R.id.buttonColor);

        // get the color images
        imPlayerOne = (ImageView) findViewById(R.id.player1Color);
        imPlayerTwo = (ImageView) findViewById(R.id.player2Color);

        // get textviews
        txPlayerOne = (TextView) findViewById(R.id.player1);
        txPlayerTwo = (TextView) findViewById(R.id.player2);

        // get edittexts
        etPlayerOne = (EditText) findViewById(R.id.player1Name);
        etPlayerTwo = (EditText) findViewById(R.id.player2Name);

        // add click listener
        btStart.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btColor.setOnClickListener(this);

        // add edittext listener
        etPlayerOne.addTextChangedListener(playerOneWatcher);
        etPlayerTwo.addTextChangedListener(playerTwoWatcher);
    }

    public void onClick(View v) {
        // change activity
        if (v == btStart) {
            // create game intent
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("PLAYER_COLOR", player_color);
            intent.putExtra("PLAYER_1_NAME", etPlayerOne.getText().toString());
            intent.putExtra("PLAYER_2_NAME", etPlayerTwo.getText().toString());
            // start single activity
            startActivity(intent);
        }
        else if (v == btBack) {
            // start two players activity
            startActivity(new Intent(this, MainActivity.class));
        }
        // color selection
        if (v == btColor) {
            if (player_color == 0) {
                player_color = 1;
                imPlayerOne.setImageResource(R.drawable.b_rook);
                imPlayerTwo.setImageResource(R.drawable.w_rook);
            }
            else if (player_color == 1) {
                player_color = 0;
                imPlayerOne.setImageResource(R.drawable.w_rook);
                imPlayerTwo.setImageResource(R.drawable.b_rook);
            }
        }
    }
}
