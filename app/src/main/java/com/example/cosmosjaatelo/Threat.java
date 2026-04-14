package com.example.cosmosjaatelo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Threat {
    int level;
    Random random = new Random();
    Handler handler = new Handler(Looper.getMainLooper());
    int spawnTimeSeconds = 2;
    int difficulty = 1;
    public boolean isSpawning = false;

    //active threats
    public List<ImageView> activeMeteors = new ArrayList<>();

    //screen sizes
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    //constructor
    public Threat(){
        this.level = 0;
    }

    //methods
    // spawns the damn rocks
    public void startMeteorSpawning(ViewGroup gameLayout, Context context) {
        isSpawning = true;
        Runnable meteorSpawner = new Runnable() {
            @Override
            public void run() { // Runnable must always use the method name run()
                if (!isSpawning) return;

                ImageView newMeteor = new ImageView(context);
                newMeteor.setImageResource(R.drawable.meteor);

                //size
                int meteorSize = 150;
                newMeteor.setLayoutParams(new ViewGroup.LayoutParams(meteorSize, meteorSize));

                //coordinates
                int meteorWidth = newMeteor.getWidth();
                if (meteorWidth == 0) meteorWidth = 100;

                int randomX = random.nextInt(screenWidth - meteorWidth);

                newMeteor.setX(randomX);
                newMeteor.setY(-200);

                activeMeteors.add(newMeteor);

                //add to screen
                gameLayout.addView(newMeteor);

                //make it go down, down sugar
                makeRocksFall(gameLayout, newMeteor);
                handler.postDelayed(this, spawnTimeSeconds * 500 * difficulty);
            }
        };

        //executes the timer, safely tucked inside our method!
        handler.post(meteorSpawner);
    }

    public void stopSpawning() {
        this.isSpawning = false;
        this.handler.removeCallbacksAndMessages(null);
    }

    void makeRocksFall(ViewGroup gameLayout, ImageView meteor){
        meteor.animate()
                .y(screenHeight + 200)
                .setDuration(5000)
                .withEndAction(() -> {
                    gameLayout.removeView(meteor);
                    activeMeteors.remove(meteor);
                })
                .start();
    }

    Boolean isDefeated(){
        return false;
    }

    String getDescription(){
        return "";
    }
}
