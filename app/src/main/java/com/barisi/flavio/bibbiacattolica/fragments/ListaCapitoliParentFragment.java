package com.barisi.flavio.bibbiacattolica.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;
import com.barisi.flavio.bibbiacattolica.model.Libro;

import java.util.ArrayList;
import java.util.List;


public class ListaCapitoliParentFragment extends Fragment {

    public static final String LIBRI = "libri";
    public static final String POSIZIONE = "posizione";
    private PagerAdapter mPagerAdapter;
    private ArrayList<Libro> libri;
    private int posizione;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListaCapitoliParentFragment() {
    }

    public static ListaCapitoliParentFragment newInstance(ArrayList<Libro> libri, int posizione) {
        ListaCapitoliParentFragment fragment = new ListaCapitoliParentFragment();
        Bundle args = new Bundle();
        args.putSerializable(LIBRI, libri);
        args.putInt(POSIZIONE, posizione);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            libri = (ArrayList<Libro>) getArguments().getSerializable(LIBRI);
            posizione = getArguments().getInt(POSIZIONE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capitoli_parent, container, false);
        ViewPager mPager = view.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), libri);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(posizione);
        //Fabs
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideFabs();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.lista_libri_grid) {
            int modalitaVisualizzazione = Preferenze.ottieniVisualizzazionieCapitoli(getActivity());
            if (modalitaVisualizzazione == 0) {
                modalitaVisualizzazione = 1;
            } else {
                modalitaVisualizzazione = 0;
            }
            Preferenze.salvaVisualizzazionieCapitoli(getContext(), modalitaVisualizzazione);
            updateFragments();
            if (getActivity() != null) {
                getActivity().invalidateOptionsMenu();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Libro> libri;

        ScreenSlidePagerAdapter(FragmentManager fm, List<Libro> libri) {
            super(fm);
            this.libri = libri;
        }

        @Override
        public Fragment getItem(int position) {
            return ListaCapitoliFragment.newInstance(libri.get(position));
        }

        @Override
        public int getCount() {
            return libri.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).update();
            }
            return super.getItemPosition(object);
        }
    }

    public void updateFragments() {
        mPagerAdapter.notifyDataSetChanged();
    }
}
