package com.barisi.flavio.bibbiacattolica.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.calendario.Util;
import com.barisi.flavio.bibbiacattolica.gui.MyPagerAdapter;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;
import com.rey.material.app.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@SuppressWarnings("WrongConstant")
public class LeggiLettureGiornoParentFragment extends LeggiAltaVoceFragment {

    static final String ARG_LETTURE = "letture";
    static final String ARG_DATA = "data";
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private Date data;
    private String letture;
    private static final int numeroGiorniIndietro = 150;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LeggiLettureGiornoParentFragment() {
    }


    public static LeggiLettureGiornoParentFragment newInstance(String letture, Date data) {
        LeggiLettureGiornoParentFragment fragment = new LeggiLettureGiornoParentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LETTURE, letture);
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            letture = getArguments().getString(ARG_LETTURE);
            data = (Date) getArguments().getSerializable(ARG_DATA);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leggi_letture_parent, container, false);
        mPager = view.findViewById(R.id.pager);
        if (letture != null) {
            mPagerAdapter = new RicercaLettureAdapter(getChildFragmentManager(), letture);
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(0);
        } else {
            List<Integer> date = Util.listaGiorniInt(numeroGiorniIndietro);
            mPagerAdapter = new LettureGiornoAdapter(getChildFragmentManager(), date);
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(numeroGiorniIndietro);
        }
        inizializza();
        return view;
    }


    @Override
    MyPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getFragmentManager() != null && getContext() != null && item.getItemId() == R.id.action_cambia_data) {
            Calendar c = Calendar.getInstance();
            c.setTime(data);
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog.Builder builder = new DatePickerDialog.Builder();
            builder.date(mDay, mMonth, mYear);
            final DatePickerDialog dialog = (DatePickerDialog) builder.build(getContext());
            dialog.positiveAction(R.string.ok);
            dialog.negativeAction(R.string.annulla);
            dialog.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invalidaTestoDaLeggere();
                    Calendar cal = Calendar.getInstance();
                    cal.set(dialog.getYear(), dialog.getMonth(), dialog.getDay(), 0, 0, 0);
                    data = Util.resetTime(cal.getTime());
                    List<Integer> date = Util.listaGiorniInt(numeroGiorniIndietro);
                    mPagerAdapter = new LettureGiornoAdapter(getChildFragmentManager(), date);
                    mPager.setAdapter(mPagerAdapter);
                    mPager.setCurrentItem(numeroGiorniIndietro);
                    dialog.dismiss();
                }
            });
            dialog.negativeActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        } else

        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    MyPagerAdapter getMPagerAdapter() {
        return mPagerAdapter;
    }


    private class RicercaLettureAdapter extends MyPagerAdapter {
        private final String letture;
        private LeggiLettureGiornoFragment mCurrentFragment;

        RicercaLettureAdapter(FragmentManager fm, String letture) {
            super(fm);
            this.letture = letture;
        }

        @Override
        public Fragment getItem(int position) {
            return LeggiLettureGiornoFragment.newInstance(letture);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).update();
            }
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public LeggiFragment getmCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (LeggiLettureGiornoFragment) object;
                cambiaPagina();
            }
            super.setPrimaryItem(container, position, object);
        }
    }

    private class LettureGiornoAdapter extends MyPagerAdapter {
        private final List<Integer> date;
        private LeggiLettureGiornoFragment mCurrentFragment;


        LettureGiornoAdapter(FragmentManager fm, List<Integer> date) {
            super(fm);
            this.date = date;
        }

        @Override
        public Fragment getItem(int position) {
            return LeggiLettureGiornoFragment.newInstance(Util.aggiungiGiorni(data, date.get(position)));
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).update();
            }
            return super.getItemPosition(object);
        }


        @Override
        public int getCount() {
            return date.size();
        }

        @Override
        public LeggiFragment getmCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (LeggiLettureGiornoFragment) object;
                cambiaPagina();
            }
            super.setPrimaryItem(container, position, object);
        }
    }
}
