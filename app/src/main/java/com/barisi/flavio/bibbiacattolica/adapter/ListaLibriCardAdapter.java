package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.adapter.holder.LibroViewHolder;
import com.barisi.flavio.bibbiacattolica.adapter.holder.MyViewHolderNoTop;
import com.barisi.flavio.bibbiacattolica.adapter.holder.TitoloViewHolder;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.MySectionIndexer;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Categoria;
import com.barisi.flavio.bibbiacattolica.model.Libro;

import java.util.ArrayList;
import java.util.List;

public class ListaLibriCardAdapter extends RecyclerView.Adapter<MyViewHolderNoTop> implements MySectionIndexer {

    private List<Object> oggetti;
    private ArrayList<Libro> libri;
    private OnLibriFragmentInteractionListener mListener;
    private Context mContext;

    public ListaLibriCardAdapter(Context context, OnLibriFragmentInteractionListener mListener) {
        this.mListener = mListener;
        this.mContext = context;
        oggetti = new ArrayList<>();
        libri = new ArrayList<>();

    }

    public void addItems(List<Categoria> categorie) {
        libri.clear();
        oggetti.clear();
        for (int i = 0; i < categorie.size(); i++) {
            Categoria cat = categorie.get(i);
            if (cat.getListaLibri() != null) {
                oggetti.add(cat);
                for (int j = 0; j < cat.getListaLibri().size(); j++) {
                    oggetti.add(cat.getListaLibri().get(j));
                    libri.add(cat.getListaLibri().get(j));
                    Libro lib = cat.getListaLibri().get(j);
                    lib.setCategoria(cat.getDesGategoria());
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return oggetti.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = oggetti.get(position);
        if (o instanceof Categoria) {
            return 0;
        } else {
            if (position != oggetti.size() - 1) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolderNoTop viewHolder, int i) {
        Object o = oggetti.get(i);
        if (o instanceof Categoria) {
            Categoria cat = (Categoria) o;
            TitoloViewHolder tvh = (TitoloViewHolder) viewHolder;
            tvh.vTitoloCategoria.setText(cat.getDesGategoria());
            tvh.vTitoloCategoria.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else {
            Libro lib = (Libro) o;
            LibroViewHolder lvh = (LibroViewHolder) viewHolder;
            lvh.vTitolo.setText(lib.getDesLibro());
            lvh.vTitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            String stringa;
            if (lib.getCodLibro().equals("Sal")) {
                stringa = mContext.getString(R.string.salmiMultipli);
            } else {
                stringa = lib.getNumeroCapitoli() == 1 ? mContext.getString(R.string.capitoloSingolo) : mContext.getString(R.string.capitoliMultipli);
            }
            lvh.vNumeroCapitolo.setText(String.format(stringa, lib.getNumeroCapitoli()));
            lvh.vNumeroCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            lvh.libri = libri;
            lvh.posizione = libri.indexOf(lib);
        }
    }

    @Override
    public MyViewHolderNoTop onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return TitoloViewHolder.newInstance(viewGroup, mContext);
        } else if (i == 1) {
            return LibroViewHolder.newInstance(viewGroup, mListener, mContext, false);
        } else {
            return LibroViewHolder.newInstance(viewGroup, mListener, mContext, true);
        }
    }

    @Override
    public String getSectionForPosition(int position) {
        Object obj = oggetti.get(position);
        if (obj instanceof Libro) {
            return ((Libro) obj).getCategoria();
        } else {
            return ((Categoria) obj).getDesGategoria();
        }
    }
}