package com.mycompany.mastermindgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MasterMindGame extends JFrame {
    private static final int ROWS = 10;
    private static final int COLS = 4;
    private static final Color[] COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
        Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK
    };
    
    private JButton[][] board;
    private JButton[][] feedback;
    private JButton[] secretCode;
    private Color[] currentSecret;
    private int currentRow;
    private int difficulty;
    private JButton checkButton;
    private JRadioButton[] levelButtons;
    private JButton newGameButton;
    private JLabel descLabel;

    public MasterMindGame() {
        setTitle("Mastermind Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(142, 229, 214)); 
        
        initializeComponents();
        layoutComponents();
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        board = new JButton[ROWS][COLS];
        feedback = new JButton[ROWS][COLS];
        secretCode = new JButton[COLS];
        currentRow = ROWS - 1;
        difficulty = 1; 
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = createCircleButton(30);
                feedback[i][j] = createCircleButton(15);
            }
        }
        
        for (int i = 0; i < COLS; i++) {
            secretCode[i] = createCircleButton(30);
            secretCode[i].setVisible(false);
        }
        
        checkButton = new JButton("Check");
        checkButton.setEnabled(false);
        checkButton.setBackground(new Color(142, 229, 214));
        checkButton.setForeground(Color.BLACK);
        checkButton.setFocusPainted(false);
        checkButton.addActionListener(e -> checkGuess());
        
        newGameButton = new JButton("Start new game"); 
        newGameButton.setBackground(new Color(220, 53, 69));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(e -> startNewGame());
        
        levelButtons = new JRadioButton[3];
        ButtonGroup levelGroup = new ButtonGroup();
        String[] levels = {"Level 1 (Easy)", "Level 2 (Medium)", "Level 3 (Hard)"};
        
        for (int i = 0; i < 3; i++) {
            levelButtons[i] = new JRadioButton(levels[i]);
            levelButtons[i].setBackground(new Color(142, 229, 214));
            levelButtons[i].setForeground(Color.BLACK);
            final int level = i + 1;
            levelButtons[i].addActionListener(e -> {
                difficulty = level;
                updateDescriptionLabel();
            });
            levelGroup.add(levelButtons[i]);
        }
        levelButtons[0].setSelected(true);

        descLabel = new JLabel("Easy Level: \"Guess the code with 4 colors and no repeated colors.\"");
        descLabel.setForeground(new Color(220, 53, 69));
    }
    
    private void layoutComponents() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(new Color(142, 229, 214));
        controlPanel.add(newGameButton);
        for (JRadioButton levelButton : levelButtons) {
            controlPanel.add(levelButton);
        }

        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(new Color(142, 229, 214));
        GridBagConstraints gbc = new GridBagConstraints();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                gbc.gridx = j;
                gbc.gridy = i;
                gbc.insets = new Insets(2, 2, 2, 2);
                gamePanel.add(board[i][j], gbc);
            }

            gbc.gridx = COLS + 1;
            gbc.gridwidth = 4;
            JPanel feedbackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            feedbackPanel.setBackground(new Color(142, 229, 214));
            for (int j = 0; j < COLS; j++) {
                feedbackPanel.add(feedback[i][j]);
            }
            gamePanel.add(feedbackPanel, gbc);
            gbc.gridwidth = 1;
        }

        gbc.gridx = COLS;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gamePanel.add(checkButton, gbc);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(142, 229, 214));
        bottomPanel.add(descLabel);

        add(controlPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JButton createCircleButton(int size) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
            }
        };
        button.setPreferredSize(new Dimension(size, size));
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }
    
    private void startNewGame() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j].setBackground(Color.WHITE);
                feedback[i][j].setBackground(Color.WHITE);
            }
        }
        
        currentRow = 0; // Update 1
        checkButton.setEnabled(true);
        
        int numColors = difficulty == 1 ? 4 : (difficulty == 2 ? 6 : 8);
        boolean allowRepetition = difficulty > 1;
        
        currentSecret = generateSecretCode(numColors, allowRepetition);
        
        enableRow(currentRow);
    }
    
    private Color[] generateSecretCode(int numColors, boolean allowRepetition) {
        Color[] code = new Color[COLS];
        ArrayList<Color> availableColors = new ArrayList<>(Arrays.asList(Arrays.copyOf(COLORS, numColors)));
        
        for (int i = 0; i < COLS; i++) {
            if (allowRepetition) {
                code[i] = COLORS[new Random().nextInt(numColors)];
            } else {
                int index = new Random().nextInt(availableColors.size());
                code[i] = availableColors.remove(index);
            }
        }
        return code;
    }
    
    private void enableRow(int row) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j].setEnabled(i == row);
                if (board[i][j].isEnabled()) {
                    final int col = j;
                    board[i][j].addActionListener(e -> selectColor(row, col));
                }
            }
        }
        updateCheckButtonState(row); // Update 5
    }
    
    private void selectColor(int row, int col) {
        JButton button = board[row][col];
        Color currentColor = button.getBackground();
        int currentIndex = -1;
        
        for (int i = 0; i < COLORS.length; i++) {
            if (COLORS[i].equals(currentColor)) {
                currentIndex = i;
                break;
            }
        }
        
        int numColors = difficulty == 1 ? 4 : (difficulty == 2 ? 6 : 8);
        int nextIndex = (currentIndex + 1) % numColors;
        if (currentColor.equals(Color.WHITE)) {
            nextIndex = 0;
        }
        
        button.setBackground(COLORS[nextIndex]);
        updateCheckButtonState(row); // Update 4
    }
    
    private void checkGuess() {
        Color[] guess = new Color[COLS];
        for (int i = 0; i < COLS; i++) {
            guess[i] = board[currentRow][i].getBackground();
        }
        
        if (!isValidGuess(guess)) {
            JOptionPane.showMessageDialog(this, "Invalid guess! Please fill all positions.");
            return;
        }
        
        int[] result = calculateFeedback(guess);
        
        for (int i = 0; i < result[0]; i++) {
            feedback[currentRow][i].setBackground(Color.BLACK); 
        }
        for (int i = result[0]; i < result[0] + result[1]; i++) {
            feedback[currentRow][i].setBackground(Color.WHITE); 
        }
        
        if (result[0] == COLS) {
            JOptionPane.showMessageDialog(this, "Congratulations! You've won!");
            checkButton.setEnabled(false);
            return;
        }
        
        currentRow++; // Update 2
        if (currentRow >= ROWS) { // Update 2
            JOptionPane.showMessageDialog(this, "Game Over! You've run out of tries.");
            checkButton.setEnabled(false);
            return;
        }
        
        enableRow(currentRow);
    }
    
    private boolean isValidGuess(Color[] guess) {
        for (Color color : guess) {
            if (color.equals(Color.WHITE)) {
                return false;
            }
        }
        
        if (difficulty == 1) {
            Set<Color> uniqueColors = new HashSet<>(Arrays.asList(guess));
            return uniqueColors.size() == COLS;
        }
        
        return true;
    }
    
    private int[] calculateFeedback(Color[] guess) {
        int exactMatches = 0;
        int colorMatches = 0;
        
        boolean[] usedGuess = new boolean[COLS];
        boolean[] usedSecret = new boolean[COLS];
        
        for (int i = 0; i < COLS; i++) {
            if (guess[i].equals(currentSecret[i])) {
                exactMatches++;
                usedGuess[i] = true;
                usedSecret[i] = true;
            }
        }
        
        for (int i = 0; i < COLS; i++) {
            if (!usedGuess[i]) {
                for (int j = 0; j < COLS; j++) {
                    if (!usedSecret[j] && guess[i].equals(currentSecret[j])) {
                        colorMatches++;
                        usedSecret[j] = true;
                        break;
                    }
                }
            }
        }
        
        return new int[]{exactMatches, colorMatches};
    }

    private void updateDescriptionLabel() {
        switch (difficulty) {
            case 1:
                descLabel.setText("Easy Level: \"Guess the code with 4 colors and no repeated colors.\"");
                break;
            case 2:
                descLabel.setText("Medium Level: \"Crack the code with 6 colors where repetition is allowed.\"");
                break;
            case 3:
                descLabel.setText("Hard Level: \"Crack the code with 8 colors where repetition is allowed.\"");
                break;
        }
    }
    
    private void updateCheckButtonState(int row) { // Update 3
        int coloredButtons = 0;
        for (int j = 0; j < COLS; j++) {
            if (!board[row][j].getBackground().equals(Color.WHITE)) {
                coloredButtons++;
            }
        }
        checkButton.setEnabled(coloredButtons == COLS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MasterMindGame().setVisible(true);
        });
    }
}

