package com.barisi.flavio.bibbiacattolica.interfaces;

import com.barisi.flavio.bibbiacattolica.model.Lettura;
import com.barisi.flavio.bibbiacattolica.model.Testamento;

import java.util.ArrayList;
import java.util.Date;

public interface OnTestamentoInteraction {
    public void onTestamentoInteraction(Testamento testamento);
    public void onCercaLetture(String letture);
    public void onApriSegnalibri();
    public void onLettureGiorno(Date data);
}
