import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Battleship {

    public Board playerBoard;
    public Board enemyBoard;
    private int shipsToPlace = 5;
    public boolean myTurn = false;
    private boolean isServer;
    public NetworkConnection connection;
    public boolean isLocked = false;
    public Group group;

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
            if (shipsToPlace == 0 && connection.connThread.socket.isConnected()){
                isLocked = true;
                lockIn.setVisible(false);
                myTurn = true;

                int[][] gridLayout = new int[10][10];
                boolean[][] vertical = new boolean[10][10];
                for (int i = 0; i < 10; i++){
                    for (int j = 0; j < 10; j++){
                        Cell c = playerBoard.getCell(j, i);
                        if (c.ship != null){
                            gridLayout[j][i] = playerBoard.getCell(j, i).ship.type;
                            vertical[j][i] = playerBoard.getCell(j, i).ship.vertical;
                        } else {
                            gridLayout[j][i] = 0;
                            vertical[j][i] = false;
                        }
                    }
                }
                try {
                    connection.send(vertical);
                    connection.send(gridLayout);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        group = new Group(playerBoard.playerGrid, lockIn);
        playerBoard.playerGrid.setLayoutX(0);
        playerBoard.playerGrid.setLayoutY(350);
        return group;
    }

    public Board getBoard(){return playerBoard;}
}
