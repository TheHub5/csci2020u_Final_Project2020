package BattleShip;

import javafx.scene.Parent;
import javafx.scene.media.Media;
import java.io.File;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

public class Ship extends Parent {

    public int type;
    public boolean vertical = true;
    public int x, y;
    public int health;

    public Ship(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        health = type;
    }

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
