package com.example.cosmosjaatelo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class Mission extends AppCompatActivity {
    //variables
    CrewMember crewA = new CrewMember("") {
        @Override
        public int act() {
            return 0;
        }

        @Override
        public String getType() {
            return "";
        }
    };
    CrewMember crewB = new CrewMember("") {
        @Override
        public int act() {
            return 0;
        }

        @Override
        public String getType() {
            return "";
        }
    };

    Threat threat = new Threat();
    Ship ship;
    List<String> missionLog = new ArrayList<>();
    Boolean active;
    int turnCount;

    int totalHP = crewA.getEnergy()+ crewB.getEnergy();

    //GAME LOOOOOOOOOOOP
    Handler gameLoopHandler = new Handler(Looper.getMainLooper());

    MissionResult currentStatus = MissionResult.IN_PROGRESS;
    TextView hpTextView;

    enum MissionResult{
        VICTORY,
        DEFEAT,
        IN_PROGRESS
    }

    enum Action{
        ATTACK,
        DEFEAND,
        SPECIAL
    }

    //view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mission);

        // "constructor"
        this.active = false;
        this.turnCount = 0;
        this.missionLog.add("Mission created and ready to launch");

        launch();
    }

    //MAIN THING THAT MAKES EVERYTHING WORKS, BE CAREFUL
    void launch(){
        this.active = true;
        this.missionLog.add("Mission Launched!");

        ViewGroup gameLayout = findViewById(R.id.gameLayout);

        //ship
        ImageView xmlShipImage = findViewById(R.id.imageView2);
        ship = new Ship(xmlShipImage);

        //texts
        hpTextView = findViewById(R.id.hpTextView);
        hpTextView.setText("HP: " + totalHP);

        //rocks
        threat.startMeteorSpawning(gameLayout, this);

        //functionalities
        startCollisionChecker();
    }

    //collisions i am crying
    void startCollisionChecker(){
        Runnable collisionRunnable = new Runnable() {
            @Override
            public void run() {
                checkCollisions();
                gameLoopHandler.postDelayed(this, 50);
            }
        };
        gameLoopHandler.post(collisionRunnable);
    }

    void checkCollisions(){
        if (ship == null || ship.shipView == null || !active) return;

        //boundaries of the ship
        Rect shipRect = new Rect(
                (int) ship.shipView.getX(),
                (int) ship.shipView.getY(),
                (int) (ship.shipView.getX() + ship.shipView.getWidth()),
                (int) (ship.shipView.getY() + ship.shipView.getHeight())
        );
        //AND boundaries of meteors
        for(int i=threat.activeMeteors.size(); i>=0; i--){
            if(totalHP > 0){
                ImageView meteor  = threat.activeMeteors.get(i);
                if (meteor != null) {
                    Rect meteorRect = new Rect(
                            (int) meteor.getX(),
                            (int) meteor.getY(),
                            (int) (meteor.getX() + meteor.getWidth()),
                            (int) (meteor.getY() + meteor.getHeight())
                    );

                    if(Rect.intersects(shipRect, meteorRect)){
                        ship.shipView.setColorFilter(Color.RED);
                        totalHP -= 10;
                        hpTextView.setText("HP: " + totalHP);
                        this.missionLog.add("Ship hit! Total HP: " + totalHP);

                        ViewGroup gameLayout = findViewById(R.id.gameLayout);
                        gameLayout.removeView(meteor);
                        threat.activeMeteors.remove(i);

                        if(totalHP <=0){
                            gameOver();
                            break;
                        }
                    }
                }
            }

        }
    }

    void gameOver(){
        this.active = false;
        this.currentStatus = MissionResult.DEFEAT;
        this.missionLog.add("Ship destroyed! Mission Defeat.");

        //stop everything
        gameLoopHandler.removeCallbacksAndMessages(null);
        threat.stopSpawning();
    }

    //methods from uml
    void executeTurn(){
        if(this.active){
            this.turnCount++;
            this.missionLog.add("Turn " + this.turnCount + " executed.");
        }
    }

    String checkOutcome(){
        return "";
    }

    String getLog(){
        return String.join("\n", missionLog);
    }
}