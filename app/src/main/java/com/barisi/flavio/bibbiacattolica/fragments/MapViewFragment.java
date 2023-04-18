package com.barisi.flavio.bibbiacattolica.fragments;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.model.Mappa;
import com.github.chrisbanes.photoview.PhotoView;

public class MapViewFragment extends Fragment {

    private Mappa mappa;

    public MapViewFragment() {
    }

    public static MapViewFragment newInstance(Mappa mappa) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putSerializable("mappa", mappa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mappa = (Mappa) getArguments().getSerializable("mappa");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_map, container, false);
        final PhotoView photoView = view.findViewById(R.id.photo_view);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                photoView.setMaximumScale(10);
                photoView.setImageResource(mappa.getImmagine());
            }
        }, 300);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
        Utility.aggiornaTitoloActionBar((AppCompatActivity) getActivity(), mappa.getNomeBreveMappa(), null);
    }

}
