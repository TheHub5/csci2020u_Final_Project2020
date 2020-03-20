import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Battleship {

    public Board playerBoard;
    private int shipsToPlace = 5;
    private boolean running = false;
    private NetworkConnection connection;
    private boolean isServer;

    public Battleship(boolean isServer, NetworkConnection connection){
        this.isServer = isServer;
        this.connection = connection;
    }

    public VBox playGame() throws Exception {
        playerBoard = new Board(event -> {
            if (running)
                return;

            Cell cell = (Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y) && shipsToPlace > 0) {
                shipsToPlace--;
            }
        });

        BorderPane root = new BorderPane();
        root.setPrefSize(600, 800);

        root.setRight(new Text("RIGHT SIDEBAR - CONTROLS"));

        playerBoard.playerGrid.getChildren().get(1).setOnMouseClicked(e-> {

        });

        VBox vbox = new VBox(50, playerBoard);
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);
        return vbox;
    }

    public Board getBoard(){return playerBoard;}
}
