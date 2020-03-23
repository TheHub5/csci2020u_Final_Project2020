package BattleShip;

import javafx.scene.Parent;
import javafx.scene.media.Media;
import java.io.File;
import javafx.scene.media.MediaPlayer;

public class Ship extends Parent {

    String blast = "src/main/resources/blast.aiff";
    Media sound = new Media(new File(blast).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(sound);

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
        mediaPlayer.play();
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
