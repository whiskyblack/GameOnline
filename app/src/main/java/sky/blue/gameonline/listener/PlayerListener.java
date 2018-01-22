package sky.blue.gameonline.listener;

/**
 * Created by Yami on 1/15/2018.
 */

public interface PlayerListener {
    void onResetPlayer(int playerX, int playerY);
    void onDrawWarning(String warning, int warningX, int warningY);
}
