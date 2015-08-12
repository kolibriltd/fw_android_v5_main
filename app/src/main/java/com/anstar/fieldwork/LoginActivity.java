package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onLoginDidSuccess() {
        Intent i = new Intent(this, DashboardActivity.class);
        startActivity(i);
        finish();
    }
}
