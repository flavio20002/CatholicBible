package com.barisi.flavio.bibbiacattolica;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

@SuppressLint("Registered")
@SuppressWarnings("deprecation")
public class MyActivity extends AppCompatActivity {

    final static private int SPL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Carica preferenze di default
        if (Preferenze.ottieniImpostaPreferenzeDefault(this) != SPL) {
            Preferenze.configuraImpostazioniDefault(this);
            Preferenze.salvaImpostaPreferenzeDefault(this, SPL);
        }
        String tema = Preferenze.ottieniTema(this);
        impostaTema(this, tema);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    private Context updateBaseContextLocale(Context context) {
        String language = Preferenze.ottieniLingua(context);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    /*private void impostaLingua(AppCompatActivity myActivity) {
        String lingua = Preferenze.ottieniLingua(myActivity);
        Configuration conf = getResources().getConfiguration();
        conf.locale = new Locale(lingua);
        getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
        //In Android N the behavour is different, so the message appears too often
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            String linguaSalvata = Preferenze.ottieniLinguaDispositivo(myActivity);
            String linguaNuova = Preferenze.ottieniLinguaDaLocale();
            if (!linguaNuova.equals(linguaSalvata)) {
                if (!linguaNuova.equals(lingua)) {
                    messaggioAvviso(this, getString(R.string.attenzione), getString(R.string.cambio_lingua), linguaNuova);
                }
                Preferenze.salvaLinguaDispositivo(myActivity, linguaNuova);
            }
        }
    }*/

    void inizializzaToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            try {
                setSupportActionBar(toolbar);
            } catch (Exception e) {
                //
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setSubtitle("");
        }
        if (toolbar != null) {
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            if (params instanceof AppBarLayout.LayoutParams) {
                AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) params;
                Boolean nascondiToolbar = Preferenze.ottieniNascondiToolbar(this);
                p.setScrollFlags(nascondiToolbar ? AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS : 0);
            }
        }
    }

    public static void impostaTema(AppCompatActivity activity, String tema) {
        int colore = Preferenze.ottieniColorePrimario(activity);
        switch (tema) {
            case "light":
                activity.setTheme(R.style.AppTheme_Light);
                break;
            case "dark":
                activity.setTheme(R.style.AppTheme_Dark);
                break;
            case "yellow":
                activity.setTheme(R.style.AppTheme_Light);
                activity.getTheme().applyStyle(R.style.OverlayYellowTheme, true);
                break;
            default:
                activity.setTheme(R.style.AppTheme_Dark);
                activity.getTheme().applyStyle(R.style.OverlayBlackTheme, true);
                break;
        }
        if (colore == activity.getResources().getColor(R.color.colorPrimary2)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor2, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary3)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor3, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary4)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor4, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary5)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor5, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary6)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor6, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary7)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor7, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary8)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor8, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary9)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor9, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary10)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor10, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary11)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor11, true);
        } else if (colore == activity.getResources().getColor(R.color.colorPrimary12)) {
            activity.getTheme().applyStyle(R.style.OverlayPrimaryColor12, true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(activity.getString(R.string.app_name), bm, Preferenze.ottieniColorePrimario(activity));
            activity.setTaskDescription(taskDesc);
        }
    }

    /*private void messaggioAvviso(final Context c, String titolo, String testo, final String linguaNuova) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titolo);
        builder.setMessage(testo);
        builder.setCancelable(true);
        final AlertDialog alert = builder.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Preferenze.salvaLingua(c, linguaNuova);
                Cache.clearCache();
                try {
                    Inizializzazione.caricaDatabaseSeNecessario(c, Preferenze.ottieniVersioneBibbia(c));
                } catch (Exception e) {
                    //
                }
                MyActivity.this.recreate();
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }*/

    @Override
    protected void onResume() {
        String lingua = Preferenze.ottieniLingua(this);
        Configuration conf = getResources().getConfiguration();
        conf.locale = new Locale(lingua);
        getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
        super.onResume();
    }
}
