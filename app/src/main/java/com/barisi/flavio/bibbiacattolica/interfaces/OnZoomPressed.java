package com.barisi.flavio.bibbiacattolica.interfaces;

import com.barisi.flavio.bibbiacattolica.model.Testamento;

public interface OnZoomPressed {
    public int zoomPlus();
    public int zoomMinus();
    public int zoomReset();
    public void modalitaNotte(int mod);
    public void mostraACapo(int modalita);
    public void testoGiustificato(int modalita);

}
