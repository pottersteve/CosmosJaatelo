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
import android.widget.Button;
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

    //int totalHP = crewA.getEnergy()+ crewB.getEnergy();
    int totalHP = 100; //debugging purposes
    int iceCreamsPerMission;

    //GAME LOOOOOOOOOOOP
    Handler gameLoopHandler = new Handler(Looper.getMainLooper());

    MissionResult currentStatus = MissionResult.IN_PROGRESS;
    TextView hpTextView;
    TextView iceacreamTextView;

    Rect meteorRect = new Rect();
    Rect shipRect = new Rect();


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
        this.iceCreamsPerMission = 0;
        this.missionLog.add("Mission created and ready to launch");

        Button shootButton = findViewById(R.id.shootButton);
        shootButton.setOnClickListener(v -> {
            ship.shoot(threat.activeMeteors); //bless javascript
        });

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
        iceacreamTextView = findViewById(R.id.icecreamTextView);
        iceacreamTextView.setText("Ice Creams: " + iceCreamsPerMission);

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

    void gotHit(){
        ship.shipView.setColorFilter(Color.RED);
        ship.shipView.postDelayed(new Runnable() { // nobody asked for this
            @Override
            public void run() {
                ship.shipView.clearColorFilter();
            }
        }, 1000);
    }

    void checkCollisions(){
        if (ship == null || ship.shipView == null || !active) return;

        ship.shipView.getHitRect(shipRect);

        for(int i = threat.activeMeteors.size() - 1; i >= 0; i--) {
            if(totalHP > 0) {
                ImageView meteor = threat.activeMeteors.get(i);
                if (meteor != null) {
                    meteor.getHitRect(meteorRect);
                    if (Rect.intersects(shipRect, meteorRect)) {

                        int currentMeteorType = -1;
                        if(meteor.getTag() != null){
                            currentMeteorType = (int) meteor.getTag();
                        }
                        if(currentMeteorType == 0){
                            gotHit();
                            totalHP -= 10;
                        } else if (currentMeteorType == -1){
                            gotHit();
                            totalHP -= 25;
                        } else{
                            iceCreamsPerMission++;
                        }

                        //updating texts
                        hpTextView.setText("HP: " + totalHP);
                        this.missionLog.add("Ship hit! Total HP: " + totalHP);
                        iceacreamTextView.setText("Ice Creams: " + iceCreamsPerMission);

                        ViewGroup gameLayout = findViewById(R.id.gameLayout);
                        gameLayout.removeView(meteor);
                        threat.activeMeteors.remove(i);

                        if (totalHP <= 0) {
                            gameOver();
                            break;
                        }

                        if(iceCreamsPerMission == 10){
                            gameWon();
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

    void gameWon(){
        this.active = false;
        this.currentStatus = MissionResult.VICTORY;
        this.missionLog.add("Got the amount needed!! Mission successful!");

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