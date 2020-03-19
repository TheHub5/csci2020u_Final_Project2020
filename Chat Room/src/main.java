import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private int port = 7777;
    private String ip = "localhost";
    private TextArea messages = new TextArea();

    public Scene start, joinServer, game;
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
        Button JS = new Button("Join Server");
        Text text = new Text("Enter IP address:");

        gridJoin.add(ipF, 5, 12, 1, 1);
        gridJoin.add(JS, 5, 13, 1, 1);
        gridJoin.add(text, 4, 12, 1, 1);

        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 11));

        gridJoin.setBackground(new Background(myBI));

        messages.setPrefHeight(550);
        messages.setLayoutX(10);
        messages.setEditable(false);
        TextField input = new TextField();
        input.setPrefWidth(480);
        input.setLayoutX(10);
        input.setLayoutY(570);

        create.setOnAction(e -> {
            NetworkConnection connection = createServer();
            try {
                connection.startConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            input.setOnAction(e1 -> {
                String message = "Player1: ";
                message += input.getText();
                input.clear();

                messages.appendText(message + "\n");

                try {
                    connection.send(message);
                } catch (Exception ex) {
                    messages.appendText("Failed to send\n");
                }
            });
            groupGame = new Group();
            groupGame.getChildren().addAll(input, messages);
            game = new Scene(groupGame, 500, 600);
            stage.setScene(game);
        });

        join.setOnAction(e -> {

            JS.setOnAction(e1 -> {
                ip = ipF.getText();
                NetworkConnection connection = createClient(ip);
                try {
                    connection.startConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                input.setOnAction(e2 -> {
                    String message = "Player2: ";
                    message += input.getText();
                    input.clear();

                    messages.appendText(message + "\n");

                    try {
                        connection.send(message);
                    } catch (Exception ex) {
                        messages.appendText("Failed to send\n");
                    }
                });

                game();
                groupGame = new Group();
                groupGame.getChildren().addAll(input, messages);
                game = new Scene(groupGame, 500, 600);
                stage.setScene(game);
            });

            joinServer = new Scene(gridJoin, 400, 300);
            stage.setScene(joinServer);
        });

        stage.show();
    }

    private Server createServer() {
        return new Server(port, data-> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient(String ip) {
        return new Client(ip, port, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    public void game(){
        //Instantiation of Battleship Game here
    }
}