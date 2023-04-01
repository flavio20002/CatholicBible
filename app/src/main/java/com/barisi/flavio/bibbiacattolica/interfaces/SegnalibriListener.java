package com.barisi.flavio.bibbiacattolica.interfaces;

import com.barisi.flavio.bibbiacattolica.model.Segnalibro;

public interface SegnalibriListener {
    void cancellaSegnalibro(Segnalibro segnalibro, boolean cancellaDB);
    void modificaNotaSegnalibro(Segnalibro segnalibro);
}
