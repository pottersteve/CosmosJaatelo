package com.example.cosmosjaatelo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ui references
    private TextView annaLoc, annaExp, annaEnergy;
    private TextView jaxLoc, jaxExp, jaxEnergy;
    private TextView currencyText;


    private ColonyManager manager;
    private CrewMember anna;
    private CrewMember jax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. initialize manager and load the saved crew.txt file
        manager = ColonyManager.getInstance();
        manager.loadFromFile(this);

        setupInitialCrew();
        initializeViews();
        setupButtons();
        refreshUI();
    }

    private void setupInitialCrew() {
        // search through manager to see if they already exist from a previous save
        for (CrewMember c : manager) {
            if (c.getName().equalsIgnoreCase("Anna")) anna = c;
            if (c.getName().equalsIgnoreCase("Jax")) jax = c;
        }

        // if they don't exist (first launch), recruit them through the manager
        if (anna == null) anna = manager.recruitCrew("Anna", "Medic");
        if (jax == null) jax = manager.recruitCrew("Jax", "Soldier");
    }

    private void initializeViews() {
        // anna fields
        annaLoc = findViewById(R.id.anna_loc);
        annaExp = findViewById(R.id.anna_exp);
        annaEnergy = findViewById(R.id.anna_energy);

        // jax fields
        jaxLoc = findViewById(R.id.jax_loc);
        jaxExp = findViewById(R.id.jax_exp);
        jaxEnergy = findViewById(R.id.jax_energy);

        currencyText = findViewById(R.id.currencyText);
    }

    private void setupButtons() {
        // play button: saves current state and launches the mission screen
        findViewById(R.id.playBtn).setOnClickListener(v -> {
            manager.saveToFile(this);
            Intent intent = new Intent(this, Mission.class);
            startActivity(intent);
        });

        findViewById(R.id.backBtn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, start_menu.class);
            startActivity(intent);
            finish();
        });

        // store/get crew :placeholder for now
        findViewById(R.id.getNewCharsBtn).setOnClickListener(v -> {
            Toast.makeText(this, "crew shop coming soon", Toast.LENGTH_SHORT).show();
        });

        // train button: placeholder
        findViewById(R.id.trainBtn).setOnClickListener(v -> {
            Toast.makeText(this, "training system disabled", Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshUI() {
        // refresh anna stats
        if (anna != null) {
            annaLoc.setText("location:" + anna.getLocation().toString().toLowerCase());
            annaExp.setText(" exp: " + anna.getExperience());
            annaEnergy.setText(" energy: " + anna.getEnergy() + "%");
        }

        // refresh jax stats
        if (jax != null) {
            jaxLoc.setText("location:" + jax.getLocation().toString().toLowerCase());
            jaxExp.setText(" exp: " + jax.getExperience());
            jaxEnergy.setText(" energy: " + jax.getEnergy() + "%");
        }

        // display current colony currency (if applicable)
        android.content.SharedPreferences prefs = getSharedPreferences("CosmosSaveData", MODE_PRIVATE);
        int totalIceCreams = prefs.getInt("TOTAL_ICE_CREAMS", 0); // 0 is the default if no save exists
        currencyText.setText(String.valueOf(totalIceCreams));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh data when returning from the mission screen
        refreshUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save data to file when the user minimizes the app
        manager.saveToFile(this);
    }
}