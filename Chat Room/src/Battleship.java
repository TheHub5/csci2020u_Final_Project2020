import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Battleship {

    public Board playerBoard;
    public Board enemyBoard;
    private int shipsToPlace = 5;
    private boolean myTurn = true;
    private boolean isServer;
    private NetworkConnection connection;
    private boolean isLocked = false;

    public Battleship(boolean isServer, NetworkConnection connection){
        this.connection = connection;
        this.isServer = isServer;
    }

    public Group playGame() throws Exception {
        Button lockIn = new Button("Lock In");
        lockIn.setLayoutX(315);
        lockIn.setLayoutY(635);

        playerBoard = new Board(event -> {
            if (shipsToPlace > 0){
                Cell cell = (Cell) event.getSource();
                if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y) && shipsToPlace > 0) {
                    shipsToPlace--;
                }
            }
        });

        lockIn.setOnAction(e -> {
            if (shipsToPlace == 0){
                isLocked = true;
                lockIn.setVisible(false);
            }
        });

        enemyBoard = new Board(event -> {
            if (isLocked && myTurn){
                Cell cell = (Cell) event.getSource();
                if (cell.wasShot == false) {
                    cell.shoot();
                    myTurn = false;
                }
            }
        });

        Group group = new Group(playerBoard, enemyBoard, lockIn);
        playerBoard.setLayoutX(0);
        playerBoard.setLayoutY(350);
        enemyBoard.setLayoutX(0);
        enemyBoard.setLayoutY(10);
        return group;
    }

    public Board getBoard(){return playerBoard;}
}
