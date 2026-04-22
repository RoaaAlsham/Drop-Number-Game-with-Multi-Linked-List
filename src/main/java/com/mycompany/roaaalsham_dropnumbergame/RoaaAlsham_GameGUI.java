package com.mycompany.roaaalsham_dropnumbergame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * RoaaAlsham_GameGUI
 *
 * Swing front-end for the Drop Number Game.
 * Predefined moves play in the exact order given in the specification.
 *
 *   ▶  "Drop Next"  — advances one move
 *   ▶  "Auto Play"  — runs automatically every 650 ms
 *   ▶  "Reset"      — restarts from scratch
 */
public class RoaaAlsham_GameGUI extends JFrame
        implements RoaaAlsham_DropNumberGrid.GridChangeListener {

    // ── tile colour palette (2048-inspired) ──────────────────────────────
    private static final Map<Integer, Color> TILE_BG   = new HashMap<>();
    private static final Map<Integer, Color> TILE_TEXT = new HashMap<>();

    static {
        Color dk = new Color(0x776E65);   // dark text (light tiles)
        Color lt = new Color(0xF9F6F2);   // light text (dark tiles)

        int[][] palette = {
            {0,    0xCDC1B4, 0},   // empty
            {2,    0xEEE4DA, 0},
            {4,    0xEDE0C8, 0},
            {8,    0xF2B179, 1},
            {16,   0xF59563, 1},
            {32,   0xF67C5F, 1},
            {64,   0xF65E3B, 1},
            {128,  0xEDCF72, 1},
            {256,  0xEDCC61, 1},
            {512,  0xEDC850, 1},
            {1024, 0xEDC53F, 1},
            {2048, 0xEDC22E, 1},
        };
        for (int[] row : palette) {
            TILE_BG  .put(row[0], new Color(row[1]));
            TILE_TEXT.put(row[0], row[2] == 1 ? lt : dk);
        }
    }

    // ── pre-defined move sequence ─────────────────────────────────────────
    private static final RoaaAlsham_NumberBlock[] MOVES = {
        new RoaaAlsham_NumberBlock(2,  0),
        new RoaaAlsham_NumberBlock(2,  3),
        new RoaaAlsham_NumberBlock(4,  1),
        new RoaaAlsham_NumberBlock(2,  2),
        new RoaaAlsham_NumberBlock(4,  4),
        new RoaaAlsham_NumberBlock(2,  1),
        new RoaaAlsham_NumberBlock(4,  4),
        new RoaaAlsham_NumberBlock(8,  0),
        new RoaaAlsham_NumberBlock(8,  0),
        new RoaaAlsham_NumberBlock(32, 1),
        new RoaaAlsham_NumberBlock(2,  2),
        new RoaaAlsham_NumberBlock(64, 2),
        new RoaaAlsham_NumberBlock(16, 3),
        new RoaaAlsham_NumberBlock(64, 1),
        new RoaaAlsham_NumberBlock(32, 2),
        new RoaaAlsham_NumberBlock(16, 0),
        new RoaaAlsham_NumberBlock(16, 4),
        new RoaaAlsham_NumberBlock(32, 2),
        new RoaaAlsham_NumberBlock(64, 1),
        new RoaaAlsham_NumberBlock(8,  3),
        new RoaaAlsham_NumberBlock(4,  3),
        new RoaaAlsham_NumberBlock(2,  3),
        new RoaaAlsham_NumberBlock(2,  3),
        new RoaaAlsham_NumberBlock(2,  1),
        new RoaaAlsham_NumberBlock(64, 2),
        new RoaaAlsham_NumberBlock(32, 2),
        new RoaaAlsham_NumberBlock(16, 2),
        new RoaaAlsham_NumberBlock(8,  2),
        new RoaaAlsham_NumberBlock(8,  2),
        new RoaaAlsham_NumberBlock(4,  1),
        new RoaaAlsham_NumberBlock(8,  1),
    };

    // ── layout constants — sized to fit any standard screen ──────────────
    private static final int ROWS      = 7;
    private static final int COLS      = 5;
    private static final int CELL      = computeCellSize(); // auto-fit
    private static final int GAP       = 7;
    private static final Color BOARD   = new Color(0xBBADA0);
    private static final Color FRAME_BG= new Color(0xFAF8EF);

    /**
     * Picks a cell size so the whole window fits within 90 % of the
     * screen height (header ~50 px + footer ~60 px + board margins ~30 px).
     * Minimum 52 px, maximum 80 px.
     */
    private static int computeCellSize() {
        int screenH = GraphicsEnvironment.getLocalGraphicsEnvironment()
                          .getMaximumWindowBounds().height;
        int overhead = 50 + 60 + 30;          // header + footer + wrapper padding
        int available = (int)(screenH * 0.90) - overhead;
        // available = ROWS * cell + (ROWS + 1) * GAP  →  cell = (available - 8*GAP) / ROWS
        int cell = (available - (ROWS + 1) * 7) / ROWS;
        return Math.max(52, Math.min(cell, 80));
    }

    // ── game state ────────────────────────────────────────────────────────
    private final RoaaAlsham_DropNumberGrid grid = new RoaaAlsham_DropNumberGrid();
    private int moveIndex = 0;
    private Timer autoTimer;

    // ── UI references ─────────────────────────────────────────────────────
    private final TilePanel[][] tiles = new TilePanel[ROWS][COLS];
    private JLabel lblStep, lblNext, lblStatus;
    private JButton btnDrop, btnAuto, btnReset;

    // =====================================================================
    //  Inner class: one tile cell
    // =====================================================================
    private static class TilePanel extends JPanel {
        private int     value       = 0;
        private boolean colActive   = false;  // next-drop column highlight

        TilePanel() {
            setPreferredSize(new Dimension(CELL, CELL));
            setOpaque(false);
        }

        void setValue(int v)           { value     = v; repaint(); }
        void setColActive(boolean b)   { colActive = b; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // Tile background
            Color bg = TILE_BG.getOrDefault(value, new Color(0xEDC22E));
            if (colActive && value == 0) bg = new Color(0xD9CFC7);  // tinted empty
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            // Subtle inner border to lift the tile
            g2.setColor(new Color(0, 0, 0, 18));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            // Number label
            if (value != 0) {
                Color fg  = TILE_TEXT.getOrDefault(value, new Color(0xF9F6F2));
                String txt = String.valueOf(value);
                // font scales with cell: 1-2 digits big, 3 medium, 4 small
                int base = CELL / 3;
                int fs = txt.length() <= 2 ? base : txt.length() == 3 ? (int)(base * 0.82) : (int)(base * 0.67);
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(fs, 10)));
                g2.setColor(fg);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(txt)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(txt, tx, ty);
            }
            g2.dispose();
        }
    }

    // =====================================================================
    //  Constructor
    // =====================================================================
    public RoaaAlsham_GameGUI() {
        super("Drop Number Game  —  Roaa Alsham");
        grid.setGridChangeListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(FRAME_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBoard(),  BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        refreshDisplay();
    }

    // ── header: step counter + next-block preview ─────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        p.setBackground(FRAME_BG);
        p.setBorder(BorderFactory.createEmptyBorder(6, 12, 2, 12));

        lblStep   = styledLabel("Step: 0 / " + MOVES.length, 13, new Color(0x776E65));
        lblNext   = styledLabel("", 13, new Color(0x5A5248));
        lblStatus = styledLabel("", 13, new Color(0xBF4040));

        p.add(lblStep);
        p.add(new JSeparator(SwingConstants.VERTICAL) {{ setPreferredSize(new Dimension(1, 18)); }});
        p.add(lblNext);
        p.add(lblStatus);
        return p;
    }

    private static JLabel styledLabel(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }

    // ── board panel ───────────────────────────────────────────────────────
    private JPanel buildBoard() {
        int boardW = COLS * CELL + (COLS + 1) * GAP;
        int boardH = ROWS * CELL + (ROWS + 1) * GAP;

        // The actual grid surface
        JPanel board = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BOARD);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        board.setOpaque(false);
        board.setPreferredSize(new Dimension(boardW, boardH));

        // Column-number labels painted on the board
        for (int c = 0; c < COLS; c++) {
            final int cc = c;
            JLabel lbl = new JLabel("Col " + c, SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                }
            };
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(new Color(0xF9F6F2));
            int x = GAP + c * (CELL + GAP);
            lbl.setBounds(x, 2, CELL, 14);
            board.add(lbl);
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                TilePanel tp = new TilePanel();
                int x = GAP + c * (CELL + GAP);
                int y = GAP + r * (CELL + GAP);
                tp.setBounds(x, y, CELL, CELL);
                board.add(tp);
                tiles[r][c] = tp;
            }
        }

        // Surround board with a padded wrapper
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        wrapper.setBackground(FRAME_BG);
        wrapper.add(board);
        return wrapper;
    }

    // ── footer: control buttons ───────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        p.setBackground(FRAME_BG);

        btnDrop  = makeBtn("Drop Next",  new Color(0x8F7A66));
        btnAuto  = makeBtn("Auto Play",  new Color(0x4A9C5A));
        btnReset = makeBtn("Reset",      new Color(0xBF4F4F));

        btnDrop .addActionListener(e -> stepOnce());
        btnAuto .addActionListener(e -> toggleAuto());
        btnReset.addActionListener(e -> resetGame());

        p.add(btnDrop);
        p.add(btnAuto);
        p.add(btnReset);
        return p;
    }

    private static JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            final Color base = bg;
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(base.brighter()); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(base); }
        });
        return b;
    }

    // =====================================================================
    //  Game control
    // =====================================================================
    private void stepOnce() {
        if (moveIndex >= MOVES.length || grid.isGameOver()) return;

        RoaaAlsham_NumberBlock block = MOVES[moveIndex];
        moveIndex++;
        grid.dropBlock(block);   // triggers onGridChanged() → refreshDisplay()

        if (grid.isGameOver()) {
            stopAuto();
            lblStatus.setText("  GAME OVER — column full!");
            btnDrop.setEnabled(false);
            btnAuto.setEnabled(false);
        } else if (moveIndex >= MOVES.length) {
            stopAuto();
            lblStatus.setText("  All moves played.");
            btnDrop.setEnabled(false);
            btnAuto.setEnabled(false);
        }
    }

    private void toggleAuto() {
        if (autoTimer != null && autoTimer.isRunning()) {
            stopAuto();
        } else {
            autoTimer = new Timer(650, e -> {
                if (moveIndex >= MOVES.length || grid.isGameOver()) stopAuto();
                else stepOnce();
            });
            autoTimer.start();
            btnAuto.setText("Pause");
            btnAuto.setBackground(new Color(0xCC7722));
            btnDrop.setEnabled(false);
        }
    }

    private void stopAuto() {
        if (autoTimer != null) autoTimer.stop();
        btnAuto.setText("Auto Play");
        btnAuto.setBackground(new Color(0x4A9C5A));
        if (!grid.isGameOver() && moveIndex < MOVES.length)
            btnDrop.setEnabled(true);
    }

    private void resetGame() {
        stopAuto();
        dispose();
        SwingUtilities.invokeLater(() -> new RoaaAlsham_GameGUI().setVisible(true));
    }

    // =====================================================================
    //  GridChangeListener
    // =====================================================================
    @Override
    public void onGridChanged() {
        refreshDisplay();
    }

    // =====================================================================
    //  Display refresh
    // =====================================================================
    private void refreshDisplay() {
        int[][] snap      = grid.getGridSnapshot();
        int     activeCol = (moveIndex < MOVES.length) ? MOVES[moveIndex].getColumn() : -1;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                tiles[r][c].setValue(snap[r][c]);
                tiles[r][c].setColActive(c == activeCol);
            }
        }

        lblStep.setText("Step: " + moveIndex + " / " + MOVES.length);

        if (moveIndex < MOVES.length && !grid.isGameOver()) {
            RoaaAlsham_NumberBlock next = MOVES[moveIndex];
            lblNext.setText("\u25BC " + next.getValue() + "  \u2192  col " + next.getColumn());
        } else {
            lblNext.setText("");
        }
    }

    // =====================================================================
    //  Entry point
    // =====================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new RoaaAlsham_GameGUI().setVisible(true));
    }
}