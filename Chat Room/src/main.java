import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    public Group groupGame, groupStart, groupJoin;

    @Override
    public void start(Stage stage) {
        groupStart = new Group();
        start = new Scene(groupStart, 300, 300);
        stage.setScene(start);

        Text title = new Text("BattleShip");
        title.setY(40);
        title.setX(100);
        Button create = new Button("Create Server");
        create.setLayoutY(100);
        create.setLayoutX(100);
        Button join = new Button("Join Server");
        join.setLayoutY(200);
        join.setLayoutX(100);

        groupStart.getChildren().addAll(title, create, join);

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
                String message = "Server: ";
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
            groupJoin = new Group();
            TextField ipF = new TextField();
            ipF.setLayoutX(100);
            ipF.setLayoutY(100);
            Button JS = new Button("Join Server");
            JS.setLayoutX(100);
            JS.setLayoutY(200);

            JS.setOnAction(e1 -> {
                ip = ipF.getText();
                NetworkConnection connection = createClient(ip);
                try {
                    connection.startConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                input.setOnAction(e2 -> {
                    String message = "Client: ";
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

            groupJoin.getChildren().addAll(ipF, JS);
            joinServer = new Scene(groupJoin, 300, 300);
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