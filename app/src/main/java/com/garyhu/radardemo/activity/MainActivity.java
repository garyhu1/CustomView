package com.garyhu.radardemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.garyhu.radardemo.widget.PolylineView;
import com.garyhu.radardemo.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    PolylineView scoreTrend;
    int score[] = new int[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTrend = (PolylineView) findViewById(R.id.scoreTrend);

        int max = 700;
        int min = 650;

        Random ramdom = new Random();
        for(int i = 0; i < 6; i++)
        {
            score[i] = ramdom.nextInt(max) % (max - min + 1) + min;
        }
        scoreTrend.setScore(score);
    }
}
