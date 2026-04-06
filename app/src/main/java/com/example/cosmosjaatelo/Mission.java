package com.example.cosmosjaatelo;

import java.util.List;

public class Mission {
    //variables
    CrewMember crewA = new CrewMember;
    CrewMember crewB = new CrewMember;

    Threat threat = new Threat;

    List<String> missionLog = new List<String>;
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

    MissionResult checkOutcome(){

    }

    String getLog(){

    }
}
