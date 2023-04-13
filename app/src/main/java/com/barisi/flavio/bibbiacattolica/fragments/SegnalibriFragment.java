package com.barisi.flavio.bibbiacattolica.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.MyActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.tasks.SegnalibriImportaTask;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ListaSegnalibriCardAdapter;
import com.barisi.flavio.bibbiacattolica.adapter.holder.SegnalibroViewHolder;
import com.barisi.flavio.bibbiacattolica.calendario.Util;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.VerticalRecyclerViewFastScroller;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.SectionTitleIndicator;
import com.barisi.flavio.bibbiacattolica.interfaces.NotaSegnalibroListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.SegnalibriListener;
import com.barisi.flavio.bibbiacattolica.model.Segnalibro;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static android.app.Activity.RESULT_OK;

public class SegnalibriFragment extends Fragment implements SegnalibriListener {

    private static final int RESULT_IMPORTA = 0;
    public static boolean preferitiModificati;

    private OnArticleFragmentInteractionListener mListener;
    private TextView emptyView;
    private ListaSegnalibriCardAdapter mAdapter;
    public ServiziDatabase serviziDatabase;
    private CoordinatorLayout coordinatorLayoutView;
    private RecyclerView recList;
    private ProgressView circularProgressBar;
    private VerticalRecyclerViewFastScroller fastScroller;
    private AppBarLayout appBar;
    private SectionTitleIndicator sectionTitleIndicator;
    private View hintIndicator;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SegnalibriFragment() {
    }

    public static SegnalibriFragment newInstance() {
        return new SegnalibriFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviziDatabase = new ServiziDatabase(getActivity());
        mAdapter = new ListaSegnalibriCardAdapter(new ArrayList<Segnalibro>(), mListener, this, getContext());
        setHasOptionsMenu(true);
        preferitiModificati = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_segnalibri, container, false);

        coordinatorLayoutView = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);

        // Set the adapter
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private boolean cancellati = false;
            private boolean mossi = false;
            private List<Segnalibro> backup = null;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPosition = viewHolder.getAdapterPosition();
                final int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mAdapter.getSegnalibri(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mAdapter.getSegnalibri(), i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                mossi = true;
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                SegnalibroViewHolder svH = (SegnalibroViewHolder) viewHolder;
                backup = new ArrayList<>(mAdapter.getSegnalibri());
                cancellaSegnalibro(svH.currentItem, true);
                //cancellati = true;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (cancellati || mossi) {
                    riordinaSegnalibri(cancellati, backup);
                }
                cancellati = false;
                mossi = false;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recList);

        //Empty View
        emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setVisibility(TextView.GONE);
        emptyView.setText(R.string.empty_list_bookmark);

        //Fast scrolling
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        sectionTitleIndicator.setVisibility(SectionTitleIndicator.GONE);

        //Hint
        hintIndicator = view.findViewById(R.id.hint);


        //Progress bar
        circularProgressBar = (ProgressView) view.findViewById(R.id.circular_progress);
        circularProgressBar.setVisibility(ProgressView.GONE);

        //Fabs
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideFabs();
        }

        refreshItems();
        return view;
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
    public void onResume() {
        super.onResume();
        if (preferitiModificati) {
            preferitiModificati = false;
            refreshItems();
        }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.segnalibri, menu);
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar actionBar = ((MyActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.bookmarks));
                actionBar.setSubtitle(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancella_segnalibri: {
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.attenzione)
                            .setMessage(R.string.vuoi_cancellare_segnalibri).setCancelable(true)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cancellaSegnalibri();
                                }
                            }).setNegativeButton(android.R.string.cancel, null).create().show();
                }
                return true;
            }
            case R.id.action_esporta_segnalibri: {
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    Gson gson = new Gson();
                    String jsonRepresentation = gson.toJson(mAdapter.getSegnalibri());
                    String filename = getString(R.string.app_name) + "-" + getString(R.string.bookmarks) + "-" + Util.formattaDataNomeFile(new Date()) + ".txt";
                    File tempPath = new File(getContext().getFilesDir(), "temp");
                    //noinspection ResultOfMethodCallIgnored
                    tempPath.mkdirs();
                    File fileBookmarks = new File(tempPath, filename);
                    FileOutputStream outputStream;
                    try {
                        outputStream = new FileOutputStream(fileBookmarks);
                        outputStream.write(jsonRepresentation.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri contentUri = FileProvider.getUriForFile(getContext(), "com.barisi.flavio.fileprovider", fileBookmarks);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.esportaSegnalibri)));
                }
                return true;
            }
            case R.id.action_importa_segnalibri: {
                if (mAdapter != null) {
                    /*if (!Utility.hasPermission(getContext(), "android.permission.READ_EXTERNAL_STORAGE")) {
                        Utility.messaggioAvviso(getContext(), R.string.attenzione, R.string.ask_permission_write_storage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
                                int permsRequestCode = 200;
                                requestPermissions(perms, permsRequestCode);
                            }
                        });

                    } else {
                        cercaFile();
                    }*/
                    if (!Preferenze.seHintVisto("hint_segnalibri_dropbox", getContext())) {
                        Preferenze.settaHintVisto("hint_segnalibri_dropbox", getContext());
                        Utility.messaggioAvviso(getContext(), R.string.attenzione, R.string.hint_segnalibri_dropbox, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cercaFile();
                            }
                        });
                    } else {
                        cercaFile();
                    }
                }
                return true;
            }
        }

        return super.

                onOptionsItemSelected(item);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    cercaFile();
                }
                break;
        }

    }

    private void cercaFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("text/plain");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, RESULT_IMPORTA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_IMPORTA:
                if (resultCode == RESULT_OK) {
                    if (mAdapter.getItemCount() > 0) {
                        Utility.showDialog(getContext(), getString(R.string.importaSegnalibri), getString(R.string.importaSegnalibriMessage), getString(R.string.importaSegnalibriSovrascrivi), getString(R.string.importaSegnalibriAggiungi), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SegnalibriImportaTask(SegnalibriFragment.this, data, null).execute();

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SegnalibriImportaTask(SegnalibriFragment.this, data, mAdapter.getSegnalibri()).execute();
                            }
                        }, true);
                    } else {
                        new SegnalibriImportaTask(this, data, null).execute();
                    }
                }
                break;
            case 1:
                break;
        }
    }

    private void cancellaSegnalibri() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    serviziDatabase.cancellareVersettiPreferiti();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void articles) {
                refreshItems();
            }
        }.execute();
    }

    public void refreshItems() {
        showProgressBar(true);
        AsyncTask<String, Void, List<Segnalibro>> task = new AsyncTask<String, Void, List<Segnalibro>>() {
            @Override
            protected List<Segnalibro> doInBackground(String... strings) {
                try {
                    return serviziDatabase.listaCapitoliPreferiti();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<Segnalibro> articles) {
                mAdapter.clearItems();
                mAdapter.addItems(articles);
                mAdapter.notifyDataSetChanged();
                fastScroller.setRecyclerView(recList);
                fastScroller.setAppBar(appBar);
                fastScroller.setSectionIndicator(sectionTitleIndicator);
                hideProgressBar();
            }

        };
        task.execute();
    }

    public void showProgressBar(boolean force) {
        if (mAdapter.getSegnalibri().size() > 30 || force) {
            circularProgressBar.setVisibility(ProgressView.VISIBLE);
            recList.setVisibility(RecyclerView.GONE);
            if (emptyView != null) {
                emptyView.setVisibility(RecyclerView.GONE);
            }
        }
    }

    public void hideProgressBar() {
        circularProgressBar.setVisibility(ProgressView.GONE);
        recList.setVisibility(RecyclerView.VISIBLE);
        if (emptyView != null) {
            emptyView.setVisibility(mAdapter.getSegnalibri().size() > 0 ? TextView.GONE : TextView.VISIBLE);
        }
    }

    private void cancellaSegnalibroDb(final String articleId, final String versetto) {
        AsyncTask<String, Void, Spanned> task = new AsyncTask<String, Void, Spanned>() {
            @Override
            protected Spanned doInBackground(String... strings) {
                serviziDatabase.cancellareVersettoPreferito(articleId, versetto);
                return null;
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
            }
        };
        task.execute();
    }

    private void riordinaSegnalibri(final boolean cancellati, final List<Segnalibro> backup) {
        showProgressBar(false);
        final AsyncTask<String, Void, Spanned> task = new AsyncTask<String, Void, Spanned>() {
            @Override
            protected Spanned doInBackground(String... strings) {
                serviziDatabase.cancellareVersettiPreferiti();
                for (int i = mAdapter.getSegnalibri().size() - 1; i >= 0; i--) {
                    Segnalibro s = mAdapter.getSegnalibri().get(i);
                    serviziDatabase.salvaVersettoPreferito(s.getIdCapitolo(), s.getVersetto(), s.getNota());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
                hideProgressBar();
                if (cancellati) {
                    Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getString(R.string.segnalibro_cancellato), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ripristinaSegnalibri(backup);
                            refreshItems();
                        }
                    }, R.string.undo);
                }
            }
        };
        task.execute();

    }

    private void ripristinaSegnalibri(List<Segnalibro> backup) {
        serviziDatabase.cancellareVersettiPreferiti();
        for (int i = backup.size() - 1; i >= 0; i--) {
            Segnalibro s = backup.get(i);
            serviziDatabase.salvaVersettoPreferito(s.getIdCapitolo(), s.getVersetto(), s.getNota());
        }
    }

    @Override
    public void cancellaSegnalibro(final Segnalibro segnalibro, boolean cancellaDb) {
        final List<Segnalibro> backup = new ArrayList<>(mAdapter.getSegnalibri());
        int index = mAdapter.getSegnalibri().indexOf(segnalibro);
        mAdapter.getSegnalibri().remove(segnalibro);
        mAdapter.notifyItemRemoved(index);
        if (emptyView != null) {
            if (mAdapter.getSegnalibri().size() > 0) {
                emptyView.setVisibility(TextView.GONE);
            } else {
                emptyView.setVisibility(TextView.VISIBLE);
            }
        }
        if (cancellaDb) {
            cancellaSegnalibroDb(segnalibro.getIdCapitolo(), segnalibro.getVersetto());
            Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getString(R.string.segnalibro_cancellato), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ripristinaSegnalibri(backup);
                    refreshItems();
                }
            }, R.string.undo);
        }
    }

    @Override
    public void modificaNotaSegnalibro(final Segnalibro segnalibro) {
        Utility.mostraDialogoOttieniTesto(getContext(), getView(), segnalibro.getNota(), new NotaSegnalibroListener() {
            @Override
            public void noteInserted(String nota) {
                int index = mAdapter.getSegnalibri().indexOf(segnalibro);
                mAdapter.getSegnalibri().get(index).setNota(nota);
                mAdapter.notifyItemChanged(index);
                serviziDatabase.aggiornareNotaVersettoPreferito(segnalibro.getIdCapitolo(), segnalibro.getVersetto(), nota);
            }
        });
    }

}
