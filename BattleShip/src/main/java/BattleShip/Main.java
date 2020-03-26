package BattleShip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javafx.scene.control.Slider;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private MediaPlayer mediaplayer;
    private long chatPort, gamePort;
    private String ip = "localhost";
    public static TextArea messages = new TextArea();
    private TextField input = new TextField();
    private static Text typing = new Text();
    private Text invalidPort = new Text();
    private Text invalid = new Text();
    private Battleship game;
    private boolean[][] vertical = null;
    private MenuBar menuBar = new MenuBar();
    private double volume = 0.02;

    public Scene start, joinServer, gameScene, createServer, EndScreen, settingsScene;
    public Group groupGame = new Group();

    @Override
    public void start(Stage stage) {
        String song = "src/main/resources/epic.mp3";
        String alert = "src/main/resources/alert.wav";
        Media musicfile = new Media (Objects.requireNonNull(getClass().getClassLoader().getResource("epic.mp3")).toExternalForm());
        Media start_alert = new Media(new File(alert).toURI().toString());
        MediaPlayer alertplayer = new MediaPlayer(start_alert);
        alertplayer.setVolume(0.2);
        mediaplayer = new MediaPlayer(musicfile);
        mediaplayer.setVolume(volume);
        mediaplayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaplayer.play();
            }
        });

        GridPane gridStart = new GridPane();
        gridStart.setHgap(10);
        gridStart.setVgap(70);
        start = new Scene(gridStart, 400, 400);
        stage.setScene(start);

        BackgroundImage myBI = new BackgroundImage(new Image("images/image1.jpg",400,400,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        stage.setResizable(false);

        gridStart.setBackground(new Background(myBI));

        Button create = new Button("Create Server");
        Button join = new Button("Join Server");

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
        gridJoin.add(invalid, 3, 11, 1, 1);

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
        gridCreate.add(invalidPort, 3, 11, 1, 1);

        back.setOnAction(e -> {
            stage.setScene(start);
        });
        back1.setOnAction(e -> {
            stage.setScene(start);
        });

        createServer = new Scene(gridCreate, 400, 320);
        joinServer = new Scene(gridJoin, 400, 320);

        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
        text1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
        text2.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
        typing.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
        invalidPort.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 9));
        invalid.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 9));

        gridJoin.setBackground(new Background(myBI));
        gridCreate.setBackground(new Background(myBI));

        messages.setPrefHeight(610);
        messages.setLayoutY(30);
        messages.setLayoutX(10);
        messages.setEditable(false);
        input.setPrefWidth(480);
        input.setLayoutX(10);
        input.setLayoutY(665);
        typing.setX(15);
        typing.setY(658);

        Group endScreen = new Group();
        EndScreen = new Scene(endScreen, 400, 400);

        Menu menuFile = new Menu("file");
        MenuItem toTxt = new MenuItem("Save log as .txt file");
        MenuItem SettingM = new MenuItem("Setting");
        MenuItem ExitM = new MenuItem("Exit");

        menuFile.getItems().addAll(SettingM,toTxt, ExitM);
        menuBar.getMenus().addAll(menuFile);

        ExitM.setOnAction(e -> {
            Platform.exit();
        });

        // Exports chat log to a .txt file
        toTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Creates a file name for exporting based on date and time
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
                Date date = new Date();
                String filename = "src/main/Logs/Log_" + dateFormat.format(date) + ".txt";
                try
                {
                    //Creates a print writer, writes to the file, and closes the writer
                    PrintWriter outputStream = new PrintWriter(filename);
                    outputStream.print(messages.getText());
                    outputStream.flush();
                    outputStream.close();
                    System.out.println("Export Complete");
                }
                catch (FileNotFoundException e)
                {
                    // Error handling
                    e.printStackTrace();
                }
            }
        });

        SettingM.setOnAction(e -> {
            Stage settingPopUp = new Stage();
            settingPopUp.setTitle("Settings");

            Text txt = new Text("Volume");
            txt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
            txt.setX(20);
            txt.setY(100);
            DecimalFormat df = new DecimalFormat("#.##");
            Text txt1 = new Text(df.format(volume));
            txt1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));
            txt1.setX(300);
            txt1.setY(100);
            Slider slider = new Slider();
            slider.setLayoutX(115);
            slider.setLayoutY(91);
            slider.setScaleX(1.4);
            slider.setScaleY(1.4);
            slider.setMin(0);
            slider.setMax(1);
            slider.setValue(volume);
            slider.setOnMouseDragged(e1 -> {
                volume = slider.getValue();
                mediaplayer.setVolume(volume);
                alertplayer.setVolume(volume);
                txt1.setText(df.format(volume));
                for(int y = 0; y < 10; y++){
                    for(int x = 0; x < 10; x++){
                        game.enemyBoard.getCell(x, y).mediaPlayer.setVolume(volume);
                        game.playerBoard.getCell(x, y).mediaPlayer.setVolume(volume);
                    }
                }
            });

            Group group = new Group(slider, txt, txt1);
            settingsScene = new Scene(group, 400, 400);
            settingPopUp.setScene(settingsScene);
            settingPopUp.show();
        });

        create.setOnAction(e -> {
            CS.setOnAction(e1 -> {
                try {
                    chatPort = Long.parseLong(pF.getText());
                    gamePort = Long.parseLong(pF.getText()) - 1;
                } catch (Exception ignored){}

                if (chatPort > 1 && chatPort < 65534) {
                    int port = (int) chatPort;
                    int port1 = (int) gamePort;
                    NetworkConnection chatConnection = createChatServer(port);
                    NetworkConnection gameConnection = createGameServer(port1);
                    mediaplayer.play();
                    messages.appendText("Entered room as Player 1" + "\n");
                    stage.setTitle("Battleship Player 1");
                    messages.appendText("Server running on port: " + chatPort + "\n" + "Waiting for Opponent to Connect....\n");

                    try {
                        game(gameConnection, chatConnection, stage);
                        chatConnection.startConnection();
                        gameConnection.startConnection();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    input.setOnKeyPressed(e2 -> {
                        try {
                            gameConnection.send(12);
                        } catch (Exception ex) {
                            input.setEditable(false);
                        }
                    });

                    input.setOnAction(e3 -> {
                        String message = "Player1: ";
                        message += input.getText();
                        input.clear();

                        if (!message.equals("Player1: ")) {
                            messages.appendText(message + "\n");

                            try {
                                chatConnection.send(message);
                            } catch (Exception ex) {
                                messages.appendText("Failed to send\n");
                            }
                        }
                    });
                } else {
                    invalidPort.setText("Invalid Port. Enter new Port.");
                }
            });
            stage.setScene(createServer);
        });

        join.setOnAction(e -> {
            JS.setOnAction(e1 -> {
                try {
                    chatPort = Long.parseLong(pF1.getText());
                    gamePort = Long.parseLong(pF1.getText()) - 1;
                    ip = ipF.getText();;
                } catch (Exception ignored){}

                if (chatPort > 1 && chatPort < 65534) {
                    int port = (int) chatPort;
                    int port1 = (int) gamePort;

                    NetworkConnection chatConnection = createChatClient(ip, port);
                    NetworkConnection gameConnection = createGameClient(ip, port1);
                    mediaplayer.play();

                    try {
                        game(gameConnection, chatConnection, stage);
                        chatConnection.startConnection();
                        gameConnection.startConnection();
                    } catch (Exception ex) {
                        invalid.setText("Invalid IP Address.");
                    }

                    try {
                        Thread.sleep(100);
                        if (chatConnection.connThread.socket.isConnected()) {
                            chatConnection.send("OPPONENT HAS CONNECTED!");
                            stage.setTitle("Battleship Player 2");
                            alertplayer.play();
                            messages.appendText("Entered room as Player 2" + "\n");
                            messages.appendText("Connected to: " + ip + ": " + chatPort + "\n");
                        }
                    } catch (Exception ex) {
                        messages.appendText("UNABLE TO CONNECT TO SERVER\n");
                    }

                    input.setOnKeyPressed(e2 -> {
                        try {
                            gameConnection.send(12);
                        } catch (Exception ex) {
                            input.setEditable(false);
                        }
                    });

                    input.setOnAction(e2 -> {
                        String message = "Player2: ";
                        message += input.getText();
                        input.clear();

                        if (!message.equals("Player2: ")) {
                            messages.appendText(message + "\n");

                            try {
                                chatConnection.send(message);
                            } catch (Exception ex) {
                                messages.appendText("Failed to send\n");
                            }
                        }
                    });
                } else {
                    invalid.setText("Invalid Port or IP Address.");
                }
            });
            stage.setScene(joinServer);
        });
        stage.setTitle("Battleship");
        stage.show();
    }

    private Server createChatServer(int port) {
        return new Server(port, (data) -> {
            Platform.runLater(() -> {
                input.setEditable(true);
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createChatClient(String ip, int port) {
        return new Client(ip, port, (data) -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Server createGameServer(int port) {
        return new Server(port, (data) -> {
            Platform.runLater(() -> {
                try {
                    handleOutputStream(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private Client createGameClient(String ip, int port) {
        return new Client(ip, port, (data) -> {
            Platform.runLater(() -> {
                try {
                    handleOutputStream(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void handleOutputStream(Serializable data) throws Exception {
        if (data instanceof String) {
            try {
                game.chatConn.send("");
                game.endScreen(true, false);
            } catch (Exception e){
                game.endScreen(true, true);
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                messages.appendText("OPPONENT HAS DISCONNECTED!\n");
                            }
                        },
                        500
                );
            }
        }
        if (data instanceof Integer[]){
            Integer[] xy = (Integer[]) data;
            game.playerBoard.getCell(xy[0], xy[1]).shoot();
            if (game.playerBoard.getCell(xy[0], xy[1]).ship != null && !game.playerBoard.getCell(xy[0], xy[1]).ship.isAlive()) {
                game.chatConn.send("You have sunk a ship!");
            }
            if (game.playerBoard.checkWin()) {
                try {
                    game.gameConn.send("");
                    game.endScreen(false, false);
                } catch (Exception e) {
                    System.out.println("Error");
                }
            }
        }
        if (data instanceof Integer){
            int value = (int) data;
            if (value == 12){
                typing.setText("Opponent is Typing......");
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                typing.setText("");
                            }
                        },
                        1500
                );
            }
        }
        if (data instanceof Boolean){
            game.myTurn = true;
            setCellsOpacity(1);
        }
        if (data instanceof boolean[][]){
            vertical = (boolean[][]) data;
        }
        if (data instanceof int[][] && vertical != null){
            int[][] gridLayout = (int[][]) data;
            setAxis(groupGame);

            game.enemyBoard = new Board(gridLayout, vertical, event ->{
                if (game.isLocked && game.myTurn){
                    Cell cell = (Cell) event.getSource();
                    if (!cell.wasShot) {
                        cell.shoot();
                        game.myTurn = false;
                        setCellsOpacity(0.5);
                        try {
                            Boolean a = true;
                            Integer[] xy = new Integer[2];
                            xy[0] = cell.x;
                            xy[1] = cell.y;
                            game.gameConn.send(a);
                            game.gameConn.send(xy);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            game.enemyBoard.playerGrid.setLayoutX(0);
            game.enemyBoard.playerGrid.setLayoutY(10);
            game.group.getChildren().add(game.enemyBoard.playerGrid);
        }
    }

    public void game(NetworkConnection gameConn, NetworkConnection chatConn, Stage stage) throws Exception {
        //Instantiation of Battleship Game here
        game = new Battleship(gameConn, chatConn, messages);

        Group gameBoard = game.playGame();
        gameBoard.setLayoutX(520);
        gameBoard.setLayoutY(25);

        int distance = 396;
        int distance1 = 527;
        for (int i = 0; i < 10; i++){
            Text txt = new Text();
            Text txt1 = new Text();
            txt.setText(String.valueOf((char) ('A' + i)));
            txt1.setText(String.valueOf(i + 1));
            txt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            txt1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            txt.setX(500);
            txt.setY(distance);
            txt1.setX(distance1);
            txt1.setY(365);
            distance += 31;
            distance1 += 31;
            groupGame.getChildren().addAll(txt, txt1);
        }

        groupGame.getChildren().addAll(input, messages, gameBoard, menuBar, typing);
        gameScene = new Scene(groupGame, 1000, 705);
        stage.setScene(gameScene);
    }

    private void setAxis(Group group){
        int distance = 50;
        int distance1 = 527;
        for (int i = 0; i < 10; i++){
            Text txt = new Text();
            Text txt1 = new Text();
            txt.setText(String.valueOf((char) ('A' + i)));
            txt1.setText(String.valueOf(i + 1));
            txt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            txt1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            txt.setX(500);
            txt.setY(distance);
            txt1.setX(distance1);
            txt1.setY(25);
            distance += 31;
            distance1 += 31;
            group.getChildren().addAll(txt, txt1);
        }
    }

    private void setCellsOpacity(double value){
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                game.enemyBoard.getCell(j, i).setOpacity(value);
            }
        }
    }

    //Print enemy's board ship health
    private void print(){
        for(int y = 0; y < 10; y++){
            for(int x = 0; x < 10; x++){
                Cell c = game.enemyBoard.getCell(x,y);
                if (c.ship != null){
                    System.out.print(c.ship.health);
                } else {
                    System.out.print(0);
                }
            }
            System.out.println();
        }
        System.out.println("\n");
    }
}
