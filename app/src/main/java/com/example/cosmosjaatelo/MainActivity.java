package com.example.cosmosjaatelo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ui references
    private TextView currencyText;
    private LinearLayout characterGrid;
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
        currencyText = findViewById(R.id.currencyText);
        characterGrid = findViewById(R.id.characterGrid);
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
            startActivity(new android.content.Intent(this, NewCrewMember.class));
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
        LinearLayout currentRow = null;
        int index = 0;

        // loop through every single crew member in the ColonyManager
        for (CrewMember crew : manager) {

            // Create a new horizontal row for every 2 items
            if (index % 2 == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                characterGrid.addView(currentRow);
            }

            // Inflate into currentRow instead of characterGrid
            View cardView = getLayoutInflater().inflate(R.layout.crew_card, currentRow, false);

            // Hide the cost text since they already own this crew member
            cardView.findViewById(R.id.card_cost).setVisibility(View.GONE);

            ImageView characterImage = cardView.findViewById(R.id.card_image);
            switch (crew.getType()) {
                case "Medic":
                    characterImage.setImageResource(R.drawable.crew_medic);
                    break;
                case "Pilot":
                    characterImage.setImageResource(R.drawable.crew_pilot);
                    break;
                case "Engineer":
                    characterImage.setImageResource(R.drawable.crew_engineer);
                    break;
                case "Scientist":
                    characterImage.setImageResource(R.drawable.crew_scientist);
                    break;
                case "Soldier":
                    characterImage.setImageResource(R.drawable.crew_scientist); // Assuming this should be crew_soldier if you have one!
                    break;
            }

            TextView nameRole = cardView.findViewById(R.id.card_name_role);
            TextView locText = cardView.findViewById(R.id.card_loc);
            TextView expText = cardView.findViewById(R.id.card_exp);
            TextView energyText = cardView.findViewById(R.id.card_energy);

            nameRole.setText(crew.getName() + "\n" + crew.getType());
            locText.setText(" Loc: " + crew.getLocation().toString().toLowerCase());
            expText.setText(" Exp: " + crew.getExperience());
            energyText.setText(" Energy: " + crew.getEnergy() + "%");

            // SELECTION LOGIC
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(android.graphics.Color.parseColor("#2D1050"));
            gd.setCornerRadius(8f);

            if (selectedCrewIds.contains(crew.getId())) {
                gd.setStroke(6, android.graphics.Color.GREEN);
            } else {
                gd.setStroke(0, android.graphics.Color.TRANSPARENT);
            }
            cardView.setBackground(gd);

            // Clickable cards
            cardView.setOnClickListener(v -> {
                if (crew.getLocation() != Location.QUARTERS) {
                    Toast.makeText(this, crew.getName() + " is currently unavailable.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCrewIds.contains(crew.getId())) {
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

            // Greyscale logic
            if (crew.getLocation() == Location.SIMULATOR) {
                locText.setTextColor(android.graphics.Color.GRAY);
                expText.setTextColor(android.graphics.Color.GRAY);
                energyText.setTextColor(android.graphics.Color.GRAY);
                nameRole.setTextColor(android.graphics.Color.GRAY);
                characterImage.setColorFilter(android.graphics.Color.GRAY); // Greyscale the image too!
            } else {
                locText.setTextColor(android.graphics.Color.parseColor("#C084B8"));
                expText.setTextColor(android.graphics.Color.parseColor("#C084B8"));
                energyText.setTextColor(android.graphics.Color.parseColor("#C084B8"));
                nameRole.setTextColor(android.graphics.Color.parseColor("#FFB3DE"));
                characterImage.clearColorFilter(); // Remove greyscale
            }

            // Add the card to the current row, then increment index
            currentRow.addView(cardView);
            index++;
        }

        // Current colony currency
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