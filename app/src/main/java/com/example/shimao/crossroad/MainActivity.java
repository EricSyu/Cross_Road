package com.example.shimao.crossroad;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    TextView time;
    Button leftB, upB, rightB;
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

    Thread roadOne,roadTwo,roadFour,roadSix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        leftB = (Button)findViewById(R.id.left);
        leftB.setOnClickListener(leftListener);
        upB = (Button)findViewById(R.id.up);
        upB.setOnClickListener(upListener);
        rightB = (Button)findViewById(R.id.right);
        rightB.setOnClickListener(rightListener);

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

    }

    private Handler mhandler = new Handler() {
       public void handleMessage(Message msg){
           switch (msg.getData().getInt("road")){
               case 1:
                   int location = msg.getData().getInt("location");
                   int t1 = stat[1][location+1];
                   stat[1][location+1] = stat[1][location];
                   stat[1][location] = t1;
                   setMap();
                   break;
               case 2:
                   location = msg.getData().getInt("location");
                   int t2 = stat[2][location+1];
                   stat[2][location+1] = stat[2][location];
                   stat[2][location] = t2;
                   setMap();
                   break;
               case 4:
                   location = msg.getData().getInt("location");
                   int t4 = stat[4][location+1];
                   stat[4][location+1] = stat[4][location];
                   stat[4][location] = t4;
                   setMap();
                   break;
               case 6:
                   location = msg.getData().getInt("location");
                   int t6 = stat[6][location+1];
                   stat[6][location+1] = stat[6][location];
                   stat[6][location] = t6;
                   setMap();
                   break;
           }
           super.handleMessage(msg);
       }
    };


    /* update the view */
    private void setMap(){

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
        if (id == R.id.action_settings) {
            return true;
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
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    public View.OnClickListener leftListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
                for (int i = 0; i<30; i++){
                    Thread.sleep(1000);                       /* 1s a move */
                    Bundle bundle = new Bundle();
                    bundle.putInt("road",1);
                    bundle.putInt("location", cur_on_road1);
                    Message m = new Message();
                    m.setData(bundle);
                    mhandler.sendMessage(m);
                    cur_on_road1++;
                    if (cur_on_road1 == 5){
                        cur_on_road1 = 0;
                        int temp = stat[1][0];
                        stat[1][0] = stat[1][5];
                        stat[1][5] = temp;
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
                for (int i = 0; i<30; i++){
                    Thread.sleep(3000);                      /* 3s a move */
                    Bundle bundle = new Bundle();
                    bundle.putInt("road",2);
                    bundle.putInt("location",cur_on_road2);
                    Message m = new Message();
                    m.setData(bundle);
                    mhandler.sendMessage(m);
                    cur_on_road2++;
                    if (cur_on_road2 == 5){
                        cur_on_road2 = 0;
                        int temp = stat[2][0];
                        stat[2][0] = stat[2][5];
                        stat[2][5] = temp;
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
                for (int i = 0; i<30; i++){
                    Thread.sleep(4000);                     /* 4s a move */
                    Bundle bundle = new Bundle();
                    bundle.putInt("road",4);
                    bundle.putInt("location",cur_on_road4);
                    Message m = new Message();
                    m.setData(bundle);
                    mhandler.sendMessage(m);
                    cur_on_road4++;
                    if (cur_on_road4 == 5){
                        cur_on_road4 = 0;
                        int temp = stat[4][0];
                        stat[4][0] = stat[4][5];
                        stat[4][5] = temp;
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
                for (int i = 0; i<30; i++){
                    Thread.sleep(2000);                          /* 2s a move */
                    Bundle bundle = new Bundle();
                    bundle.putInt("road",6);
                    bundle.putInt("location",cur_on_road6);
                    Message m = new Message();
                    m.setData(bundle);
                    mhandler.sendMessage(m);
                    cur_on_road6++;
                    if (cur_on_road6 == 5){
                        cur_on_road6 = 0;
                        int temp = stat[6][0];
                        stat[6][0] = stat[6][5];
                        stat[6][5] = temp;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
