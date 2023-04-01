package com.barisi.flavio.bibbiacattolica.calendario;

import android.content.Context;

import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;

import java.util.HashMap;
import java.util.List;

public class CalendarioDAOImpl extends CalendarioDAO {

    ServiziDatabase serviziDatabase;

    public CalendarioDAOImpl(Context context) {
        serviziDatabase = new ServiziDatabase(context);
    }

    public CalendarioDAOImpl(ServiziDatabase serviziDatabase) {
        this.serviziDatabase = serviziDatabase;
    }

    @Override
    public List<HashMap> query(String sql, String[] parametri) {
        return serviziDatabase.query(sql, parametri);
    }

    @Override
    public String queryTesto(String idCapitolo, String versioneBibbia) throws Exception {
        return serviziDatabase.testoCapitolo(idCapitolo, versioneBibbia).getTesto();
    }
}
