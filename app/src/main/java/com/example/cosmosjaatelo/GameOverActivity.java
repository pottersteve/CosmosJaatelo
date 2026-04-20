package com.example.cosmosjaatelo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        TextView titleView = findViewById(R.id.gameOverTitle);
        TextView statusView = findViewById(R.id.statStatus);
        TextView expAView = findViewById(R.id.statExpA);
        TextView expBView = findViewById(R.id.statExpB);
        TextView iceCreamView = findViewById(R.id.statIceCreams);
        TextView hpView = findViewById(R.id.statHP);
        TextView energyAView = findViewById(R.id.statEnergyA);
        TextView energyBView = findViewById(R.id.statEnergyB);
        TextView turnsView = findViewById(R.id.statTurns);

        // passed from Mission.java
        Intent intent = getIntent();
        String status = intent.getStringExtra("STATUS");
        int iceCreams = intent.getIntExtra("ICE_CREAMS", 0);
        int required = intent.getIntExtra("REQUIRED_ICE_CREAMS", 20);
        int hp = intent.getIntExtra("HP", 0);
        int energyA = intent.getIntExtra("ENERGY_A", 0);
        int energyB = intent.getIntExtra("ENERGY_B", 0);
        int turns = intent.getIntExtra("TURNS", 0);
        int expCrewA;
        int expCrewB;

        //save stuff
        android.content.SharedPreferences prefs = getSharedPreferences("CosmosSaveData", MODE_PRIVATE);
        int currentBank = prefs.getInt("TOTAL_ICE_CREAMS", 0);
        prefs.edit().putInt("TOTAL_ICE_CREAMS", currentBank + iceCreams).apply();

        // calculated stats per character
        if("VICTORY".equals(status)){
            expCrewA = intent.getIntExtra("EXP_CREW_A", 0);
            expCrewB = intent.getIntExtra("EXP_CREW_B", 0);
        }
        else{
            expCrewA = 0;
            expCrewB = 0;
        }


        // set texts
        if ("VICTORY".equals(status)) {
            titleView.setText("MISSION SUCCESS");
            statusView.setText("Outcome: VICTORY");
            statusView.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            titleView.setText("MISSION FAILED");
            statusView.setText("Outcome: DEFEAT");
            statusView.setTextColor(Color.parseColor("#F44336"));
        }

        expAView.setText("Crew A XP: +" + expCrewA);
        expBView.setText("Crew B XP: +" + expCrewB);

        iceCreamView.setText("Ice Creams Gathered: " + iceCreams + " / " + required);
        hpView.setText("Remaining HP: " + hp);
        energyAView.setText("Crew A Energy: " + energyA);
        energyBView.setText("Crew B Energy: " + energyB);
        turnsView.setText("Turns Elapsed: " + turns);

        // buttons
        Button btnViewStats = findViewById(R.id.btnViewStats);
        Button btnMainMenu = findViewById(R.id.btnMainMenu);

        btnViewStats.setOnClickListener(v -> {
            Intent statsIntent = new Intent(GameOverActivity.this, ColonyStats.class);
            startActivity(statsIntent);
            finish();
        });

        btnMainMenu.setOnClickListener(v -> {
            Intent mainIntent = new Intent(GameOverActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        });
    }
}