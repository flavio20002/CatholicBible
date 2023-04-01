package com.barisi.flavio.bibbiacattolica;

import com.barisi.flavio.bibbiacattolica.calendario.Util;
import com.barisi.flavio.bibbiacattolica.model.Pair;
import com.barisi.flavio.bibbiacattolica.model.Triple;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class LettureGiornoTest {

    @Test
    public void testLettureGiorno() {
        Date data = Util.getDataCorrente();
        assertNotNull(data);
    }

    @Test
    public void testordinario() {
        Date data = Util.aggiungiGiorni(Util.ottoGennaio(2017), 1);
        Triple<String, Integer, Integer> result1 = Util.liturgiaFeriale(data, false);
        Pair<String, Integer> result2 = Util.liturgiafestiva(data, false);
        result2.equals(result1);
    }

}