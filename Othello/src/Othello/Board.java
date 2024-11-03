package assignment2;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private String name;
    private static final int BOARD_SIZE = 8;
    private Position[][] boardPieces = new Position[BOARD_SIZE][BOARD_SIZE];
    Scanner sc = new Scanner(System.in);


    // Constructor for starting a new game
    public Board() {
        initializeBoardPieces();
    }

    // Constructor for a saved game
    public Board(String saved_file){
        this.name = saved_file;
        initializeBoardPieces();
    }

    // Method to draw the board using ASCII characters
    public void drawBoard() {
        // Print column numbers
        System.out.print("  ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.printf(" %2d ", i);
        }
        System.out.println();

        // Print the board
        for (int i = 0; i < boardPieces.length; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < boardPieces.length; j++) {
                System.out.print("| " + boardPieces[i][j].getPiece() + " ");
            }
            System.out.println("|");
            System.out.print("  ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print("+---");
            }
            System.out.println("+");
        }
    }

    // Method to initialize the board pieces
    private void initializeBoardPieces() {
        // Initialize an empty board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardPieces[i][j] = new PlayablePosition();
            }
        }
    }

    // Initialize the board based on the user's choice
    public void initializeBoard(int startOption) {

        // Set the unplayable positions
        for(int j = 2; j <= 5; j++){
            boardPieces[0][j] = new UnplayablePosition();
        }
        switch (startOption) {
            case 1:
                initializeFourByFour();
                break;
            case 2:
                System.out.println("Which offset position? 1, 2, 3, or 4?");
                int offsetPosition = sc.nextInt();
                initializeOffset(offsetPosition);
                break;
            default:
                System.out.println("Invalid choice. Please choose a valid option.");
                break;
        }
    }

    // Initialize the board for the specific 4x4 setup
    public void initializeFourByFour() {
        char[][] pieces = {
                { Position.WHITE, Position.WHITE, Position.BLACK, Position.BLACK },
                { Position.WHITE, Position.WHITE, Position.BLACK, Position.BLACK },
                { Position.BLACK, Position.BLACK, Position.WHITE, Position.WHITE },
                { Position.BLACK, Position.BLACK, Position.WHITE, Position.WHITE }
        };

        for (int i = 2; i <= 5; i++) {
            for (int j = 2; j <= 5; j++) {
                boardPieces[i][j].setPiece(pieces[i - 2][j - 2]);
            }
        }
    }

    // Initialize the board for the specific offset setup
    public void initializeOffset(int option) {
        switch (option) {
            case 1:
                boardPieces[2][2].setPiece(Position.WHITE);
                boardPieces[2][3].setPiece(Position.BLACK);
                boardPieces[3][2].setPiece(Position.BLACK);
                boardPieces[3][3].setPiece(Position.WHITE);
                break;
            case 2:
                boardPieces[2][4].setPiece(Position.WHITE);
                boardPieces[2][5].setPiece(Position.BLACK);
                boardPieces[3][4].setPiece(Position.BLACK);
                boardPieces[3][5].setPiece(Position.WHITE);
                break;
            case 3:
                boardPieces[4][2].setPiece(Position.WHITE);
                boardPieces[4][3].setPiece(Position.BLACK);
                boardPieces[5][2].setPiece(Position.BLACK);
                boardPieces[5][3].setPiece(Position.WHITE);
                break;
            case 4:
                boardPieces[4][4].setPiece(Position.WHITE);
                boardPieces[4][5].setPiece(Position.BLACK);
                boardPieces[5][4].setPiece(Position.BLACK);
                boardPieces[5][5].setPiece(Position.WHITE);
                break;
            default:
                System.out.println("Invalid choice. Please choose a valid option.");
                break;
        }
    }

    // Method to take a turn and update the board
    public void takeTurn(Player currentPlayer){
        boolean validMove = false;

        while(!validMove){
            // Prompt for row and column
            System.out.println(currentPlayer.getName() + "'s turn. Enter row and column (e.g., 3 4):");
            int row = sc.nextInt();
            int col = sc.nextInt();

            // Validate the move
            if (!canPlay(row, col) || !validateMove(row, col, currentPlayer)) {
                System.out.println("Invalid move. Try again!");
            } else {
                // Place the piece and flip the opponent's pieces
                setPiece(row, col, currentPlayer.getColor());
                flipPieces(row, col, currentPlayer.getColor());
                drawBoard(); // Redraw the board after making the move
                validMove = true;
            }
        }
    }

    // Method to validate the player's move
    public boolean validateMove(int row, int col, Player player) {
        // Determine the opponent's color based on the current player's color
        char oppCol = (player.getColor() == Position.BLACK) ? Position.WHITE : Position.BLACK;

        // Check if the selected cell is empty; if not, the move is invalid
        if (boardPieces[row][col].getPiece() != Position.EMPTY) {
            return false;
        }

        // Define all eight possible directions
        int[][] directions = {
                {-1,  0}, // Up
                {-1,  1}, // Up-Right
                { 0,  1}, // Right
                { 1,  1}, // Down-Right
                { 1,  0}, // Down
                { 1, -1}, // Down-Left
                { 0, -1}, // Left
                {-1, -1}  // Up-Left
        };

        // Iterate through each direction to check if the move is valid
        for (int[] direction : directions) {
            int tempRow = row;
            int tempCol = col;
            boolean foundOpponent = false;

            while (true) {
                // Move one step in the current direction
                tempRow += direction[0];
                tempCol += direction[1];

                // Check if the new position is out of bounds
                if (tempRow < 0 || tempRow >= BOARD_SIZE || tempCol < 0 || tempCol >= BOARD_SIZE) {
                    break;
                }

                // Get the piece at the new position
                char currentPiece = boardPieces[tempRow][tempCol].getPiece();

                // If the piece belongs to the opponent, set the flag to true
                if (currentPiece == oppCol) {
                    foundOpponent = true;
                }
                // If the piece belongs to the current player and opponent's pieces were found
                else if (currentPiece == player.getColor()) {
                    if (foundOpponent) {
                        // Valid move: current player's piece encloses opponent's pieces
                        return true;
                    }
                    break;
                }
                // If the piece is empty or any other case
                else {
                    break;
                }
            }
        }

        // Return false if no valid move was found
        return false;
    }

    // Method to flip the opponent's pieces
    public void flipPieces(int row, int col, char playerColor) {
        // Determine the opponent's color based on the current player's color
        char opponentColor = (playerColor == Position.BLACK) ? Position.WHITE : Position.BLACK;

        // Define all eight possible directions (up, down, left, right, and diagonals)
        int[][] directions = {
                {-1,  0}, // Up
                {-1,  1}, // Up-Right
                { 0,  1}, // Right
                { 1,  1}, // Down-Right
                { 1,  0}, // Down
                { 1, -1}, // Down-Left
                { 0, -1}, // Left
                {-1, -1}  // Up-Left
        };

        // Iterate through each direction to flip pieces
        for (int[] direction : directions) {
            int tempRow = row;
            int tempCol = col;
            List<int[]> piecesToFlip = new ArrayList<>();

            while (true) {
                // Move one step in the current direction
                tempRow += direction[0];
                tempCol += direction[1];

                // Check if the new position is out of bounds
                if (tempRow < 0 || tempRow >= BOARD_SIZE || tempCol < 0 || tempCol >= BOARD_SIZE) {
                    break;
                }

                // Get the piece at the new position
                char currentPiece = boardPieces[tempRow][tempCol].getPiece();

                // If the piece belongs to the opponent, add to the list
                if (currentPiece == opponentColor) {
                    piecesToFlip.add(new int[]{tempRow, tempCol});
                }
                // If the piece belongs to the current player and opponent's pieces were found
                else if (currentPiece == playerColor) {
                    if (!piecesToFlip.isEmpty()) {
                        // Valid sequence: flip all the pieces in the list
                        for (int[] piece : piecesToFlip) {
                            boardPieces[piece[0]][piece[1]].setPiece(playerColor);
                        }
                    }
                    break;
                }
                // If the piece is empty or any other case, break the loop
                else {
                    break;
                }
            }
        }
    }

    // Method to count the pieces of players when the game is over
    public int countPieces(Player player) {
        int count = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (getPiece(i,j) == player.getColor()) {
                    count++;
                }
            }
        }
        return count;
    }

    public char getPiece(int row, int col) {
        return boardPieces[row][col].getPiece();
    }

    public void setPiece(int row, int col, char piece) {
        boardPieces[row][col].setPiece(piece);
    }

    public boolean canPlay(int row, int col) {
        return boardPieces[row][col].canPlay();
    }

}






