package com.example.minesweeper;

public class Cell {
    // fixed coordinates on the board
    private final int row;
    private final int col;

    // game state
    private boolean isMine = false;
    private boolean isRevealed = false;
    private boolean isFlagged = false;
    private int adjacent = 0;   // # of neighbor mines (0-8)

    // constructor
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // getters
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isMine() { return isMine; }
    public boolean isRevealed() { return isRevealed; }
    public boolean isFlagged() { return isFlagged; }
    public int getAdjacent() { return adjacent; }

    // functions
    // isCovered = not revealed yet
    public boolean isCovered() { return !isRevealed; }

    // mark cell with mine
    public void setMine(boolean mine) { this.isMine = mine; }

    // adjacent mine counts
    public void setAdjacent(int count) { this.adjacent = count; }

    // reveal cell
    public void reveal() { this.isRevealed = true; }

    // toggle flag
    public boolean toggleFlag() {
        if (isRevealed) return false;
        isFlagged = !isFlagged;
        return true;
    }

    // reset
    public void reset() {
        isMine = false;
        isRevealed = false;
        isFlagged = false;
        adjacent = 0;
    }

    // for debugging
    @Override
    public String toString() {
        return "Cell(" + row + "," + col + ") mine=" + isMine +
                " revealed=" + isRevealed +
                " flagged=" + isFlagged +
                " adj=" + adjacent;
    }

}
