package com.example.cosmosjaatelo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ui references
    private TextView currencyText;
    private GridLayout characterGrid;
    private ColonyManager manager;
    private List<Integer> selectedCrewIds = new ArrayList<>();


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

    private void setupInitialCrew() {//im sorry vania, but this is how we generated them
        //starter squad
        if (!manager.iterator().hasNext()) {
            manager.recruitCrew("Anna", "Medic");
            manager.recruitCrew("Jax", "Soldier");
            manager.recruitCrew("Elena", "Engineer");
            manager.recruitCrew("Kael", "Pilot");
            manager.recruitCrew("Nova", "Scientist");

            // save immediately so they are written to file
            manager.saveToFile(this);
        }
    }

    private void initializeViews() {
        characterGrid = findViewById(R.id.characterGrid);
        currencyText = findViewById(R.id.currencyText);
    }

    private void setupButtons() {
        // play button: saves current state and launches the mission screen
        findViewById(R.id.playBtn).setOnClickListener(v -> {
            if (selectedCrewIds.size() != 2) {
                Toast.makeText(this, "Please select exactly 2 crew members for the mission!", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.saveToFile(this);
            Intent intent = new Intent(this, Mission.class);

            intent.putExtra("CREW_A_ID", selectedCrewIds.get(0));
            intent.putExtra("CREW_B_ID", selectedCrewIds.get(1));

            selectedCrewIds.clear();
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
        //not anymore :)
        findViewById(R.id.trainBtn).setOnClickListener(v -> {
            if (selectedCrewIds.size() != 1) {
                Toast.makeText(this, "Please select exactly 1 crew member to train!", Toast.LENGTH_SHORT).show();
                return;
            }
            int idToTrain = selectedCrewIds.get(0);
            manager.moveCrew(idToTrain, Location.SIMULATOR);
            manager.trainCrew(idToTrain);

            Toast.makeText(this, manager.getCrewById(idToTrain).getName() + " finished training!", Toast.LENGTH_SHORT).show();

            selectedCrewIds.clear(); // Clear selection after training
            refreshUI();
        });

        findViewById(R.id.statsBtn).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ColonyStats.class);
            startActivity(intent);
        });
    }

    private void refreshUI() {
        characterGrid.removeAllViews();

        // loop through every single crew member in the ColonyManager
        for (CrewMember crew : manager) {
            View cardView = getLayoutInflater().inflate(R.layout.crew_card, characterGrid, false);

            TextView nameRole = cardView.findViewById(R.id.card_name_role);
            TextView locText = cardView.findViewById(R.id.card_loc);
            TextView expText = cardView.findViewById(R.id.card_exp);
            TextView energyText = cardView.findViewById(R.id.card_energy);

            nameRole.setText(crew.getName() + "\n" + crew.getType());
            locText.setText(" Loc: " + crew.getLocation().toString().toLowerCase());
            expText.setText(" Exp: " + crew.getExperience());
            energyText.setText(" Energy: " + crew.getEnergy() + "%");

            //GOD DAMN SELECTION LOGIC
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(android.graphics.Color.parseColor("#2D1050"));
            gd.setCornerRadius(8f); //gemini said to ask rounded corners.....

            if (selectedCrewIds.contains(crew.getId())) {
                gd.setStroke(6, android.graphics.Color.GREEN);
            } else {
                gd.setStroke(0, android.graphics.Color.TRANSPARENT);
            }
            cardView.setBackground(gd);

            //clilckable cards
            cardView.setOnClickListener(v -> {
                if (crew.getLocation() != Location.QUARTERS) {
                    Toast.makeText(this, crew.getName() + " is currently unavailable.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCrewIds.contains(crew.getId())) {
                    // deselection o.o
                    selectedCrewIds.remove((Integer) crew.getId());
                } else {
                    if (selectedCrewIds.size() < 2) {
                        selectedCrewIds.add(crew.getId());
                    } else {
                        Toast.makeText(this, "You can only select up to 2 crew members!", Toast.LENGTH_SHORT).show();
                    }
                }
                refreshUI();
            });

            // greyscale logic if they are training
            if (crew.getLocation() == Location.SIMULATOR) {
                locText.setTextColor(android.graphics.Color.GRAY);
                expText.setTextColor(android.graphics.Color.GRAY);
                energyText.setTextColor(android.graphics.Color.GRAY);
                nameRole.setTextColor(android.graphics.Color.GRAY);
            } else {
                locText.setTextColor(android.graphics.Color.parseColor("#C084B8"));
                expText.setTextColor(android.graphics.Color.parseColor("#C084B8"));
                energyText.setTextColor(android.graphics.Color.parseColor("#C084B8"));
                nameRole.setTextColor(android.graphics.Color.parseColor("#FFB3DE"));
            }

            //
            characterGrid.addView(cardView);
        }

        //current colony currency
        android.content.SharedPreferences prefs = getSharedPreferences("CosmosSaveData", MODE_PRIVATE);
        int totalIceCreams = prefs.getInt("TOTAL_ICE_CREAMS", 0);
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