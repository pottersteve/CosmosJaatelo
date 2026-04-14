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
}