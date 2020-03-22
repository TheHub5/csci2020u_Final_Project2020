package BattleShip;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Battleship {

    public Board playerBoard;
    public Board enemyBoard;
    private int shipsToPlace = 5;
    public boolean myTurn = false;
    public NetworkConnection gameConn, chatConn;
    public TextArea messages;
    public boolean isLocked = false;
    public Group group;

    public Battleship(NetworkConnection gameConn, NetworkConnection chatConn, TextArea messages){
        this.gameConn = gameConn;
        this.chatConn = chatConn;
        this.messages = messages;
    }

    public Group playGame() throws Exception {
        Button lockIn = new Button("Lock In");
        Button resetShips = new Button("Reset Ships");
        resetShips.setLayoutX(315);
        resetShips.setLayoutY(595);
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

        resetShips.setOnAction(e -> {
            shipsToPlace = 5;
            for (int i = 0; i < 10; i++){
                for (int j = 0; j < 10; j++){
                    Cell c = playerBoard.getCell(j, i);
                    c.ship = null;
                    c.setFill(Color.TRANSPARENT);
                    c.setStroke(Color.BLACK);
                }
            }
        });

        lockIn.setOnAction(e -> {
            try {
                if (shipsToPlace == 0 && gameConn.connThread.socket.isConnected()){
                    isLocked = true;
                    lockIn.setVisible(false);
                    resetShips.setVisible(false);
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
                        gameConn.send(vertical);
                        gameConn.send(gridLayout);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                messages.appendText("OPPONENT HAS NOT CONNECTED\n");
            }
        });

        group = new Group(playerBoard.playerGrid, lockIn, resetShips);
        playerBoard.playerGrid.setLayoutX(0);
        playerBoard.playerGrid.setLayoutY(350);
        return group;
    }

    public void gameOver() throws Exception{
    //lock board and possibly display message
    }

    public void restartGame() throws Exception{
    //reset board and game variables here
    }

    public Board getBoard(){return playerBoard;}
}
