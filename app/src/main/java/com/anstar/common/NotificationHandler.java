package com.anstar.common;

import android.app.Activity;
import android.os.Bundle;

public class NotificationHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        String action = (String) getIntent().getExtras().get("do_action");
//        if (action != null) {
//            if (action.equals("Setting")) {
//            	Intent in =new Intent(getApplicationContext(),SettingsActivity.class);
//            	startActivity(in);
//                // for example play a music
//            } else if (action.equals("Dismiss")) {
//            	DailyQouteService.cancelNotification(getApplicationContext(), 1001);
//                // close current notification
//            }
//        }

        finish();
  }
}