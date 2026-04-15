
package com.mycompany.roaaalsham_dropnumbergame;

/**
 *
 * @author Roaa
 */

public class Node {
        public int value, row, col;
        public Node up, down, left, right;

        Node(int value, int row, int col) {
            this.value = value;
            this.row   = row;
            this.col   = col;
        }
    }