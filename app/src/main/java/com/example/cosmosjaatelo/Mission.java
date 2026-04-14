package com.example.cosmosjaatelo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;

class Threat {
    int level;


    //constructor
    public Threat(){
        this.level = 0;
    }

    //methods
    void spawnRocks(int level){

    }

    void makeRocksFall(){

    }

    Boolean isDefeated(){
        return false;
    }

    String getDescription(){
        return "";
    }
}


public class Mission  extends AppCompatActivity {
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

    //constructor
    public Mission(){
        this.active = false;
        this.turnCount = 0;
        this.missionLog.add("Mission created and ready to lunch");
    }

    void launch(){
        this.active = true;
        this.missionLog.add("Mission Launched!");
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
