package com.example.user.stchess;

public class Move {
    Piece piece, captured;
    int from, to;
    byte flag;

    Move(Piece p, int f, int t) {
        piece = p; // piece moved
        from = f; // piece origin
        to = t; // piece destination
        captured = null; // captured piece
        flag = 0;
    }

    Move(Piece p, int f, int t, Piece c) {
        piece = p; // piece moved
        from = f; // piece origin
        to = t; // piece destination
        captured = c; // captured piece
        flag = 0;
    }

    Move(Piece p, int f, int t, byte F) {
        piece = p; // piece moved
        from = f; // piece origin
        to = t; // piece destination
        captured = null; // captured piece
        flag = F;
    }

    Move(Piece p, int f, int t, Piece c, byte F) {
        piece = p; // piece moved
        from = f; // piece origin
        to = t; // piece destination
        captured = c; // captured piece
        flag = F;
    }
}
