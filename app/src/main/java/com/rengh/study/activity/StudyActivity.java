package com.rengh.study.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rengh.study.R;
import com.rengh.study.model.BlackDuck;
import com.rengh.study.model.Duck;

public class StudyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        BlackDuck blackDuck = new BlackDuck();
    }
}
