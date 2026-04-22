package com.mycompany.roaaalsham_dropnumbergame;

public class RoaaAlsham_DropNumberGrid {

    /**
     * Implement in the GUI panel; called automatically after every move.
     */
    public interface GridChangeListener {

        void onGridChanged();
    }

    private GridChangeListener listener;

    // ══════════════════════════════════════════════════════════════════════
    /**
     * Register the GUI once at startup.
     */
    public void setGridChangeListener(GridChangeListener listener) {
        this.listener = listener;
    }

    public int getROWS() {
        return ROWS;
    }

    public int getCOLS() {
        return COLS;
    }

    /**
     * Snapshot of the current board as a plain 2-D int array. In purpose to
     * deliver it to GUI layer Cell value == 0 means the cell is empty.
     */
    public int[][] getGridSnapshot() {
        int[][] snap = new int[ROWS][COLS];
        for (int c = 0; c < COLS; c++) {
            Node cur = getColumnFloor(c);
            while (cur != null) {
                snap[cur.row][cur.col] = cur.value;
                cur = cur.up;
            }
        }
        return snap;
    }

    /**
     * Top-of-stack value for a column (0 if empty).
     */
    public int getPeakValue(int col) {
        Node peak = getColumnPeak(col);
        return (peak == null) ? 0 : peak.value;
    }

    private Node head;
    private static final int ROWS = 7;
    private static final int COLS = 5;

    // ------------------------------------------------------------------
    // NAVIGATION
    // ------------------------------------------------------------------
    private Node getColumnFloor(int col) {
        if (head == null) {
            return null;
        }

        Node cur = head;
        while (cur.down != null) {
            cur = cur.down;
        }
        while (cur.left != null) {
            cur = cur.left;
        }

        while (cur != null) {
            if (cur.col == col) {
                return cur;
            }
            if (cur.col > col) {
                break;
            }
            cur = cur.right;
        }
        return null;
    }

    private Node getColumnPeak(int col) {
        Node floor = getColumnFloor(col);
        if (floor == null) {
            return null;
        }
        Node cur = floor;
        while (cur.up != null) {
            cur = cur.up;
        }
        return cur;
    }

    public boolean isColumnFull(int col) {
        Node peak = getColumnPeak(col);
        return peak != null && peak.row == 0;
    }

    public boolean isGameOver() {
        for (int i = 0; i < COLS; i++) {
            if (isColumnFull(i)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // HORIZONTAL WIRING
    // ------------------------------------------------------------------
    private void wireHorizontal(Node n) {
        Node leftNeighbor = null;
        for (int c = n.col - 1; c >= 0; c--) {
            Node temp = getColumnPeak(c);
            while (temp != null && temp.row < n.row) {
                temp = temp.down;
            }
            if (temp != null && temp.row == n.row) {
                leftNeighbor = temp;
                break;
            }
        }

        Node rightNeighbor = null;
        for (int c = n.col + 1; c < COLS; c++) {
            Node temp = getColumnPeak(c);
            while (temp != null && temp.row < n.row) {
                temp = temp.down;
            }
            if (temp != null && temp.row == n.row) {
                rightNeighbor = temp;
                break;
            }
        }

        if (leftNeighbor != null) {
            n.left = leftNeighbor;
            leftNeighbor.right = n;
        }
        if (rightNeighbor != null) {
            n.right = rightNeighbor;
            rightNeighbor.left = n;
        }
    }

    // ------------------------------------------------------------------
    // CORE LOGIC: DROP & MERGE
    // ------------------------------------------------------------------
    public void dropBlock(RoaaAlsham_NumberBlock block) {
        int col = block.getColumn();
        int val = block.getValue();

        if (isColumnFull(col)) {
            return;
        }

        Node peak = getColumnPeak(col);

        if (peak != null && peak.value == val) {
            peak.value *= 2;
            cascadeMerge(peak);
        } else {
            int targetRow = (peak == null) ? ROWS - 1 : peak.row - 1;
            Node newNode = new Node(val, targetRow, col);

            if (head == null) {
                head = newNode;
            }

            if (peak != null) {
                peak.up = newNode;
                newNode.down = peak;
            }
            wireHorizontal(newNode);
        }

        // update the scene of GUI
        if (listener != null) {
            listener.onGridChanged();
        }
    }

    private void cascadeMerge(Node node) {
        if (node.down == null || node.value != node.down.value) {
            return;
        }
        Node bottomNode = node.down;
        bottomNode.value *= 2;
        removeNode(node);
        cascadeMerge(bottomNode);
    }

    private void removeNode(Node n) {
        if (n.up != null) {
            n.up.down = n.down;
        }
        if (n.down != null) {
            n.down.up = n.up;
        }
        if (n.left != null) {
            n.left.right = n.right;
        }
        if (n.right != null) {
            n.right.left = n.left;
        }

        if (head == n) {
            if (n.down != null) {
                head = n.down;
            } else if (n.right != null) {
                head = n.right;
            } else if (n.left != null) {
                head = n.left;
            } else {
                head = null;
            }
        }
    }

    //Kept to allow console based tracking of the output (debugging)
    public void printGrid() {
//        int[][] display = new int[ROWS][COLS];
//        for (int c = 0; c < COLS; c++) {
//            Node cur = getColumnPeak(c);
//            while (cur != null) {
//                display[cur.row][cur.col] = cur.value;
//                cur = cur.down;
//            }
//        }
//        for (int r = 0; r < ROWS; r++) {
//            for (int c = 0; c < COLS; c++) {
//                if (display[r][c] == 0) System.out.print("[    ]");
//                else                    System.out.printf("[%4d]", display[r][c]);
//            }
//            System.out.println();
//        }
//        System.out.println("-----------------------------------");

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Node node = findNode(r, c);

                if (node != null) {
                    System.out.printf("[%4d]", node.value);
                } else {
                    System.out.print("[    ]");
                }
            }
            System.out.println(); // New line after each row
        }
        System.out.println("-----------------------------------");

    }

    private Node findNode(int row, int col) {

        Node bottom = getColumnFloor(col);
        while (bottom != null) {

            if (bottom.row == row) {
                return bottom;
            }
            bottom = bottom.up;
        }
        return null;

    }
}
