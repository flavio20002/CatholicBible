package com.barisi.flavio.bibbiacattolica.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ListaCapitoliCardAdapter;
import com.barisi.flavio.bibbiacattolica.adapter.ListaCapitoliGridAdapter;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.SpacesItemDecoration;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.VerticalRecyclerViewFastScroller;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.SectionTitleIndicator;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;
import com.barisi.flavio.bibbiacattolica.model.Libro;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ListaCapitoliFragment extends Fragment implements UpdateableFragment {

    private static final String ARG_LIBRO = "libro";

    private Libro libro;
    private OnArticleFragmentInteractionListener mListener;
    private TextView emptyView;
    private ListaCapitoliCardAdapter mAdapter1;
    private ListaCapitoliGridAdapter mAdapter2;
    private RecyclerView recList;
    private ServiziDatabase serviziDatabase;
    private VerticalRecyclerViewFastScroller fastScroller;
    private AppBarLayout appBar;
    private SectionTitleIndicator sectionTitleIndicator;
    private View hintIndicator;
    private SpacesItemDecoration itemDecoration;
    private int modalitaVisualizzazione;
    private ViewGroup.LayoutParams layoutParams;
    private SearchView searchView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListaCapitoliFragment() {
    }

    public static ListaCapitoliFragment newInstance(Libro libro) {
        ListaCapitoliFragment fragment = new ListaCapitoliFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIBRO, libro);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            libro = (Libro) getArguments().getSerializable(ARG_LIBRO);
        }
        serviziDatabase = new ServiziDatabase(getActivity());
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capitoli, container, false);

        // RecyclerView
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.my_margin);
        itemDecoration = new SpacesItemDecoration(spacingInPixels);
        layoutParams = recList.getLayoutParams();
        mAdapter1 = new ListaCapitoliCardAdapter(mListener, getContext());
        mAdapter2 = new ListaCapitoliGridAdapter(mListener);

        //Fast scrolling
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);

        //Hint
        hintIndicator = view.findViewById(R.id.hint);

        //Empty View
        emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setVisibility(TextView.GONE);
        emptyView.setText(R.string.empty_list);
        modalitaVisualizzazione = Preferenze.ottieniVisualizzazionieCapitoli(getContext());
        impostaVisualizzazione();
        refreshItems("");
        return view;
    }

    private void impostaVisualizzazione() {
        recList.removeItemDecoration(itemDecoration);
        if (modalitaVisualizzazione == 0) {
            final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            FrameLayout.LayoutParams lP = new FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lP.setMargins(0, 0, 0, 0);
            recList.setLayoutParams(lP);
            if (mAdapter1.getItemCount() > 5) {
                fastScroller.setRecyclerView(recList);
                fastScroller.setAppBar(appBar);
                fastScroller.setSectionIndicator(sectionTitleIndicator);
            } else {
                fastScroller.setRecyclerView(null);
                fastScroller.setAppBar(null);
                fastScroller.setSectionIndicator(null);
            }
            recList.setAdapter(mAdapter1);
        } else {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
            recList.addItemDecoration(itemDecoration);
            recList.setLayoutManager(mLayoutManager);
            recList.setLayoutParams(layoutParams);
            fastScroller.setRecyclerView(null);
            fastScroller.setAppBar(null);
            fastScroller.setSectionIndicator(null);
            recList.setAdapter(mAdapter2);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArticleFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.lista_libri, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                refreshItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        aggiornaActionBar();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem bottoneGriglia = menu.findItem(R.id.lista_libri_grid);
        if (bottoneGriglia != null) {
            if (modalitaVisualizzazione == 0) {
                bottoneGriglia.setIcon(R.drawable.ic_grid_white_24dp);
            } else {
                bottoneGriglia.setIcon(R.drawable.ic_list_white_24dp);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void refreshItems(final String query) {
        AsyncTask<String, Void, List<Capitolo>> task = new AsyncTask<String, Void, List<Capitolo>>() {
            @Override
            protected List<Capitolo> doInBackground(String... strings) {
                try {
                    return serviziDatabase.listaCapitoli(libro.getCodLibro(), query);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<Capitolo> articles) {
                mAdapter1.addItems(articles);
                mAdapter2.addItems(articles);
                if (emptyView != null) {
                    if (articles.size() > 0) {
                        emptyView.setVisibility(TextView.GONE);
                    } else {
                        emptyView.setVisibility(TextView.VISIBLE);
                    }
                }
                if (modalitaVisualizzazione == 0 && mAdapter1.getItemCount() > 5) {
                    fastScroller.setRecyclerView(recList);
                    fastScroller.setAppBar(appBar);
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                } else {
                    fastScroller.setRecyclerView(null);
                    fastScroller.setAppBar(null);
                    fastScroller.setSectionIndicator(null);
                }
            }

        };
        task.execute();
    }

    private void aggiornaActionBar() {
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(libro.getDesLibro());
                if (libro.getNumeroCapitoli() > 0) {
                    String stringa;
                    if (libro.getCodLibro().equals("Sal")) {
                        stringa = getString(R.string.salmiMultipli);
                    } else {
                        stringa = libro.getNumeroCapitoli() == 1 ? getString(R.string.capitoloSingolo) : getString(R.string.capitoliMultipli);
                    }
                    actionBar.setSubtitle(String.format(stringa, libro.getNumeroCapitoli()));
                } else {
                    actionBar.setSubtitle(" ");
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && sectionTitleIndicator != null) {
            sectionTitleIndicator.animateAlpha(0f);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (sectionTitleIndicator != null) {
            sectionTitleIndicator.animateAlpha(0f);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recList.removeItemDecoration(itemDecoration);
                if (modalitaVisualizzazione == 1) {
                    recList.addItemDecoration(itemDecoration);
                }
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hintIndicator != null && !Preferenze.seHintVisto("hint_fast_scrolling", getContext())) {
            Preferenze.settaHintVisto("hint_fast_scrolling", getContext());
            new MaterialShowcaseView.Builder(getActivity())
                    .setTarget(hintIndicator)
                    .setDismissOnTouch(true)
                    .setMaskColour(Utility.adjustAlpha(Preferenze.ottieniColorePrimario(getContext()), Costanti.TRASPARENZA_HINT_FAST_SCROLLING))
                    .setDismissText(getString(R.string.hintVisto))
                    .setContentText(getString(R.string.hintFastScrolling))
                    .show();
        }
    }

    @Override
    public void update() {
        int visualizzazione = Preferenze.ottieniVisualizzazionieCapitoli(getContext());
        if (recList != null && modalitaVisualizzazione != visualizzazione) {
            modalitaVisualizzazione = visualizzazione;
            impostaVisualizzazione();
            getActivity().invalidateOptionsMenu();
        }
    }
}
