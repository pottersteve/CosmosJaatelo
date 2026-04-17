package com.example.cosmosjaatelo;

import android.graphics.Color;

public class CrewUtils {
    public static int getBaseCost(String role) {
        switch (role) {
            case "Medic":     return 10;
            case "Engineer":  return 12;
            case "Pilot":     return 15;
            case "Scientist": return 20;
            case "Soldier":   return 25;
            default:          return 10;
        }
    }

    public static int getRandomColor() {
        java.util.Random rnd = new java.util.Random();
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
