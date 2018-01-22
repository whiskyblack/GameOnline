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
import sky.blue.gameonline.listener.PlayerListener;
import sky.blue.gameonline.listener.SocketListener;
import sky.blue.gameonline.model.Database;

import static sky.blue.gameonline.utils.Map.tilesSize;

/**
 * Created by Yami on 1/13/2018.
 */

public class Player implements PlayerListener {
    public static int[] playerPoint = {4, 5};
    private Bitmap player;
    private int size = 0, translateX = 0, translateY = 0, playerWidth = 0, playerHeight = 0,
            speed=4, currentFrame = 0, frame = 0, warningTime=250, chatTime = 250, maxW=0;
    static int x = 0, y = 0;
    public static int playerId=3;
    public static boolean running = false;
    private boolean showWarning =false, chat = false;
    private Paint bgPaint, textPaint, paintName;
    private String warning, name="", message;
    private Rect warningRect;
    private RectF warningRectF;
    private int[] warningPoint={25, 6};
    SocketListener listener;
    RectF messageRectF;
    Rect messageRect;
    Bitmap arrow;

    public Player(Context context) {

        arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_down);
        arrow = Bitmap.createScaledBitmap(arrow, tilesSize / 4, tilesSize / 4, true);

        messageRectF = new RectF();
        messageRect = new Rect();

        bgPaint=new Paint();
        bgPaint.setColor(Color.parseColor("#55000000"));
        bgPaint.setStyle(Paint.Style.FILL);

        textPaint =new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16);

        paintName =new Paint();
        paintName.setTextSize(20);

        warningRect=new Rect();
        warningRectF=new RectF();

        setSpeed(playerId);

        try {
            player = BitmapFactory.decodeStream(context.getAssets().open("nhanvat/"+playerId+".png"));

            playerWidth = tilesSize;
            playerHeight = tilesSize;

            x = playerPoint[0] * tilesSize;
            y = playerPoint[1] * tilesSize;
        } catch (IOException e) {
            Log.i("DRAWMAP", e.toString());
        }

    }

    private void setSpeed(int playerId){
        if (playerId==9)
            speed=8;
    }

    public void setName(){
        name=Database.getName();
        if (name.equals("Admin")){
            paintName.setColor(Color.RED);
            paintName.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            paintName.setColor(Color.YELLOW);
        }
    }

    public void setMessage(String message) {
        this.message = message;
        chat = true;
        chatTime=250;
    }

    public void setSocketListener(SocketListener listener){
        this.listener=listener;
    }

    private void setPoint(int playerX, int playerY){
        playerPoint[0]=playerX;
        playerPoint[1]=playerY;
        x = playerPoint[0] * tilesSize;
        y = playerPoint[1] * tilesSize;
        translateX = translateY = 0;
        size = 0;
        running = false;
    }

    public void draw(int key, Canvas canvas) {

        if (running) {
            if (size != tilesSize) {
                if (translateX == 0 && translateY == 0) {
                    currentFrame = key;

                    if (key == 0) {
                        translateY = speed;
                    } else if (key == 1) {
                        translateX = -speed;
                    } else if (key == 2) {
                        translateX = speed;
                    } else if (key == 3) {
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
                        playerPoint[1] = playerPoint[1] - 1;
                    } else if (key == 0) {
                        playerPoint[1] = playerPoint[1] + 1;
                    } else if (key == 1) {
                        playerPoint[0] = playerPoint[0] - 1;
                    } else if (key == 2) {
                        playerPoint[0] = playerPoint[0] + 1;
                    }

                    // reset biến cục bộ
                    translateX = translateY = 0;
                    size = 0;
                    running = false;
                    listener.emitPlayerPoint(playerPoint[0], playerPoint[1], true);
                }

            }
        } else {
            if (translateX == 0 && translateY == 0) {
                currentFrame=key;
            }
        }

        int srcX = (frame/2) * playerWidth;
        int srcY = currentFrame * playerHeight;
        Rect src = new Rect(srcX, srcY, srcX + playerWidth, srcY + playerHeight);
        Rect dst = new Rect(x, y, x + playerWidth, y + playerHeight);

        canvas.drawBitmap(player, src, dst, null);
        if (frame==4)
            frame=0;

        if (showWarning){
            warningTime--;
            int j=0;
            canvas.drawRoundRect(warningRectF, 5, 5, bgPaint);

            for (String line:warning.split("@")){
                textPaint.getTextBounds(line, 0, line.length(), warningRect);
                if (maxW<warningRect.width()/2) {
                    maxW=warningRect.width() / 2;
                    warningRectF.left = warningPoint[0] * tilesSize - maxW-10;
                    warningRectF.right=warningPoint[0] * tilesSize + maxW+10;
                }
                warningRectF.top=warningPoint[1] * tilesSize-warningRect.height()-10;
                warningRectF.bottom=warningPoint[1] * tilesSize+j*(warningRect.height()+5)+10;
                canvas.drawText(line, warningPoint[0]*tilesSize-warningRect.width()/2,
                        warningPoint[1]*tilesSize+j*(warningRect.height()+5), textPaint);
                j++;
            }

            if (warningTime==0){
                warningTime=250;
                maxW=0;
                showWarning =false;
            }
        }

        if (chat) {
            chatTime--;
            int j = 0, X=x+tilesSize/2, textHeight=0;
            float height;

            for (String line : message.split("@")) {
                textPaint.getTextBounds(line, 0, line.length(), messageRect);
                if (maxW < messageRect.width() / 2) {
                    maxW = messageRect.width() / 2;
                }

                messageRectF.left = X - maxW - 20;
                messageRectF.right = X + maxW + 20;


                if (j == 0) {
                    textHeight=messageRect.height()+10;
                    messageRectF.top = y - messageRect.height();
                    messageRectF.bottom = y + message.split("@").length * textHeight;
                    height = messageRectF.top - messageRectF.bottom;
                    messageRectF.top = messageRectF.top + height + 10-tilesSize/3;
                    messageRectF.bottom = y - messageRect.height()+10-tilesSize/3;
                    canvas.drawRoundRect(messageRectF, 5, 5, bgPaint);
                }

                j++;
                canvas.drawText(line, X - messageRect.width() / 2,
                        messageRectF.top + j * textHeight, textPaint);
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

//        Log.i("PlayerPoint", "PlayerPoint: "+playerPoint[0]+" - "+playerPoint[1]);
    }

    public PlayerListener getListener(){
        return this;
    }

    @Override
    public void onResetPlayer(int playerX, int playerY) {
        setPoint(playerX, playerY);
        listener.emitPlayerPoint(playerX, playerY, true);
    }

    @Override
    public void onDrawWarning(String warning, int X, int Y) {
//        Log.i("PlayerWarning", warning);
        this.warning=warning;
        warningPoint[0]=X;
        warningPoint[1]=Y-1;
        showWarning =true;
    }
}
