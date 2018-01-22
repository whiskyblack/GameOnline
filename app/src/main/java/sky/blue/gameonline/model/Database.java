package sky.blue.gameonline.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yami on 1/18/2018.
 */

public class Database {
    private static SharedPreferences preferences;

    public static void openDatabase(Context context, String name){
        preferences=context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void setName(String name){
        preferences.edit().putString("name", name).apply();
    }

    public static void setId(int id){
        preferences.edit().putInt("id", id).apply();
    }

    public static int getId(){
        return preferences.getInt("id", -1);
    }

    public static String getName(){
        return preferences.getString("name", "");
    }
}
