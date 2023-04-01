package com.barisi.flavio.bibbiacattolica;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.barisi.flavio.bibbiacattolica.model.Libro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.List;

public class IntroActivity extends AppIntro2 {

    public static final String PRIMA_VOLTA = "prima_volta";
    private boolean primaVolta = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, Preferenze.ottieniColorePrimario(this));
            setTaskDescription(taskDesc);
        }
        super.onCreate(savedInstanceState);
        primaVolta = getIntent().getBooleanExtra(PRIMA_VOLTA, true);

        setImageSkipButton(getResources().getDrawable(R.drawable.skip));
        setProgressButtonEnabled(true);
        setImmersiveMode(true);
        setColorTransitionsEnabled(true);

        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), getString(R.string.intro1), R.drawable.chiavi, Preferenze.ottieniColorePrimario(this)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title_home), getString(R.string.introHome), R.drawable.home, getResources().getColor(R.color.intro1)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.introRicercaTitolo), getString(R.string.introRicercaTesto), R.drawable.ricerca, getResources().getColor(R.color.intro2)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.introCarattere), getString(R.string.introCarattereTesto), R.drawable.opzioni_carattere, getResources().getColor(R.color.intro3)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.introCapitoloTitolo), getString(R.string.introCapitoloTesto), R.drawable.segnalibri_versetto, getResources().getColor(R.color.intro4)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.introSegnalibriTitolo), getString(R.string.introSegnalibri), R.drawable.segnalibri, getResources().getColor(R.color.intro5)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.impostazioni), getString(R.string.introImpostazioni), R.drawable.impostazioni, getResources().getColor(R.color.intro6)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.fine), getString(R.string.introFine), R.drawable.pesce, Preferenze.ottieniColorePrimario(this)));
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        this.finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        this.finish();
    }

}