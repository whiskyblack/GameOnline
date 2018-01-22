package sky.blue.gameonline.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import sky.blue.gameonline.model.Database;

/**
 * Created by Yami on 1/19/2018.
 */

public class ListPlayer {
    ArrayList<OtherPlayer> players;
    Random random;
    int[] keys;
    Context context;

    public ListPlayer(Context context, JSONArray arrayPlayer) {

        this.context=context;
        players = new ArrayList<>();
        random = new Random();

//        keys=new int[arrayPlayer.length()];
        keys=new int[arrayPlayer.length()];
        try {
            for (int i = 0; i < arrayPlayer.length(); i++) {
                JSONObject objectPlayer = arrayPlayer.getJSONObject(i);

                players.add(new OtherPlayer(context, objectPlayer.getString("playerName"), objectPlayer.getInt("accountId"), objectPlayer.getInt("playerId"),
                        objectPlayer.getInt("playerX"), objectPlayer.getInt("playerY")));
            }
//            Log.i("CHECKSIZEPLAYER", "Size: " +players.size());
        } catch (Exception e) {
            Log.i("GAMEERROR", "ListPlayer: " + e.toString());
        }
    }

    public void addPlayer(JSONArray arrayPlayers){
        try {
            if (arrayPlayers.length()>players.size()){
                int[] k=new int[arrayPlayers.length()];
                for (int i=0; i< players.size(); i++){
                    k[i]=keys[i];
                }
                keys=k;
            }
            for (int i=0; i<arrayPlayers.length(); i++) {
                JSONObject objectPlayer = arrayPlayers.getJSONObject(i);
                for (int j=0; j<players.size(); j++){
                    if (players.get(j).accountId==objectPlayer.getInt("accountId"))
                        break;
                    else {
                        if (j==players.size()-1){
                            players.add(new OtherPlayer(context, objectPlayer.getString("playerName"), objectPlayer.getInt("accountId"), objectPlayer.getInt("playerId"),
                                    objectPlayer.getInt("playerX"), objectPlayer.getInt("playerY")));
                        }
                    }
                }
            }
            keys=new int[players.size()];
            Log.i("CHECKEY", "Key: " + keys[0]);
        } catch (JSONException e) {
            Log.i("GAMEERROR", "ListPlayer.addPlayer: " + e.toString());
        }
    }

    public void removePlayer(JSONArray arrayPlayers){
        try {
            for (int i = 0; i < players.size(); i++) {

                for (int j=0; j<arrayPlayers.length(); j++) {
                    JSONObject objectPlayer = arrayPlayers.getJSONObject(j);
                    if (players.get(i).accountId == objectPlayer.getInt("accountId")) {
                        break;
                    } else {
                        if (j==arrayPlayers.length()-1) {
                            players.remove(i);
                            int[] k=new int[arrayPlayers.length()];
                            for (int a=0; a< keys.length; a++){
                                if (a!=i)
                                    k[a]=keys[a];
                            }
                            keys=k;
                        }
                    }
                }
            }
            keys=new int[players.size()];
        } catch (Exception e){

        }

    }

    public void setMessage(JSONObject objectMessage){
        try {
            int accountId=objectMessage.getInt("accountId");
            for (int i=0; i<players.size(); i++){
                if (accountId==players.get(i).accountId){
                    players.get(i).setMessage(FormatString.toMessageBox(objectMessage.getString("message")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPlayers(JSONArray arrayPlayer) {
        try {
//            players.clear();

            Log.i("CHECKSIZE", "Size: " + keys.length + " - " + arrayPlayer.length());
//            keys=new int[arrayPlayer.length()];
            for (int i = 0; i < arrayPlayer.length(); i++) {

                if (!players.get(i).running) {
                    JSONObject objectPlayer = arrayPlayer.getJSONObject(i);

                    int keyX = players.get(i).otherPlayerPoint[0] - objectPlayer.getInt("playerX");
                    int keyY = players.get(i).otherPlayerPoint[1] - objectPlayer.getInt("playerY");

                    if (!keyLogic(i, keyX, keyY)) {
                        players.get(i).setPoint(objectPlayer.getInt("playerX"), objectPlayer.getInt("playerY"));
                    }
//                    Log.i("MONSTERKEYSET", "X - X: " + objectPlayer.getInt("playerX")+" - "+players.get(i).otherPlayerPoint[0]);
                }
//                players.get(i).setPoint(objectPlayer.getInt("playerX"), objectPlayer.getInt("playerY"));
            }
        } catch (Exception e) {
            Log.i("GAMEERROR", "ListPlayer.setPlayers: " + e.toString());
        }
    }

    private boolean keyLogic(int i, int keyX, int keyY) {
//        Log.i("MONSTERKEY", i+": X - Y: " + keyX+" - "+keyY);
        if (Math.abs(keyX) == 1 && keyY == 0 || Math.abs(keyY) == 1 && keyX == 0) {
            if (keyX == -1) {
                keys[i] = 2;
            } else if (keyX == 1) {
                keys[i] = 1;
            }

            if (keyY == -1) {
                keys[i] = 0;
            } else if (keyY == 1) {
                keys[i] = 3;
            }
//            Log.i("MONSTERKEYABS", "X - Y: " + keyX+" - "+keyY);
            return true;
        } else {
            keys[i] = -1;
//            Log.i("MONSTERKEYSET", "X - Y: " + keyX+" - "+keyY);
            return false;
        }
    }

    public void setKey(JSONArray arrayKey) {
        try {
            for (int i = 0; i < arrayKey.length(); i++) {
                keys[i] = arrayKey.getJSONObject(i).getInt("key");
            }
        } catch (Exception e) {
            Log.i("GAMEERROR", "ListMonster.setKey: " + e.toString());
        }
    }

    public void draw(Canvas canvas) {
//        Log.i("PLAYERKEYS", "Length: " + keys.length + " - " + players.size());
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).accountId!= Database.getId()) {
                if (keys.length == players.size()) {
                    Log.i("PLAYERKEYS", "Length: " + Database.getId() + " - " + players.get(i).accountId);
                    try {
                        if (!players.get(i).running) {
                            players.get(i).key = keys[i];
                            Log.i("PLAYERSKEYSET", i + ": Set Key: " + keys[i]);
                        }
                    } catch (Exception e){
                        Log.i("GAMEERROR", "ListPlayer.draw: " + e.toString());
                    }
                }
                players.get(i).draw(canvas);
            }

//            if (players.get(i).otherPlayerPoint[0] == Player.playerPoint[0]
//                    && players.get(i).otherPlayerPoint[1] == Player.playerPoint[1])
//                listener.onResetPlayer(25, 7);
        }
    }
}
