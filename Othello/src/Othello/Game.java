package assignment2;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Game {
    private static final int BOARD_SIZE = 8;
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    Scanner sc = new Scanner(System.in);

    public Game(Player p1, Player p2){
        this.player1 = p1;
        this.player2 = p2;
        this.board = new Board();
        this.currentPlayer = p1;
    }

    // Constructor for loading a saved game
    public Game(String save_file) {
        this.player1 = new Player("", Position.BLACK); // Initialize with temporary values
        this.player2 = new Player("", Position.WHITE);
        this.board = new Board(save_file);
        loadGameState(save_file);
    }

     public void start(){
        System.out.println("Welcome to Othello!");
        System.out.println("Please enter you choice among the following options.");
        System.out.println("1. Quit");
        System.out.println("2. Load a Game");
        System.out.println("3. Start a new Game");

        int option = sc.nextInt();
        switch (option){
            case 1:
                System.out.println("You have successfully quited the game. See you next time! ");
                System.exit(0);
                break;
            case 2:
                System.out.println("Loading previous game...");
                System.out.println("Enter the name of the saved game file:");
                String saveFile = sc.next();
                Game loadedGame = new Game(saveFile);
                if (loadedGame.board != null) {
                    loadedGame.board.drawBoard();
                    loadedGame.play();
                } else {
                    System.out.println("Failed to load the game. Starting a new game.");
                    startNewGame();
                    play();
                }
                break;
            case 3:
                startNewGame();
                play();
                break;
        }
    }

    public void play(){
        while (true) {
            if (isGameOver()) {
                declareWinner();
                break;
            }
            // Prompt current player for an action
            System.out.println(currentPlayer.getName() + "'s turn. Choose an action: move, save, concede, forfeit");
            String action = sc.next();

            if (action.equalsIgnoreCase("move")) {
                board.takeTurn(currentPlayer);
                switchPlayer();
            } else if (action.equalsIgnoreCase("save")){
                save();
                break;
            } else if (action.equalsIgnoreCase("concede")){
                concedeGame(currentPlayer);
                break;
            } else if (action.equalsIgnoreCase("forfeit")){
                if (!checkValidMoves(currentPlayer)){
                    System.out.println(currentPlayer.getName() + " has no valid moves. Turn Forfeited.");
                    switchPlayer();
                    break;
                } else {
                    System.out.println("Cannot forfeit. Valid moves are available. Try again");
                }
            } else{
                System.out.println("Invalid action. Try again.");
            }
        }
    }

    // Method to save the game state
    private void save() {
        BufferedWriter bufferedWriter = null;

        try {
            // Ask the user for the file name
            System.out.print("Enter the name of the save file: ");
            sc.nextLine();
            String fileName = sc.nextLine();

            // Open the temporary file for writing
            String tempFileName = "temp_" + fileName;
            FileWriter fileWriter = new FileWriter(tempFileName);
            bufferedWriter = new BufferedWriter(fileWriter);

            // Write player names
            bufferedWriter.write(player1.getName());
            bufferedWriter.newLine();
            bufferedWriter.write(player2.getName());
            bufferedWriter.newLine();
            bufferedWriter.write(currentPlayer.getName());
            bufferedWriter.newLine();

            // Generate and write board state string
            for (int i = 0; i < BOARD_SIZE; i++) {
                StringBuilder boardState = new StringBuilder();
                for (int j = 0; j < BOARD_SIZE; j++) {
                    boardState.append(board.getPiece(i, j));
                }
                bufferedWriter.write(boardState.toString());
                bufferedWriter.newLine();
            }

            // Close the writer to ensure data is written
            bufferedWriter.close();
            bufferedWriter = null; // Prevent closing again in finally

            // Rename the temporary file to the actual save file
            java.io.File tempFile = new java.io.File(tempFileName);
            java.io.File saveFile = new java.io.File(fileName);
            if (!tempFile.renameTo(saveFile)) {
                System.out.println("Failed to rename temporary file to save file.");
            } else {
                // Notify player of success
                System.out.println("Game has been saved successfully.");
                System.out.println("The name of the file is: " + saveFile);
                System.out.println("See you next time!");
            }

        } catch (IOException e) {
            // Handle exceptions
            System.out.println("An error occurred while saving the game: " + e.getMessage());

        } finally {
            // Ensure the writer is closed
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    // Handle exception during closing
                    System.out.println("An error occurred while closing the file: " + e.getMessage());
                }
            }
        }
    }

    // Method to check if the player has valid moves
    public boolean checkValidMoves(Player player) {
        boolean hasValidMove = false;

        // Iterate through the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                // Check if the position can be played
                if (board.canPlay(row,col)) {
                    // Validate the move for the current player
                    if (board.validateMove(row, col, player)) {
                        hasValidMove = true;
                        // Break out of both loops as we found a valid move
                        return true;
                    }
                }
            }
        }
        return hasValidMove;
    }

    // Method to concede the game
    public void concedeGame(Player currentPlayer) {
        Player winner = (currentPlayer == player1) ? player2 : player1;
        System.out.println(currentPlayer.getName() + " has conceded. " + winner.getName() + " wins!");
        System.exit(0);
    }

    // Method to switch to next player
    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public void startNewGame(){
        System.out.println("Let's start the game!");
        // Ask user for the starting position choice
        System.out.println("Please enter 1 for a Four-by-Four starting position and 2 for an offset position");
        int choice = sc.nextInt();
        board.initializeBoard(choice);
        board.drawBoard();  // Draw the board after initialization
    }

    public void loadGameState(String save_file) {
        File file = new File(save_file);

        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found: " + save_file);
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            // Read player names
            String player1Name = scanner.nextLine().trim();
            String player2Name = scanner.nextLine().trim();
            String currentPlayerName = scanner.nextLine().trim();

            System.out.println("Current Player: " + currentPlayerName);

            // Set player names
            player1.setName(player1Name);
            player2.setName(player2Name);
            currentPlayer = currentPlayerName.equals(player1Name) ? player1 : player2;

            // Load the board state
            board = loadBoardState(scanner);
            System.out.println("Board state loaded successfully! Here is where you left off:");
        } catch (IOException | NoSuchElementException | StringIndexOutOfBoundsException e) {
            System.out.println("An error occurred while loading the game: " + e.getMessage());
        }
    }

    public static Board loadBoardState(Scanner scanner) {
        Board board = new Board();

        try {
            // Read and set the board state
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // Ensure the line has exactly BOARD_SIZE characters
                    if (line.length() < BOARD_SIZE) {
                        line = String.format("%-" + BOARD_SIZE + "s", line).replace(' ', Position.EMPTY);
                    }
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        char piece = line.charAt(j);
                        board.setPiece(i, j, piece);
                    }
                } else {
                    throw new IOException("Unexpected end of board state data.");
                }
            }
        } catch (IOException | NoSuchElementException | StringIndexOutOfBoundsException e) {
            System.out.println("An error occurred while loading the board state: " + e.getMessage());
            return null;
        }
        return board;
    }

    private boolean isGameOver() {
        return !checkValidMoves(player1) && !checkValidMoves(player2);
    }

    public void declareWinner() {
        int player1Score = board.countPieces(player1);
        int player2Score = board.countPieces(player2);

        System.out.println("Game over!");
        System.out.println(player1.getName() + " (Black): " + player1Score);
        System.out.println(player2.getName() + " (White): " + player2Score);

        if (player1Score > player2Score) {
            System.out.println(player1.getName() + " wins!");
        } else if (player2Score > player1Score) {
            System.out.println(player2.getName() + " wins!");
        } else {
            System.out.println("It's a tie!");
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        Player player1 = new Player("Bob", Position.BLACK);
        Player player2 = new Player("Dex", Position.WHITE);

        Game game = new Game(player1, player2);
        game.start();
    }


}