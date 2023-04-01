package com.barisi.flavio.bibbiacattolica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;
import com.barisi.flavio.bibbiacattolica.tasks.MainTask;
import com.rey.material.widget.ProgressView;


public class LoadingActivity extends MyActivity {

    private TextView myTextView;
    private ProgressView progressView;
    private View sadImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish();
                return;
            }
        }
        if (Inizializzazione.daInizializzare(this)) {
            setContentView(R.layout.activity_loading);
            myTextView = findViewById(R.id.testoMessaggio);
            progressView = findViewById(R.id.progressView);
            sadImageView = findViewById(R.id.sadImage);
            new MainTask(this).execute();
        } else {
            startMainActivity();
        }
    }

    public void showLoginLoadingScreen() {
        progressView.setVisibility(View.VISIBLE);
        sadImageView.setVisibility(View.GONE);
        myTextView.setText(R.string.PreparazioneCorso);
    }

    public void stopAnimation() {
        progressView.stop();
    }

    public void showErrorScreen() {
        progressView.setVisibility(View.GONE);
        sadImageView.setVisibility(View.VISIBLE);
        myTextView.setText(R.string.errore_des);
    }
    
    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

}
