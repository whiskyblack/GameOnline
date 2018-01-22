package sky.blue.gameonline.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import sky.blue.gameonline.R;
import sky.blue.gameonline.listener.PlayerListener;

import static sky.blue.gameonline.utils.Player.x;
import static sky.blue.gameonline.utils.Player.y;

/**
 * Created by Yami on 1/14/2018.
 */

public class Map {
    private static int mapRow = 44, mapCol = 40;
    static int tilesSize=48;
    private static byte byteMap[];
    private Bitmap tile;
    private Vector<Bitmap> tiles;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static PlayerListener listener;

    public Map(Context context, PlayerListener listener) {

        tiles=new Vector<>();
        Map.listener =listener;
        Map.context =context;
    }

    public void createMap(byte[] map){
        try {

            for (int i=0; i<38; i++){
                tile=BitmapFactory.decodeStream(context.getAssets().open("tiles/"+i+".png"));
                tile=Bitmap.createScaledBitmap(tile, tilesSize, tilesSize, true);
                tiles.add(tile);
            }

//            InputStream inputStream = context.getAssets().open("maps/map.m");
//            byte[] map = new byte[inputStream.available()];
//            inputStream.read(map);
//            inputStream.close();

//            Log.i("DRAWMAP", ""+ map.length);

            mapRow=(int) map[0];
            mapCol=(int) map[1];

            byteMap=new byte[map.length-2];

            Log.i("DRAWMAP", ""+byteMap.length);
            Log.i("DRAWMAP", "Size: "+mapRow+" - "+mapCol);

            System.arraycopy(map, 2, byteMap, 0, map.length - 2);

        } catch (IOException e) {
            Log.i("GAMEERROR", "Map.createMap: "+e.toString());
        }
    }

    public void draw(Canvas canvas){
        setMapPosition(canvas);

        // Vẽ map
        if (byteMap!=null) {
            for (int row = 0; row < mapRow; row++) {
                for (int col = 0; col < mapCol; col++) {
                    int c = (int) byteMap[row * mapCol + col];
                    canvas.drawBitmap(tiles.get(c), col * tilesSize, row * tilesSize, null);
                }
            }
        }
    }

    // Set vị trí cho canvas trước khi vẽ map và nhân vật
    private static void setMapPosition(Canvas canvas){
        canvas.translate(canvas.getWidth()/2-x, canvas.getHeight()/2-y);
    }

    // Kiểm tra vị trí phía trước nhân vật có cho phép đi hay không
    public static boolean checkPoint(int X, int Y, boolean isPlayer){
        int c = (int) byteMap[Y * mapCol + X];
//        Log.i("CheckPointABC", "Player Point: "+c);
        return logicPoint(c, X, Y, isPlayer);
    }

    private static boolean logicPoint(int tilesId, int X, int Y, boolean isPlayer){
        if (tilesId<10)
            return true;
        else if (isPlayer){
            if (tilesId==27){
                if (X==25 && Y==6){
                    listener.onDrawWarning(context.getResources().getString(R.string.thongbao1), X, Y);
                } else if (X==39 && Y==44){
                    listener.onDrawWarning(context.getResources().getString(R.string.thongbao2), X, Y);
                }
            } else if (tilesId==31){
                if (X==40 && Y==44){
                    listener.onDrawWarning(context.getResources().getString(R.string.thongbao1), X, Y);
                    return true;
                }
            } else if (tilesId==35 || tilesId==36 || tilesId==37){
                return true;
            }
            return false;
        } else
            return false;
    }
}
