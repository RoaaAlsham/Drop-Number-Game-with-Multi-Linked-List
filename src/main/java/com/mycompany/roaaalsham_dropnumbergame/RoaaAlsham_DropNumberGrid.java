package com.mycompany.roaaalsham_dropnumbergame;
/**
 *
 * @author Roaa
 */

public class RoaaAlsham_DropNumberGrid {

    private Node head; // Points to the very first node dropped
    private static final int ROWS = 7;
    private static final int COLS = 5;

    // ------------------------------------------------------------------
    // NAVIGATION — Floor-based routing
    // ------------------------------------------------------------------
    // Finds the lowest block resting in a specific column
    private Node getColumnFloor(int col) {
        if (head == null) {
            return null;
        }

        // 1. Navigate to the absolute bottom-left of the entire grid
        Node cur = head;
        while (cur.down != null) {
            cur = cur.down;
        }
        while (cur.left != null) {
            cur = cur.left;
        }

        // 2. Walk right across the solid floor to find the target column
        while (cur != null) {
            if (cur.col == col) {
                return cur;
            }
            if (cur.col > col) {
                break; // We passed it; column is empty
            }
            cur = cur.right;
        }
        return null;
    }

    // Finds the highest stacked block in a column (lowest row index)
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
            System.out.println("Column " + col + " is full! Move rejected.");
            return;
        }

        Node peak = getColumnPeak(col);

        if (peak != null && peak.value == val) {
            // ── MERGE PATH ──
            // Double the peak's value in place, then cascade downward
            peak.value *= 2;
            cascadeMerge(peak);
        } else {
            // ── INSERT PATH ──
            // Stack upwards: target row is peak's row minus 1
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
        printGrid();
    }

    private void cascadeMerge(Node node) {
        // Check downwards. If the node below matches the newly doubled value, merge them.
        if (node.down == null || node.value != node.down.value) {
            return;
        }

        // Double the bottom node, prepare to delete the top node
        Node bottomNode = node.down;
        bottomNode.value *= 2;
        removeNode(node);

        // Recurse downwards
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

    // ------------------------------------------------------------------
    // PRINT
    // ------------------------------------------------------------------
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
                Node node= findNode(r,c);

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
