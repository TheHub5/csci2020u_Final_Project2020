import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    public int x, y;
    public Ship ship = null;
    public boolean wasShot = false;

    private Board board;

    public Cell(int x, int y, Board board) {
        super(30, 30);
        this.x = x;
        this.y = y;
        this.board = board;
        setFill(Color.LIGHTGRAY);
        setStroke(Color.BLACK);
    }

    public boolean shoot() {
        wasShot = true;

        if (ship.type != 0) {
            ship.hit();
            setFill(Color.RED);
            if (!ship.isAlive()) {
                board.ships--;
            }
            return true;
        } else setFill(Color.BLACK);

        return false;
    }
}