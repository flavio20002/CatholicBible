package com.barisi.flavio.bibbiacattolica.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.tasks.PrayersTask;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ListaPreghiereCardAdapter;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.VerticalRecyclerViewFastScroller;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.SectionTitleIndicator;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class PrayersFragment extends Fragment {

    public RecyclerView recList;
    public ListaPreghiereCardAdapter mAdapter;
    public SectionTitleIndicator sectionTitleIndicator;
    public VerticalRecyclerViewFastScroller fastScroller;
    public AppBarLayout appBar;
    private View hintIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new ListaPreghiereCardAdapter(new ArrayList<Preghiera>(), (MainActivity) getActivity());
        refreshItems("");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prayers, container, false);
        recList = view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        if (getActivity() != null) {
            recList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recList.setAdapter(mAdapter);

            //Fast scrolling
            if (getActivity() != null) {
                fastScroller = view.findViewById(R.id.fast_scroller);
                sectionTitleIndicator = view.findViewById(R.id.fast_scroller_section_title_indicator);
                appBar = getActivity().findViewById(R.id.appBarLayout);

                fastScroller.setRecyclerView(recList);
                fastScroller.setAppBar(appBar);
                fastScroller.setSectionIndicator(sectionTitleIndicator);

                //Hint
                hintIndicator = view.findViewById(R.id.hint);
            }
        }
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.preghiere, menu);
        Utility.aggiornaTitoloActionBar((AppCompatActivity) getActivity(), getString(R.string.preghiere), null);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
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
    }

    private void refreshItems(final String query) {
        new PrayersTask(this, query).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null && hintIndicator != null && !Preferenze.seHintVisto("hint_fast_scrolling_preghiere", getContext())) {
            Preferenze.settaHintVisto("hint_fast_scrolling_preghiere", getContext());
            new MaterialShowcaseView.Builder(getActivity())
                    .setTarget(hintIndicator)
                    .setDismissOnTouch(true)
                    .setMaskColour(Utility.adjustAlpha(Preferenze.ottieniColorePrimario(getContext()), Costanti.TRASPARENZA_HINT_FAST_SCROLLING))
                    .setDismissText(getString(R.string.hintVisto))
                    .setContentText(getString(R.string.hintFastScrollingPreghiere))
                    .show();
        }
    }
}
