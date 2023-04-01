package com.barisi.flavio.bibbiacattolica.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.HomeCardAdapter;
import com.barisi.flavio.bibbiacattolica.adapter.RicercaCardAdapter;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.BulletSpanWithRadius;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.VerticalRecyclerViewFastScroller;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.SectionTitleIndicator;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnTestamentoInteraction;
import com.barisi.flavio.bibbiacattolica.model.CacheRicerca;
import com.barisi.flavio.bibbiacattolica.model.Generico;
import com.barisi.flavio.bibbiacattolica.model.Lettura;
import com.barisi.flavio.bibbiacattolica.model.RisultatiRicerca;
import com.barisi.flavio.bibbiacattolica.model.Testamento;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


@SuppressWarnings("deprecation")
public class HomeFragment extends Fragment {

    private boolean flag_loading;
    private ServiziDatabase serviziDatabase;
    private OnArticleFragmentInteractionListener mListener;
    private OnTestamentoInteraction tListener;
    private TextView emptyView;
    private SearchView searchView;
    private RicercaCardAdapter searchAdapter;
    private HomeCardAdapter homeAdapter;
    private RecyclerView recList;
    private SparseArray<CacheRicerca> cacheRicerca = null;

    private VerticalRecyclerViewFastScroller fastScroller;
    private AppBarLayout appBar;
    private SectionTitleIndicator sectionTitleIndicator;
    private View hintIndicator;
    private FrameLayout myFrameLayout;
    private boolean searchViewExpanded;
    private String searchViewText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviziDatabase = new ServiziDatabase(getActivity());
        searchAdapter = new RicercaCardAdapter(mListener, getContext());
        homeAdapter = new HomeCardAdapter(getActivity(), mListener, tListener);
        searchViewExpanded = false;
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set the adapter
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setItemAnimator(null);
        recList.setLayoutManager(llm);
        recList.setAdapter(homeAdapter);

        //Empty View
        emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setVisibility(TextView.GONE);

        //Fast scrolling
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);

        //Hint
        hintIndicator = view.findViewById(R.id.hint);

        myFrameLayout = (FrameLayout) view.findViewById(R.id.myFrameLayout);


        //Fabs
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideFabs();
        }
        aggiornaHome();
        return view;
    }

    private void aggiornaHome() {
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    homeAdapter.aggiorna();
                    if (cacheRicerca == null) {
                        long startTime = System.nanoTime();
                        cacheRicerca = serviziDatabase.cacheRicerca();
                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime) / 1000000;
                        Log.i("Durata", String.valueOf(duration));
                    }
                } catch (Exception e) {
                    //
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                searchAdapter.setCacheRicerca(cacheRicerca);
                homeAdapter.notifyDataSetChanged();
            }
        };
        task.execute();
    }

    private void addItems(final String filter, final boolean parolaEsatta, final boolean ignoraAccenti) {
        flag_loading = true;
        AsyncTask<String, Void, RisultatiRicerca> task = new AsyncTask<String, Void, RisultatiRicerca>() {

            private Exception exception = null;

            @Override
            protected RisultatiRicerca doInBackground(String... strings) {
                try {
                    return serviziDatabase.cercaCapitoli(strings[0], parolaEsatta, ignoraAccenti);
                } catch (Exception e) {
                    exception = e;
                    e.printStackTrace();
                }
                return new RisultatiRicerca();
            }

            @Override
            protected void onPostExecute(RisultatiRicerca ricerca) {
                if (exception == null) {
                    if (ricerca.getRowIds() != null) {
                        if (emptyView != null) {
                            if (ricerca.getRowIds().size() > 0) {
                                emptyView.setVisibility(TextView.GONE);
                            } else {
                                emptyView.setVisibility(TextView.VISIBLE);
                            }
                        }
                        searchAdapter.addItems(ricerca);
                        searchAdapter.notifyDataSetChanged();
                    }
                } else {
                    messaggioAvviso(getString(R.string.errore), getString(R.string.errore_des));
                }
                aggiornaTitoloActionBar();
                flag_loading = false;
                if (searchAdapter.getItemCount() > 5) {
                    //fastScrolling
                    fastScroller.setRecyclerView(recList);
                    fastScroller.setAppBar(appBar);
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                    if (hintIndicator != null && !Preferenze.seHintVisto("hint_ricerca", getContext())) {
                        Preferenze.settaHintVisto("hint_ricerca", getContext());
                        new MaterialShowcaseView.Builder(getActivity())
                                .setTarget(hintIndicator)
                                .setDismissOnTouch(true)
                                .setMaskColour(Utility.adjustAlpha(Preferenze.ottieniColorePrimario(getContext()), Costanti.TRASPARENZA_HINT_FAST_SCROLLING))
                                .setDismissText(getString(R.string.hintVisto))
                                .setContentText(getString(R.string.hintRicerca))
                                .show();
                    }
                } else {
                    fastScroller.setRecyclerView(null);
                    fastScroller.setAppBar(null);
                    fastScroller.setSectionIndicator(null);
                }
            }
        };
        task.execute(filter);
    }

    private void messaggioAvviso(String titolo, String testo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titolo);
        builder.setMessage(testo);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    private void messaggioHelpRicerca(String titolo) {
        final String lingua = Preferenze.ottieniLingua(getContext());
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float a = dm.xdpi / DisplayMetrics.DENSITY_DEFAULT;
        int bulletSize = (int) (a * 3);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titolo);
        CharSequence t0 = getText(R.string.helpRicerca);
        SpannableString s0 = new SpannableString(t0);
        SpannableString s1 = bullet(R.string.helpRicerca1, bulletSize);
        SpannableString s2 = bullet(R.string.helpRicerca2, bulletSize);
        SpannableString s3 = bullet(R.string.helpRicerca3, bulletSize);
        SpannableString s4 = bullet(R.string.helpRicerca4, bulletSize);
        SpannableString s5 = bullet(R.string.helpRicerca5, bulletSize);
        builder.setMessage(TextUtils.concat(s0, s1, s2, s3, s4, s5));
        builder.setCancelable(true);
        final AlertDialog alert = builder.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.abbreviazioni), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.setTitle(getActivity().getString(R.string.abbreviazioni));
                alert.setMessage(Html.fromHtml(serviziDatabase.guidaAbbreviazioni(lingua)));
                alert.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(Button.INVISIBLE);
            }
        });
    }

    private SpannableString bullet(int stringa, int bulletSize) {
        CharSequence t = getText(stringa);
        SpannableString s = new SpannableString(t);
        s.setSpan(new BulletSpanWithRadius(bulletSize, 30), 0, t.length(), 0);
        return s;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnArticleFragmentInteractionListener) context;
            tListener = (OnTestamentoInteraction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
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
        inflater.inflate(R.menu.home, menu);
        aggiornaTitoloActionBar();
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        final MenuItem vaiAItem = menu.findItem(R.id.vaiA);
        final MenuItem filterItem = menu.findItem(R.id.filter);
        filterItem.setVisible(false);
        final MenuItem helpItem = menu.findItem(R.id.help);
        helpItem.setVisible(false);
        final MenuItem wholeWordMenuItem = menu.findItem(R.id.whole_word);
        final MenuItem ignoraAccentiItem = menu.findItem(R.id.ignora_accenti);

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!flag_loading) {
                    ArrayList<Lettura> lett = Regex.decodificaLetture(query);
                    if (lett.size() > 0) {
                        tListener.onCercaLetture(query);
                    } else {
                        searchViewText = query;
                        searchAdapter.clearItems();
                        emptyView.setVisibility(TextView.GONE);
                        addItems(query, wholeWordMenuItem.isChecked(), ignoraAccentiItem.isChecked());
                        recList.scrollToPosition(0);
                        searchView.clearFocus();
                        myFrameLayout.requestFocus();
                    }
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                //if (!Preferenze.ottieniVersioneBibbia(getActivity()).equals("cei2008")) {
                //    messaggioAvviso(getString(R.string.attenzione), getString(R.string.ricerca_non_implementata));
                //    return false;
                //}
                //Blocca il drawer, per evitare navigazioni anomale
                /*DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                if (mDrawerLayout != null) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }*/
                searchViewExpanded = true;
                recList.setAdapter(searchAdapter);
                filterItem.setVisible(true);
                helpItem.setVisible(true);
                vaiAItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchViewExpanded = false;
                searchAdapter.clearItems();
                searchAdapter.notifyDataSetChanged();
                recList.scrollToPosition(0);
                emptyView.setVisibility(TextView.GONE);
                //Sblocca il drawer
                DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                /*if (mDrawerLayout != null) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }*/
                recList.setAdapter(homeAdapter);
                filterItem.setVisible(false);
                helpItem.setVisible(false);
                vaiAItem.setVisible(true);
                //fastScrolling
                fastScroller.setRecyclerView(null);
                fastScroller.setAppBar(null);
                fastScroller.setSectionIndicator(null);
                return true;
            }
        });
        if (getActivity() != null && searchViewExpanded) {
            searchMenuItem.expandActionView();
            searchView.setQuery(searchViewText,false);
            Utility.closeKeyboard(getActivity());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.whole_word: {
                item.setChecked(!item.isChecked());
                if (searchView != null) {
                    searchView.requestFocus();
                }
                return true;
            }
            case R.id.ignora_accenti: {
                item.setChecked(!item.isChecked());
                if (searchView != null) {
                    searchView.requestFocus();
                }
                return true;
            }
            case R.id.help: {
                messaggioHelpRicerca(getString(R.string.help));
                return true;
            }
            case R.id.vaiA: {
                showDialogVaiA();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void aggiornaTitoloActionBar() {
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.app_name));
                actionBar.setSubtitle(null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (homeAdapter.getItemCount() > 2) {
            aggiornaHome();
        }
    }

    private void showDialogVaiA() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.vaiA));
        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_vai_a, (ViewGroup) getView(), false);
        final Spinner spinner1 = (Spinner) viewInflated.findViewById(R.id.spinner1);
        final List<Generico> testamenti = new ArrayList<>();
        testamenti.add(new Generico(Testamento.getAntico().getCodice(), Testamento.getAntico().getDescrizione(getContext())));
        testamenti.add(new Generico(Testamento.getNuovo().getCodice(), Testamento.getNuovo().getDescrizione(getContext())));
        ArrayAdapter<Generico> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, testamenti);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        final Spinner spinner2 = (Spinner) viewInflated.findViewById(R.id.spinner2);
        final Spinner spinner3 = (Spinner) viewInflated.findViewById(R.id.spinner3);
        builder.setView(viewInflated);
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String id = ((Generico) spinner3.getSelectedItem()).getCodice();
                            mListener.onArticleFragmentInteraction(id, null);
                        } catch (Exception e) {
                            messaggioAvviso(getString(R.string.errore), getString(R.string.errore_des));
                        }
                    }
                }

        );

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
        );
        final AlertDialog dialog = builder.create();
        dialog.show();

        viewInflated.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewInflated.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                if (dialog.getWindow() != null) {
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = dialog.getWindow().getDecorView().getWidth();
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);
                }
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Generico> lista2 = serviziDatabase.listaLibriVaiA(testamenti.get(position).getCodice());
                ArrayAdapter<Generico> dataAdapter2 = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, lista2);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(dataAdapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Generico> lista = serviziDatabase.listaCapitoliVaiA(((Generico) spinner2.getSelectedItem()).getCodice());
                ArrayAdapter<Generico> dataAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, lista);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
