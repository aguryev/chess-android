package com.example.user.stchess;

public class Piece {
    // piece type
    byte type;
    // piece color
    // true - white
    // false - blacl
    boolean isWhite;
    // specific feature
    byte flag;

    public Piece(byte t, boolean w) {
        type = t;
        isWhite = w;
        flag = 0; // default
    }

    public Piece(byte t, boolean w, byte f) {
        type = t;
        isWhite = w;
        flag = f;
    }

    public Piece copy(){
        // creates a copy of the Piece object
        return new Piece(type, isWhite, flag);
    }

    public void setType(byte t) {
        // sets Piece.type to t
        type = t;
    }

    public void setFlag(byte f) {
        // sets Piece.flag to f
        flag = f;
    }
}
