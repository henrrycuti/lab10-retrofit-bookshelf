package com.tecsup.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tecsup.lab10.data.SerieApiService
import com.tecsup.lab10.data.SerieModel
import com.tecsup.lab10.data.validarSerie
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.Locale

private const val ERROR_CONEXION = "No pude conectar con Django. Abre Iniciar_servidor.cmd en la carpeta Lab10_Series y vuelve a intentarlo."

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    val listaSeries = remember { mutableStateListOf<SerieModel>() }
    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var intento by remember { mutableIntStateOf(0) }
    LaunchedEffect(intento) {
        cargando = true
        error = ""
        try {
            val listado = servicio.selectSeries()
            listaSeries.clear()
            listaSeries.addAll(listado)
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { error = ERROR_CONEXION }
        finally { cargando = false }
    }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        if (cargando) { CircularProgressIndicator(); Text("Cargando series…") }
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Button(onClick = { intento++ }) { Text("Reintentar") }
        } else if (!cargando && listaSeries.isEmpty()) Text("No hay series. Pulsa + para agregar la primera.")
        LazyColumn {
            item {
                Row(Modifier.fillParentMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("ID", Modifier.weight(.1f), fontWeight = FontWeight.Bold)
                    Text("SERIE", Modifier.weight(.7f), fontWeight = FontWeight.Bold)
                    Text("Acción", Modifier.weight(.2f), fontWeight = FontWeight.Bold)
                }
            }
            items(listaSeries, key = { it.id }) { item ->
                Row(Modifier.fillParentMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("${item.id}", Modifier.weight(.1f))
                    Text(item.name, Modifier.weight(.6f), fontSize = 20.sp)
                    IconButton(onClick = { navController.navigate("serieVer/${item.id}") }, modifier = Modifier.weight(.15f)) {
                        Icon(Icons.Outlined.Edit, "Editar ${item.name}")
                    }
                    IconButton(onClick = { navController.navigate("serieDel/${item.id}") }, modifier = Modifier.weight(.15f)) {
                        Icon(Icons.Outlined.Delete, "Eliminar ${item.name}")
                    }
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEditar(navController: NavHostController, servicio: SerieApiService, pid: Int = 0) {
    var name by rememberSaveable(pid) { mutableStateOf("") }
    var release_date by rememberSaveable(pid) { mutableStateOf("") }
    var rating by rememberSaveable(pid) { mutableStateOf("") }
    var category by rememberSaveable(pid) { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(pid != 0) }
    var cargado by remember { mutableStateOf(pid == 0) }
    var grabando by remember { mutableStateOf(false) }
    var intento by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(pid, intento) {
        if (pid == 0) return@LaunchedEffect
        cargando = true
        error = ""
        try {
            val respuesta = servicio.selectSerie(pid.toString())
            val serie = respuesta.body()
            if (respuesta.isSuccessful && serie != null) {
                name = serie.name
                release_date = serie.release_date
                rating = serie.rating.toString()
                category = serie.category
                cargado = true
            } else error = "No pude abrir esta serie. Vuelve a la lista y comprueba que todavía exista."
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { error = ERROR_CONEXION }
        finally { cargando = false }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (cargando) CircularProgressIndicator()
        TextField(pid.toString(), {}, label = { Text("ID (solo lectura)") }, readOnly = true, singleLine = true)
        TextField(name, { name = it }, label = { Text("Name:") }, singleLine = true, enabled = !cargando && !grabando)
        TextField(release_date, { release_date = it }, label = { Text("Release Date: AAAA-MM-DD") }, singleLine = true, enabled = !cargando && !grabando)
        TextField(rating, { rating = it }, label = { Text("Rating: número entero") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), enabled = !cargando && !grabando)
        TextField(category, { category = it }, label = { Text("Category:") }, singleLine = true, enabled = !cargando && !grabando)
        Text("Categorías: horror, comedy, action, drama", style = MaterialTheme.typography.bodySmall)
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        if (!cargado && !cargando) OutlinedButton(onClick = { intento++ }) { Text("Reintentar") }
        Button(enabled = cargado && !cargando && !grabando, onClick = {
            val validacion = validarSerie(name, release_date, rating, category)
            if (validacion != null) { error = validacion; return@Button }
            val serie = SerieModel(pid, name.trim(), release_date.trim(), rating.trim().toInt(), category.trim().lowercase(Locale.ROOT))
            grabando = true
            error = ""
            scope.launch {
                try {
                    val respuesta = if (pid == 0) servicio.insertSerie(serie) else servicio.updateSerie(pid.toString(), serie)
                    if (respuesta.isSuccessful) {
                        navController.navigate("series") { popUpTo("series") { inclusive = true }; launchSingleTop = true }
                    } else error = "Django rechazó los datos (HTTP ${respuesta.code()}). Revisa los campos; todavía no se guardaron."
                } catch (e: CancellationException) { throw e }
                catch (e: Exception) { error = ERROR_CONEXION }
                finally { grabando = false }
            }
        }) { Text(if (grabando) "Guardando…" else "Grabar", fontSize = 16.sp) }
    }
}

@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var borrando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    fun volver() { navController.popBackStack() }
    AlertDialog(
        onDismissRequest = { if (!borrando) volver() },
        title = { Text("Confirmar Eliminación") },
        text = { Column { Text("¿Está seguro de eliminar la Serie?"); if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error) } },
        confirmButton = {
            Button(enabled = !borrando, onClick = {
                borrando = true
                error = ""
                scope.launch {
                    try {
                        val respuesta = servicio.deleteSerie(id.toString())
                        if (respuesta.isSuccessful) navController.navigate("series") { popUpTo("series") { inclusive = true }; launchSingleTop = true }
                        else error = "No pude eliminar la serie (HTTP ${respuesta.code()})."
                    } catch (e: CancellationException) { throw e }
                    catch (e: Exception) { error = ERROR_CONEXION }
                    finally { borrando = false }
                }
            }) { Text(if (borrando) "Eliminando…" else "Aceptar") }
        },
        dismissButton = { OutlinedButton(enabled = !borrando, onClick = { volver() }) { Text("Cancelar") } }
    )
}
