package com.tecsup.lab10.data

import org.junit.Assert.*
import org.junit.Test

class SerieValidacionTest {
    @Test fun letrasEnRatingNoCierranLaApp() { assertNotNull(validarSerie("Dark", "2017-12-01", "sdgdsfa", "drama")) }
    @Test fun ratingVacioSeRechaza() { assertNotNull(validarSerie("Dark", "2017-12-01", "", "drama")) }
    @Test fun fechaImposibleSeRechaza() { assertNotNull(validarSerie("Dark", "2026-02-30", "5", "drama")) }
    @Test fun categoriaInvalidaSeRechaza() { assertNotNull(validarSerie("Dark", "2017-12-01", "5", "inventada")) }
    @Test fun formularioValidoSeAcepta() { assertNull(validarSerie("Dark", "2017-12-01", "5", "drama")) }
}
