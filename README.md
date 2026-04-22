# Drop-Number-Game-with-Multi-Linked-List
a project made as a requirement for Data Structures Programming Course

**Author:** Roaa Alsham - 2421051350

Note: GUI is supported in 'gui' branch of this project

## 1. Introduction

- **Goal:** The objective of the game is to merge falling tiles of the same number to double their value, continuously creating larger numbers until the grid fills up.
- **Constraints:** The core challenge of this project was implementing the game mechanics strictly using a multi-linked list. As per the project requirements, the use of arrays or matrices to store the 7x5 grid data was strictly prohibited.

## 2. Data Structure Design

### The Node Class

- **Structure:** Each Node stores data (value, row, col) and uses four directional pointers: `up`, `down`, `left`, and `right`.
- **Node Design Decision:** To determine which node design is the best for a game that depends on `merge()` function and requires continuous traversal, I compared the time complexity between two designs. Accordingly, a 4-pointer node was chosen over a 2-pointer (right, down) node.
<img width="869" height="705" alt="image" src="https://github.com/user-attachments/assets/9e60c5d5-de86-4bc1-b111-f850070bf7f6" />

### The Grid Architecture & The `head` Pointer

- To optimize memory, the grid does not store empty cells.
- The `head` pointer points to the very first node dropped. Every other node in the grid is reachable by first navigating to absolute bottom-left via the `head` pointer.

## 3. Implementation Details (Updating the Linked List)

### A. Vertical Navigation

Because blocks always stack upwards from the bottom of the grid, navigation relies on finding the bottom-most block in a column.

- **`getColumnFloor(int col)`:** This method traverses down and left from the head to find the absolute bottom-left node of the entire grid. From there, it walks right across the horizontal "floor" links until it finds the target column.
- **`getColumnPeak(int col)`:** To find where a new block should land, the program first locates the column's floor, then traverses the `up` pointers until it reaches the highest stacked block (the peak).

### B. Dropping a Block (`dropBlock`)

When a block is dropped into a target column, the program checks the peak of that column:

- **Insert Path:** If the column is empty, the block lands at the maximum depth (`ROWS - 1`). If the column has blocks but the top block does not match the incoming value, the new block is stacked exactly one row above the current peak (`peak.row - 1`).
- **Merge Path:** If the incoming block's value matches the peak block's value, a new node is not created. Instead, the existing peak's value is multiplied by 2 in place, and the `cascadeMerge` method is called recursively to check any other merge operations.

### C. Horizontal Wiring (`wireHorizontal`)

When a new node is inserted, it must connect to its adjacent neighbors on the exact same row to maintain horizontal integrity.

- The method scans the columns to the immediate left and right.
- It finds the peak of those adjacent columns and walks down until it finds a node whose row exactly matches the new node's row.
- Once found, the `left` and `right` pointers of both nodes are doubly linked.

### D. Cascade Merging Logic (`cascadeMerge`)

In the standard Drop Number game, a newly merged block can trigger a chain reaction if its new doubled value matches the block directly below it.

- The algorithm checks the node directly down from the newly doubled block.
- If the values match, the bottom node's value is doubled, and the top node is deleted using the `removeNode` method, which safely bypasses all 4 of its pointers and reassigns the `head` if necessary.
- This process calls itself recursively (`cascadeMerge(bottomNode)`) until no more matches are found downwards.

## 4. Game Over Condition

The game actively monitors the height of every column. The `isGameOver()` method iterates through the columns and calls `isColumnFull(col)`. A column is considered completely full if its peak node reaches row 0 (the very top of the grid). If any column reaches this state, the game terminates.

## 5. Visual Diagram of the Multi-Linked List in the Final Stage

<img width="550" height="682" alt="image" src="https://github.com/user-attachments/assets/65b7317e-e487-47c6-85ce-081ef3080f4d" />


## 6. Program Output on Console

### Initialization
<img width="383" height="809" alt="image" src="https://github.com/user-attachments/assets/9d832a9d-3657-4872-b630-0522340446f2" />

### Example of a Cascaded Merge

<img width="397" height="592" alt="image" src="https://github.com/user-attachments/assets/63673170-c92c-4661-9a2c-78c1dd71fbd2" />

### The final result
<img width="433" height="349" alt="image" src="https://github.com/user-attachments/assets/032f6022-fa67-436b-9ac2-0c52a1d94e6c" />

## 7. GUI Output

<img width="520" height="899" alt="image" src="https://github.com/user-attachments/assets/c70dc5ea-3f92-4990-8a83-cd5f812a7e0b" />


finally:

<img width="521" height="901" alt="image" src="https://github.com/user-attachments/assets/03a2bf68-13a6-4bf0-8aeb-21ccc062f3bb" />
