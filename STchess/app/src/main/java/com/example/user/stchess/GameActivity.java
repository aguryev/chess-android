package com.example.user.stchess;

import android.view.Gravity;
import android.widget.Toast;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnDragListener;
import android.view.View.DragShadowBuilder;
import android.widget.PopupMenu;

import java.util.ArrayList;
import android.os.CountDownTimer;
import android.media.MediaPlayer;

public class GameActivity extends AppCompatActivity
        implements OnClickListener, OnLongClickListener, OnDragListener {

    // board model
    Board board;
    boolean turn;
    ArrayList<Integer> validTargets;

    // AI
    AI ai;
    int difficultyLevel;

    // player name
    TextView PlayerOneName, PlayerTwoName;

    // images
    ImageView[] sqImages; // square images
    int [] squareID; // square IDs
    int[] wPieceDraw, bPieceDraw; // piece drawable ID

    // captures pieces
    ImageView[] capturedByOne, capturedByTwo;
    int sizeOne, sizeTwo;

    // buttons
    ImageButton btPause;
    ImageButton btBack;

    // timer
    TextView timerOne, timerTwo;
    CountDownTimer timer;

    // sounds
    //static MediaPlayer playSound;
    MediaPlayer soundMove;
    MediaPlayer soundFail;
    //MediaPlayer playMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // initialize media player
        // sound effects
        soundMove = MediaPlayer.create(this, R.raw.move);
        soundFail = MediaPlayer.create(this, R.raw.fail);

        // initialise the turn
        if (getIntent().getIntExtra("PLAYER_COLOR", -1) == 0)
            turn = true;
        else turn = false;

        // get buttons
        btPause = (ImageButton)findViewById(R.id.pause);
        btBack = (ImageButton)findViewById(R.id.takeback);
        btPause.setOnClickListener(this);
        btBack.setOnClickListener(this);

        // get  TextViews
        PlayerOneName = (TextView)findViewById(R.id.player1Name);
        PlayerTwoName = (TextView)findViewById(R.id.player2Name);
        timerOne = (TextView)findViewById(R.id.timerOne);
        timerTwo = (TextView)findViewById(R.id.timerTwo);
        // set Players' names
        PlayerOneName.setText(getIntent().getStringExtra("PLAYER_1_NAME"));
        PlayerTwoName.setText(getIntent().getStringExtra("PLAYER_2_NAME"));
        // set text color
        if (turn) {
            PlayerOneName.setTextColor(getResources().getColor(R.color.red));
            PlayerTwoName.setTextColor(getResources().getColor(R.color.orange));
        } else {
            PlayerOneName.setTextColor(getResources().getColor(R.color.orange));
            PlayerTwoName.setTextColor(getResources().getColor(R.color.red));
        }

        // get ImageViews for squares
        // se ID container
        squareID = new int[]{
                R.id.a1, R.id.b1, R.id.c1, R.id.d1, R.id.e1, R.id.f1, R.id.g1, R.id.h1,
                R.id.a2, R.id.b2, R.id.c2, R.id.d2, R.id.e2, R.id.f2, R.id.g2, R.id.h2,
                R.id.a3, R.id.b3, R.id.c3, R.id.d3, R.id.e3, R.id.f3, R.id.g3, R.id.h3,
                R.id.a4, R.id.b4, R.id.c4, R.id.d4, R.id.e4, R.id.f4, R.id.g4, R.id.h4,
                R.id.a5, R.id.b5, R.id.c5, R.id.d5, R.id.e5, R.id.f5, R.id.g5, R.id.h5,
                R.id.a6, R.id.b6, R.id.c6, R.id.d6, R.id.e6, R.id.f6, R.id.g6, R.id.h6,
                R.id.a7, R.id.b7, R.id.c7, R.id.d7, R.id.e7, R.id.f7, R.id.g7, R.id.h7,
                R.id.a8, R.id.b8, R.id.c8, R.id.d8, R.id.e8, R.id.f8, R.id.g8, R.id.h8 };
        // set containers for piece drawables
        wPieceDraw = new int[] {
                R.drawable.w_pawn,
                R.drawable.w_knight,
                R.drawable.w_bishop,
                R.drawable.w_rook,
                R.drawable.w_queen,
                R.drawable.w_king};
        bPieceDraw = new int[] {
                R.drawable.b_pawn,
                R.drawable.b_knight,
                R.drawable.b_bishop,
                R.drawable.b_rook,
                R.drawable.b_queen,
                R.drawable.b_king};
        // initialise the board
        board = new Board(turn);
        // get difficulty level and AI
        // set -1 for two players game
        difficultyLevel = getIntent().getIntExtra("DIFFICULTY_LEVEL", -1);
        switch (difficultyLevel) {
            case 0: // AI easy level
                ai = new AI(board, 1);
                break;
            case 1: // AI easy level
                ai = new AI(board, 2);
                break;
            case 2: // AI easy level
                ai = new AI(board, 4);
                break;
            default: // two-player game: no AI
                break;
        }
        // connect model and GUI
        sqImages = new ImageView[64];
        for (int i = 0; i < 64; i++) {
            sqImages[i] = (ImageView)findViewById(squareID[i]);
            sqImages[i].setTag(i); // visualisation feedback
            sqImages[i].setOnLongClickListener(this);
            sqImages[i].setOnDragListener(this);
        }
        // get captured slots
        sizeOne = 0;
        capturedByOne = new ImageView[] {
                (ImageView)findViewById(R.id.captOne_1),
                (ImageView)findViewById(R.id.captOne_2),
                (ImageView)findViewById(R.id.captOne_3),
                (ImageView)findViewById(R.id.captOne_4),
                (ImageView)findViewById(R.id.captOne_5),
                (ImageView)findViewById(R.id.captOne_6),
                (ImageView)findViewById(R.id.captOne_7),
                (ImageView)findViewById(R.id.captOne_8),
                (ImageView)findViewById(R.id.captOne_9),
                (ImageView)findViewById(R.id.captOne_10),
                (ImageView)findViewById(R.id.captOne_11),
                (ImageView)findViewById(R.id.captOne_12),
                (ImageView)findViewById(R.id.captOne_13),
                (ImageView)findViewById(R.id.captOne_14),
                (ImageView)findViewById(R.id.captOne_15)};
        sizeTwo = 0;
        capturedByTwo = new ImageView[] {
                (ImageView)findViewById(R.id.captTwo_1),
                (ImageView)findViewById(R.id.captTwo_2),
                (ImageView)findViewById(R.id.captTwo_3),
                (ImageView)findViewById(R.id.captTwo_4),
                (ImageView)findViewById(R.id.captTwo_5),
                (ImageView)findViewById(R.id.captTwo_6),
                (ImageView)findViewById(R.id.captTwo_7),
                (ImageView)findViewById(R.id.captTwo_8),
                (ImageView)findViewById(R.id.captTwo_9),
                (ImageView)findViewById(R.id.captTwo_10),
                (ImageView)findViewById(R.id.captTwo_11),
                (ImageView)findViewById(R.id.captTwo_12),
                (ImageView)findViewById(R.id.captTwo_13),
                (ImageView)findViewById(R.id.captTwo_14),
                (ImageView)findViewById(R.id.captTwo_15)};

        // set up timer
        if (difficultyLevel < 0) { // two-player game
            timer = new CountDownTimer(60000, 1000) {
                //timer = new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    String timerText = String.format("%02d", millisUntilFinished / 60000) + ":" +
                            String.format("%02d", (millisUntilFinished / 1000) % 60);
                    if (turn) timerOne.setText(timerText);
                    else timerTwo.setText(timerText);
                }

                public void onFinish() {
                    nextTurn();
                }
            };
        }
        else { // one-player game
            timer = new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) { }

                public void onFinish() {
                    // computer turn
                    Move aiMove = ai.getMove(turn);
                    board.makeMove(aiMove);
                    if (aiMove.captured != null) drawCapture(aiMove);
                    updateBoardView();
                    // play sound
                    soundMove.start();
                    // change turn
                    turn = !turn;
                    CheckMate();
                    PlayerOneName.setTextColor(getResources().getColor(R.color.red));
                    PlayerTwoName.setTextColor(getResources().getColor(R.color.orange));
                }
            };
            // computer plays white
            if (!turn) {
                turn = !turn;
                timer.start();
            }
        }
        // draw the board
        updateBoardView();
        // adjust two-players window
        if (difficultyLevel < 0) {
            // hide takeBack button for two players
            btBack.setVisibility(View.INVISIBLE);
            // start timer
            turn = !turn;
            nextTurn();
        }
        // adjust one-player window
        else {
            timerOne.setVisibility(View.INVISIBLE);
            timerTwo.setVisibility(View.INVISIBLE);
        }
    }

    private void updateBoardView() {
        for (int i = 0; i < 64; i++){
            // set image
            if (board.squares[i] == null) sqImages[i].setImageResource(0);
            else if (board.isWhite(i))sqImages[i].setImageResource(wPieceDraw[board.getType(i)]);
            else sqImages[i].setImageResource(bPieceDraw[board.getType(i)]);
            // set background
            if ((i/8) % 2 == (i%8)%2) sqImages[i].setBackground(getDrawable(R.drawable.b_cell));
            else sqImages[i].setBackground(getDrawable(R.drawable.w_cell));
        }
    }

    private void CheckMate() {
        // stale mate
        if (board.isStaleMate(turn)) {
            // create and show a draw toast
            LayoutInflater myInflater = LayoutInflater.from(this);
            View view = myInflater.inflate(R.layout.game_over_draw, null);
            Toast draw = new Toast(getApplicationContext());
            draw.setView(view);
            draw.setGravity(Gravity.CENTER, 0, 0);
            draw.setDuration(Toast.LENGTH_LONG);
            draw.show();
        }
        else if (board.isCheckMate(turn)) {
            // create and show a Check toast
            LayoutInflater myInflater = LayoutInflater.from(this);
            View view;
            if (turn) { // black wins
                view = myInflater.inflate(R.layout.game_over_black, null);
            }
            else { // white wins
                view = myInflater.inflate(R.layout.game_over_white, null);
            }
            Toast gameover = new Toast(getApplicationContext());
            gameover.setView(view);
            gameover.setGravity(Gravity.CENTER, 0, 0);
            gameover.setDuration(Toast.LENGTH_LONG);
            gameover.show();
            gameOver();
        }
        else if (board.isCheck(turn)) {
            // create and show a Check toast
            LayoutInflater myInflater = LayoutInflater.from(this);
            View view = myInflater.inflate(R.layout.check_toast, null);
            Toast check = new Toast(getApplicationContext());
            check.setView(view);
            check.setGravity(Gravity.CENTER, 0, 0);
            check.setDuration(Toast.LENGTH_LONG);
            check.show();
        }

    }

    private void gameOver() {
        // play sound
        MediaPlayer soundGameOver = MediaPlayer.create(this, R.raw.gameover);
        soundGameOver.start();
        // delay exit
        timer = new CountDownTimer(1500, 1500) {

            @Override
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                // exit to main menu
                startActivity(new Intent(GameActivity.this, MainActivity.class));
            }
        };
        timer.start();
    }

    private void drawValidMoves(int position) {
        // draw borders for the valid moves
        for (int i : validSquares(position)) {
            if ((i / 8) % 2 == (i % 8) % 2) {
                sqImages[i].setBackground(getDrawable(R.drawable.b_border_valid));
            } else sqImages[i].setBackground(getDrawable(R.drawable.w_border_valid));
        }
    }

    private ArrayList<Integer> validSquares(int position) {
        // find valid squares for move piece at position
        ArrayList<Integer> valid = new ArrayList<Integer>();
        ArrayList<Move> allMoves = board.getAllMoves(turn == board.isWhiteDown);
        for (Move m : board.getMoves(position)) {
            boolean contains = false;
            for (Move all : allMoves) {
                if (m.from == all.from && m.to == all.to) {
                    contains = true;
                    break;
                }
            }
            if (contains) valid.add(m.to);
        }
        return valid;
    }

    private void nextTurn() {
        // switch turn
        turn = !turn;
        // if check
        CheckMate();
        // two-player game
        if (difficultyLevel < 0) {
            // switch timer text
            if (turn) {
                timerOne.setTextColor(getResources().getColor(R.color.red));
                PlayerOneName.setTextColor(getResources().getColor(R.color.red));
                timerTwo.setTextColor(getResources().getColor(R.color.orange));
                PlayerTwoName.setTextColor(getResources().getColor(R.color.orange));
            } else {
                timerOne.setTextColor(getResources().getColor(R.color.orange));
                PlayerOneName.setTextColor(getResources().getColor(R.color.orange));
                timerTwo.setTextColor(getResources().getColor(R.color.red));
                PlayerTwoName.setTextColor(getResources().getColor(R.color.red));
            }
        }
        else {
            PlayerOneName.setTextColor(getResources().getColor(R.color.orange));
            PlayerTwoName.setTextColor(getResources().getColor(R.color.red));
        }
        // one-player game
        timer.cancel();
        timer.start();
    }

    private void drawCapture(Move m) {
        int i = 0;
        if (turn) {
            if (board.isWhiteDown)
                capturedByOne[sizeOne].setImageResource(bPieceDraw[m.captured.type]);
            else
                capturedByOne[sizeOne].setImageResource(wPieceDraw[m.captured.type]);
            sizeOne++;
        }
        else {
            if (board.isWhiteDown)
                capturedByTwo[sizeTwo].setImageResource(wPieceDraw[m.captured.type]);
            else
                capturedByTwo[sizeTwo].setImageResource(bPieceDraw[m.captured.type]);
            sizeTwo++;
        }
    }

    public void onClick(View v) {
        if (v == btPause) {
            // create PopupMenu
            PopupMenu pauseMenu = new PopupMenu(this, v);
            pauseMenu.setOnMenuItemClickListener(new onMenuListener());
            pauseMenu.getMenuInflater().inflate(R.menu.pause, pauseMenu.getMenu());
            // show menu
            pauseMenu.show();
        }
        else if (v == btBack) {
            // check move history availability
            if (board.moveList.size() >= 2) {
                // play back twice: computer's move back first
                // check if computer captured a piece
                if (board.moveList.get(board.moveList.size() - 1).captured != null) {
                    sizeTwo--;
                    capturedByTwo[sizeTwo].setImageResource(0);
                }
                board.takeBack();
                updateBoardView();
                // player's move back
                // check if player captured a piece
                if (board.moveList.get(board.moveList.size() - 1).captured != null) {
                    sizeOne--;
                    capturedByOne[sizeOne].setImageResource(0);
                }
                board.takeBack();
                updateBoardView();
            }
            else { // no move back available
                Toast.makeText(getApplicationContext(),
                    "No move back available",
                    Toast.LENGTH_LONG).show();

            }
        }
    }

    public boolean onLongClick(View v) {
        // get v's position
        int position = ((Integer)v.getTag());
        // check v's piece color is in turn now
        if (board.squares[position] != null &&
                board.isWhite(position) == (turn == board.isWhiteDown)) {
            //isMoveDone = false;
            // build drag shadow
            DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
            // start drag and remove piece from board
            v.startDrag(null, shadowBuilder, v, 0);
            ((ImageView) v).setImageResource(0);
            drawValidMoves(position);
            return true;
        }
        else return false;
    }

    public boolean onDrag(View v, DragEvent e) {
        int fromPos = (Integer)((ImageView) e.getLocalState()).getTag();
        int toPos = (Integer) v.getTag();
        switch (e.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                // set square frame
                if(validSquares(fromPos).contains(toPos)) {
                    if ((toPos / 8) % 2 == (toPos % 8) % 2)
                        v.setBackground(getDrawable(R.drawable.b_border));
                    else v.setBackground(getDrawable(R.drawable.w_border));
                }
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                if(validSquares(fromPos).contains(toPos)) {
                    if ((toPos/8) % 2 == (toPos%8)%2)
                        v.setBackground(getDrawable(R.drawable.b_border_valid));
                    else v.setBackground(getDrawable(R.drawable.w_border_valid));
                }
                break;
            case DragEvent.ACTION_DROP:
                int i = 0;
                // check if droped on vaalid square
                if(validSquares(fromPos).contains(toPos)) {
                    // move piece
                    // get move
                    for (Move m : board.getMoves(fromPos)) {
                        if (m.to == toPos) {
                            board.makeMove(m);
                            // check for capture
                            if (m.captured != null) drawCapture(m);
                            break;
                        }
                    }
                    updateBoardView();
                    // play sound
                    soundMove.start();
                    // switch turn
                    nextTurn();
                }
                else soundFail.start();
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                updateBoardView();
                break;
            default:
                break;
        }
        return true;
    }

    private final class onMenuListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item){
            switch (item.getItemId()) {
                case R.id.resume:
                    return true;
                case R.id.restart:
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    return true;
                case R.id.quit:
                    startActivity(new Intent(GameActivity.this, MainActivity.class));
                    return true;
                default:
                    return false;
            }
        }
    }
}
