package com.example.cosmosjaatelo;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    //variables
    CrewMember crewA = new CrewMember();
    CrewMember crewB = new CrewMember();

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

    //enumerator
    void Mission(){

    }

    void launch(){

    }

    void executeTurn(){

    }

    String checkOutcome(){
        return "";
    }

    String getLog(){
        return "";
    }
}
