package sky.blue.gameonline.listener;

/**
 * Created by Yami on 1/18/2018.
 */

public interface SocketListener {
    void emitPlayerPoint(int playerX, int playerY, boolean isLife);
}
