package com.example.cosmosjaatelo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Mission extends AppCompatActivity {
    //variables
    CrewMember crewA;
    CrewMember crewB;
    String roleA;
    String roleB;

    Threat threat = new Threat();
    Ship ship;
    List<String> missionLog = new ArrayList<>();
    Boolean active;
    int turnCount;
    int weWantThisManyIceCreams; int totalIceCreams = 0;

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




    enum Action{
        ATTACK,
        DEFEND,
        SPECIAL
    }

    //view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission);

        //unpack intent
        int crewAId = getIntent().getIntExtra("CREW_A_ID", -1);
        int crewBId = getIntent().getIntExtra("CREW_B_ID", -1);
        //fetch data
        ColonyManager manager = ColonyManager.getInstance();
        crewA = manager.getCrewById(crewAId);
        crewB = manager.getCrewById(crewBId);
        roleA = crewA.getType();
        roleB = crewB.getType();
        energyCrewA = crewA.getEnergy();
        energyCrewB = crewB.getEnergy();
        totalHP = 100;

        // constructor
        this.active = false;
        this.turnCount = 0;
        this.iceCreamsPerMission = 0;
        this.weWantThisManyIceCreams = 20;
        this.missionLog.add("Mission created and ready to launch");

        launch();


        Button shootButton = findViewById(R.id.shootButton);
        shootButton.setOnClickListener(v -> {
            ship.shoot(threat.activeMeteors);
        });

        //special abilities
        Button healButton = findViewById(R.id.healButton);
        Button repairButton = findViewById(R.id.repairButton);
        //if medic
        if(roleA.equals("Medic") || roleB.equals("Medic")){ //bless javascript

            healButton.setOnClickListener(v -> {
                heal();
            });
        }
        else{
            healButton.setVisibility(android.view.View.GONE);
        }

        //if engineer
        if(roleA.equals("Engineer") || roleB.equals("Engineer")) {

            repairButton.setOnClickListener(v -> {
                repairShip();
            });
        }
        else{
            repairButton.setVisibility(android.view.View.GONE);
        }
        //if pilot
        if(roleA.equals("Pilot") || roleB.equals("Pilot")) {
            ship.speed += 50;
        }


    }

    //MAIN THING THAT MAKES EVERYTHING WORKS, BE CAREFUL
    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
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
                            if(Objects.equals(roleA, "Scientist") || Objects.equals(roleB, "Scientist")){
                                totalHP -= 5;
                            }
                            else{
                                totalHP -= 10;
                            }

                        } else if (currentMeteorType == -1){
                            gotHit();
                            if(Objects.equals(roleA, "Scientist") || Objects.equals(roleB, "Scientist")){
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
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if(active && totalHP>0){
                    exactEnergyA -= drainRateA;
                    exactEnergyB -= drainRateB;

                    energyCrewA = (int) exactEnergyA;
                    energyCrewB = (int) exactEnergyB;

                    if(energyCrewA<=0 || energyCrewB<=0){
                        gameOver(); //for that specific crew member to be transfered to medbay
                    }

                    energyCrewATextView.setText("Energy a: " + energyCrewA);
                    energyCrewBTextView.setText("Energy b: " + energyCrewB);

                    energyDrainHandler.postDelayed(this, 1000);
                }
            }
        };
        energyDrainHandler.post(drainRunnable);
    }

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
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

    private void launchGameOverScreen() {
        android.content.SharedPreferences prefs = getSharedPreferences("CosmosSaveData", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();

        int colonyWins = prefs.getInt("COLONY_WINS", 0);
        int colonyLosses = prefs.getInt("COLONY_LOSSES", 0);

        if (currentStatus == MissionResult.VICTORY) {
            crewA.recordWin();
            crewB.recordWin();
            editor.putInt("COLONY_WINS", colonyWins + 1);
        } else {
            crewA.recordLoss();
            crewB.recordLoss();
            editor.putInt("COLONY_LOSSES", colonyLosses + 1);
        }

        editor.apply();
        Intent intent = new Intent(Mission.this, GameOverActivity.class);
        intent.putExtra("STATUS", currentStatus.name());
        intent.putExtra("ICE_CREAMS", iceCreamsPerMission);
        intent.putExtra("REQUIRED_ICE_CREAMS", weWantThisManyIceCreams);

        int safeHP = Math.max(0, totalHP);
        int safeEnergyA = Math.max(0, energyCrewA);
        int safeEnergyB = Math.max(0, energyCrewB);

        intent.putExtra("HP", safeHP);
        intent.putExtra("ENERGY_A", safeEnergyA);
        intent.putExtra("ENERGY_B", safeEnergyB);
        intent.putExtra("TURNS", turnCount);

        // Experience per char
        int baseExp = (currentStatus == MissionResult.VICTORY) ? 500 : 100; // Shared
        int hpBonusExp = safeHP;

        // personal bonus: just math so that the numbers to make sense
        int expCrewA = (baseExp + hpBonusExp + safeEnergyA)/10;
        int expCrewB = (baseExp + hpBonusExp + safeEnergyB)/10;
        crewA.setExperience(crewA.getExperience() + expCrewA);
        crewB.setExperience(crewB.getExperience() + expCrewB);
        ColonyManager.getInstance().saveToFile(this);

        intent.putExtra("EXP_CREW_A", expCrewA);
        intent.putExtra("EXP_CREW_B", expCrewB);

        startActivity(intent);
        finish();
    }

    void gameOver(){
        this.active = false;
        this.currentStatus = MissionResult.DEFEAT;
        this.missionLog.add("Ship destroyed! Mission Defeat.");

        //stop everything
        gameLoopHandler.removeCallbacksAndMessages(null);
        threat.stopSpawning();

        launchGameOverScreen();
    }

    void gameWon(){
        totalIceCreams += weWantThisManyIceCreams;
        this.active = false;
        this.currentStatus = MissionResult.VICTORY;
        this.missionLog.add("Got the amount needed!! Mission successful!");

        //stop everything
        gameLoopHandler.removeCallbacksAndMessages(null);
        threat.stopSpawning();

        launchGameOverScreen();
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