package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.adapter.holder.MyViewHolder;
import com.barisi.flavio.bibbiacattolica.calendario.CalendarioDAO;
import com.barisi.flavio.bibbiacattolica.calendario.CalendarioDAOImpl;
import com.barisi.flavio.bibbiacattolica.calendario.DataUtils;
import com.barisi.flavio.bibbiacattolica.calendario.Util;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.interfaces.HomeListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnTestamentoInteraction;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;
import com.barisi.flavio.bibbiacattolica.model.Testamento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeCardAdapter extends RecyclerView.Adapter<MyViewHolder> implements HomeListener {

    private OnArticleFragmentInteractionListener mListener;
    private OnTestamentoInteraction tListener;
    private Context mContext;
    private ServiziDatabase db;
    private List<Capitolo> cronologia;
    private Capitolo capitoloCasuale;
    private Date data;
    private String dataOdierna;
    private String liturgiaGiorno;
    private CalendarioDAO dao;
    private List<String> cards;


    public HomeCardAdapter(Context context, OnArticleFragmentInteractionListener mListener, OnTestamentoInteraction tListener) {
        this.mListener = mListener;
        this.tListener = tListener;
        this.mContext = context;
        db = new ServiziDatabase(mContext);
        dao = new CalendarioDAOImpl(db);
        cards = new ArrayList<>();
        cards.add("AnticoTestamento");
        cards.add("NuovoTestamento");
    }

    public void aggiorna() {
        try {
            cronologia = new ArrayList<>();
            List<String> cron = Preferenze.cronologia(mContext);
            cronologia.addAll(db.capitoli(cron));
            capitoloCasuale = db.capitoloCasuale();
            data = Util.getDataCorrente();
            String lingua = Preferenze.ottieniLingua(mContext);
            dataOdierna = Util.capitalizza(Util.formattaDataFull(data, lingua));
            liturgiaGiorno = DataUtils.estraiNomeLiturgia(data, Preferenze.ottieniEpifaniaFestivaLiturgia(mContext), dao, lingua);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cards.clear();
        cards.add("AnticoTestamento");
        cards.add("NuovoTestamento");
        /*long installDate = Utility.getInstallDate(mContext);
        if (!Preferenze.seHintVisto("prayTracker", mContext) && (installDate % 10) == 0 && "it".equals(Preferenze.ottieniLinguaDaLocale())) {
            cards.add("PrayTracker");
        }*/
        cards.add("LettureGiorno");
        cards.add("CapitoloCasuale");
        cards.add("Segnalibri");
        cards.add("Cronologia");
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder c, int position) {
        if (cards != null && position >= 0 && position < cards.size() && cards.get(position) != null) {
            if (c instanceof TestamentoViewHolder && cards.get(position).equals("AnticoTestamento")) {
                TestamentoViewHolder contactViewHolder = (TestamentoViewHolder) c;
                contactViewHolder.vTitoloTestamento.setText(mContext.getResources().getString(R.string.title_anticoTestamento).toUpperCase());
                contactViewHolder.frase.setText(R.string.AnticoTestamentoVers);
                contactViewHolder.libro.setText(R.string.AnticoTestamentoDesVer);
                contactViewHolder.currentItem = Testamento.getAntico();
            } else if (c instanceof TestamentoViewHolder && cards.get(position).equals("NuovoTestamento")) {
                TestamentoViewHolder contactViewHolder = (TestamentoViewHolder) c;
                contactViewHolder.vTitoloTestamento.setText(mContext.getResources().getString(R.string.title_nuovoTestamento).toUpperCase());
                contactViewHolder.frase.setText(R.string.NuovoTestamentoDes);
                contactViewHolder.libro.setText(R.string.NuovoTestamentoDesVer);
                contactViewHolder.currentItem = Testamento.getNuovo();
            } else if (c instanceof LettureDelGiornoViewHolder && cards.get(position).equals("LettureGiorno")) {
                LettureDelGiornoViewHolder contactViewHolder = (LettureDelGiornoViewHolder) c;
                contactViewHolder.vData.setText(dataOdierna);
                if (Regex.stringaVuota(liturgiaGiorno)) {
                    contactViewHolder.vGiornoLiturgia.setText(R.string.nessunaLetturaDisponibile);
                } else {
                    contactViewHolder.vGiornoLiturgia.setText(liturgiaGiorno);
                }
            } else if (c instanceof CapitoloCasualeViewHolder && cards.get(position).equals("CapitoloCasuale")) {
                Capitolo capitolo = capitoloCasuale;
                CapitoloCasualeViewHolder contactViewHolder = (CapitoloCasualeViewHolder) c;
                if (mContext != null && contactViewHolder != null && contactViewHolder.vNumeroCapitolo != null && capitolo != null) {
                    contactViewHolder.vTitoloCategoria.setText(R.string.CapitoloCasualeDes);
                    contactViewHolder.vTitoloCategoria.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    contactViewHolder.vNumeroCapitolo.setText(mContext.getString(R.string.CapitoloMeno, capitolo.getNomeLibro(), String.valueOf(capitolo.getNumero())));
                    contactViewHolder.vNumeroCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if (capitolo.getTitolo().equals("")) {
                        contactViewHolder.vTitoloCapitolo.setVisibility(View.GONE);
                    } else {
                        contactViewHolder.vTitoloCapitolo.setVisibility(View.VISIBLE);
                        contactViewHolder.vTitoloCapitolo.setText(capitolo.getTitolo());
                        contactViewHolder.vTitoloCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    }
                    contactViewHolder.vTestoCapitolo.setText(Html.fromHtml(capitolo.getTestoBreve()).toString().replace("\n", ""));
                    contactViewHolder.vTestoCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    contactViewHolder.currentItem = capitolo;
                }
            } else if (c instanceof CronologiaViewHolder && cards.get(position).equals("Cronologia")) {
                CronologiaViewHolder contactViewHolder = (CronologiaViewHolder) c;
                contactViewHolder.vTitoloCategoria.setText(R.string.Cronologia);
                contactViewHolder.vTitoloCategoria.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                ListaCronologiaHomeAdapter mAdapter = new ListaCronologiaHomeAdapter(mContext, cronologia);
                contactViewHolder.vListView.setAdapter(mAdapter);
                contactViewHolder.emptyView.setText(R.string.CronologiaVuota);
                if (contactViewHolder.emptyView != null) {
                    if (cronologia.size() > 0) {
                        contactViewHolder.emptyView.setVisibility(TextView.GONE);
                    } else {
                        contactViewHolder.emptyView.setVisibility(TextView.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (cards != null && position >= 0 && position < cards.size() && cards.get(position) != null) {
            if (cards.get(position).equals("AnticoTestamento") || cards.get(position).equals("NuovoTestamento")) {
                return 0;
            } else if (cards.get(position).equals("PrayTracker")) {
                return 1;
            } else if (cards.get(position).equals("LettureGiorno")) {
                return 2;
            } else if (cards.get(position).equals("CapitoloCasuale")) {
                return 3;
            } else if (cards.get(position).equals("Segnalibri")) {
                return 4;
            }
        }
        return 5;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.elemento_home_testamento, viewGroup, false);
            return new TestamentoViewHolder(itemView, tListener, mContext);
        } else if (i == 1) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.elemento_home_praytracker, viewGroup, false);
            return new PrayTrackerViewHolder(itemView, tListener, mContext);
        } else if (i == 2) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.elemento_home_card_letture_del_giorno, viewGroup, false);
            return new LettureDelGiornoViewHolder(itemView, tListener, mContext);
        } else if (i == 3) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.elemento_home_capitolo_casuale, viewGroup, false);
            return new CapitoloCasualeViewHolder(itemView, mListener, this, mContext
            );
        } else if (i == 4) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.elemento_home_card_segnalibri, viewGroup, false);
            return new PreferitiViewHolder(itemView, tListener, mContext);
        } else {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.elemento_home_card_cronologia, viewGroup, false);
            return new CronologiaViewHolder(itemView, mListener, HomeCardAdapter.this, mContext);
        }
    }

    @Override
    public void svuotaListaCronologia() {
        if (cronologia.size() > 0) {
            Preferenze.cancellaCronologia(mContext);
            cronologia.clear();
            this.notifyItemChanged(5);
        }
    }

    @Override
    public void aggiornaCapitoloCasuale() {
        try {
            capitoloCasuale = db.capitoloCasuale();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.notifyItemChanged(3);
    }

    public static class PreferitiViewHolder extends MyViewHolder {

        public PreferitiViewHolder(View v, final OnTestamentoInteraction tListener,
                                   Context c) {
            super(v, c, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tListener.onApriSegnalibri();
                }
            });
        }
    }

    public class LettureDelGiornoViewHolder extends MyViewHolder {
        protected TextView vData;
        protected TextView vGiornoLiturgia;

        public LettureDelGiornoViewHolder(View v, final OnTestamentoInteraction tListener,
                                          Context c) {
            super(v, c, false);
            vData = (TextView) v.findViewById(R.id.data_odierna);
            vGiornoLiturgia = (TextView) v.findViewById(R.id.giorno_liturgia);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tListener.onLettureGiorno(data);
                }
            });
        }
    }

    public class PrayTrackerViewHolder extends MyViewHolder {

        protected AppCompatImageView vCancel;

        public PrayTrackerViewHolder(View v, final OnTestamentoInteraction tListener,
                                     final Context c) {
            super(v, c, false);
            vCancel = v.findViewById(R.id.cancel);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.barisi.flavio.praytracker")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.barisi.flavio.praytracker")));
                    }
                }
            });
            vCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferenze.settaHintVisto("prayTracker", c);
                    int index = cards.indexOf("PrayTracker");
                    cards.remove(index);
                    notifyItemRemoved(index);
                }
            });
        }
    }

    public static class CronologiaViewHolder extends MyViewHolder {
        protected TextView vTitoloCategoria;
        protected AbsListView vListView;
        private TextView emptyView;

        public CronologiaViewHolder(View v,
                                    final OnArticleFragmentInteractionListener mListener,
                                    final HomeListener homeListener,
                                    Context c) {
            super(v, c, true);
            vTitoloCategoria = (TextView) v.findViewById(R.id.titolo_categoria);
            vListView = (AbsListView) v.findViewById(android.R.id.list);
            vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (null != mListener) {
                        Object o = parent.getAdapter().getItem(position);
                        if (o instanceof Capitolo) {
                            Capitolo item = (Capitolo) o;
                            mListener.onArticleFragmentInteraction(item.getId(), null);
                        }
                    }
                }
            });
            emptyView = (TextView) v.findViewById(android.R.id.empty);
            emptyView.setVisibility(TextView.GONE);
            emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            AppCompatImageView cancella = (AppCompatImageView) v.findViewById(R.id.cancella);
            cancella.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeListener.svuotaListaCronologia();
                }
            });
        }
    }

    public static class CapitoloCasualeViewHolder extends MyViewHolder {
        protected TextView vNumeroCapitolo;
        protected TextView vTitoloCapitolo;
        protected TextView vTestoCapitolo;
        protected TextView vTitoloCategoria;
        public Capitolo currentItem;

        public CapitoloCasualeViewHolder(View v,
                                         final OnArticleFragmentInteractionListener mListener,
                                         final HomeListener homeListener,
                                         Context c) {
            super(v, c, false);
            vNumeroCapitolo = (TextView) v.findViewById(R.id.numero_capitolo);
            vTitoloCapitolo = (TextView) v.findViewById(R.id.titolo_capitolo);
            vTestoCapitolo = (TextView) v.findViewById(R.id.testo_capitolo);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onArticleFragmentInteraction(currentItem.getId(), null);
                }
            });
            AppCompatImageView im = (AppCompatImageView) v.findViewById(R.id.aggiorna);
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeListener.aggiornaCapitoloCasuale();
                }
            });
            vTitoloCategoria = (TextView) v.findViewById(R.id.titolo_categoria);
        }
    }

    public static class TestamentoViewHolder extends MyViewHolder {
        protected TextView frase;
        protected TextView libro;
        protected TextView vTitoloTestamento;
        public Testamento currentItem;

        public TestamentoViewHolder(View v, final OnTestamentoInteraction tListener, Context c) {
            super(v, c, false);
            frase = (TextView) v.findViewById(R.id.frase);
            libro = (TextView) v.findViewById(R.id.libro);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tListener.onTestamentoInteraction(currentItem);
                }
            });
            vTitoloTestamento = (TextView) v.findViewById(R.id.titolo_testamento);
        }
    }


}