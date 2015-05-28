package com.example.brumeuxannie.textstego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity  {

    /** Button for Hide Message Activity */
    private Button mHideBtn;

    /** Button for Reveal Data Activity */
    private Button mRevealBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setUpButtonListeners();

    }

    private void init() {

        mHideBtn   = (Button) findViewById(R.id.btext);
        mRevealBtn = (Button) findViewById(R.id.button_reveal_msg);

    }

    private void setUpButtonListeners() {

        mRevealBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openClass(RevealInfo.class);
            }
        });
        mHideBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                openClass(TextHide.class);
            }
        });
    }

    private void openClass(Class nextClass){
        Intent i = new Intent(getApplicationContext(), nextClass);
        startActivity(i);
    }
}