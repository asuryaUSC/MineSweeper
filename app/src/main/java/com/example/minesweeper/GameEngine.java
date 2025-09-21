package com.example.minesweeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
public class GameEngine {
    // board config
    private final int rows = 10;
    private final int cols = 10;
    private final int totalMines = 5;

    // state
    private Cell[][] board;
    private boolean isGameOver = false;
    private boolean didWin = false;
    private int flagsPlaced = 0;
    private int revealedSafeCount = 0;

    // 8 neighbors
    private static final int[] DR = {-1,-1,-1, 0, 0, 1, 1, 1};
    private static final int[] DC = {-1, 0, 1,-1, 1,-1, 0, 1};

    // public API:

    // new game
    public void initNewGame() {
        isGameOver = false;
        didWin = false;
        flagsPlaced = 0;
        revealedSafeCount = 0;

        // create empty game
        board = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new Cell(r, c);
            }
        }

        // place mines
        placeMinesRandomly(totalMines);

        // compute adjacency counts
        computeAdjacencyCounts();
    }

    // toggle flag
    public boolean toggleFlag(int r, int c) {
        if (isOutOfBounds(r, c) || isGameOver) return false;
        Cell cell = board[r][c];
        if (cell.isRevealed()) return false; // cannot flag revealed cells

        boolean changed = cell.toggleFlag();
        if (changed) {
            flagsPlaced += cell.isFlagged() ? 1 : -1;
        }
        return changed;
    }

    // dig at (r,c)
    public List<int[]> dig(int r, int c) {
        List<int[]> changed = new ArrayList<>();
        if (isOutOfBounds(r, c) || isGameOver) return changed;

        Cell cell = board[r][c];

        // cant dig flagged or alr revealed
        if (cell.isFlagged() || cell.isRevealed()) return changed;

        // hit mine -> game over
        if (cell.isMine()) {
            isGameOver = true;
            didWin = false;
            // reveal all cells and return all newly revealed positions
            return revealAll();
        }

        // safe dig
        cell.reveal();
        changed.add(new int[]{r, c});
        if (!cell.isMine()) revealedSafeCount++;

        // flood fill and reveal neighbors
        if (cell.getAdjacent() == 0) {
            List<int[]> flood = FloodFill.revealFromZero(board, r, c);
            for (int[] pos : flood) {
                int rr = pos[0], cc = pos[1];
                Cell n = board[rr][cc];
                if (!n.isMine()) {
                    if (!(rr == r && cc == c)) {
                        revealedSafeCount++;
                    }
                }
                changed.add(pos);
            }
        }

        // check win after dig
        if (checkWin()) {
            isGameOver = true;
            didWin = true;
            List<int[]> end = revealAll();
            changed.addAll(end);
        }

        return changed;
    }

    // reveal all cells
    public List<int[]> revealAll() {
        List<int[]> changed = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board[r][c];
                if (!cell.isRevealed()) {
                    cell.reveal();
                    changed.add(new int[]{r, c});
                }
            }
        }
        return changed;
    }

    // check for win
    public boolean checkWin() {
        int safeCells = rows * cols - totalMines;
        return revealedSafeCount >= safeCells;
    }

    // getters
    public Cell[][] getBoard() { return board; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTotalMines() { return totalMines; }
    public int getFlagsPlaced() { return flagsPlaced; }
    public boolean isGameOver() { return isGameOver; }
    public boolean didWin() { return didWin; }

    // helpers

    // place mines
    private void placeMinesRandomly(int mines) {
        Random rand = new Random();
        Set<Integer> spots = new HashSet<>();
        int max = rows * cols;

        while (spots.size() < mines) {
            int idx = rand.nextInt(max); // 0..99
            spots.add(idx);
        }

        for (int idx : spots) {
            int r = idx / cols;
            int c = idx % cols;
            board[r][c].setMine(true);
        }
    }

    // comp adjacency counts
    private void computeAdjacencyCounts() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c].isMine()) {
                    board[r][c].setAdjacent(0);
                    continue;
                }
                int count = 0;
                for (int k = 0; k < 8; k++) {
                    int nr = r + DR[k];
                    int nc = c + DC[k];
                    if (!isOutOfBounds(nr, nc) && board[nr][nc].isMine()) {
                        count++;
                    }
                }
                board[r][c].setAdjacent(count);
            }
        }
    }

    // out of bounds
    private boolean isOutOfBounds(int r, int c) {
        return r < 0 || r >= rows || c < 0 || c >= cols;
    }



}
