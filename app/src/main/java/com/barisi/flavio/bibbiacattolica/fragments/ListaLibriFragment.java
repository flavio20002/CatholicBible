package com.barisi.flavio.bibbiacattolica.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ListaLibriCardAdapter;
import com.barisi.flavio.bibbiacattolica.adapter.ListaLibriGridAdapter;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.SpacesItemDecoration;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.VerticalRecyclerViewFastScroller;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.SectionTitleIndicator;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;
import com.barisi.flavio.bibbiacattolica.model.Categoria;
import com.barisi.flavio.bibbiacattolica.model.Testamento;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ListaLibriFragment extends Fragment implements UpdateableFragment {

    private OnLibriFragmentInteractionListener mListener;

    private Testamento testamento;
    private ListaLibriCardAdapter mAdapter1;
    private ListaLibriGridAdapter mAdapter2;
    private RecyclerView recList;
    private ServiziDatabase serviziDatabase;
    private SectionTitleIndicator sectionTitleIndicator;
    private View hintIndicator;
    private VerticalRecyclerViewFastScroller fastScroller;
    private AppBarLayout appBar;
    private SpacesItemDecoration itemDecoration;
    private int modalitaVisualizzazione;
    private ViewGroup.LayoutParams layoutParams;
    private SearchView searchView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListaLibriFragment() {
    }

    public static ListaLibriFragment newInstance(Testamento testamento) {
        ListaLibriFragment fragment = new ListaLibriFragment();
        Bundle args = new Bundle();
        args.putSerializable("testamento", testamento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviziDatabase = new ServiziDatabase(getActivity());
        if (getArguments() != null) {
            testamento = (Testamento) getArguments().getSerializable("testamento");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_libri, container, false);

        // RecyclerView
        recList = (RecyclerView) view.findViewById(R.id.cardListCategory);
        recList.setHasFixedSize(true);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.my_margin);
        itemDecoration = new SpacesItemDecoration(spacingInPixels);
        layoutParams = recList.getLayoutParams();
        mAdapter1 = new ListaLibriCardAdapter(getActivity(), mListener);
        mAdapter2 = new ListaLibriGridAdapter(mListener);


        //Fast scrolling
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);

        //Hint
        hintIndicator = view.findViewById(R.id.hint);

        modalitaVisualizzazione = Preferenze.ottieniVisualizzazionieLibri(getContext());
        impostaVisualizzazione();
        refreshItems("");
        //Fabs
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideFabs();
        }
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
            fastScroller.setRecyclerView(recList);
            fastScroller.setAppBar(appBar);
            fastScroller.setSectionIndicator(sectionTitleIndicator);
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
            mListener = (OnLibriFragmentInteractionListener) activity;
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

    private void aggiornaActionBar() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(testamento.getDescrizione(getContext()));
                actionBar.setSubtitle(String.format(getString(R.string.numeroLibri), testamento.getNumeroLibri()));
            }
        }
    }

    private void refreshItems(final String query) {
        AsyncTask<String, Void, List<Categoria>> task = new AsyncTask<String, Void, List<Categoria>>() {
            @Override
            protected List<Categoria> doInBackground(String... strings) {
                try {
                    return serviziDatabase.listaLibri(testamento.getCodice(), query);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Categoria> categories) {
                if (categories != null) {
                    mAdapter1.addItems(categories);
                    mAdapter2.addItems(categories);
                }
            }
        };
        task.execute();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && sectionTitleIndicator != null) {
            sectionTitleIndicator.animateAlpha(0f);
        }
        if (isVisibleToUser && hintIndicator != null && !Preferenze.seHintVisto("hint_fast_scrolling", getContext())) {
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
    public void update() {
        int visualizzazione = Preferenze.ottieniVisualizzazionieLibri(getContext());
        if (recList != null && modalitaVisualizzazione != visualizzazione) {
            modalitaVisualizzazione = visualizzazione;
            impostaVisualizzazione();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.lista_libri_grid) {
            int modalitaVisualizzazione = Preferenze.ottieniVisualizzazionieLibri(getContext());
            if (modalitaVisualizzazione == 0) {
                modalitaVisualizzazione = 1;
            } else {
                modalitaVisualizzazione = 0;
            }
            Preferenze.salvaVisualizzazionieLibri(getContext(), modalitaVisualizzazione);
            this.modalitaVisualizzazione = modalitaVisualizzazione;
            impostaVisualizzazione();
            if (getActivity() != null) {
                getActivity().invalidateOptionsMenu();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}