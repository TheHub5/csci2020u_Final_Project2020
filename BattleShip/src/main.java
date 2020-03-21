import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private int chatPort, gamePort;
    private String ip = "localhost";
    private TextArea messages = new TextArea();
    private TextField input = new TextField();
    private Text warning = new Text();
    private boolean isServer;
    private Battleship game;
    private boolean[][] vertical = null;

    public Scene start, joinServer, gameScene, createServer;
    public Group groupGame;

    @Override
    public void start(Stage stage) {
        GridPane gridStart = new GridPane();
        gridStart.setHgap(10);
        gridStart.setVgap(70);
        start = new Scene(gridStart, 400, 400);
        stage.setScene(start);

        BackgroundImage myBI= new BackgroundImage(new Image("Images/image1.jpg",400,400,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        stage.setResizable(false);

        gridStart.setBackground(new Background(myBI));

        Button create = new Button("Create Server");
        Button join = new Button("Join Server");

        create.setStyle("-fx-background-color: black");
        create.setStyle("-fx-font-size: 2em; ");
        join.setStyle("-fx-font-size: 2em; ");

        gridStart.add(create, 12, 2, 1, 1);
        gridStart.add(join, 12, 3, 1, 1);

        GridPane gridJoin = new GridPane();
        gridJoin.setHgap(10);
        gridJoin.setVgap(10);
        TextField ipF = new TextField();
        TextField pF1 = new TextField();
        Button JS = new Button("Join Server");
        Text text = new Text("Enter IP address:");
        Text text2 = new Text("Enter server Port:");
        Button back = new Button("<-Return");
        Button back1 = new Button("<-Return");

        gridJoin.add(ipF, 3, 12, 1, 1);
        gridJoin.add(text, 2, 12, 1, 1);
        gridJoin.add(text2, 2, 13, 1, 1);
        gridJoin.add(pF1, 3, 13, 1, 1);
        gridJoin.add(JS, 3, 14, 1, 1);
        gridJoin.add(back, 1, 19, 1, 1);

        GridPane gridCreate = new GridPane();
        gridCreate.setHgap(10);
        gridCreate.setVgap(10);
        TextField pF = new TextField();
        Button CS = new Button("Create Server");
        Text text1 = new Text("Enter Port:");

        gridCreate.add(pF, 3, 12, 1, 1);
        gridCreate.add(CS, 3, 13, 1, 1);
        gridCreate.add(text1, 2, 12, 1, 1);
        gridCreate.add(back1, 1, 21, 1, 1);

        back.setOnAction(e -> {
            stage.setScene(start);
        });
        back1.setOnAction(e -> {
            stage.setScene(start);
        });

        createServer = new Scene(gridCreate, 400, 300);
        joinServer = new Scene(gridJoin, 400, 300);

        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
        text1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
        text2.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));

        gridJoin.setBackground(new Background(myBI));
        gridCreate.setBackground(new Background(myBI));

        messages.setPrefHeight(620);
        messages.setLayoutX(10);
        messages.setEditable(false);
        input.setPrefWidth(480);
        input.setLayoutX(10);
        input.setLayoutY(635);
        warning.setX(400);
        warning.setY(350);

        create.setOnAction(e -> {
            isServer = true;
            CS.setOnAction(e1 -> {
                chatPort = Integer.valueOf(pF.getText());
                gamePort = Integer.valueOf(pF.getText()) - 1;
                if (chatPort > 1 && chatPort < 65534) {
                    NetworkConnection chatConnection = createChatServer(chatPort);
                    NetworkConnection gameConnection = createGameServer(gamePort);
                    try {
                        game(gameConnection, stage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        chatConnection.startConnection();
                        gameConnection.startConnection();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    input.setOnAction(e2 -> {
                        String message = "Player1: ";
                        message += input.getText();
                        input.clear();

                        messages.appendText(message + "\n");

                        try {
                            chatConnection.send(message);
                        } catch (Exception ex) {
                            messages.appendText("Failed to send\n");
                        }
                    });
                } else {
                    System.out.println("Invalid Port!!!");
                }
            });
            stage.setScene(createServer);
        });

        join.setOnAction(e -> {
            isServer = false;
            JS.setOnAction(e1 -> {
                chatPort = Integer.valueOf(pF1.getText());
                gamePort = Integer.valueOf(pF1.getText()) - 1;
                ip = ipF.getText();
                NetworkConnection chatConnection = createChatClient(ip, chatPort);
                NetworkConnection gameConnection = createGameClient(ip, gamePort);
                try {
                    game(gameConnection, stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    chatConnection.startConnection();
                    gameConnection.startConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                input.setOnAction(e2 -> {
                    String message = "Player2: ";
                    message += input.getText();
                    input.clear();

                    messages.appendText(message + "\n");

                    try {
                        chatConnection.send(message);
                    } catch (Exception ex) {
                        messages.appendText("Failed to send\n");
                    }
                });
            });
            stage.setScene(joinServer);
        });
        stage.show();
    }

    private Server createChatServer(int port) {
        return new Server(port, data-> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createChatClient(String ip, int port) {
        return new Client(ip, port, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Server createGameServer(int port) {
        return new Server(port, data-> {
            Platform.runLater(() -> {
                if (data instanceof boolean[][]){
                    vertical = (boolean[][]) data;
                }
                if (data instanceof int[][] && vertical != null){
                    int[][] gridLayout = (int[][]) data;

                    game.enemyBoard = new Board(gridLayout, vertical, event ->{
                        if (game.isLocked && game.myTurn){
                            Cell cell = (Cell) event.getSource();
                            if (cell.wasShot == false) {
                                cell.shoot();
                                game.myTurn = false;
                            }
                        }
                    });

                    for (int i = 0; i < 10; i++){
                        for (int j = 0; j < 10; j++){
                            System.out.print(game.enemyBoard.getCell(j, i).ship.type);
                        }
                        System.out.println();
                    }

                    System.out.println();
                    System.out.println();

                    for (int i = 0; i < 10; i++){
                        for (int j = 0; j < 10; j++){
                            System.out.print(game.enemyBoard.getCell(j, i).ship.vertical + " ");
                        }
                        System.out.println();
                    }
                }
            });
        });
    }

    private Client createGameClient(String ip, int port) {
        return new Client(ip, port, data -> {
            Platform.runLater(() -> {
                if (data instanceof boolean[][]){
                    vertical = (boolean[][]) data;
                }
                if (data instanceof int[][] && vertical != null){
                    int[][] gridLayout = (int[][]) data;

                    game.enemyBoard = new Board(gridLayout, vertical, event ->{
                        if (game.isLocked && game.myTurn){
                            Cell cell = (Cell) event.getSource();
                            if (cell.wasShot == false) {
                                cell.shoot();
                                game.myTurn = false;
                            }
                        }
                    });

                    for (int i = 0; i < 10; i++){
                        for (int j = 0; j < 10; j++){
                            System.out.print(game.enemyBoard.getCell(j, i).ship.type);
                        }
                        System.out.println();
                    }

                    System.out.println();
                    System.out.println();

                    for (int i = 0; i < 10; i++){
                        for (int j = 0; j < 10; j++){
                            System.out.print(game.enemyBoard.getCell(j, i).ship.vertical + " ");
                        }
                        System.out.println();
                    }
                }
            });
        });
    }

    public void game(NetworkConnection conn, Stage stage) throws Exception {
        //Instantiation of Battleship Game here
        game = new Battleship(isServer, conn);

        Group gameBoard = game.playGame();
        gameBoard.setLayoutX(500);
        gameBoard.setLayoutY(0);

        warning.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));

        groupGame = new Group();
        groupGame.getChildren().addAll(input, messages, gameBoard);
        gameScene = new Scene(groupGame, 1000, 700);
        stage.setScene(gameScene);
    }
}