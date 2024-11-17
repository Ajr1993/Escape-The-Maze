package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EscapeGame extends Application {
    private static final int ROWS = 5;
    private static final int COLUMNS = 5;
    private char[][] grid = new char[ROWS][COLUMNS];
    private int playerRow = 0;
    private int playerCol = 1;
    private int[] enemyRow;
    private int[] enemyCol;
    private int points = 0;
    private int lives = 3;
    private GridPane gridPane = new GridPane();
    private Random random = new Random();
    private Label livesLabel = new Label("Lives: " + lives);
    private Label pointsLabel = new Label("Points: " + points);
    private Label contact = new Label("You have hit an enemy");
    private Label gameOver = new Label();
    private Label winning = new Label();
    private Label instructions = new Label();
    private TextField playAgain = new TextField("Do you want to play again? type Y for yes and N for no ");
    List<int[]> enemies = new ArrayList<>();
    private Label userInput = new Label();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Escape The Maze");
        initialiseGrid();
        initialisePlayer();
        initialiseFood();
        initialiseEnemies(enemies);
        enemyAiLogic(enemies);
        updateGridDisplay();


        userInput();

        // VBox to hold grid and labels
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20px;");

        // Add labels (instructions, points, lives) to the VBox
        instructions.setText("Welcome to Escape The Maze! Use WASD to move.");
        instructions.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center;");
        root.getChildren().add(instructions);
        root.getChildren().add(pointsLabel);
        root.getChildren().add(livesLabel);
        root.getChildren().add(contact);
        root.getChildren().add(gameOver);
        root.getChildren().add(winning);
        root.getChildren().add(userInput);
        root.getChildren().add(playAgain);

        playAgain.setVisible(false);

        // Add the grid to the VBox
        root.getChildren().add(gridPane);
        Scene scene = new Scene(root, 500, 400);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        stage.setScene(scene);
        stage.show();
    }

    private void initialiseGrid() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                grid[i][j] = ',';
                grid[ROWS - 1][COLUMNS - 1] = 'X';
            }
        }
    }

    private void initialisePlayer() {
        grid[playerRow][playerCol] = 'P';

    }

    private void initialiseFood() {
        for (int i = 0; i < 4; i++) {
            int foodRow = random.nextInt(ROWS);
            int foodCol = random.nextInt(COLUMNS);
            if (grid[foodRow][foodCol] == ',') {
                grid[foodRow][foodCol] = 'F';
            }
        }
    }

    private void initialiseEnemies(List<int[]> enemies) {
        boolean isTaken = false;
        while (!isTaken) {
            int enemyRow = random.nextInt(ROWS);
            int enemyCol = random.nextInt(COLUMNS);

            if (grid[enemyRow][enemyCol] == ',' && playerRow != enemyRow && playerCol != enemyCol) {
                grid[enemyRow][enemyCol] = 'E';
                enemies.add(new int[]{enemyRow, enemyCol});
                isTaken = true;
            }

        }


    }

    public void handleKeyPress(KeyCode keyCode) {
        grid[playerRow][playerCol] = ',';

        switch (keyCode) {
            case A:
                if (playerCol > 0) playerCol--;
                break;
            case D:
                if (playerCol < COLUMNS - 1) playerCol++;
                break;
            case S:
                if (playerRow < ROWS - 1) playerRow++;
                break;
            case W:
                if (playerRow > 0) playerRow--;
                break;
            default:
                break;
        }
        gameLogic();
        enemyAiLogic(enemies);
        grid[playerRow][playerCol] = 'P';
        updateGridDisplay();
    }

    public void gameLogic() {
        // Check if the player hits an enemy
        if (grid[playerRow][playerCol] == 'E') {
            lives--;   // Deduct life
            points -= 20;  // Deduct points for hitting an enemy
            contact.setText("You hit an enemy! Lives: " + lives + ", Points: " + points);
            contact.setStyle("-fx-font-weight: bold");
            grid[playerRow][playerCol] = ',';
            playerCol = 0;
            playerRow = 0;
            grid[playerRow][playerCol] = 'P';
            updateGridDisplay();


            if (lives <= 0) {
                gameOver.setText("Game over! You ran out of lives.");
                gameOver.setStyle("-fx-font-weight:bold;");
                return;
            }
        }

        // Check if the player collects food
        if (grid[playerRow][playerCol] == 'F') {
            points += 30;  // Add points for collecting food
            contact.setText("You collected food! Points: " + points);
            contact.setStyle("-fx-font-weight:bold");

            // Remove food from the grid after collecting it
            grid[playerRow][playerCol] = ',';  // Set the position to empty
        }

        // Check if the player has won (reached bottom-right corner)
        if (playerRow == ROWS - 1 && playerCol == COLUMNS - 1) {
            winning.setText("Congratulations, you won!");
            winning.setStyle("-fx-font-weight:bold");
            userInput.setText("Do you want to play again. Type yes or no");
            userInput.setStyle("-fx-font-weight:bold;");
            playAgain.setVisible(true);
            playAgain.setDisable(false);

        }
        // Update the display labels for points and lives
        pointsLabel.setText("Points: " + points);
        pointsLabel.setStyle("-fx-font-weight: bold;");

        livesLabel.setText("Lives: " + lives);
        livesLabel.setStyle("-fx-font-weight: bold;");

    }

    public void updateGridDisplay() {
        gridPane.getChildren().clear();  // Clear the grid

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Label cell = new Label();
                cell.setMinSize(50, 50);
                cell.setStyle("-fx-background-color: red; -fx-font- weight: bold;");
                cell.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-text-alignment: center;");
                if (grid[i][j] == 'P') {
                    cell.setStyle("-fx-background-color: green; -fx-font-weight: bold;");
                    cell.setText("P");
                } else if (grid[i][j] == 'E') {
                    cell.setText("E");
                    cell.setStyle("-fx-background-color: red; -fx-font-weight: bold;");
                } else if (grid[i][j] == 'F') {
                    cell.setStyle("-fx-background-color: blue; -fx-font-weight: bold;");
                    cell.setText("F");
                } else if (grid[i][j] == 'X') {
                    cell.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
                    cell.setText("X");

                }

                gridPane.add(cell, j, i);  // Add cell to the grid
            }
        }
    }

    public void enemyAiLogic(List<int[]> enemies) {
        for (int i = 0; i < enemies.size(); i++) {
            int[] enemyPosition = enemies.get(i);
            int enemyRow = enemyPosition[0];
            int enemyCol = enemyPosition[1];
            grid[enemyRow][enemyCol] = ',';

            double chance = Math.random();
            if (chance < 0.3) {
                int movement = (int) (Math.random() * 4);
                switch (movement) {
                    case 0:  // Move down
                        if (enemyRow < ROWS - 1) enemyRow++;
                        break;
                    case 1:  // Move up
                        if (enemyRow > 0) enemyRow--;
                        break;
                    case 2:  // Move left
                        if (enemyCol > 0) enemyCol--;
                        break;
                    case 3:  // Move right
                        if (enemyCol < COLUMNS - 1) enemyCol++;
                        break;
                }
            } else {  // Move towards the player
                if (enemyRow > playerRow) enemyRow--;
                else if (enemyRow < playerRow) enemyRow++;
                if (enemyCol > playerCol) enemyCol--;
                else if (enemyCol < playerCol) enemyCol++;
            }

            // Update the grid with the new enemy position
            grid[enemyRow][enemyCol] = 'E';
            enemies.set(i, new int[]{enemyRow, enemyCol});  // Update enemy's position in the list
        }
    }


    public void userInput() {
        playAgain.setOnAction(e -> {
            if (playAgain.getText().equalsIgnoreCase("Yes")) {
                userInput.setText("PLayer chooses to play again");
                playAgain.setVisible(false);
                playAgain.clear();
                playerRow = 0;
                playerCol = 1;
                lives = 3;
                points = 0;
                gameOver.setText(""); // clear game over message
                winning.setText(""); // clear winning message

                initialiseGrid();
                initialisePlayer();
                initialiseFood();
                initialiseEnemies(enemies);
                enemyAiLogic(enemies);
                updateGridDisplay();

            } else if (playAgain.getText().equalsIgnoreCase("No")) {
                userInput.setText("Thank you for playing my game");
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}


