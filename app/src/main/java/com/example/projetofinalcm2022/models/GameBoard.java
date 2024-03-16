package com.example.projetofinalcm2022.models;

import android.widget.ImageView;

import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.utils.Utils;

import java.io.Serializable;

public class GameBoard implements Serializable {
    
    public class CoordXY {
        int x, y, columns;
        CoordXY(int x, int y) {this.x = x; this.y = y;}
        CoordXY(int pos1D) {
            columns = Utils.BOARD_COLUMNS;
            x = pos1D / columns;
            y = pos1D % columns;
        }
    }

    private int board[][] = new int[Utils.BOARD_COLUMNS][Utils.BOARD_COLUMNS];

    public GameBoard() {
        reset();
    }
    public GameBoard(int arr[][]) {
        int columns = 8;
        for (int i = 0; i < columns; i++)
            System.arraycopy(arr[i], 0, board[i], 0, columns);
    }

    public boolean insertBoat1(Integer pos) {
        CoordXY c = convert1Dto2D(pos);
        if (checkPositionForBoat1(c)) {
            board[c.x][c.y] = 2;
            return true;
        }
        else
            return false;
    }
    public boolean insertBoat2(int pos, boolean vertical) {
        CoordXY c = convert1Dto2D(pos);
        if (checkPositionForBoat2(c, vertical)) {
            if (vertical) {
                if (checkIfPositionIsValid(c.x + 1, c.y, Utils.BOARD_COLUMNS)) {
                    board[c.x][c.y] = 3;
                    board[c.x + 1][c.y] = 3;
                    return true;
                }
                return false;
            }
            else if (checkIfPositionIsValid(c.x, c.y + 1, Utils.BOARD_COLUMNS)) {
                board[c.x][c.y] = 3;
                board[c.x][c.y + 1] = 3;
                return true;
            }
        }
        return false;
    }
    public boolean insertBoat3(int pos, boolean vertical) {
        CoordXY c = convert1Dto2D(pos);
        if (checkPositionForBoat3(c, vertical)) {
            if (vertical) {
                if (checkIfPositionIsValid(c.x + 1, c.y, Utils.BOARD_COLUMNS) && checkIfPositionIsValid(c.x + 2, c.y, Utils.BOARD_COLUMNS)) {
                    board[c.x][c.y] = 4;
                    board[c.x + 1][c.y] = 4;
                    board[c.x + 2][c.y] = 4;
                    return true;
                }
                return false;
            }
            else if (checkIfPositionIsValid(c.x, c.y + 1, Utils.BOARD_COLUMNS) && checkIfPositionIsValid(c.x, c.y + 2, Utils.BOARD_COLUMNS)) {
                board[c.x][c.y] = 4;
                board[c.x][c.y + 1] = 4;
                board[c.x][c.y + 2] = 4;
                return true;
            }
            return false;
        }
        else
            return false;
    }
    public boolean insertBoat5(int pos, boolean vertical, boolean alternativePosition) {
        CoordXY c = convert1Dto2D(pos);
        if (checkPositionForBoat5(c, vertical, alternativePosition)) {
            int columns = Utils.BOARD_COLUMNS;
            if (vertical && !alternativePosition) {
                if (checkIfPositionIsValid(c.x + 1, c.y, columns)
                        && checkIfPositionIsValid(c.x + 2, c.y, columns)
                        && checkIfPositionIsValid(c.x, c.y - 1, columns)
                        && checkIfPositionIsValid(c.x, c.y + 1, columns)) {
                    board[c.x][c.y] = 6;
                    board[c.x + 1][c.y] = 6;
                    board[c.x + 2][c.y] = 6;
                    board[c.x][c.y - 1] = 6;
                    board[c.x][c.y + 1] = 6;
                    return true;
                }
                return false;
            } else if (!vertical && !alternativePosition) {
                if (checkIfPositionIsValid(c.x, c.y + 1, columns)
                        && checkIfPositionIsValid(c.x, c.y + 2, columns)
                        && checkIfPositionIsValid(c.x - 1, c.y, columns)
                        && checkIfPositionIsValid(c.x + 1, c.y, columns)) {
                    board[c.x][c.y] = 6;
                    board[c.x][c.y + 1] = 6;
                    board[c.x][c.y + 2] = 6;
                    board[c.x - 1][c.y] = 6;
                    board[c.x + 1][c.y] = 6;
                    return true;
                }
                return false;
            }
            else if (vertical && alternativePosition) {
                if (checkIfPositionIsValid(c.x - 1, c.y, columns)
                        && checkIfPositionIsValid(c.x - 2, c.y, columns)
                        && checkIfPositionIsValid(c.x, c.y - 1, columns)
                        && checkIfPositionIsValid(c.x, c.y + 1, columns)) {
                    board[c.x][c.y] = 6;
                    board[c.x - 1][c.y] = 6;
                    board[c.x - 2][c.y] = 6;
                    board[c.x][c.y - 1] = 6;
                    board[c.x][c.y + 1] = 6;
                    return true;
                }
                return false;
            }
            else {
                if (checkIfPositionIsValid(c.x, c.y - 1, columns)
                        && checkIfPositionIsValid(c.x, c.y - 2, columns)
                        && checkIfPositionIsValid(c.x - 1, c.y, columns)
                        && checkIfPositionIsValid(c.x + 1, c.y, columns)) {
                    board[c.x][c.y] = 6;
                    board[c.x][c.y - 1] = 6;
                    board[c.x][c.y - 2] = 6;
                    board[c.x - 1][c.y] = 6;
                    board[c.x + 1][c.y] = 6;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean removeBoat1(int pos) {
        CoordXY c = new CoordXY(pos);
        if (board[c.x][c.y] == 2) {
            board[c.x][c.y] = 1;
            return true;
        }
        return false;
    }
    public boolean removeBoat2(int pos) {

        CoordXY posXY = new CoordXY(pos);

        if (posXY == null)
            return false;

        int l = posXY.x;
        int c = posXY.y;
        int columns = posXY.columns;

        if (checkIfPositionIsValid(l, c, columns))
            if (board[l][c] != 3)
                return false;

        if (checkIfPositionIsValid(l, c - 1, columns)) {
            if (board[l][c - 1] < 0)
                return false;
            else if (board[l][c-1] == 3) {
                board[l][c] = 1;
                board[l][c-1] = 1;
                return true;
            }
        }
        if (checkIfPositionIsValid(l - 1, c, columns)) {
            if (board[l - 1][c] < 0)
                return false;
            else if (board[l - 1][c] == 3) {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                return true;
            }
        }
        if (checkIfPositionIsValid(l, c + 1, columns)) {
            if (board[l][c + 1] < 0)
                return false;
            else if (board[l][c + 1] == 3) {
                board[l][c] = 1;
                board[l][c + 1] = 1;
                return true;
            }
        }
        if (checkIfPositionIsValid(l + 1, c, columns)) {
            if (board[l + 1][c] < 0)
                return false;
            else if (board[l + 1][c] == 3) {
                board[l][c] = 1;
                board[l + 1][c] = 1;
                return true;
            }
        }
        return false;
    }
    public boolean removeBoat3(int pos) {

        CoordXY posXY = new CoordXY(pos);

        if (posXY == null)
            return false;

        int l = posXY.x;
        int c = posXY.y;
        int columns = posXY.columns;

        if (checkIfPositionIsValid(l, c, columns))
            if (board[l][c] != 4)
                return false;

        if (checkIfPositionIsValid(l,c - 1, columns)) {
            if (board[l][c - 1] == 4) {
                if (checkIfPositionIsValid(l, c - 2, columns)) {
                    if (board[l][c - 2] == 4) {
                        board[l][c] = 1;
                        board[l][c - 1] = 1;
                        board[l][c - 2] = 1;
                        return true;
                    }
                    else if (checkIfPositionIsValid(l, c + 1, columns)) {
                        if (board[l][c + 1] == 4) {
                            board[l][c] = 1;
                            board[l][c - 1] = 1;
                            board[l][c + 1] = 1;
                            return true;
                        }
                    }
                }
            }
        }
        if (checkIfPositionIsValid(l,c + 1, columns)) {
            if (board[l][c + 1] == 4) {
                if (checkIfPositionIsValid(l, c + 2, columns)) {
                    if (board[l][c + 2] == 4) {
                        board[l][c] = 1;
                        board[l][c + 1] = 1;
                        board[l][c + 2] = 1;
                        return true;
                    }
                    else if (checkIfPositionIsValid(l, c - 1, columns)) {
                        if (board[l][c - 1] == 4) {
                            board[l][c] = 1;
                            board[l][c + 1] = 1;
                            board[l][c - 1] = 1;
                            return true;
                        }
                    }
                }
            }
        }
        if (checkIfPositionIsValid(l - 1,c, columns)) {
            if (board[l - 1][c] == 4) {
                if (checkIfPositionIsValid(l - 2, c, columns)) {
                    if (board[l - 2][c] == 4) {
                        board[l][c] = 1;
                        board[l - 1][c] = 1;
                        board[l - 2][c] = 1;
                        return true;
                    }
                    else if (checkIfPositionIsValid(l + 1, c, columns)) {
                        if (board[l + 1][c] == 4) {
                            board[l][c] = 1;
                            board[l - 1][c] = 1;
                            board[l + 1][c] = 1;
                            return true;
                        }
                    }
                }
            }
        }
        if (checkIfPositionIsValid(l + 1,c, columns)) {
            if (board[l + 1][c] == 4) {
                if (checkIfPositionIsValid(l + 2, c, columns)) {
                    if (board[l + 2][c] == 4) {
                        board[l][c] = 1;
                        board[l + 1][c] = 1;
                        board[l + 2][c] = 1;
                        return true;
                    }
                    else if (checkIfPositionIsValid(l - 1, c, columns)) {
                        if (board[l - 1][c] == 4) {
                            board[l][c] = 1;
                            board[l + 1][c] = 1;
                            board[l - 1][c] = 1;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    public boolean removeBoat5(int pos) {

        CoordXY posXY = new CoordXY(pos);

        if (posXY == null)
            return false;

        int l = posXY.x;
        int c = posXY.y;
        int columns = posXY.columns;

        if (checkIfPositionIsValid(l, c, columns))
            if (board[l][c] != 6)
                return false;

        // VERTICAL NORMAL
        // CASO B
        if (checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l + 1, c, columns)
                && checkIfPositionIsValid(l + 2, c, columns))
            if (board[l][c - 1] == 6
                    && board[l][c + 1] == 6
                    && board[l + 1][c] == 6
                    && board[l + 2][c] == 6)
            {
                board[l][c] = 1;
                board[l][c - 1] = 1;
                board[l][c + 1] = 1;
                board[l + 1][c] = 1;
                board[l + 2][c] = 1;
                return true;
            }
        // CASO A
        if (checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l, c + 2, columns)
                && checkIfPositionIsValid(l + 1, c + 1, columns)
                && checkIfPositionIsValid(l + 2, c + 1, columns))
            if (board[l][c + 1] == 6
                    && board[l][c + 2] == 6
                    && board[l + 1][c + 1] == 6
                    && board[l + 2][c + 1] == 6)
            {
                board[l][c] = 1;
                board[l][c + 1] = 1;
                board[l][c + 2] = 1;
                board[l + 1][c + 1] = 1;
                board[l + 2][c + 1] = 1;
                return true;
            }
        // CASO C
        if (checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l, c - 2, columns)
                && checkIfPositionIsValid(l + 1, c - 1, columns)
                && checkIfPositionIsValid(l + 2, c - 1, columns))
            if (board[l][c - 1] == 6
                    && board[l][c - 2] == 6
                    && board[l + 1][c - 1] == 6
                    && board[l + 2][c - 1] == 6)
            {
                board[l][c] = 1;
                board[l][c - 1] = 1;
                board[l][c - 2] = 1;
                board[l + 1][c - 1] = 1;
                board[l + 2][c - 1] = 1;
                return true;
            }
        // CASO D
        if (checkIfPositionIsValid(l - 1, c + 1, columns)
                && checkIfPositionIsValid(l - 1, c - 1, columns)
                && checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l + 1, c, columns))
            if (board[l - 1][c + 1] == 6
                    && board[l - 1][c - 1] == 6
                    && board[l - 1][c] == 6
                    && board[l + 1][c] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c + 1] = 1;
                board[l - 1][c - 1] = 1;
                board[l - 1][c] = 1;
                board[l + 1][c] = 1;
                return true;
            }
        // CASO E
        if (checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l - 2, c, columns)
                && checkIfPositionIsValid(l - 2, c + 1, columns)
                && checkIfPositionIsValid(l - 2, c - 1, columns))
            if (board[l - 1][c] == 6
                    && board[l - 2][c] == 6
                    && board[l - 2][c + 1] == 6
                    && board[l - 2][c - 1] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                board[l - 2][c] = 1;
                board[l - 2][c + 1] = 1;
                board[l - 2][c - 1] = 1;
                return true;
            }


        // VERTICAL ALTERNATIVO
        // CASO B
        if (checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l - 2, c, columns))
            if (board[l][c - 1] == 6
                    && board[l][c + 1] == 6
                    && board[l - 1][c] == 6
                    && board[l - 2][c] == 6)
            {
                board[l][c] = 1;
                board[l][c - 1] = 1;
                board[l][c + 1] = 1;
                board[l - 1][c] = 1;
                board[l - 2][c] = 1;
                return true;
            }
        // CASO A
        if (checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l, c + 2, columns)
                && checkIfPositionIsValid(l - 1, c + 1, columns)
                && checkIfPositionIsValid(l - 2, c + 1, columns))
            if (board[l][c + 1] == 6
                    && board[l][c + 2] == 6
                    && board[l - 1][c + 1] == 6
                    && board[l - 2][c + 1] == 6)
            {
                board[l][c] = 1;
                board[l][c + 1] = 1;
                board[l][c + 2] = 1;
                board[l - 1][c + 1] = 1;
                board[l - 2][c + 1] = 1;
                return true;
            }
        // CASO C
        if (checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l, c - 2, columns)
                && checkIfPositionIsValid(l - 1, c - 1, columns)
                && checkIfPositionIsValid(l - 2, c - 1, columns))
            if (board[l][c - 1] == 6
                    && board[l][c - 2] == 6
                    && board[l - 1][c - 1] == 6
                    && board[l - 2][c - 1] == 6)
            {
                board[l][c] = 1;
                board[l][c - 1] = 1;
                board[l][c - 2] = 1;
                board[l - 1][c - 1] = 1;
                board[l - 2][c - 1] = 1;
                return true;
            }
        // CASO D
        if (checkIfPositionIsValid(l + 1, c + 1, columns)
                && checkIfPositionIsValid(l + 1, c - 1, columns)
                && checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l + 1, c, columns))
            if (board[l + 1][c + 1] == 6
                    && board[l + 1][c - 1] == 6
                    && board[l - 1][c] == 6
                    && board[l + 1][c] == 6)
            {
                board[l][c] = 1;
                board[l + 1][c + 1] = 1;
                board[l + 1][c - 1] = 1;
                board[l - 1][c] = 1;
                board[l + 1][c] = 1;
                return true;
            }
        // CASO E
        if (checkIfPositionIsValid(l + 1, c, columns)
                && checkIfPositionIsValid(l + 2, c, columns)
                && checkIfPositionIsValid(l + 2, c + 1, columns)
                && checkIfPositionIsValid(l + 2, c - 1, columns))
            if (board[l + 1][c] == 6
                    && board[l + 2][c] == 6
                    && board[l + 2][c + 1] == 6
                    && board[l + 2][c - 1] == 6)
            {
                board[l][c] = 1;
                board[l + 1][c] = 1;
                board[l + 2][c] = 1;
                board[l + 2][c + 1] = 1;
                board[l + 2][c - 1] = 1;
                return true;
            }




        // HORIZONTAL NORMAL
        // CASO B
        if (checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l + 1, c, columns)
                && checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l, c + 2, columns))
            if (board[l - 1][c] == 6
                    && board[l + 1][c] == 6
                    && board[l][c + 1] == 6
                    && board[l][c + 2] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                board[l + 1][c] = 1;
                board[l][c + 1] = 1;
                board[l][c + 2] = 1;
                return true;
            }
        // CASO A
        if (checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l - 2, c, columns)
                && checkIfPositionIsValid(l - 1, c + 1, columns)
                && checkIfPositionIsValid(l - 1, c + 2, columns))
            if (board[l - 1][c] == 6
                    && board[l - 2][c] == 6
                    && board[l - 1][c + 1] == 6
                    && board[l - 1][c + 2] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                board[l - 2][c] = 1;
                board[l - 1][c + 1] = 1;
                board[l - 1][c + 2] = 1;
                return true;
            }
        // CASO C
        if (checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l - 2, c, columns)
                && checkIfPositionIsValid(l - 1, c + 1, columns)
                && checkIfPositionIsValid(l - 1, c + 2, columns))
            if (board[l - 1][c] == 6
                    && board[l - 2][c] == 6
                    && board[l - 1][c + 1] == 6
                    && board[l - 1][c + 2] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                board[l - 2][c] = 1;
                board[l - 1][c + 1] = 1;
                board[l - 1][c + 2] = 1;
                return true;
            }
        // CASO D
        if (checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l - 1, c - 1, columns)
                && checkIfPositionIsValid(l + 1, c - 1, columns))
            if (board[l][c + 1] == 6
                    && board[l][c - 1] == 6
                    && board[l - 1][c - 1] == 6
                    && board[l + 1][c - 1] == 6)
            {
                board[l][c] = 1;
                board[l][c + 1] = 1;
                board[l][c - 1] = 1;
                board[l - 1][c - 1] = 1;
                board[l + 1][c - 1] = 1;
                return true;
            }
        // CASO E
        if (checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l, c - 2, columns)
                && checkIfPositionIsValid(l + 1, c - 2, columns)
                && checkIfPositionIsValid(l - 1, c - 2, columns))
            if (board[l][c - 1] == 6
                    && board[l][c - 2] == 6
                    && board[l + 1][c - 2] == 6
                    && board[l - 1][c - 2] == 6)
            {
                board[l][c] = 1;
                board[l][c - 1] = 1;
                board[l][c - 2] = 1;
                board[l + 1][c - 2] = 1;
                board[l - 1][c - 2] = 1;
                return true;
            }




        // HORIZONTAL ALTERNATIVO
        // CASO B
        if (checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l + 1, c, columns)
                && checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l, c - 2, columns))
            if (board[l - 1][c] == 6
                    && board[l + 1][c] == 6
                    && board[l][c - 1] == 6
                    && board[l][c - 2] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                board[l + 1][c] = 1;
                board[l][c - 1] = 1;
                board[l][c - 2] = 1;
                return true;
            }
        // CASO A
        if (checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l - 2, c, columns)
                && checkIfPositionIsValid(l - 1, c - 1, columns)
                && checkIfPositionIsValid(l - 1, c - 2, columns))
            if (board[l - 1][c] == 6
                    && board[l - 2][c] == 6
                    && board[l - 1][c - 1] == 6
                    && board[l - 1][c - 2] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c] = 1;
                board[l - 2][c] = 1;
                board[l - 1][c - 1] = 1;
                board[l - 1][c - 2] = 1;
                return true;
            }
        // CASO C
        if (checkIfPositionIsValid(l - 1, c - 1, columns)
                && checkIfPositionIsValid(l - 1, c - 2, columns)
                && checkIfPositionIsValid(l - 1, c, columns)
                && checkIfPositionIsValid(l - 2, c, columns))
            if (board[l - 1][c - 1] == 6
                    && board[l - 1][c - 2] == 6
                    && board[l - 1][c] == 6
                    && board[l - 2][c] == 6)
            {
                board[l][c] = 1;
                board[l - 1][c - 1] = 1;
                board[l - 1][c - 2] = 1;
                board[l - 1][c] = 1;
                board[l - 2][c] = 1;
                return true;
            }
        // CASO D
        if (checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l, c - 1, columns)
                && checkIfPositionIsValid(l - 1, c + 1, columns)
                && checkIfPositionIsValid(l + 1, c + 1, columns))
            if (board[l][c + 1] == 6
                    && board[l][c - 1] == 6
                    && board[l - 1][c + 1] == 6
                    && board[l + 1][c + 1] == 6)
            {
                board[l][c] = 1;
                board[l][c + 1] = 1;
                board[l][c - 1] = 1;
                board[l - 1][c + 1] = 1;
                board[l + 1][c + 1] = 1;
                return true;
            }
        // CASO E
        if (checkIfPositionIsValid(l, c + 1, columns)
                && checkIfPositionIsValid(l, c + 2, columns)
                && checkIfPositionIsValid(l + 1, c + 2, columns)
                && checkIfPositionIsValid(l - 1, c + 2, columns))
            if (board[l][c + 1] == 6
                    && board[l][c + 2] == 6
                    && board[l + 1][c + 2] == 6
                    && board[l - 1][c + 2] == 6)
            {
                board[l][c] = 1;
                board[l][c + 1] = 1;
                board[l][c + 2] = 1;
                board[l + 1][c + 2] = 1;
                board[l - 1][c + 2] = 1;
                return true;
            }


        return false;
    }

    private boolean checkPositionForBoat1(CoordXY posXY) {

        if (posXY == null)
            return false;

        int columns = Utils.BOARD_COLUMNS;

        int l = posXY.x;
        int c = posXY.y;

        // ESQ CIMA
        if (checkIfPositionIsValid(l - 1,c - 1, columns)) {
            if (board[l - 1][c - 1] > 1)
                return false;
        }
        // CIMA
        if (checkIfPositionIsValid(l - 1, c, columns)) {
            if (board[l - 1][c] > 1)
                return false;
        }
        // DIR CIMA
        if (checkIfPositionIsValid(l - 1,c + 1, columns)) {
            if (board[l - 1][c + 1] > 1)
                return false;
        }
        // ESQ
        if (checkIfPositionIsValid(l,c - 1, columns)) {
            if (board[l][c - 1] > 1)
                return false;
        }
        // POS ESCOLHIDA
        if (board[l][c] > 1)
            return false;
        // DIR
        if (checkIfPositionIsValid(l,c + 1, columns)) {
            if (board[l][c + 1] > 1)
                return false;
        }
        // ESQ BAIXO
        if (checkIfPositionIsValid(l + 1, c - 1, columns)) {
            if (board[l + 1][c - 1] > 1)
                return false;
        }
        // BAIXO
        if (checkIfPositionIsValid(l + 1, c, columns)) {
            if (board[l + 1][c] > 1)
                return false;
        }
        // DIR BAIXO
        if (checkIfPositionIsValid(l + 1,c + 1, columns)) {
            if (board[l + 1][c + 1] > 1)
                return false;
        }

        return true;
    }
    private boolean checkPositionForBoat2(CoordXY posXY, boolean vertical) {

        if (posXY == null)
            return false;

        int columns = Utils.BOARD_COLUMNS;

        int l = posXY.x;
        int c = posXY.y;

        if (vertical) {
            // ESQ BAIXO2
            if (checkIfPositionIsValid(l + 2, c - 1, columns)) {
                if (board[l + 2][c - 1] > 1) {
                    return false;
                }
            }
            // BAIXO2
            if (checkIfPositionIsValid(l + 2, c, columns)) {
                if (board[l + 2][c] > 1) {
                    return false;
                }
            }
            // DIR BAIXO2
            if (checkIfPositionIsValid(l + 2, c + 1, columns)) {
                if (board[l + 2][c + 1] > 1) {
                    return false;
                }
            }
        }
        else {
            // CIMA DIR2
            if (checkIfPositionIsValid(l - 1, c + 2, columns)) {
                if (board[l - 1][c + 2] > 1) {
                    return false;
                }
            }
            // DIR2
            if (checkIfPositionIsValid(l, c + 2, columns)) {
                if (board[l][c + 2] > 1) {
                    return false;
                }
            }
            // BAIXO DIR2
            if (checkIfPositionIsValid(l + 1, c + 2, columns)) {
                if (board[l + 1][c + 2] > 1) {
                    return false;
                }
            }
        }

        return checkPositionForBoat1(posXY);
    }
    private boolean checkPositionForBoat3(CoordXY posXY, boolean vertical) {

        if (posXY == null)
            return false;

        int columns = Utils.BOARD_COLUMNS;

        int l = posXY.x;
        int c = posXY.y;

        if (vertical) {
            // ESQ BAIXO3
            if (checkIfPositionIsValid(l + 3, c - 1, columns)) {
                if (board[l + 3][c - 1] > 1) {
                    return false;
                }
            }
            // BAIXO3
            if (checkIfPositionIsValid(l + 3, c, columns)) {
                if (board[l + 3][c] > 1) {
                    return false;
                }
            }
            // DIR BAIXO3
            if (checkIfPositionIsValid(l + 3, c + 1, columns)) {
                if (board[l + 3][c + 1] > 1) {
                    return false;
                }
            }
        }
        else {
            // CIMA DIR2
            if (checkIfPositionIsValid(l - 1, c + 3, columns)) {
                if (board[l - 1][c + 3] > 1) {
                    return false;
                }
            }
            // DIR2
            if (checkIfPositionIsValid(l, c + 3, columns)) {
                if (board[l][c + 3] > 1) {
                    return false;
                }
            }
            // BAIXO DIR2
            if (checkIfPositionIsValid(l + 1, c + 3, columns)) {
                if (board[l + 1][c + 3] > 1) {
                    return false;
                }
            }
        }

        return checkPositionForBoat2(posXY, vertical);
    }
    private boolean checkPositionForBoat5(CoordXY posXY, boolean vertical, boolean posAlternativa) {

        if (posXY == null)
            return false;

        int columns = Utils.BOARD_COLUMNS;

        int l = posXY.x;
        int c = posXY.y;

        if (vertical && !posAlternativa) {
            // ESQ2 CIMA
            if (checkIfPositionIsValid(l - 1, c - 2, columns)) {
                if (board[l - 1][c - 2] > 1) {
                    return false;
                }
            }
            // ESQ2
            if (checkIfPositionIsValid(l, c - 2, columns)) {
                if (board[l][c - 2] > 1) {
                    return false;
                }
            }
            // ESQ2 BAIXO
            if (checkIfPositionIsValid(l + 1, c - 2, columns)) {
                if (board[l + 1][c - 2] > 1) {
                    return false;
                }
            }

            // DIR2 CIMA
            if (checkIfPositionIsValid(l - 1, c + 2, columns)) {
                if (board[l - 1][c + 2] > 1) {
                    return false;
                }
            }
            // DIR2
            if (checkIfPositionIsValid(l, c + 2, columns)) {
                if (board[l][c + 2] > 1) {
                    return false;
                }
            }
            // DIR2 BAIXO
            if (checkIfPositionIsValid(l + 1, c + 2, columns)) {
                if (board[l + 1][c + 2] > 1) {
                    return false;
                }
            }
            return checkPositionForBoat3(posXY, vertical);
        }
        else if (!vertical && !posAlternativa) {
            // CIMA2 ESQ
            if (checkIfPositionIsValid(l - 2, c - 1, columns)) {
                if (board[l - 2][c - 1] > 1) {
                    return false;
                }
            }
            // CIMA2
            if (checkIfPositionIsValid(l - 2, c, columns)) {
                if (board[l - 2][c] > 1) {
                    return false;
                }
            }
            // CIMA2 DIR
            if (checkIfPositionIsValid(l - 2, c + 1, columns)) {
                if (board[l - 2][c + 1] > 1) {
                    return false;
                }
            }

            // BAIXO2 ESQ
            if (checkIfPositionIsValid(l + 2, c - 1, columns)) {
                if (board[l + 2][c - 1] > 1) {
                    return false;
                }
            }
            // BAIXO2
            if (checkIfPositionIsValid(l + 2, c, columns)) {
                if (board[l + 2][c] > 1) {
                    return false;
                }
            }
            // BAIXO2 DIR
            if (checkIfPositionIsValid(l + 2, c + 1, columns)) {
                if (board[l + 2][c + 1] > 1) {
                    return false;
                }
            }
            return checkPositionForBoat3(posXY, vertical);
        }
        else if (vertical && posAlternativa) {

            // Posicao Actual
            if (checkIfPositionIsValid(l, c, columns)) {
                if (board[l][c] > 1)
                    return false;
            }
            // ESQ
            if (checkIfPositionIsValid(l, c - 1, columns)) {
                if (board[l][c - 1] > 1)
                    return false;
            }
            // ESQ2
            if (checkIfPositionIsValid(l, c - 2, columns)) {
                if (board[l][c - 2] > 1)
                    return false;
            }
            // DIR
            if (checkIfPositionIsValid(l, c + 1, columns)) {
                if (board[l][c + 1] > 1)
                    return false;
            }
            // DIR2
            if (checkIfPositionIsValid(l, c + 2, columns)) {
                if (board[l][c + 2] > 1)
                    return false;
            }

            // BAIXO
            if (checkIfPositionIsValid(l + 1, c, columns)) {
                if (board[l + 1][c] > 1)
                    return false;
            }
            // BAIXO ESQ
            if (checkIfPositionIsValid(l + 1, c - 1, columns)) {
                if (board[l + 1][c - 1] > 1)
                    return false;
            }
            // BAIXO ESQ2
            if (checkIfPositionIsValid(l + 1, c - 2, columns)) {
                if (board[l + 1][c - 2] > 1)
                    return false;
            }
            // BAIXO DIR
            if (checkIfPositionIsValid(l + 1, c + 1, columns)) {
                if (board[l + 1][c + 1] > 1)
                    return false;
            }
            // BAIXO DIR2
            if (checkIfPositionIsValid(l + 1, c + 2, columns)) {
                if (board[l + 1][c + 2] > 1)
                    return false;
            }

            // CIMA
            if (checkIfPositionIsValid(l - 1, c, columns)) {
                if (board[l - 1][c] > 1)
                    return false;
            }
            // CIMA ESQ
            if (checkIfPositionIsValid(l - 1, c - 1, columns)) {
                if (board[l - 1][c - 1] > 1)
                    return false;
            }
            // CIMA ESQ2
            if (checkIfPositionIsValid(l - 1, c - 2, columns)) {
                if (board[l - 1][c - 2] > 1)
                    return false;
            }
            // CIMA DIR
            if (checkIfPositionIsValid(l - 1, c + 1, columns)) {
                if (board[l - 1][c + 1] > 1)
                    return false;
            }
            // CIMA DIR2
            if (checkIfPositionIsValid(l - 1, c + 2, columns)) {
                if (board[l - 1][c + 2] > 1)
                    return false;
            }

            // CIMA2
            if (checkIfPositionIsValid(l - 2, c, columns)) {
                if (board[l - 2][c] > 1)
                    return false;
            }
            // CIMA2 ESQ
            if (checkIfPositionIsValid(l - 2, c - 1, columns)) {
                if (board[l - 2][c - 1] > 1)
                    return false;
            }
            // CIMA2 DIR
            if (checkIfPositionIsValid(l - 2, c + 1, columns)) {
                if (board[l - 2][c + 1] > 1)
                    return false;
            }

            // CIMA3
            if (checkIfPositionIsValid(l - 3, c, columns)) {
                if (board[l - 3][c] > 1)
                    return false;
            }
            // CIMA3 ESQ
            if (checkIfPositionIsValid(l - 3, c - 1, columns)) {
                if (board[l - 3][c - 1] > 1)
                    return false;
            }
            // CIMA3 DIR
            if (checkIfPositionIsValid(l - 3, c + 1, columns)) {
                if (board[l - 3][c + 1] > 1)
                    return false;
            }
            return true;
        }
        else {
            // Posicao Actual
            if (checkIfPositionIsValid(l, c, columns)) {
                if (board[l][c] > 1)
                    return false;
            }
            // DIR
            if (checkIfPositionIsValid(l, c + 1, columns)) {
                if (board[l][c + 1] > 1)
                    return false;
            }
            // ESQ
            if (checkIfPositionIsValid(l, c - 1, columns)) {
                if (board[l][c - 1] > 1)
                    return false;
            }
            // ESQ2
            if (checkIfPositionIsValid(l, c - 2, columns)) {
                if (board[l][c - 2] > 1)
                    return false;
            }
            // ESQ3
            if (checkIfPositionIsValid(l, c - 3, columns)) {
                if (board[l][c - 3] > 1)
                    return false;
            }

            // BAIXO
            if (checkIfPositionIsValid(l + 1, c, columns)) {
                if (board[l + 1][c] > 1)
                    return false;
            }
            // BAIXO DIR
            if (checkIfPositionIsValid(l + 1, c + 1, columns)) {
                if (board[l + 1][c + 1] > 1)
                    return false;
            }
            // BAIXO ESQ
            if (checkIfPositionIsValid(l + 1, c - 1, columns)) {
                if (board[l + 1][c - 1] > 1)
                    return false;
            }
            // BAIXO ESQ2
            if (checkIfPositionIsValid(l + 1, c - 2, columns)) {
                if (board[l + 1][c - 2] > 1)
                    return false;
            }
            // BAIXO ESQ3
            if (checkIfPositionIsValid(l + 1, c - 3, columns)) {
                if (board[l + 1][c - 3] > 1)
                    return false;
            }

            // CIMA
            if (checkIfPositionIsValid(l - 1, c, columns)) {
                if (board[l - 1][c] > 1)
                    return false;
            }
            // CIMA DIR
            if (checkIfPositionIsValid(l - 1, c + 1, columns)) {
                if (board[l - 1][c + 1] > 1)
                    return false;
            }
            // CIMA ESQ
            if (checkIfPositionIsValid(l - 1, c - 1, columns)) {
                if (board[l - 1][c - 1] > 1)
                    return false;
            }
            // CIMA ESQ2
            if (checkIfPositionIsValid(l - 1, c - 2, columns)) {
                if (board[l - 1][c - 2] > 1)
                    return false;
            }
            // CIMA ESQ3
            if (checkIfPositionIsValid(l - 1, c - 3, columns)) {
                if (board[l - 1][c - 3] > 1)
                    return false;
            }

            // BAIXO2
            if (checkIfPositionIsValid(l + 2, c, columns)) {
                if (board[l + 2][c] > 1)
                    return false;
            }
            // BAIXO2 DIR
            if (checkIfPositionIsValid(l + 2, c + 1, columns)) {
                if (board[l + 2][c + 1] > 1)
                    return false;
            }
            // BAIXO2 ESQ
            if (checkIfPositionIsValid(l + 2, c - 1, columns)) {
                if (board[l + 2][c - 1] > 1)
                    return false;
            }

            // CIMA2
            if (checkIfPositionIsValid(l - 2, c, columns)) {
                if (board[l - 2][c] > 1)
                    return false;
            }
            // CIMA2 DIR
            if (checkIfPositionIsValid(l - 2, c + 1, columns)) {
                if (board[l - 2][c + 1] > 1)
                    return false;
            }
            // CIMA2 ESQ
            if (checkIfPositionIsValid(l - 2, c - 1, columns)) {
                if (board[l - 2][c - 1] > 1)
                    return false;
            }
            return true;
        }

    }


    private boolean checkIfPositionIsValid(int x, int y, int columns) {
        return ((x >= 0 && x < columns) && (y >= 0 && y < columns));
    }
    private CoordXY convert1Dto2D(int pos) {

        int col = Utils.BOARD_COLUMNS;
        if (!checkIfPositionIsValid(pos/col, pos % col, col))
            return null;
        return new CoordXY(pos / col, pos % col);
    }

    public int getBoardSize() {
        return board.length * board.length;
    }
    public void reset() {
        for (int i = 0; i < Utils.BOARD_COLUMNS; i++)
            for (int j = 0; j < Utils.BOARD_COLUMNS; j++)
                board[i][j] = 1;
    }
    public int[][] getBoardIn2D() {
        int arr[][] = new int[Utils.BOARD_COLUMNS][Utils.BOARD_COLUMNS];
        for (int i = 0; i < Utils.BOARD_COLUMNS; i++)
            System.arraycopy(board[i], 0, arr[i], 0, Utils.BOARD_COLUMNS);
        return arr;
    }
    public ImageView putImageBasedOnNumbers(ImageView iv, int pos) {

        int col = Utils.BOARD_COLUMNS;
        int l = pos / col;
        int c = pos % col;

        if (board[l][c] == 1)
            iv.setImageResource(R.drawable.water);
        else if (board[l][c] > 1)
            iv.setImageResource(R.drawable.boat_alt);
        else if (board[l][c] == -1)
            iv.setImageResource(R.drawable.shot_hit_alt);
        else
            iv.setImageResource(R.drawable.boat_sinking);
        return iv;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Utils.BOARD_COLUMNS; i++) {
            for (int j = 0; j < Utils.BOARD_COLUMNS; j++) {
                sb.append(board[i][j] + " ");
            }
        }
        return sb.toString();
    }
}
