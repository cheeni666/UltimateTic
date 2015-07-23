package com.myprey.ultimatetic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
    static int j=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Engine v = (Engine) findViewById(R.id.surfaceView);
        if (getIntent().getBooleanExtra("BACK", false)) {
            finish();
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        Intent i=new Intent(this,HELP.class);
        if(j==0){
            startActivity(i);
            j++;
        }
    }
}
