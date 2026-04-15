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
    String roleA = crewA.getType();
    String roleB = crewB.getType();

    Threat threat = new Threat();
    Ship ship;
    List<String> missionLog = new ArrayList<>();
    Boolean active;
    int turnCount;
    int weWantThisManyIceCreams;

    //int totalHP = crewA.getEnergy()+ crewB.getEnergy();
    int totalHP = 100; //debugging purposes
    //int energyCrewA = crewA.getEnergy();
    //int energyCrewB = crewB.getEnergy();
    int energyCrewA = 70; //debugging purposes
    int energyCrewB = 90;//debugging purposes
    double exactEnergyA;
    double exactEnergyB;

    int iceCreamsPerMission;

    //GAME LOOOOOOOOOOOP
    Handler gameLoopHandler = new Handler(Looper.getMainLooper());
    Handler energyDrainHandler = new Handler(Looper.getMainLooper());

    MissionResult currentStatus = MissionResult.IN_PROGRESS;
    TextView hpTextView;
    TextView iceacreamTextView;
    TextView energyCrewATextView;
    TextView energyCrewBTextView;
    TextView textsPlayerShouldKnowTexrView;

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
        this.weWantThisManyIceCreams = 20;
        this.missionLog.add("Mission created and ready to launch");

        launch();


        Button shootButton = findViewById(R.id.shootButton);
        shootButton.setOnClickListener(v -> {
            ship.shoot(threat.activeMeteors); //bless javascript
        });

        //special abilities

        //if medic
        Button healButton = findViewById(R.id.healButton);
        healButton.setOnClickListener(v -> {
            heal();
        });
        //if engineer
        Button repairButton = findViewById(R.id.repairButton);
        repairButton.setOnClickListener(v ->{
            repairShip();
        });
        //if pilot
        ship.speed += 50;



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
        energyCrewATextView = findViewById(R.id.energyATextView);
        energyCrewATextView.setText("Energy a: " + energyCrewA);
        energyCrewBTextView = findViewById(R.id.energyBTextView);
        energyCrewBTextView.setText("Energy b: " + energyCrewB);
        textsPlayerShouldKnowTexrView = findViewById(R.id.textsPlayerShouldKnow);
        textsPlayerShouldKnowTexrView.setText("");

        //rocks
        threat.startMeteorSpawning(gameLayout, this);

        //functionalities
        startCollisionChecker();
        energyDrain(energyCrewA , energyCrewB);
    }

    //should have done this from the start, change later
    void checkGameState() {
        if (!active) return;
        if (energyCrewA <= 0 && energyCrewB <= 0) {
            this.missionLog.add("Crew ran out of energy!");
            gameOver();
        }
    }

    //collisions i am crying
    void startCollisionChecker(){
        Runnable collisionRunnable = new Runnable() {
            @Override
            public void run() {
                checkCollisions();
                checkGameState();
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
                        if (meteor.getTag() != null) {
                            if (meteor.getTag() instanceof int[]) {
                                int[] meteorData = (int[]) meteor.getTag();
                                currentMeteorType = meteorData[0];
                            } else {
                                currentMeteorType = (int) meteor.getTag();
                            }
                        }
                        if(currentMeteorType == 0){
                            gotHit();
                            if(roleA == "Scientist" || roleB == "Scientist"){
                                totalHP -= 5;
                            }
                            else{
                                totalHP -= 10;
                            }

                        } else if (currentMeteorType == -1){
                            gotHit();
                            if(roleA == "Scientist" || roleB == "Scientist"){
                                totalHP -= 20;
                            }else{
                                totalHP -= 25;
                            }

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

                        if(iceCreamsPerMission == weWantThisManyIceCreams){
                            gameWon();
                            break;
                        }
                    }
                }
            }
        }
    }

    void energyDrain(int a, int b){
        exactEnergyA = a;
        exactEnergyB = b;
        final double drainRateA = (double) a / 180.0;
        final double drainRateB = (double) b / 180.0;

        Runnable drainRunnable = new Runnable() {
            @Override
            public void run() {
                if(active && totalHP>0){
                    exactEnergyA -= drainRateA;
                    exactEnergyB -= drainRateB;

                    energyCrewA = (int) exactEnergyA;
                    energyCrewB = (int) exactEnergyB;

                    if(energyCrewA<=0 || energyCrewB<=0){
                        gameOver(); //and that specific crew member goes to medbay
                    }

                    energyCrewATextView.setText("Energy a: " + energyCrewA);
                    energyCrewBTextView.setText("Energy b: " + energyCrewB);

                    energyDrainHandler.postDelayed(this, 1000);
                }
            }
        };
        energyDrainHandler.post(drainRunnable);
    }

    void heal(){
        if(iceCreamsPerMission > 2){
            iceCreamsPerMission -= 2;
            iceacreamTextView.setText("Ice Creams: " + iceCreamsPerMission);
            textsPlayerShouldKnowTexrView.setText("Crew Healed!");

            if(energyCrewB < energyCrewA){
                exactEnergyB += 25;
                energyCrewB = (int) exactEnergyB;
                energyCrewBTextView.setText("Energy B: " + energyCrewB);
            }
            else {
                exactEnergyA += 25;
                energyCrewA = (int) exactEnergyA;
                energyCrewATextView.setText("Energy A: " + energyCrewA);
            }
        }
        else{
            textsPlayerShouldKnowTexrView.setText("Not enough resources!");
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            textsPlayerShouldKnowTexrView.setText("");
        }, 2000);
    }

    void repairShip(){
        if(iceCreamsPerMission > 5){
            iceCreamsPerMission -= 5;
            totalHP += 25;
            iceacreamTextView.setText("Ice Creams: " + iceCreamsPerMission);
            textsPlayerShouldKnowTexrView.setText("Ship Repaired!");
            hpTextView.setText("HP: " + totalHP);
        } else {
            textsPlayerShouldKnowTexrView.setText("Not enough resources!");
        }

        // clear the text after 2 seconds (2000 milliseconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            textsPlayerShouldKnowTexrView.setText("");
        }, 2000);
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