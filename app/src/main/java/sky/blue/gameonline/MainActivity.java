package sky.blue.gameonline;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import sky.blue.gameonline.listener.SocketListener;
import sky.blue.gameonline.model.Database;
import sky.blue.gameonline.ui.ButtonControll;
import sky.blue.gameonline.ui.ChatView;
import sky.blue.gameonline.ui.GameView;
import sky.blue.gameonline.utils.Player;

import static sky.blue.gameonline.utils.Map.checkPoint;

public class MainActivity extends AppCompatActivity implements SocketListener{
    ButtonControll btnUp, btnLeft, btnRight, btnDown;
    GameView gameView;
    Socket socket;
    EditText txtMessage;
    ChatView chatView;
    ImageView buttonChat;
    boolean createMonsters =false, createPlayers=false, chat=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database.openDatabase(this, "UserAccount");
        Database.setId(8);
        Database.setName("????");
        onView();
        onEvent();
        socketConnect();
    }

    private void socketEmit() {
//        socket.emit("getMap", 0);
//        socket.emit("getMonsterList");
//        socket.emit("getMonstersKey");
    }

    private void socketOn() {
        socket.on("getAccount", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject objectPlayer=new JSONObject();
                try {
                    objectPlayer.put("accountId", Database.getId());
                    objectPlayer.put("playerName", Database.getName());
                    objectPlayer.put("playerId", Player.playerId);
                    objectPlayer.put("playerX", Player.playerPoint[0]);
                    objectPlayer.put("playerY", Player.playerPoint[1]);
                    objectPlayer.put("isLife", true);

                    socket.emit("returnAccount", objectPlayer.toString());
                    socket.emit("getMap", 0);
                } catch (JSONException e) {
                    Log.i("GAMEERROR", "MainActivity.socketOn: "+e.toString());
                }
            }
        });

        socket.on("returnMap", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                gameView.createMap(args[0].toString().getBytes());
                gameView.setSocket(socket);
                gameView.player.setName();
//                socket.emit("getMonsterList");
//                socket.emit("getPlayerList");
                Log.i("GETMAP", args[0].toString());
            }
        });

        socket.on("returnMonsterList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (!createMonsters) {
                    gameView.createMonster(args[0].toString());
//                    JSONObject objectPlayer=new JSONObject();
//                    try {
//                        objectPlayer.put("accountId", Database.getId());
//                        objectPlayer.put("playerId", Player.playerId);
//                        objectPlayer.put("playerX", Player.playerPoint[0]);
//                        objectPlayer.put("playerY", Player.playerPoint[1]);
//                        objectPlayer.put("isLife", true);
//
//                        socket.emit("returnPlayerPoint", objectPlayer.toString());
//                    } catch (JSONException e) {
//                        Log.i("GAMEERROR", "MainActivity.socketOn: "+e.toString());
//                    }
                    createMonsters =true;
                } else {
                    gameView.setMonsters(args[0].toString());
                }
//                socket.emit("getMonsterList");
//                Log.i("LISTMONSTER", args[0].toString());
            }
        });

        socket.on("applyPlayerPoint", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        socket.on("returnPlayerList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("LISTPLAYERS", args[0].toString());

                if (gameView.createFinish) {
                    if (!createPlayers) {
                        gameView.createOtherPlayer(args[0].toString());
                        createPlayers = true;
                    } else {
                        gameView.setOtherPlayer(args[0].toString());
                    }
                }
//                socket.emit("getPlayerList");
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (gameView.createFinish){
                    gameView.setOtherPlayerMessage(args[0].toString());
                    Log.i("MESSAGEOB", "Message: "+args[0].toString());
                }
            }
        });

//        socket.on("returnMonstersKey", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                gameView.setMonstersKey(args[0].toString());
//                Log.i("LISTKEYS", args[0].toString());
//                socket.emit("getMonstersKey");
//            }
//        });

        socket.on("playerLogin", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("LISTMONSTER", "Player Login: "+args[0].toString());
                if (gameView.createFinish)
                    gameView.addOtherPlayer(args[0].toString());
            }
        });

        socket.on("playerLogout", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("LISTMONSTER", "Player Logout: "+args[0].toString());
                if (gameView.createFinish)
                    gameView.removePlayer(args[0].toString());
            }
        });
    }

    private void socketConnect() {
        try {
            socket= IO.socket("http://192.168.43.218:1234");
            socket.connect();
            socketOn();
            socketEmit();
        } catch (URISyntaxException e) {
            Log.i("GAMEERROR", "MainActivity.socketConnect: "+e.toString());
        }
    }

    private void onEvent() {
        gameView.setSocketListener(this);

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chat) {
                    chat=true;
                    chatView.show(true);
                    buttonChat.setImageResource(R.drawable.send);
                    txtMessage.setVisibility(View.VISIBLE);
                } else {
                    sendMessage();
                }
            }
        });

        buttonChat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                chatView.show(false);
                buttonChat.setImageResource(R.drawable.mail);
                txtMessage.setVisibility(View.GONE);
                chat=false;
                return true;
            }
        });

        btnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!Player.running) {
                    gameView.setKey(3);
                    if (checkPoint(Player.playerPoint[0], Player.playerPoint[1] - 1, true)) {
                        Player.running = true;
                    }
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.trans_bg);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.hold_on_bg);
                        break;
                }
                view.performClick();
                return true;
            }
        });

        btnDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!Player.running) {
                    gameView.setKey(0);
                    if (checkPoint(Player.playerPoint[0], Player.playerPoint[1] + 1, true)) {
                        Player.running = true;
                    }
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.trans_bg);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.hold_on_bg);
                        break;
                }
                view.performClick();
                return true;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!Player.running) {
                    gameView.setKey(1);
                    if (checkPoint(Player.playerPoint[0] - 1, Player.playerPoint[1], true)) {
                        Player.running = true;
                    }
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.trans_bg);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.hold_on_bg);
                        break;
                }
                view.performClick();
                return true;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!Player.running) {
                    gameView.setKey(2);
                    if (checkPoint(Player.playerPoint[0] + 1, Player.playerPoint[1], true)) {
                        Player.running = true;
                    }
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.trans_bg);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.hold_on_bg);
                        break;
                }

                view.performClick();
                return true;
            }
        });
    }

    private void onView() {
        gameView = findViewById(R.id.gameView);
        btnUp = findViewById(R.id.btnUp);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnDown = findViewById(R.id.btnDown);
        txtMessage=findViewById(R.id.txtMessage);
        buttonChat=findViewById(R.id.buttonChat);
        chatView=findViewById(R.id.chatView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.close();
    }

    @Override
    public void emitPlayerPoint(int playerX, int playerY, boolean isLife) {
        JSONObject objectPlayer=new JSONObject();
        try {
            objectPlayer.put("accountId", Database.getId());
            objectPlayer.put("playerName", Database.getName());
            objectPlayer.put("playerId", Player.playerId);
            objectPlayer.put("playerX", Player.playerPoint[0]);
            objectPlayer.put("playerY", Player.playerPoint[1]);
            objectPlayer.put("isLife", true);

            socket.emit("updatePlayerPoint", objectPlayer.toString());

            Log.i("emitPlayerPoint", objectPlayer.toString());
        } catch (JSONException e) {
            Log.i("GAMEERROR", "MainActivity.socketOn: "+e.toString());
        }
    }

    public void sendMessage() {
        if (!txtMessage.getText().toString().isEmpty()) {
            JSONObject objectMessage=new JSONObject();
            try {

                objectMessage.put("accountId", Database.getId());
                objectMessage.put("playerName", Database.getName());
                objectMessage.put("message", txtMessage.getText().toString());

                socket.emit("message", objectMessage.toString());
                gameView.setPlayerMessage(txtMessage.getText().toString());
                txtMessage.setText("");

            } catch (JSONException e) {
                Log.i("GAMEERROR", "MainActivity.sendMessage: "+e.toString());
            }
        }
    }
}
