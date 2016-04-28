package com.example.shimao.crossroad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int cur_on_road1,cur_on_road2,cur_on_road4,cur_on_road6 = 0;  /* current car location on road */
    /* current man location */
    int cur_location_i = 7;
    int cur_location_j = 0;
    /* accident happen boolean */
    int gameStat = 1;   /* 1-game playing 2-collision 3-pause 4-restart*/
    TextView time;
    Button leftB, upB, rightB, stopB;
    Button[][] buttons = new Button[8][6];
    int[][] id = {
            {R.id.button00,R.id.button01,R.id.button02,R.id.button03,R.id.button04,R.id.button05},
            {R.id.button10,R.id.button11,R.id.button12,R.id.button13,R.id.button14,R.id.button15},
            {R.id.button20,R.id.button21,R.id.button22,R.id.button23,R.id.button24,R.id.button25},
            {R.id.button30,R.id.button31,R.id.button32,R.id.button33,R.id.button34,R.id.button35},
            {R.id.button40,R.id.button41,R.id.button42,R.id.button43,R.id.button44,R.id.button45},
            {R.id.button50,R.id.button51,R.id.button52,R.id.button53,R.id.button54,R.id.button55},
            {R.id.button60,R.id.button61,R.id.button62,R.id.button63,R.id.button64,R.id.button65},
            {R.id.button70,R.id.button71,R.id.button72,R.id.button73,R.id.button74,R.id.button75}
    };
    int[][] stat = {
            /*
            0 -- safe area
            2 -- car position
            1 -- still safe area on the road
            3 -- man location
             */
            {0,0,0,0,0,0},
            {2,1,1,1,1,1},
            {2,1,1,1,1,1},
            {0,0,0,0,0,0},
            {2,1,1,1,1,1},
            {0,0,0,0,0,0},
            {2,1,1,1,1,1},
            {3,0,0,0,0,0}
    };
    int [][] initStat = {
            {0,0,0,0,0,0},
            {2,1,1,1,1,1},
            {2,1,1,1,1,1},
            {0,0,0,0,0,0},
            {2,1,1,1,1,1},
            {0,0,0,0,0,0},
            {2,1,1,1,1,1},
            {3,0,0,0,0,0}
    };

    Thread roadOne,roadTwo,roadFour,roadSix;

    //music
    private MediaPlayer mp;
    private boolean isStoped = true;
    private boolean music_stop = true;

    //Sound
    private static final int SOUND_COUNT = 5;
    private int when_jump;
    private int when_dead;
    private int ui_click;
    private int when_win;
    private SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        leftB = (Button)findViewById(R.id.left);
        leftB.setOnClickListener(leftListener);
        upB = (Button)findViewById(R.id.up);
        stopB = (Button)findViewById(R.id.stop);
        upB.setOnClickListener(upListener);
        rightB = (Button)findViewById(R.id.right);
        rightB.setOnClickListener(rightListener);
        stopB.setOnClickListener(stopListener);

        //Sound
        soundPool = new SoundPool(SOUND_COUNT, AudioManager.STREAM_MUSIC, 0);
        ui_click = soundPool.load(this, R.raw.ui_click, 1);
        when_jump = soundPool.load(this, R.raw.flog, 1);
        when_dead = soundPool.load(this, R.raw.dead, 1);
        when_win = soundPool.load(this, R.raw.success, 1);

        roadOne = new roadOne();
        roadTwo = new roadTwo();
        roadFour = new roadFour();
        roadSix = new roadSix();
        roadOne.start();
        roadTwo.start();
        roadFour.start();
        roadSix.start();

        time = (TextView)findViewById(R.id.time);
        for (int i = 0; i<8; i++){
            for (int j = 0 ; j<6; j++){
                buttons[i][j] = (Button)findViewById(id[i][j]);
            }
        }

        timer.start();
        playMusic();

    }

    private Handler mhandler = new Handler() {
       public void handleMessage(Message msg){
           switch (msg.getData().getInt("road")){
               case 1:
                   int location = msg.getData().getInt("location");
                   if (location != 5 ){

                       int t1 = stat[1][location+1];
                       stat[1][location+1] = stat[1][location];
                       stat[1][location] = t1;
                   }else {
                       int t1 = stat[1][0];
                       stat[1][0] = stat[1][location];
                       stat[1][location] = t1;
                   }
                   setMap();
                   break;
               case 2:
                   location = msg.getData().getInt("location");
                   if (location!=5){

                       int t2 = stat[2][location+1];
                       stat[2][location+1] = stat[2][location];
                       stat[2][location] = t2;
                   }else{
                       int t1 = stat[2][0];
                       stat[2][0] = stat[2][location];
                       stat[2][location] = t1;
                   }
                   setMap();
                   break;
               case 4:
                   location = msg.getData().getInt("location");
                   if (location!=5){

                       int t4 = stat[4][location+1];
                       stat[4][location+1] = stat[4][location];
                       stat[4][location] = t4;
                   }else {
                       int t1 = stat[4][0];
                       stat[4][0] = stat[4][location];
                       stat[4][location] = t1;
                   }
                   setMap();
                   break;
               case 6:
                   location = msg.getData().getInt("location");
                   if (location!=5){

                       int t6 = stat[6][location+1];
                       stat[6][location+1] = stat[6][location];
                       stat[6][location] = t6;
                   }else{
                       int t1 = stat[6][0];
                       stat[6][0] = stat[6][location];
                       stat[6][location] = t1;
                   }
                   setMap();
                   break;
           }
           super.handleMessage(msg);

           for (int i = 0;i<8;i++){

                   Log.i("map",stat[i][0]+" "+stat[i][1]+" "+stat[i][2]+" "+stat[i][3]+" "+stat[i][4]+" "+stat[i][5]);

           }
           Log.i("map","=========");
       }
    };


    /* update the view */
    private void setMap(){
        if (cur_location_i == 1){
           if (cur_location_j == cur_on_road1){
               gameStat = 2;
               stat[1][cur_on_road1] = 4;         /* collision , accident!!*/
               collisionDialog(1);
           }
        }else if (cur_location_i == 2){
            if (cur_location_j == cur_on_road2){
                gameStat = 2;
                stat[2][cur_on_road1] = 4;         /* collision , accident!!*/
                collisionDialog(2);
            }
        }else if (cur_location_i == 4){
            if (cur_location_j == cur_on_road4){
                gameStat = 2;
                stat[4][cur_on_road1] = 4;         /* collision , accident!!*/
                collisionDialog(4);
            }
        }else if (cur_location_i == 6){
            if (cur_location_j == cur_on_road6){
                gameStat = 2;
                stat[6][cur_on_road1] = 4;         /* collision , accident!!*/
                collisionDialog(6);
            }
        }
        for (int i = 0; i<8; i++){
            for (int j = 0; j<6; j++){
                if (stat[i][j] == 2){
                    buttons[i][j].setBackgroundColor(Color.RED);
                }else if (stat[i][j] == 1){
                    buttons[i][j].setBackgroundColor(Color.GRAY);
                }else if (stat[i][j] == 3){
                    buttons[i][j].setBackgroundColor(Color.BLACK);
                }else if (stat[i][j] == 0){
                    buttons[i][j].setBackgroundColor(Color.GREEN);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.highscore) {
            return true;
        }else if (id == R.id.leave){
            finish();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }


    CountDownTimer timer = new CountDownTimer(30000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            time.setText(millisUntilFinished+" s");
        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        playMusic();//Play music
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();//Stop music
        timer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopMusic();//Stop music
    }


    public View.OnClickListener leftListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            soundPool.play(when_jump, 1, 1, 0, 0, 1);//play sound
            if (cur_location_j!=0){
                int temp = stat[cur_location_i][cur_location_j-1];
                stat[cur_location_i][cur_location_j-1] = stat[cur_location_i][cur_location_j];
                stat[cur_location_i][cur_location_j] = temp;
                setMap();
                cur_location_j = cur_location_j-1;
            }
        }
    };

    public View.OnClickListener upListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            soundPool.play(when_jump, 1, 1, 0, 0, 1);//play sound
            if (cur_location_i!=0){
                if (cur_location_i == 3 || cur_location_i == 5 || cur_location_i == 7) {
                    /* if move to the road */
                    stat[cur_location_i - 1][cur_location_j] = 3;
                    stat[cur_location_i][cur_location_j] = 0;
                    setMap();
                    cur_location_i = cur_location_i - 1;
                }else if(cur_location_i == 1 || cur_location_i == 4 || cur_location_i == 6){
                    /*move to the safe area */
                    stat[cur_location_i-1][cur_location_j] = 3;
                    stat[cur_location_i][cur_location_j] = 1;
                    setMap();
                    cur_location_i = cur_location_i-1;
                }else{
                    /* move to still the road */
                    int temp = stat[cur_location_i-1][cur_location_j];
                    stat[cur_location_i-1][cur_location_j] = stat[cur_location_i][cur_location_j];
                    stat[cur_location_i][cur_location_j] = temp;
                    setMap();
                    cur_location_i = cur_location_i-1;
                }
            }
        }
    };

    public View.OnClickListener rightListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            soundPool.play(when_jump, 1, 1, 0, 0, 1);//play sound
            if (cur_location_j!=5){
                int temp = stat[cur_location_i][cur_location_j+1];
                stat[cur_location_i][cur_location_j+1] = stat[cur_location_i][cur_location_j];
                stat[cur_location_i][cur_location_j] = temp;
                setMap();
                cur_location_j = cur_location_j+1;
            }
        }
    };

    class roadOne extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while (gameStat!=2) {
                    if (gameStat == 1){

                        Bundle bundle = new Bundle();
                        bundle.putInt("road", 1);
                        bundle.putInt("location", cur_on_road1);
                        Message m = new Message();
                        m.setData(bundle);
                        mhandler.sendMessage(m);
                        cur_on_road1++;
                        if (cur_on_road1 == 6) {
                            cur_on_road1 = 0;
                        }
                        Thread.sleep(500);                       /* 1s a move */
                    }
                    if (gameStat == 3){

                    }
                    if (gameStat == 4){
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    class roadTwo extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while (gameStat != 2) {
                    if (gameStat == 1){

                        Bundle bundle = new Bundle();
                        bundle.putInt("road",2);
                        bundle.putInt("location",cur_on_road2);
                        Message m = new Message();
                        m.setData(bundle);
                        mhandler.sendMessage(m);
                        cur_on_road2++;
                        if (cur_on_road2 == 6){
                            cur_on_road2 = 0;
                        }
                        Thread.sleep(800);                      /* 3s a move */
                    }
                    if (gameStat == 3){

                    }
                    if (gameStat == 4){
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class roadFour extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while (gameStat != 2) {
                    if (gameStat == 1){

                        Bundle bundle = new Bundle();
                        bundle.putInt("road",4);
                        bundle.putInt("location",cur_on_road4);
                        Message m = new Message();
                        m.setData(bundle);
                        mhandler.sendMessage(m);
                        cur_on_road4++;
                        if (cur_on_road4 == 6){
                            cur_on_road4 = 0;
                        }
                        Thread.sleep(700);                     /* 4s a move */
                    }
                    if (gameStat == 3){

                    }
                    if (gameStat == 4){
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class roadSix extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while (gameStat != 2) {
                    if (gameStat == 1){

                        Bundle bundle = new Bundle();
                        bundle.putInt("road",6);
                        bundle.putInt("location",cur_on_road6);
                        Message m = new Message();
                        m.setData(bundle);
                        mhandler.sendMessage(m);
                        cur_on_road6++;
                        if (cur_on_road6 == 6){
                            cur_on_road6 = 0;
                        }
                        Thread.sleep(1000);                          /* 2s a move */
                    }
                    if (gameStat == 3){

                    }
                    if (gameStat == 4){
                        break;
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void collisionDialog(int road){
        soundPool.play(when_dead, 1, 1, 0, 0, 1);//play sound
        stopMusic();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.collision)
                .setMessage("發生車禍於第"+road+"路")
                .setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameStat = 4;
                        stat = initStat.clone();
                        cur_on_road1 = 0;
                        cur_on_road2 = 0;
                        cur_on_road4 = 0;
                        cur_on_road6 = 0;
                        cur_location_i = 7;
                        cur_location_j = 0;
                        setMap();
                        gameStat = 1;
                        roadOne = new roadOne();
                        roadOne.start();
                        roadTwo = new roadTwo();
                        roadTwo.start();
                        roadFour = new roadFour();
                        roadFour.start();
                        roadSix = new roadSix();
                        roadSix.start();
                        playMusic();
                    }
                })
                .setNegativeButton(R.string.leave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    public View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(gameStat!=3)
            {
                gameStat = 3;
            }
            else{
                gameStat = 1;
            }
        }
    };

    //----------------------   Music  ----------------------//
    private void playMusic(){
        if( mp == null || isStoped){
            mp = create(MainActivity.this,  R.raw.crazyfrog);
            isStoped = false;
        }
        mp.setLooping(true);
        mp.start();
    }
    private void stopMusic(){
        if( mp == null || isStoped)
            return;
        mp.stop();
        isStoped = true;

    }
    //Build environment for playing music
    public static MediaPlayer create(Context context, int resid){
        AssetFileDescriptor afd;
        afd = context.getResources().openRawResourceFd(resid);
        if(afd == null)
            return null;
        try{
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            return mp;
        }catch (Exception e){
            Log.e("Play music error!", e.toString());
            return null;
        }
    }
/*
    private Button.OnClickListener music_control = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            if(!isStoped){
                soundPool.play(ui_click, 1, 1, 0, 0, 1);//sound
                stopMusic();
                btn_music.setImageResource(R.drawable.music_off1);
            }
            else{
                soundPool.play(ui_click, 1, 1, 0, 0, 1);//sound
                playMusic();
                btn_music.setImageResource(R.drawable.music_on1);
            }
        }
    };*/
}
