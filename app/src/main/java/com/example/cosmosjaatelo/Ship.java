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



public class Ship{
    ImageView shipView;
    int speed = 75;
    int shipSize = 60;  //the size of the sprite, kind of has to be hard coded
    int thingThatfalls = -1;
    //screen sizes
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;


    //constructor
    Ship(ImageView existingShipView) {
        this.shipView = existingShipView;
        controls();
    }

    void moveLeft() {
        if (shipView.getX() - speed > 0) {
            shipView.setX(shipView.getX() - speed);
        }
    }

    void moveRight() {
        if (shipView.getX() + speed < screenWidth - shipSize) {
            shipView.setX(shipView.getX() + speed);
        }
    }

    void moveUp() {
        if (shipView.getY() - speed > 0) {
            shipView.setY(shipView.getY() - speed);
        }
    }

    void moveDown() {
        if (shipView.getY() + speed < screenHeight - shipSize) {
            shipView.setY(shipView.getY() + speed);
        }
    }

    @SuppressLint("ClickableViewAccessibility") //no idea what this is but was suggested when i got errors
    void controls(){
        shipView.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.setX(event.getRawX() + dX);
                        view.setY(event.getRawY() + dY);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    //abilities
    void shoot(List<ImageView> activeMeteors){
        //create bullet
        View bullet = new View(shipView.getContext()); //heavy gemini help
        bullet.setBackgroundColor(Color.WHITE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(6, 40);
        bullet.setLayoutParams(params);

        //position bullet
        float startX = shipView.getX() + (shipView.getWidth() / 2f) - 3f;
        float startY = shipView.getY();
        bullet.setX(startX);
        bullet.setY(startY);

        //add bullet to game
        ViewGroup gameLayout = (ViewGroup) shipView.getParent();
        gameLayout.addView(bullet);

        moveBullet(bullet, gameLayout, activeMeteors);
    }

    void moveBullet(View bullet, ViewGroup gameLayout, List<ImageView> activeMeteors) {
        Handler handler = new Handler(Looper.getMainLooper());
        int bulletSpeed = 40;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                bullet.setY(bullet.getY() - bulletSpeed);
                Rect bulletRect = new Rect();
                bullet.getHitRect(bulletRect);

                boolean collided = false;

                //collisions AGAIN
                for(ImageView meteor : activeMeteors){
                    Rect meteorRect = new Rect();
                    meteor.getHitRect(meteorRect);

                    if(Rect.intersects(bulletRect, meteorRect)){
                        collided = true;
                        Random random = new Random();
                        int chance = random.nextInt(3);
                        if (chance == 0){
                            meteor.setImageResource((R.drawable.explosion));
                            meteor.setTag(0);
                        }
                        else if (chance == 1){
                            meteor.setImageResource((R.drawable.icecream));
                            meteor.setTag(1);
                        }
                        else{
                            gameLayout.removeView(meteor);
                            activeMeteors.remove(meteor);
                        }
                        break;
                    }
                }
                if (bullet.getY() + bullet.getHeight() < 0 || collided) {
                    gameLayout.removeView(bullet);
                } else {
                    // loop this runnable again in ~16 milliseconds (approx 60 FPS)
                    handler.postDelayed(this, 16);
                }
            }
        };
        handler.post(runnable);
    }
}