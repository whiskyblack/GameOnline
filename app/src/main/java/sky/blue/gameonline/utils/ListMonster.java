package sky.blue.gameonline.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import sky.blue.gameonline.listener.PlayerListener;

/**
 * Created by Yami on 1/15/2018.
 */

public class ListMonster {
    ArrayList<Monster> monsters;
    Random random;
    PlayerListener listener;
    int[] keys;

    public ListMonster(Context context, PlayerListener listener, JSONArray arrayMonster) {
        this.listener = listener;

        monsters = new ArrayList<>();
        random = new Random();

        keys=new int[arrayMonster.length()];
        try {
            for (int i = 0; i < arrayMonster.length(); i++) {
                JSONObject objectMonster = arrayMonster.getJSONObject(i);

                monsters.add(new Monster(context, objectMonster.getInt("id"),
                        objectMonster.getInt("monsterX"), objectMonster.getInt("monsterY")));
            }
        } catch (Exception e) {
            Log.i("GAMEERROR", "ListMonster: " + e.toString());
        }
    }

    public void setMonsters(JSONArray arrayMonster) {
        try {
//            Log.i("CHECKSIZE", "Size: " + keys.length + " - " + arrayMonster.length());
            for (int i = 0; i < arrayMonster.length(); i++) {
                if (!monsters.get(i).running) {
                    JSONObject objectMonster = arrayMonster.getJSONObject(i);

                    int keyX = monsters.get(i).monsterPonit[0] - objectMonster.getInt("monsterX");
                    int keyY = monsters.get(i).monsterPonit[1] - objectMonster.getInt("monsterY");

                    if (!keyLogic(i, keyX, keyY)) {
                        monsters.get(i).setPoint(objectMonster.getInt("monsterX"), objectMonster.getInt("monsterY"));
                    }
//                    Log.i("MONSTERKEYSET", "X - X: " + objectMonster.getInt("monsterX")+" - "+players.get(i).otherPlayerPoint[0]);
                }
//                players.get(i).setPoint(objectMonster.getInt("monsterX"), objectMonster.getInt("monsterY"));
            }
        } catch (Exception e) {
            Log.i("GAMEERROR", "ListMonster: " + e.toString());
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
//        Log.i("MONSTERKEY", "Length: " + keys.size() + " - " + players.size());
        for (int i = 0; i < monsters.size(); i++) {
            if (keys.length == monsters.size()) {
                if (!monsters.get(i).running) {
                    monsters.get(i).key = keys[i];
//                    Log.i("MONSTERKEYSET", i + ": Set Key: " + keys.get(i));
                }
            }
            monsters.get(i).draw(canvas);
            if (monsters.get(i).monsterPonit[0] == Player.playerPoint[0]
                    && monsters.get(i).monsterPonit[1] == Player.playerPoint[1])
                listener.onResetPlayer(25, 7);
        }
    }
}
