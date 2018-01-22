package sky.blue.gameonline.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import static sky.blue.gameonline.utils.Map.checkPoint;
import static sky.blue.gameonline.utils.Map.tilesSize;

/**
 * Created by Yami on 1/15/2018.
 */

public class Monster {
    public int[] monsterPonit = {31, 34};
    private Bitmap monster;
    private int size = 0, translateX = 0, translateY = 0, monsterWidth = 0, monsterHeight = 0,
            currentFrame = 0, frame = 0, x = 0, y = 0, speed=2;
    public int key=0;
    public boolean running = false;
    private Random random;

    public Monster(Context context, int monsterId, int monsterX, int monsterY) {
        monsterPonit[0]=monsterX;
        monsterPonit[1]=monsterY;
        random=new Random();

        setSpeed(monsterId);

        try {
            monster = BitmapFactory.decodeStream(context.getAssets().open("monster/"+monsterId+".png"));

            monsterWidth = tilesSize;
            monsterHeight = tilesSize;

            x = monsterPonit[0] * tilesSize;
            y = monsterPonit[1] * tilesSize;
        } catch (IOException e) {
            Log.i("DRAWMAP", e.toString());
        }

    }

    private void setSpeed(int monsterId) {
        if (monsterId==6 || monsterId==7)
            speed=6;
        else if (monsterId==2 || monsterId==3 || monsterId==1)
            speed=4;
        else if (monsterId==4 || monsterId==5 || monsterId==0)
            speed=2;
    }

    private void move(){
        if (!running) {
//            key=random.nextInt(4);

            switch (key){
                case 0:
                    if (checkPoint(monsterPonit[0], monsterPonit[1] + 1, false)) {
                        running = true;
                    }
                    break;
                case 1:
                    if (checkPoint(monsterPonit[0] - 1, monsterPonit[1], false)) {
                        running = true;
                    }
                    break;
                case 2:
                    if (checkPoint(monsterPonit[0] + 1, monsterPonit[1], false)) {
                        running = true;
                    }
                    break;
                case 3:
                    if (checkPoint(monsterPonit[0], monsterPonit[1] - 1, false)) {
                        running = true;
                    }
                    break;
            }
        }
    }

    public void setPoint(int monsterX, int monsterY){
        monsterPonit[0]=monsterX;
        monsterPonit[1]=monsterY;

        x = monsterPonit[0] * tilesSize;
        y = monsterPonit[1] * tilesSize;
        translateX = translateY = 0;
        size = 0;
        running = false;
    }

    void draw(Canvas canvas) {
        move();

        if (running && key>=0) {
            if (size != tilesSize) {
                if (translateX == 0 && translateY == 0) {
                    currentFrame = key;
                    if (key == 0) {
//                        translateY = -4;
                        translateY = speed;
                    } else if (key == 1) {
//                        translateY = 4;
                        translateX = -speed;
                    } else if (key == 2) {
//                        translateX = -4;
                        translateX = speed;
                    } else if (key == 3) {
//                        translateX = 4;
                        translateY = -speed;
                    }
                }

                x += translateX;
                y += translateY;
                frame++;

                size += speed;

                // Nếu đi hết một tiles thì update vị trí nhân vật
                if (size == tilesSize) {
                    if (key == 3) {
                        monsterPonit[1] = monsterPonit[1] - 1;
                    } else if (key == 0) {
                        monsterPonit[1] = monsterPonit[1] + 1;
                    } else if (key == 1) {
                        monsterPonit[0] = monsterPonit[0] - 1;
                    } else if (key == 2) {
                        monsterPonit[0] = monsterPonit[0] + 1;
                    }

                    // reset biến cục bộ
                    translateX = translateY = 0;
                    size = 0;
                    running = false;
                    key=-1;
                }

            }
        }

        int srcX = (frame/3) * monsterWidth;
        int srcY = currentFrame * monsterHeight;
        Rect src = new Rect(srcX, srcY, srcX + monsterWidth, srcY + monsterHeight);
        Rect dst = new Rect(x, y, x + monsterWidth, y + monsterHeight);

        canvas.drawBitmap(monster, src, dst, null);
        if (frame==7)
            frame=0;
//        Log.i("MONSTERKEY", "MONSTERKEY: "+key);
    }
}
