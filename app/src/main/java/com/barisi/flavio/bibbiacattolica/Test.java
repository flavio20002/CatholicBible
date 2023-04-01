package com.barisi.flavio.bibbiacattolica;


import android.util.Log;

import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;
import com.barisi.flavio.bibbiacattolica.model.Categoria;
import com.barisi.flavio.bibbiacattolica.model.TestoCapitoloPerConfronto;

import java.util.List;

public class Test {
    public static void verificaDisallineamento(ServiziDatabase serviziDatabase, String n) {
        List<Categoria> antico = serviziDatabase.listaLibri(n,"");
        for (int i = 0; i < antico.size(); i++) {
            for (int j = 0; j < antico.get(i).getListaLibri().size(); j++) {
                String libro = antico.get(i).getListaLibri().get(j).getCodLibro();
                try {
                    List<Capitolo> capitoli = serviziDatabase.listaCapitoli(libro,"");
                    for (int k = 0; k < capitoli.size(); k++) {
                        TestoCapitoloPerConfronto t = serviziDatabase.testoCapitoloPerConfronto(capitoli.get(k).getId(), "cei2008", "vulgata", "vulgata");
                        if (t.getTesto1().size() != t.getTesto2().size()) {
                            Log.e("Vulgata", capitoli.get(k).getNomeLibro() + "-" + capitoli.get(k).getNumero());
                        }
                        TestoCapitoloPerConfronto t1 = serviziDatabase.testoCapitoloPerConfronto(capitoli.get(k).getId(), "cei2008", "lxx", "lxx");
                        if (t1.getTesto1().size() != t1.getTesto2().size()) {
                            Log.e("Lxx", capitoli.get(k).getNomeLibro() + "-" + capitoli.get(k).getNumero());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
