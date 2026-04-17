package com.example.cosmosjaatelo;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewCrewMember extends AppCompatActivity {

    private TextView currencyText;
    private LinearLayout recruitContainer;
    private List<PotentialRecruit> shopPool = new ArrayList<>();
    private SharedPreferences prefs;

    class PotentialRecruit {
        String role;
        int cost;
        PotentialRecruit(String role, int cost) {
            this.role = role;
            this.cost = cost;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_crew_members);

        prefs = getSharedPreferences("CosmosSaveData", MODE_PRIVATE);
        currencyText = findViewById(R.id.currencyTextBuy);
        recruitContainer = findViewById(R.id.buyRecruitContainer);

        findViewById(R.id.backBtnBuy).setOnClickListener(v -> finish());

        generateShopPool();
        refreshShopUI();
    }

    private void generateShopPool() {
        String[] roles = {"Soldier", "Medic", "Pilot", "Scientist", "Engineer"};
        Random random = new Random();

        //generationg
        int shopSize = 4 + random.nextInt(3);
        for (int i = 0; i < shopSize; i++) {
            String randomRole = roles[random.nextInt(roles.length)];
            //
            int cost = CrewUtils.getBaseCost(randomRole) + random.nextInt(6);
            shopPool.add(new PotentialRecruit(randomRole, cost));
        }
    }

    private void refreshShopUI() {
        // money text
        int currentIceCreams = prefs.getInt("TOTAL_ICE_CREAMS", 0);
        currencyText.setText(String.valueOf(currentIceCreams));

        recruitContainer.removeAllViews();
        LinearLayout currentRow = null;

        for (int i = 0; i < shopPool.size(); i++) {

            PotentialRecruit recruit = shopPool.get(i);

            if (i % 2 == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                recruitContainer.addView(currentRow);
            }

            View cardView = getLayoutInflater().inflate(R.layout.crew_card, currentRow, false);
            TextView nameRole = cardView.findViewById(R.id.card_name_role);
            ImageView image = cardView.findViewById(R.id.card_image);
            TextView costText = cardView.findViewById(R.id.card_cost);

            // hide stats
            cardView.findViewById(R.id.card_loc).setVisibility(View.GONE);
            cardView.findViewById(R.id.card_exp).setVisibility(View.GONE);
            cardView.findViewById(R.id.card_energy).setVisibility(View.GONE);

            nameRole.setText("???\n" + recruit.role);
            costText.setText("Cost: " + recruit.cost);

            //make each char unique
            switch (recruit.role) {
                case "Medic":
                    image.setImageResource(R.drawable.crew_medic);
                    break;
                case "Pilot":
                    image.setImageResource(R.drawable.crew_pilot);
                    break;
                case "Engineer":
                    image.setImageResource(R.drawable.crew_engineer);
                    break;
                case "Scientist":
                    image.setImageResource(R.drawable.crew_scientist);
                    break;
                case "Soldier":
                    image.setImageResource(R.drawable.crew_soldier);
                    break;
            }
            image.setColorFilter(CrewUtils.getRandomColor(), android.graphics.PorterDuff.Mode.MULTIPLY);

            cardView.setOnClickListener(v -> attemptPurchase(recruit));

            currentRow.addView(cardView);
        }
    }

    private void attemptPurchase(PotentialRecruit recruit) {
        int currentIceCreams = prefs.getInt("TOTAL_ICE_CREAMS", 0);

        if (currentIceCreams >= recruit.cost) {
            showNamingDialog(recruit);
        } else {
            Toast.makeText(this, "Not enough Ice Creams!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNamingDialog(PotentialRecruit recruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recruit " + recruit.role);
        builder.setMessage("Enter a name for your new crew member:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Recruit", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) name = "Rookie"; // Fallback name


            int newBalance = prefs.getInt("TOTAL_ICE_CREAMS", 0) - recruit.cost;
            prefs.edit().putInt("TOTAL_ICE_CREAMS", newBalance).apply();

            ColonyManager manager = ColonyManager.getInstance();
            manager.recruitCrew(name, recruit.role);
            manager.saveToFile(this);

            shopPool.remove(recruit);
            Toast.makeText(this, name + " joined the colony!", Toast.LENGTH_SHORT).show();
            refreshShopUI();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}