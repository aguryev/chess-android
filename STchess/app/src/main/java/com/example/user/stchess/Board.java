package com.example.user.stchess;

import java.util.ArrayList;

public class Board {
    // chessboard object
    // piece types:
    static final byte TYPE_PAWN = 0;
    static final byte TYPE_KNIGHT = 1;
    static final byte TYPE_BISHOP = 2;
    static final byte TYPE_ROOK = 3;
    static final byte TYPE_QUEEN = 4;
    static final byte TYPE_KING = 5;
    // flags
    static final byte FLAG_CASTLING = 1;
    static final byte FLAG_PROMOTION = 2;

    Piece[] squares; // board squares
    boolean isWhiteDown; // white side of the board: true - bottom, false - top
    ArrayList<Move> moveList; // move history

    public Board(boolean white) {
        // creates Board object with initial placement of the pieces
        // white is true if white pieces occupy board bottom

        // create move track list
        moveList = new ArrayList<Move>();
        // set colours
        isWhiteDown = white;
        // set board
        squares = new Piece[64];
        // set bottom pieces
        squares[0] = new Piece(TYPE_ROOK, white, FLAG_CASTLING);
        squares[7] = new Piece(TYPE_ROOK, white, FLAG_CASTLING);
        squares[1] = new Piece(TYPE_KNIGHT, white);
        squares[6] = new Piece(TYPE_KNIGHT, white);
        squares[2] = new Piece(TYPE_BISHOP, white);
        squares[5] = new Piece(TYPE_BISHOP, white);
        // set top pieces
        squares[56] = new Piece(TYPE_ROOK, !white, FLAG_CASTLING);
        squares[63] = new Piece(TYPE_ROOK, !white, FLAG_CASTLING);
        squares[57] = new Piece(TYPE_KNIGHT, !white);
        squares[62] = new Piece(TYPE_KNIGHT, !white);
        squares[58] = new Piece(TYPE_BISHOP, !white);
        squares[61] = new Piece(TYPE_BISHOP, !white);
        // set king and queen
        if (white) {
            squares[3]  = new Piece(TYPE_QUEEN, white);
            squares[4]  = new Piece(TYPE_KING, white, FLAG_CASTLING);
            squares[59] = new Piece(TYPE_QUEEN, !white);
            squares[60] = new Piece(TYPE_KING, !white, FLAG_CASTLING);
        }
        else {
            squares[3]  = new Piece(TYPE_KING, white, FLAG_CASTLING);
            squares[4]  = new Piece(TYPE_QUEEN, white);
            squares[59] = new Piece(TYPE_KING, !white, FLAG_CASTLING);
            squares[60] = new Piece(TYPE_QUEEN, !white);
        }
        // set pawns
        for (int i = 0; i < 8; i++) {
            // bottom pawns on 8 - 15
            squares[8 + i]  = new Piece(TYPE_PAWN, white);
            // top pawns on 48 - 55
            squares[48 + i] = new Piece(TYPE_PAWN, !white);
        }
        // set empty squares at 16 - 47
        for (int i = 16; i < 48; i++) squares[i] = null;
    }

    public byte getType(int position) {
        // gets type of piece on position square
        if (squares[position] == null) return -1;
        else return squares[position].type;
    }

    public boolean isWhite(int position) {
        // gets colour of piece on position square
        return squares[position].isWhite;
    }

    public byte getFlag(int position) {
        // gets flag of piece on position square
        if (squares[position] == null) return 0;
        else return squares[position].flag;
    }

    public void movePiece(int from, int to) {
        // moves a piece from square[from] to square[to]
        // create copy of the piece on move a piece to the destination square
        squares[to] = squares[from].copy();
        // remove flag
        squares[to].setFlag((byte)0);
        // clear the origin square
        squares[from] = null;
    }

    public void makeMove(Move m) {
        // add move to track list
        moveList.add(m);
        // move piece
        movePiece(m.from, m.to);
        // check promotion flag
        if (m.flag == FLAG_PROMOTION) squares[m.to].setType(TYPE_QUEEN);
            // check castling flag
        else if (m.flag == FLAG_CASTLING) {
            //Move rookMove;
            // bottom-left: move rook from square[0] to right adjacent square to king
            if (m.to < 3) movePiece(0, m.to + 1);
            // bottom-right: move rook from square[7] to left adjacent square to king
            else if (m.to < 7) movePiece(7, m.to - 1);
            // top-left: move rook from square[56] to right adjacent square to king
            else if (m.to < 59) movePiece(56, m.to + 1);
            // top-right: move rook from square[63] to left adjacent square to king
            else movePiece(63, m.to - 1);
        }
    }

    public void takeBack() {
        // cancels the last move done
        // pop the last move from the history
        Move lastMove = moveList.remove(moveList.size() - 1);
        // move the piece back
        squares[lastMove.from] = lastMove.piece.copy();
        // check if a piece was captured
        if (lastMove.captured == null) squares[lastMove.to] = null;
        else squares[lastMove.to] = lastMove.captured.copy();
        // castling case: return rook
        if (lastMove.flag == FLAG_CASTLING) {
            // left rook
            if (lastMove.to%8 < 3) {
                squares[8*(lastMove.to/8)] =
                        new Piece(TYPE_ROOK, lastMove.piece.isWhite, FLAG_CASTLING);
                squares[lastMove.to + 1] = null;
            }
            // right rook
            else if (lastMove.to%8 > 4) {
                squares[8*(lastMove.to/8) + 7] =
                        new Piece(TYPE_ROOK, lastMove.piece.isWhite, FLAG_CASTLING);
                squares[lastMove.to - 1] = null;
            }
        }
    }

    public ArrayList<Move> getMoves(int position) {
        ArrayList<Move> moves = new ArrayList<Move>();
        // PAWN
        if (getType(position) == TYPE_PAWN) {
            // check pawn's side
            int side = (isWhite(position) == isWhiteDown)?8:-8;
            // check move cases
            // base case
            if (squares[position + side] == null) {
                // check promotion
                if (((position + side) / 8 == 0) || ((position + side) / 8 == 7))
                    moves.add(new Move(squares[position].copy(),
                            position, position + side, FLAG_PROMOTION));
                else moves.add(new Move(squares[position].copy(), position, position + side));
                // the very first move
                if (((side > 0 && position < 16) || // initial bottom position
                        (side < 0 && position > 47)) && // initial top position
                                squares[position + 2 * side] == null)
                    moves.add(new Move(squares[position].copy(), position, position + 2 * side));
            }
            // check attack cases
            for (int i = -1; i <= 1; i += 2) { // diagonally left (-1) and right (1)
                if ((position + side + i)/8 == (position + side)/8 && // the next row
                    squares[position + side + i] != null && // the diagonally square is occupaied
                    isWhite(position + side + i) != isWhite(position)) { // by opponent's piece
                    // check promotion
                    if (((position + side) / 8 == 0) || ((position + side) / 8 == 7))
                        moves.add(new Move(squares[position].copy(),
                            position, position + side + i,
                            squares[position + side + i].copy(), FLAG_PROMOTION));
                    else moves.add(new Move(squares[position].copy(),
                        position, position + side + i, squares[position + side + i].copy()));
                }
            }
        }
        // BISHOP
        else if(getType(position) == TYPE_BISHOP) {
                // loop directions
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        // select diagonal directions
                        if (i != 0 && j != 0) {
                            // the first square within the direction
                            int r = position/8 + i;
                            int c = position%8 + j;
                            // get empty squares in board span
                            while ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) &&
                                    squares[8*r + c] == null) {
                                moves.add(new Move(squares[position].copy(), position, 8*r + c));
                                // move in the direction alternately
                                r += i;
                                c += j;
                            }
                            // get attack squares
                            if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) &&
                                    (isWhite(8*r + c) != isWhite(position))) // opponents
                                moves.add(new Move(squares[position].copy(),
                                        position, 8*r + c, squares[8*r + c].copy()));
                        }
                    }
                }
            }

        // KNIGHT
        else if(getType(position) == TYPE_KNIGHT) {
            // loop directions
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    // select knight's valid moves
                    if (i != 0 && j != 0 && Math.abs(i) != Math.abs(j)) {
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // check square is within the board
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8))
                            // empty squares
                            if (squares[8*r + c] == null)
                                moves.add(new Move(squares[position].copy(), position, 8*r + c));
                                // opponent's squares
                            else if (isWhite(8*r + c) != isWhite(position))
                                moves.add(new Move(squares[position].copy(),
                                        position, 8*r + c, squares[8*r + c].copy()));
                    }
                }
            }
        }
        // ROOK
        else if(getType(position) == TYPE_ROOK) {
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select vertical and horizintal directions
                    if (Math.abs(i) != Math.abs(j)) {
                        // adjacent square
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get empty squares in board span
                        while ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) &&
                                squares[8*r + c] == null) {
                            moves.add(new Move(squares[position].copy(), position, 8*r + c));
                            // move in the direction alternately
                            r += i;
                            c += j;
                        }
                        // get  attack squares
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) &&
                                (isWhite(8*r + c) != isWhite(position))) // opponent's
                            moves.add(new Move(squares[position].copy(),
                                    position, 8*r + c, squares[8*r + c].copy()));
                    }
                }
            }
        }
        // QUEEN
        else if(getType(position) == TYPE_QUEEN) {
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select any direction
                    if (i != 0 || j != 0) {
                        // the next square
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get empty squares in board span
                        while ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) &&
                                squares[8*r + c] == null) {
                            moves.add(new Move(squares[position].copy(), position, 8*r + c));
                            // move in the direction alternately
                            r += i;
                            c += j;
                        }
                        // get attack squares
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) &&
                                (isWhite(8*r + c) != isWhite(position))) // opponent's
                            moves.add(new Move(squares[position].copy(),
                                    position, 8*r + c, squares[8*r + c].copy()));
                    }
                }
            }
        }
        // KING
        else if(getType(position) == TYPE_KING) {
            // list of squares attacked by opponent
            ArrayList<Integer> attack = getAllAttacks(!isWhite(position));
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select any direction
                    if (i != 0 || j != 0) {
                        // get adjoining square
                        int r = position/8 + i;
                        int c = position%8 + j;
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) && // board span
                                !attack.contains(8*r + c)) // not under attack
                            // get not occupied square
                            if (squares[8*r + c] == null)
                                moves.add(new Move(squares[position].copy(), position, 8*r + c));
                            // get enemy squares
                            else if (isWhite(8*r + c) != isWhite(position))
                                moves.add(new Move(squares[position].copy(),
                                        position, 8*r + c, squares[8*r + c].copy()));
                    }
                }
            }
            // castling cases
            // castling left
            if (getFlag(position) == FLAG_CASTLING && // king
                    getFlag(8*(position/8)) == FLAG_CASTLING) { // rook
                // check the path is empty and safe
                int i = 8*(position/8);
                while (++i != position) {
                    if (squares[i] != null) break;
                    if (position - i < 3 && attack.contains(i)) break;
                }
                if (i == position) // castling valid
                    moves.add(new Move(squares[position].copy(),
                            position, position - 2, FLAG_CASTLING));
            }
            // castling right
            if (getFlag(position) == FLAG_CASTLING && // king
                    getFlag(8*(position/8) + 7) == FLAG_CASTLING) { // rook
                // check the path is empty and safe
                int i = 8*(position/8) + 7;
                while (--i != position) {
                    if (squares[i] != null) break;
                    if (i - position < 3 && attack.contains(i)) break;
                }
                if (i == position) // castling valid
                    moves.add(new Move(squares[position].copy(),
                            position, position + 2, FLAG_CASTLING));
            }
        }
        return moves;
    }

    public ArrayList<Integer> getAttack(int position) {
        ArrayList<Integer> attack = new ArrayList<Integer>();
        // PAWN
        if (getType(position) == TYPE_PAWN) {
            // check pawn's side
            int side = (isWhite(position) == isWhiteDown)?8:-8;
            // check attack cases
            if ((position + side - 1) >=0 && (position + side - 1)/8 == (position + side)/8)
                attack.add(position + side - 1);
            if ((position + side + 1)/8 == (position + side)/8)
                attack.add(position + side + 1);
        }
        // ROOK
        else if(getType(position) == TYPE_ROOK) {
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select vertical and horizintal directions
                    if (Math.abs(i) != Math.abs(j)) {
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get empty squares in valid span
                        while ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) && squares[8*r + c] == null) {
                            attack.add(8*r + c);
                            r += i;
                            c += j;
                        }
                        // get occupied squares
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8))
                            attack.add(8*r + c);
                    }
                }
            }
        }
        // KNIGHT
        else if(getType(position) == TYPE_KNIGHT) {
            // loop directions
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    // select knight's valid moves
                    if (i != 0 && j != 0 && Math.abs(i) != Math.abs(j)) {
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get squares in span
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8))
                            attack.add(8*r + c);
                    }
                }
            }
        }
        // BISHOP
        else if(getType(position) == TYPE_BISHOP) {
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select diagonal directions
                    if (i != 0 && j != 0) {
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get empty squares in span
                        while ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) && squares[8*r + c] == null) {//(getType(8*r + c) == TYPE_EMPTY)) {
                            attack.add(8*r + c);
                            r += i;
                            c += j;
                        }
                        // get occupied squares
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8))
                            attack.add(8*r + c);
                    }
                }
            }
        }
        // QUEEN
        else if(getType(position) == TYPE_QUEEN) {
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select any direction
                    if (i != 0 || j != 0) {
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get empty squares in span
                        while ((r >= 0) && (r < 8) && (c >= 0) && (c < 8) && squares[8*r + c] == null) {//(getType(8*r + c) == TYPE_EMPTY)) {
                            attack.add(8*r + c);
                            r += i;
                            c += j;
                        }
                        // get occupied squares
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8))
                            attack.add(8*r + c);
                    }
                }
            }
        }
        // KING
        else if(getType(position) == TYPE_KING) {
            // loop directions
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // select any direction
                    if (i != 0 || j != 0) {
                        int r = position/8 + i;
                        int c = position%8 + j;
                        // get empty and friendly squares
                        if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8))
                            // get squares in span
                            attack.add(8*r + c);
                    }
                }
            }
        }

        return attack;
    }

    public boolean isCheck(boolean white) {
        // get king location
        int posKing = -1;
        for (int i = 0; i < 64; i++) {
            if (getType(i) == TYPE_KING && isWhite(i) == white) {
                posKing = i;
                break;
            }
        }
        // get enimy moves
        if (getAllAttacks(!white).contains(posKing)) return true;
        else return false;
    }

    public boolean isCheckMate(boolean white) {
        // check available moves and check
        if (getAllMoves(white).size() == 0 && isCheck(white)) return true;
        else return false;
    }

    public boolean isStaleMate(boolean white) {
        // check available moves and no check
        if (getAllMoves(white).size() == 0 && !isCheck(white)) return true;
        else return false;
    }

    public ArrayList<Move> getAllMoves(boolean white) {
        ArrayList<Move> moves = new ArrayList<Move>();
        if (isCheck(white)) {
            // look for moves unChecked moves
            // scan the board for white pieces
            for (int i = 0; i < 64; i++)
                if (squares[i] != null && isWhite(i) == white) {
                    for (Move m : getMoves(i)) {
                        makeMove(m);
                        if (!isCheck(white)) moves.add(m);
                        takeBack();
                    }
                }
        }
        else {
            // scan the board for white pieces
            for (int i = 0; i < 64; i++)
                if (squares[i] != null && isWhite(i) == white) moves.addAll(getMoves(i));
        }
        return moves;
    }

    public ArrayList<Integer> getAllAttacks(boolean white) {
        ArrayList<Integer> attack = new ArrayList<Integer>();
        // scan the board for white pieces
        for (int i = 0; i < 64; i++)
            if (squares[i] != null && isWhite(i) == white) attack.addAll(getAttack(i));
        return attack;
    }
}