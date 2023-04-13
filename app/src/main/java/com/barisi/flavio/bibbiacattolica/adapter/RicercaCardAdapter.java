package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.adapter.holder.NumeroRisultatiViewHolder;
import com.barisi.flavio.bibbiacattolica.adapter.holder.RicercaViewHolder;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.MySectionIndexer;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.CacheRicerca;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;
import com.barisi.flavio.bibbiacattolica.model.RisultatiRicerca;

import java.util.List;

@SuppressWarnings("deprecation")
public class RicercaCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MySectionIndexer {

    private RisultatiRicerca risultatiRicerca;
    private OnArticleFragmentInteractionListener mListener;
    private Context mContext;
    private ServiziDatabase db;
    private SparseArray<CacheRicerca> cacheRicerca;
    private SparseArray<String> cacheTestiBrevi;
    private boolean cancelled = false;

    private String testoNumeroRisultati = "";

    public RicercaCardAdapter(OnArticleFragmentInteractionListener mListener,
                              Context mContext) {
        this.risultatiRicerca = new RisultatiRicerca();
        this.mListener = mListener;
        this.mContext = mContext;
        this.db = new ServiziDatabase(mContext);
        cacheTestiBrevi = new SparseArray<>();
    }

    public void clearItems() {
        addItems(new RisultatiRicerca());
        cancelled = true;
        cacheTestiBrevi.clear();
    }

    public void addItems(RisultatiRicerca risultatiRicerca) {
        this.risultatiRicerca = risultatiRicerca;
        cancelled = false;
        switch (risultatiRicerca.getRowIds().size()) {
            case 0:
                testoNumeroRisultati = "";
                break;
            case 1:
                testoNumeroRisultati = "1 capitolo";
                break;
            default:
                testoNumeroRisultati = risultatiRicerca.getRowIds().size() + " capitoli";
        }
    }

    @Override
    public int getItemCount() {
        return risultatiRicerca.getRowIds().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            if (position != getItemCount() - 1) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (i == 0) {
            NumeroRisultatiViewHolder ivh = (NumeroRisultatiViewHolder) viewHolder;
            ivh.vNumeroRisultati.setText(testoNumeroRisultati);
            ivh.vNumeroRisultati.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else {
            Integer rowid = risultatiRicerca.getRowIds().get(i - 1);
            String filtro = risultatiRicerca.getFiltro();
            Capitolo capitolo = new Capitolo();
            String testoBreve = "";
            try {
                capitolo = cacheRicerca.get(rowid);
                capitolo.setFiltro(filtro);
                testoBreve = cacheTestiBrevi.get(rowid);
                if (testoBreve == null) {
                    caricaTestoBreveAsync(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RicercaViewHolder contactViewHolder = (RicercaViewHolder) viewHolder;

            contactViewHolder.vNumeroCapitolo.setText(String.format(mContext.getString(R.string.capitoloRicerca), capitolo.getNomeLibro(), capitolo.getNumero()));

            contactViewHolder.vNumeroCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            if (capitolo.getTitolo() == null || capitolo.getTitolo().equals("")) {
                contactViewHolder.vTitoloCapitolo.setVisibility(View.GONE);
            } else {
                contactViewHolder.vTitoloCapitolo.setVisibility(View.VISIBLE);
                contactViewHolder.vTitoloCapitolo.setText(capitolo.getTitolo());
                contactViewHolder.vTitoloCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
            if (testoBreve == null || testoBreve.equals("")) {
                contactViewHolder.vTestoCapitolo.setVisibility(View.VISIBLE);
                contactViewHolder.vTestoCapitolo.setText("");
                contactViewHolder.vTestoCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            } else {
                contactViewHolder.vTestoCapitolo.setVisibility(View.VISIBLE);
                contactViewHolder.vTestoCapitolo.setText(Html.fromHtml(testoBreve));
                contactViewHolder.vTestoCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            contactViewHolder.currentItem = capitolo;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            RicercaViewHolder contactViewHolder = (RicercaViewHolder) holder;
            if (payloads.get(0) instanceof String && payloads.get(0) != null) {
                contactViewHolder.vTestoCapitolo.setText(Html.fromHtml((String) payloads.get(0)));
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return NumeroRisultatiViewHolder.newInstance(viewGroup, mContext);
        } else if (i == 1) {
            return RicercaViewHolder.newInstance(viewGroup, mListener, mContext, false);
        } else {
            return RicercaViewHolder.newInstance(viewGroup, mListener, mContext, true);
        }
    }

    @Override
    public String getSectionForPosition(int position) {
        if (position == 0) {
            return null;
        } else {
            try {
                return cacheRicerca.get(risultatiRicerca.getRowIds().get(position - 1)).getSiglaCapitolo();
            } catch (Exception e) {
                return "";
            }
        }
    }

    public void setCacheRicerca(SparseArray<CacheRicerca> cacheRicerca) {
        this.cacheRicerca = cacheRicerca;
    }


    private void caricaTestoBreveAsync(final int i) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    if (!cancelled) {
                        Integer rowid = risultatiRicerca.getRowIds().get(i - 1);
                        String filtro = risultatiRicerca.getFiltro();
                        String testoBreve = ottieniTestoBreve(rowid, filtro);
                        cacheTestiBrevi.put(rowid, testoBreve);
                        return testoBreve;
                    }
                } catch (Exception e) {
                    //
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null && !cancelled)
                    RicercaCardAdapter.this.notifyItemChanged(i, s);
            }
        };
        task.execute();
    }

    private synchronized String ottieniTestoBreve(int rowid, String filtro) {
        String risultato = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                risultato = db.testoBreveCapitoloRicerca(rowid, filtro);
            }
        } catch (Exception e) {
            //
        }
        try {
            if (Regex.stringaVuota(risultato)) {
                risultato = db.testoBreveStandard(rowid);
            }
        } catch (Exception e) {
            //
        }
        return risultato;
    }

}