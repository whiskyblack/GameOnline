package sky.blue.gameonline.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.io.IOException;

import sky.blue.gameonline.R;
import sky.blue.gameonline.listener.OtherPlayerListener;

import static sky.blue.gameonline.utils.Map.checkPoint;
import static sky.blue.gameonline.utils.Map.tilesSize;

/**
 * Created by Yami on 1/19/2018.
 */

public class OtherPlayer implements OtherPlayerListener {
    int[] otherPlayerPoint = {31, 34};
    private Bitmap monster;
    private int size = 0, translateX = 0, translateY = 0, monsterWidth = 0, monsterHeight = 0,
            currentFrame = 0, frame = 0, x = 0, y = 0, speed = 6, maxW = 0;
    int key = 0, chatTime = 250;
    boolean running = false, chat = false;
    int accountId;
    private Paint bgPaint, textPain, paintName;
    private String name, message;
    RectF messageRectF;
    Rect messageRect;
    Bitmap arrow;

    OtherPlayer(Context context, String name, int accountId, int playerId, int monsterX, int monsterY) {

        this.accountId = accountId;
        this.name = name;

        arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_down);
        arrow = Bitmap.createScaledBitmap(arrow, tilesSize / 4, tilesSize / 4, true);

        messageRectF = new RectF();
        messageRect = new Rect();

        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#000000"));
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(100);

        textPain = new Paint();
        textPain.setColor(Color.WHITE);
        textPain.setTextSize(16);

        paintName =new Paint();
        if (name.equals("Admin")){
            paintName.setColor(Color.RED);
            paintName.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            paintName.setColor(Color.YELLOW);
        }
        paintName.setTextSize(20);

        otherPlayerPoint[0] = monsterX;
        otherPlayerPoint[1] = monsterY;

        setSpeed(playerId);

        try {
            monster = BitmapFactory.decodeStream(context.getAssets().open("nhanvat/" + playerId + ".png"));

            monsterWidth = tilesSize;
            monsterHeight = tilesSize;

            x = otherPlayerPoint[0] * tilesSize;
            y = otherPlayerPoint[1] * tilesSize;
        } catch (IOException e) {
            Log.i("DRAWMAP", e.toString());
        }

    }

    public void setMessage(String message) {
        this.message = message;
        chatTime = 250;
        chat = true;
    }

    private void setSpeed(int playerId) {
        if (playerId == 9)
            speed = 8;
    }

    private void move() {
        if (!running) {
//            key=random.nextInt(4);

            switch (key) {
                case 0:
                    if (checkPoint(otherPlayerPoint[0], otherPlayerPoint[1] + 1, true)) {
                        running = true;
                    }
                    break;
                case 1:
                    if (checkPoint(otherPlayerPoint[0] - 1, otherPlayerPoint[1], true)) {
                        running = true;
                    }
                    break;
                case 2:
                    if (checkPoint(otherPlayerPoint[0] + 1, otherPlayerPoint[1], true)) {
                        running = true;
                    }
                    break;
                case 3:
                    if (checkPoint(otherPlayerPoint[0], otherPlayerPoint[1] - 1, true)) {
                        running = true;
                    }
                    break;
            }
        }
    }

    void setPoint(int monsterX, int monsterY) {
        otherPlayerPoint[0] = monsterX;
        otherPlayerPoint[1] = monsterY;

        x = otherPlayerPoint[0] * tilesSize;
        y = otherPlayerPoint[1] * tilesSize;
        translateX = translateY = 0;
        size = 0;
        running = false;
    }

    void draw(Canvas canvas) {
        move();

        if (running && key >= 0) {
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
                        otherPlayerPoint[1] = otherPlayerPoint[1] - 1;
                    } else if (key == 0) {
                        otherPlayerPoint[1] = otherPlayerPoint[1] + 1;
                    } else if (key == 1) {
                        otherPlayerPoint[0] = otherPlayerPoint[0] - 1;
                    } else if (key == 2) {
                        otherPlayerPoint[0] = otherPlayerPoint[0] + 1;
                    }

                    // reset biến cục bộ
                    translateX = translateY = 0;
                    size = 0;
                    running = false;
                    key = -1;
                }

            }
        }

        int srcX = (frame / 3) * monsterWidth;
        int srcY = currentFrame * monsterHeight;
        Rect src = new Rect(srcX, srcY, srcX + monsterWidth, srcY + monsterHeight);
        Rect dst = new Rect(x, y, x + monsterWidth, y + monsterHeight);

        canvas.drawBitmap(monster, src, dst, null);

        if (chat) {
            chatTime--;
            int j = 0, X = x + tilesSize / 2, textHeight = 0;
            float height;

            for (String line : message.split("@")) {
                textPain.getTextBounds(line, 0, line.length(), messageRect);
                if (maxW < messageRect.width() / 2) {
                    maxW = messageRect.width() / 2;
                }

                messageRectF.left = X - maxW - 20;
                messageRectF.right = X + maxW + 20;


                if (j == 0) {
                    textHeight = messageRect.height() + 10;
                    messageRectF.top = y - messageRect.height();
                    messageRectF.bottom = y + message.split("@").length * textHeight;
                    height = messageRectF.top - messageRectF.bottom;
                    messageRectF.top = messageRectF.top + height + 10 - tilesSize / 3;
                    messageRectF.bottom = y - messageRect.height() + 10 - tilesSize / 3;
                    canvas.drawRoundRect(messageRectF, 5, 5, bgPaint);
                }

                j++;
                canvas.drawText(line, X - messageRect.width() / 2,
                        messageRectF.top + j * textHeight, textPain);
                if (j == message.split("@").length) {
                    canvas.drawBitmap(arrow, X - arrow.getWidth() / 2, messageRectF.bottom, bgPaint);
                }
            }

            if (chatTime == 0) {
                chatTime = 250;
                maxW = 0;
                chat = false;
            }
        } else {
            Rect rectName=new Rect();
            paintName.getTextBounds(name, 0, name.length(), rectName);
            canvas.drawText(name, x+tilesSize/2 - rectName.width() / 2,
                    y - rectName.height()+5, paintName);
        }

        if (frame == 7)
            frame = 0;
//        Log.i("MONSTERKEY", "MONSTERKEY: "+key);
    }

    @Override
    public void onResetOtherPlayer(int playerX, int playerY) {

    }
}
