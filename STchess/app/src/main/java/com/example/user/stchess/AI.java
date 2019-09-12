package com.example.user.stchess;

import java.util.ArrayList;

public class AI {
    // piece values
    static final int[] VALUES = new int[]{1, 3, 4, 7, 10, 100};

    Board board;
    int searchDepth;

    public AI(Board b, int depth) {
        // get the board
        board = b;
        // set the depth
        searchDepth = depth; // easy
    }

    private int evaluateBoard() {
        int value = 0;
        for (int i = 0; i < 64; i++) {
            if (board.squares[i] != null) {
                if (board.isWhite(i)) value += VALUES[board.getType(i)];
                else value -= VALUES[board.getType(i)];
            }
        }
        return value;
    }

    private int evaluateMove(boolean white, int depth) {
        // search for the best move value
        // current state
        if (depth == 0) {
            return evaluateBoard();
        }
        // deep cases
        else {
            int v; // move value
            if (white) v = -9999; // maximise for white
            else v = 9999; // minimise for black
            // loop all valid moves
            for (Move m : board.getAllMoves(white)) {
                // evaluate the move
                board.makeMove(m);
                int vi = evaluateMove(!white, depth-1);
                board.takeBack();
                // select the best final value
                if ((white && vi > v) || (!white && vi < v)) v = vi;
            }
            return v;
        }
    }

    private ArrayList<Move> getBestMoves(boolean white, int depth) {
        // the best move value
        int v;// - -9999;
        if (white) v = -9999;
        else v = 9999;
        // define list of the best moves
        ArrayList<Move> moves = new ArrayList<Move>();
        // loop all possible moves
        for (Move m : board.getAllMoves(white)) {
            // evaluate the move
            board.makeMove(m);
            int vi = evaluateMove(!white, depth-1);
            board.takeBack();
            // select the best final value
            if ((white && vi > v) || (!white && vi < v)) {
                v = vi;
                // store the move
                moves = new ArrayList<Move>();
                moves.add(m);
            }
                else if (vi == v) moves.add(m); // store the move
        }
        return moves;
    }


    public Move getMove(boolean white) {
        // get the moves with the best value
        ArrayList<Move> m = getBestMoves(white, searchDepth);
        // randomly return one of the best moves
        return m.get((int)(Math.random() * m.size()));
    }
}

