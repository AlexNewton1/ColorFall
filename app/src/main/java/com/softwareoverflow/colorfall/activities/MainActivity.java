package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.softwareoverflow.colorfall.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void playGame(View v){

        startActivity(new Intent(this, GameActivity.class));


        //TODO get levels from v.getId() and pass in
        switch (v.getId()){

        }
    }
}
