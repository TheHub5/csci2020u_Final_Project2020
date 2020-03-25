package BattleShip;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.Scene;
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
        group = new Group();
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
                    c.setFill(Color.TRANSPARENT);
                    c.setStroke(Color.BLACK);
                }
            }
        });

        lockIn.setOnAction(e -> {
            try {
                if (shipsToPlace > 0 && gameConn.connThread.socket.isConnected()){
                    messages.appendText("Place all five ships.\n");
                }
                if (shipsToPlace == 0 && gameConn.connThread.socket.isConnected()) {
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

        group.getChildren().addAll(playerBoard.playerGrid, lockIn, resetShips);
        playerBoard.playerGrid.setLayoutX(0);
        playerBoard.playerGrid.setLayoutY(350);
        return group;
    }

    public Board getBoard(){return playerBoard;}

    public void endScreen(boolean won, boolean disc) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        GridPane grid = new GridPane();
        Text gameOverMessage = new Text();
        Text discM = new Text();
        gameOverMessage.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
        discM.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));

        BackgroundImage wonI = new BackgroundImage(new Image("images/Won.png",400,200,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        BackgroundImage lostI = new BackgroundImage(new Image("images/Lost.jpg",400,200,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        if (won && !disc){
            gameOverMessage.setText("You Won");
            grid.setBackground(new Background(wonI));
            grid.add(gameOverMessage, 15, 1, 1, 1);
        }
        else if (!disc) {
            gameOverMessage.setText("You Lost");
            grid.setBackground(new Background(lostI));
            grid.add(gameOverMessage, 15, 1, 1, 1);
        }
        else {
            grid.add(discM, 15, 1, 1, 1);
            discM.setText("Opponent Disconnected\n");
        }

        Button restart = new Button("Play Again");
        restart.setScaleX(1.2);
        restart.setScaleY(1.2);
        Button exit = new Button("Exit");
        exit.setScaleX(1.2);
        exit.setScaleY(1.2);
//        Button back = new Button("RETURN TO MAIN MENU");
//        back.setPrefWidth(150);



        grid.add(exit, 15, 5, 1, 1);
        Scene popupScene = new Scene(grid,300,200);
        popup.setScene(popupScene);
        popup.show();

        exit.setOnAction(e->{
            Platform.exit();
        });

        restart.setOnAction(e1->{

        });
    }
}
