package com.example.cosmosjaatelo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class MissionResultHandler {

    static void launch(AppCompatActivity context,
                       MissionResult currentStatus,
                       CrewMember crewA, CrewMember crewB,
                       int iceCreamsPerMission, int weWantThisManyIceCreams,
                       int totalHP, int energyCrewA, int energyCrewB,
                       int turnCount) {

        android.content.SharedPreferences prefs = context.getSharedPreferences("CosmosSaveData", AppCompatActivity.MODE_PRIVATE);
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
        Intent intent = new Intent(context, GameOverActivity.class);
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

        intent.putExtra("EXP_CREW_A", expCrewA);
        intent.putExtra("EXP_CREW_B", expCrewB);

        context.startActivity(intent);
        context.finish();
    }
}