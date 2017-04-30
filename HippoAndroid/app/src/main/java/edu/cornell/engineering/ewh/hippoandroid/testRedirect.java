package edu.cornell.engineering.ewh.hippoandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class testRedirect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_redirect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("APP", MODE_PRIVATE);
        String token = sharedPreferences.getString("G_TOKEN", "None");
        String auth = sharedPreferences.getString("AUTH_CODE", "None");

//        String token2 = GoogleAuthUtil.getToken(this, "ldp54@cornell.edu",)

        System.out.print(token);

    }

}
