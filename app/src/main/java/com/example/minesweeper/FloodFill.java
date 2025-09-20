package com.example.minesweeper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
public class FloodFill {

    // 8 neighbors
    private static final int[] DR = {-1,-1,-1, 0, 0, 1, 1, 1}; // row
    private static final int[] DC = {-1, 0, 1,-1, 1,-1, 0, 1}; // col

    private FloodFill() {}

    public static List<int[]> revealFromZero(Cell[][] board, int sr, int sc) {
        List<int[]> changed = new ArrayList<>();
        if (board == null || board.length == 0) return changed;

        int rows = board.length;
        int cols = board[0].length;
        if (!inBounds(sr, sc, rows, cols)) return changed;

        Cell start = board[sr][sc];
        // must be a safe + unflagged cell to proceed
        if (start.isMine() || start.isFlagged()) return changed;

        // reveal start cell if needed
        if (!start.isRevealed()) {
            start.reveal();
            changed.add(new int[]{sr, sc});
        }

        // if not zero then we are done
        if (start.getAdjacent() != 0) return changed;

        boolean[][] visited = new boolean[rows][cols];
        Deque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sr, sc});
        visited[sr][sc] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1];

            for (int k = 0; k < 8; k++) {
                int nr = r + DR[k];
                int nc = c + DC[k];
                if (!inBounds(nr, nc, rows, cols)) continue;
                if (visited[nr][nc]) continue;

                Cell n = board[nr][nc];

                // never auto-reveal flags or mines
                if (n.isFlagged() || n.isMine()) {
                    visited[nr][nc] = true;
                    continue;
                }

                // reveal if hidden
                if (!n.isRevealed()) {
                    n.reveal();
                    changed.add(new int[]{nr, nc});
                }
                visited[nr][nc] = true;

                // only enqueue further if neighbor is also zero
                if (n.getAdjacent() == 0) {
                    q.add(new int[]{nr, nc});
                }
            }
        }

        return changed;
    }

    private static boolean inBounds(int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}
