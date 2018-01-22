package sky.blue.gameonline.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import io.socket.client.Socket;
import sky.blue.gameonline.listener.SocketListener;
import sky.blue.gameonline.utils.FormatString;
import sky.blue.gameonline.utils.ListMonster;
import sky.blue.gameonline.utils.ListPlayer;
import sky.blue.gameonline.utils.Map;
import sky.blue.gameonline.utils.Monster;
import sky.blue.gameonline.utils.OtherPlayer;
import sky.blue.gameonline.utils.Player;

/**
 * Created by Yami on 1/12/2018.
 */

public class GameView extends View {
    int width, height, key;
    public Player player;
    Socket socket;
    Map map;
    ListMonster listMonster;
    ListPlayer listPlayer;
    Context context;
    public boolean createFinish=false;
//    Monster monster;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;

        player=new Player(context);

        map=new Map(context, player.getListener());

    }

    public void setSocket(Socket socket){
        this.socket=socket;
        createFinish=true;
    }

    public void createMap(byte[] byteMap){
        map.createMap(byteMap);
    }

    public void setSocketListener(SocketListener listener){
        player.setSocketListener(listener);
    }

    public void createMonster(String jsonMonsters){
        try {
            JSONArray arrayMonster=new JSONArray(jsonMonsters);
            listMonster=new ListMonster(context, player.getListener(), arrayMonster);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.createMonster: "+e.toString());
        }
    }

    public void setOtherPlayerMessage(String jsonMessage){
        try {
            JSONObject objectMessage=new JSONObject(jsonMessage);
            listPlayer.setMessage(objectMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerMessage(String message){
        player.setMessage(FormatString.toMessageBox(message));
    }

    public void setMonsters(String jsonMonsters){
        try {
            JSONArray arrayMonster=new JSONArray(jsonMonsters);
            listMonster.setMonsters(arrayMonster);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.createMonster: "+e.toString());
        }
    }

    public void createOtherPlayer(String jsonPlayers){
        try {
            JSONArray arrayPlayer=new JSONArray(jsonPlayers);
            listPlayer=new ListPlayer(context, arrayPlayer);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.createPlayers: "+e.toString());
        }
    }

    public void addOtherPlayer(String jsonPlayers){
        try {
            JSONArray arrayPlayer=new JSONArray(jsonPlayers);
            listPlayer.addPlayer(arrayPlayer);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.createMonster: "+e.toString());
        }
    }

    public void removePlayer(String jsonPlayers){
        try {
            JSONArray arrayPlayer=new JSONArray(jsonPlayers);
            listPlayer.removePlayer(arrayPlayer);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.createMonster: "+e.toString());
        }
    }

    public void setOtherPlayer(String jsonPlayers){
        try {
            JSONArray arrayPlayer=new JSONArray(jsonPlayers);
            listPlayer.setPlayers(arrayPlayer);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.createMonster: "+e.toString());
        }
    }

    public void setMonstersKey(String jsonKeys){
        try {
            JSONArray arrayKeys=new JSONArray(jsonKeys);
            listMonster.setKey(arrayKeys);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "GameView.setMonstersKey: "+e.toString());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawColor(Color.BLACK);

        map.draw(canvas);
        if (listMonster!=null)
            listMonster.draw(canvas);
        if (listPlayer!=null)
            listPlayer.draw(canvas);
        player.draw(key, canvas);
        if (socket!=null){
            socket.emit("getPlayerList");
            socket.emit("getMonsterList");
        }

        canvas.restore();
        delay();
    }

    private void delay(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 10);
    }

    public void setKey(int key){
        this.key=key;
    }

}
