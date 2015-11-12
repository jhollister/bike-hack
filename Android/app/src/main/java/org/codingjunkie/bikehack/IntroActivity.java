package org.codingjunkie.bikehack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {
    private final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Button goBtn = (Button) findViewById(R.id.introActivityButton);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(IntroActivity.this, "Go", Toast.LENGTH_SHORT).show();
                Intent goIntent = new Intent(IntroActivity.this, SelectDeviceActivity.class);
                startActivity(goIntent);
//                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart!");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy!");
    }
}
