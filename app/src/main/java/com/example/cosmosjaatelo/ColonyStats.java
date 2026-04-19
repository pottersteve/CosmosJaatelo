package com.example.cosmosjaatelo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ColonyStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colony_stats);

        findViewById(R.id.backBtnStats).setOnClickListener(v -> finish());
        SharedPreferences prefs = getSharedPreferences("CosmosSaveData", MODE_PRIVATE);
        int wins = prefs.getInt("COLONY_WINS", 0);
        int losses = prefs.getInt("COLONY_LOSSES", 0);
        int total = wins + losses;

        TextView txtTotal = findViewById(R.id.totalMissionsText);
        TextView txtWins = findViewById(R.id.totalWinsText);
        TextView txtLosses = findViewById(R.id.totalLossesText);

        txtTotal.setText("Missions: " + total);
        txtWins.setText("Wins: " + wins);
        txtLosses.setText("Losses: " + losses);

        LinearLayout container = findViewById(R.id.crewStatsContainer);
        ColonyManager manager = ColonyManager.getInstance();

        for (CrewMember crew : manager) {
            TextView crewStatView = new TextView(this);
            crewStatView.setTextColor(Color.parseColor("#FFB3DE"));
            crewStatView.setBackgroundColor(Color.parseColor("#2D1050"));
            crewStatView.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            crewStatView.setLayoutParams(params);

            String stats = crew.getName() + " (" + crew.getType() + ")\n" +
                    "Played: " + crew.getMissionsPlayed() +
                    " | Won: " + crew.getMissionsWon() +
                    " | Lost: " + crew.getMissionsLost();

            crewStatView.setText(stats);
            container.addView(crewStatView);
        }
    }
}