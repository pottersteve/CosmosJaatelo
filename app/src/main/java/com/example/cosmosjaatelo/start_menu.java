package com.example.cosmosjaatelo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class start_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        ColonyManager.getInstance().loadFromFile(this);

        Button playBtn     = findViewById(R.id.playBtn);
        Button missionsBtn = findViewById(R.id.starterMissionsBtn);
        Button crewBtn     = findViewById(R.id.starterCrewBtn);
        Button recordBtn   = findViewById(R.id.starterRecordBtn);

        //StatsActivity subject to change
        playBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        missionsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, Mission.class)));

        //crewBtn.setOnClickListener(v ->
        //        startActivity(new Intent(this, StatsActivity.class)));

        //recordBtn.setOnClickListener(v ->
        //        startActivity(new Intent(this, StatsActivity.class)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(() -> {
            ColonyManager.getInstance().loadFromFile(this);
        }).start();
    }
}