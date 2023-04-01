package com.barisi.flavio.bibbiacattolica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.barisi.flavio.bibbiacattolica.calendario.Util;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;
import com.barisi.flavio.bibbiacattolica.fragments.CreditsFragment;
import com.barisi.flavio.bibbiacattolica.fragments.HomeFragment;
import com.barisi.flavio.bibbiacattolica.fragments.LeggiCapitoloParentFragment;
import com.barisi.flavio.bibbiacattolica.fragments.LeggiLettureGiornoParentFragment;
import com.barisi.flavio.bibbiacattolica.fragments.ListaCapitoliParentFragment;
import com.barisi.flavio.bibbiacattolica.fragments.ListaLibriFragment;
import com.barisi.flavio.bibbiacattolica.fragments.MapViewFragment;
import com.barisi.flavio.bibbiacattolica.fragments.MapsFragment;
import com.barisi.flavio.bibbiacattolica.fragments.PrayerViewFragment;
import com.barisi.flavio.bibbiacattolica.fragments.PrayersFragment;
import com.barisi.flavio.bibbiacattolica.fragments.SegnalibriFragment;
import com.barisi.flavio.bibbiacattolica.fragments.SettingsFragment;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnMappaInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnPreghieraInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnTestamentoInteraction;
import com.barisi.flavio.bibbiacattolica.model.Libro;
import com.barisi.flavio.bibbiacattolica.model.Mappa;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;
import com.barisi.flavio.bibbiacattolica.model.Testamento;
import com.barisi.flavio.bibbiacattolica.tasks.MainTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends MyActivity
        implements OnArticleFragmentInteractionListener,
        OnLibriFragmentInteractionListener,
        OnTestamentoInteraction,
        NavigationView.OnNavigationItemSelectedListener,
        OnPreghieraInteractionListener,
        OnMappaInteractionListener {

    public ActionBarDrawerToggle mDrawerToggle;
    private int nextPosition;
    private NavigationView navigationView;
    private List<Class> listaFragmentHamburgerIcon = Arrays.asList(new Class[]{HomeFragment.class, ListaLibriFragment.class, MapsFragment.class, PrayersFragment.class, LeggiLettureGiornoParentFragment.class, SegnalibriFragment.class});
    private FloatingActionButton fab, fab2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (!Preferenze.seHintVisto("intro", MainActivity.this)) {
            Preferenze.settaHintVisto("intro", MainActivity.this);
            lanciaIntro(true);
        }*/
        inizializzaActivity(savedInstanceState);
        if (Utility.isDebuggable(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

    }

    public void inizializzaActivity(Bundle savedInstanceState) {
        inizializzaToolbar();
        inizializzaNavigationDrawer(savedInstanceState);
        handleBackButton();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                handleBackButton();
            }
        });
        if (savedInstanceState == null) {
            HomeFragment firstFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, firstFragment).commitAllowingStateLoss();
        }

        fab = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        hideFabs();
    }

    private void inizializzaNavigationDrawer(Bundle savedInstanceState) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                if (nextPosition != -1) {
                    navigationDrawerItemSelected(nextPosition);
                    nextPosition = -1;
                }
            }
        };
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nextPosition = -1;
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void navigationDrawerItemSelected(int position) {
        Utility.closeKeyboard(this);
        if (position == 1) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (position == 2) {
            aggiungiFragment(ListaLibriFragment.newInstance(Testamento.getAntico()), false);
        } else if (position == 3) {
            aggiungiFragment(ListaLibriFragment.newInstance(Testamento.getNuovo()), false);
        } else if (position == 4) {
            aggiungiFragment(LeggiLettureGiornoParentFragment.newInstance(null, Util.getDataCorrente()), false);
        } else if (position == 5) {
            aggiungiFragment(new SegnalibriFragment(), false);
        } else if (position == 6) {
            aggiungiFragment(new SettingsFragment(), true);
        } else if (position == 7) {
            aggiungiFragment(new CreditsFragment(), true);
        } else if (position == 8) {
            lanciaIntro(false);
        } else if (position == 9) {
            aggiungiFragment(new MapsFragment(), false);
        } else if (position == 10) {
            aggiungiFragment(new PrayersFragment(), false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty, menu);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if (actionBar != null) {
            actionBar.setSubtitle(null);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        //setActionBarArrowDependingOnFragmentsBackStack();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.isDrawerIndicatorEnabled() && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home &&
                getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onArticleFragmentInteraction(String articleId, String parolaDaCercare) {
        aggiungiFragmentCumulative(LeggiCapitoloParentFragment.newInstance(String.valueOf(articleId), parolaDaCercare));
    }

    @Override
    public void onLibriFragmentInteraction(ArrayList<Libro> libri, int posizione) {
        aggiungiFragment(ListaCapitoliParentFragment.newInstance(new ArrayList<>(libri), posizione), true);
    }

    @Override
    public void onTestamentoInteraction(Testamento testamento) {
        if (testamento.getCodice().equals("A")) {
            navigationDrawerItemSelected(2);
            navigationView.setCheckedItem(R.id.nav_antico);
        } else {
            navigationDrawerItemSelected(3);
            navigationView.setCheckedItem(R.id.nav_nuovo);
        }
    }

    @Override
    public void onCercaLetture(String letture) {
        aggiungiFragment(LeggiLettureGiornoParentFragment.newInstance(letture, null), false);
    }

    @Override
    public void onLettureGiorno(Date data) {
        navigationDrawerItemSelected(4);
        navigationView.setCheckedItem(R.id.nav_daily);
        Utility.mostraToolBar(this);
    }

    @Override
    public void onApriSegnalibri() {
        navigationDrawerItemSelected(5);
        navigationView.setCheckedItem(R.id.nav_bookmarks);
        Utility.mostraToolBar(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onDestroy() {
        //Svuota cronologia
        Boolean svuotaCronologia = Preferenze.ottieniSvuotaCronologia(this);
        if (svuotaCronologia) {
            Preferenze.cancellaCronologia(this);
        }
        //Elimina file temporanei
        File dir = new File(getFilesDir(), "temp");
        String[] children = dir.list();
        if (children != null && children.length > 0) {
            for (String aChildren : children) {
                //noinspection ResultOfMethodCallIgnored
                new File(dir, aChildren).delete();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean result = true;
        if (id == R.id.nav_home) {
            nextPosition = 1;
        } else if (id == R.id.nav_antico) {
            nextPosition = 2;
        } else if (id == R.id.nav_nuovo) {
            nextPosition = 3;
        } else if (id == R.id.nav_daily) {
            nextPosition = 4;
        } else if (id == R.id.nav_bookmarks) {
            nextPosition = 5;
        } else if (id == R.id.nav_settings) {
            nextPosition = 6;
            result = false;
        } else if (id == R.id.nav_crediti) {
            nextPosition = 7;
            result = false;
        } else if (id == R.id.nav_guida) {
            nextPosition = 8;
            result = false;
        } else if (id == R.id.nav_maps) {
            nextPosition = 9;
        } else if (id == R.id.nav_prayers) {
            nextPosition = 10;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void lanciaIntro(final boolean primaVolta) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, IntroActivity.class);
                i.putExtra(IntroActivity.PRIMA_VOLTA, primaVolta);
                startActivity(i);
            }
        });
        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            boolean result = data.getBooleanExtra("risultato", false);
            if (result) {
                MainActivity.this.recreate();
            }
        }
    }

    private void aggiungiFragment(Fragment fragment, boolean accessory) {
        getSupportFragmentManager().popBackStack(accessory ? "accessory" : null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (accessory) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.fragment_container, fragment, "accessoryFragment");
        } else {
            transaction.replace(R.id.fragment_container, fragment);
        }
        transaction.addToBackStack(accessory ? "accessory" : null);
        transaction.commit();
    }

    private void aggiungiFragmentCumulative(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, "accessoryFragment");
        transaction.addToBackStack("accessory");
        transaction.commit();
    }


    private void handleBackButton() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (backStackEntryCount == 0) {
            changeToHaburgerIcon();
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (backStackEntryCount == 1 && fragment != null &&
                listaFragmentHamburgerIcon.contains(fragment.getClass())) {
            changeToHaburgerIcon();
        } else {
            changeToBackArrow();
        }
    }

    private void changeToBackArrow() {
        if (getSupportActionBar() != null) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void changeToHaburgerIcon() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }

    public void hideFabs() {
        if (fab != null && fab2 != null) {
            fab.setVisibility(FloatingActionButton.GONE);
            fab2.setVisibility(FloatingActionButton.GONE);
        }
    }


    @Override
    public void onPreghieraInteration(Preghiera preghiera) {
        aggiungiFragmentCumulative(PrayerViewFragment.newInstance(preghiera));
    }

    @Override
    public void onmappaInteraction(Mappa mappa) {
        aggiungiFragmentCumulative(MapViewFragment.newInstance(mappa));
    }
}
