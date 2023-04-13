package com.barisi.flavio.bibbiacattolica.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.tasks.MapsTask;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ListaMappeCardAdapter;
import com.barisi.flavio.bibbiacattolica.model.Mappa;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    public ListaMappeCardAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        RecyclerView recList = view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        if (getActivity() != null) {
            mAdapter = new ListaMappeCardAdapter(new ArrayList<Mappa>(), (MainActivity) getActivity());
            recList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recList.setAdapter(mAdapter);
            refreshItems();
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
        Utility.aggiornaTitoloActionBar((AppCompatActivity) getActivity(), getString(R.string.maps), null);
    }

    private void refreshItems() {
        new MapsTask(this).execute();
    }
}
