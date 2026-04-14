package com.example.cosmosjaatelo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;

class Threat {
    int level;
    Random random = new Random();
    Handler handler = new Handler(Looper.getMainLooper());
    int spawnTimeSeconds = 2;
    int difficulty = 1;

    //screen sizes
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    //constructor
    public Threat(){
        this.level = 0;
    }

    //methods
    // spawns the damn rocks
    public void startMeteorSpawning(ViewGroup gameLayout, Context context) {

        Runnable meteorSpawner = new Runnable() {
            @Override
            public void run() { // Runnable must always use the method name run()
                ImageView newMeteor = new ImageView(context);
                newMeteor.setImageResource(R.drawable.meteor);

                //size
                int meteorSize = 150;
                newMeteor.setLayoutParams(new ViewGroup.LayoutParams(meteorSize, meteorSize));

                //coordinates
                int meteorWidth = newMeteor.getWidth();
                if (meteorWidth == 0) meteorWidth = 100;

                int randomX = random.nextInt(screenWidth - meteorWidth);

                newMeteor.setX(randomX);
                newMeteor.setY(-200);

                //add to screen
                gameLayout.addView(newMeteor);

                //make it go down, down sugar
                makeRocksFall(gameLayout, newMeteor);
                handler.postDelayed(this, spawnTimeSeconds * 500 * difficulty);
            }
        };

        //executes the timer, safely tucked inside our method!
        handler.post(meteorSpawner);
    }

    void makeRocksFall(ViewGroup gameLayout, ImageView meteor){
        meteor.animate()
                .y(screenHeight + 200)
                .setDuration(5000)
                .withEndAction(() -> {
                    gameLayout.removeView(meteor);
                })
                .start();
    }

    Boolean isDefeated(){
        return false;
    }

    String getDescription(){
        return "";
    }
}

class Ship{

}


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

    List<String> missionLog = new ArrayList<>();
    Boolean active;
    int turnCount;

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

    void launch(){
        this.active = true;
        this.missionLog.add("Mission Launched!");

        ViewGroup gameLayout = findViewById(R.id.gameLayout);
        threat.startMeteorSpawning(gameLayout, this);
    }

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