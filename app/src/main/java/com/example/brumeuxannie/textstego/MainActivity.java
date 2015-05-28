package com.example.brumeuxannie.textstego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ShareActionProvider;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
public class MainActivity extends ActionBarActivity implements
		OnClickListener {

    private Toolbar toolbar;


    /**
     * to hide image data
     */
    private Button b_text;

    /**
     * toreveal image data
     */
    private Button b_reveal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        b_text = (Button) findViewById(R.id.btext);
        b_reveal = (Button) findViewById(R.id.button_reveal_msg);
        b_reveal.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent x = new Intent(getApplicationContext(), RevealInfo.class);
                startActivity(x);
            }
        });
        b_text.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent t = new Intent(getApplicationContext(), TextHide.class);
                startActivity(t);
            }
        });
    }

    @Override
    public void onClick (View v){

    }
}