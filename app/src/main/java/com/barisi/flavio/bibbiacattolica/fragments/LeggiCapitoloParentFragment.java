package com.barisi.flavio.bibbiacattolica.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.barisi.flavio.bibbiacattolica.Cache;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.calendario.DataUtils;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.Css;
import com.barisi.flavio.bibbiacattolica.gui.MyPagerAdapter;
import com.barisi.flavio.bibbiacattolica.gui.SplitPaneLayout;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;

import java.util.List;


@SuppressWarnings({"WrongConstant", "deprecation"})
public class LeggiCapitoloParentFragment extends LeggiAltaVoceFragment {

    public static final String ARG_ARTICLE_ID = "article_id";
    public static final String ARG_PAROLA_DA_CERCARE = "parolaDaCercare";
    private static final String STATE_NOTE = "StatoNote";

    private ScreenSlidePagerAdapter mPagerAdapter;
    private WebView noteView;
    private ServiziDatabase serviziDatabase;
    private SplitPaneLayout splitPanel;
    private String idCapitolo;
    private String parolaDaCercare;
    private ViewPager mPager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LeggiCapitoloParentFragment() {
    }

    public static LeggiCapitoloParentFragment newInstance(String idCapitolo, String parolaDaCercare) {
        LeggiCapitoloParentFragment fragment = new LeggiCapitoloParentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, idCapitolo);
        args.putString(ARG_PAROLA_DA_CERCARE, parolaDaCercare);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idCapitolo = getArguments().getString(ARG_ARTICLE_ID);
            parolaDaCercare = getArguments().getString(ARG_PAROLA_DA_CERCARE);
        }
        setHasOptionsMenu(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leggi_capitolo_parent, container, false);

        mPager = view.findViewById(R.id.pager);
        noteView = view.findViewById(R.id.note);
        Boolean accellerazioneHW = Preferenze.ottieniAccellerazioneHardware(getContext());
        if (!accellerazioneHW) {
            noteView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        noteView.getSettings().setBlockNetworkLoads(true);
        noteView.getSettings().setJavaScriptEnabled(true);
        int zoomDefault = Preferenze.zoomDefault(getContext());
        if (zoomDefault == -1) {
            float scale = getResources().getConfiguration().fontScale;
            noteView.getSettings().setTextZoom(Regex.round(0.75 * scale * 120, 1));
        } else {
            noteView.getSettings().setTextZoom(Regex.round(0.75 * zoomDefault, 1));
        }

        splitPanel = view.findViewById(R.id.visualizza_layout);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_NOTE)) {
                noteView.setVisibility(View.VISIBLE);
            } else {
                noteView.setVisibility(View.GONE);
            }
        } else {
            Boolean mostraNote = Preferenze.ottieniMostraNote(getContext());
            if (mostraNote) {
                splitPanel.setSplitterPositionPercent(0.7f);
                noteView.setVisibility(WebView.VISIBLE);
            } else {
                splitPanel.setSplitterPositionPercent(1.0f);
                noteView.setVisibility(WebView.GONE);
            }
        }

        serviziDatabase = new ServiziDatabase(getContext());
        List<String> articleIds = Cache.getArticleIds(getContext());
        int posizione = articleIds.indexOf(idCapitolo);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), articleIds, posizione, parolaDaCercare);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                visualizzaNote(mPagerAdapter.getId(position));
            }
        });
        mPager.setCurrentItem(posizione);
        inizializza();
        return view;
    }

    protected void aggiornaNote() {
        visualizzaNote(mPagerAdapter.getId(mPager.getCurrentItem()
        ));
    }

    @Override
    MyPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_NOTE, noteView.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_note: {
                if (getActivity() != null) {
                    if (noteView.getVisibility() == WebView.VISIBLE) {
                        splitPanel.setSplitterPositionPercent(1.0f);
                        noteView.setVisibility(WebView.GONE);
                        getActivity().invalidateOptionsMenu();
                        Preferenze.salvaMostraNote(getContext(), false);
                    } else {
                        splitPanel.setSplitterPositionPercent(0.7f);
                        noteView.setVisibility(WebView.VISIBLE);
                        getActivity().invalidateOptionsMenu();
                        Preferenze.salvaMostraNote(getContext(), true);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    MyPagerAdapter getMPagerAdapter() {
        return mPagerAdapter;
    }

    private void visualizzaNote(final String articleId) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    return serviziDatabase.note(articleId);
                } catch (Exception e) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String note) {
                if (getContext() != null) {
                    int modalitaNotte = Preferenze.ottieniModalitaNotte(getContext());
                    String stiliAggiuntivi =
                            Css.getColoreStyle(getContext(), modalitaNotte);
                    String n = DataUtils.intestazioneHtml(stiliAggiuntivi, note, false, null, false);
                    noteView.loadDataWithBaseURL("file:///android_asset/", n, "text/html", "UTF-8", null);
                }
            }
        };
        task.execute();
    }

    private class ScreenSlidePagerAdapter extends MyPagerAdapter {
        private final int posizioneIniziale;
        private List<String> idCapitoli;
        private String parolaDaCercare;
        private LeggiCapitoloFragment mCurrentFragment;

        ScreenSlidePagerAdapter(FragmentManager fm, List<String> idCapitoli, int posizioneIniziale, String parolaDaCercare) {
            super(fm);
            this.idCapitoli = idCapitoli;
            this.posizioneIniziale = posizioneIniziale;
            this.parolaDaCercare = parolaDaCercare;
        }

        String getId(int position) {
            return idCapitoli.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return LeggiCapitoloFragment.newInstance(idCapitoli.get(position), position == posizioneIniziale, position == posizioneIniziale ? parolaDaCercare : null);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).update();
            }
            return super.getItemPosition(object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (LeggiCapitoloFragment) object;
                cambiaPagina();
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return idCapitoli.size();
        }

        @Override
        public LeggiFragment getmCurrentFragment() {
            return mCurrentFragment;
        }
    }

    public void coloreNoteSplitter() {
        int modalitaNotte = Preferenze.ottieniModalitaNotte(getContext());
        if (getContext() != null) {
            if (modalitaNotte == 0) {
                splitPanel.setColor(Preferenze.colorePrincipale(getContext()));
            } else if (modalitaNotte == 1) {
                splitPanel.setColor(Preferenze.coloreSecondario(getContext()));
            } else if (modalitaNotte == 2) {
                splitPanel.setColor(Preferenze.colorePrincipale(getContext()));
            } else if (modalitaNotte == 3) {
                splitPanel.setColor(Preferenze.coloreSecondario(getContext()));
            }
        }
    }
}
