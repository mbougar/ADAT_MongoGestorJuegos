import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.util.*
import androidx.compose.ui.unit.DpSize
import model.Juego
import repository.RepositorioJuegos
import ui.DatePicker
import utilities.formatDate
import utilities.isNumeric

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun App() {
    val repositorioJuegos = remember { RepositorioJuegos() }
    var filtro by remember { mutableStateOf(TextFieldValue("")) }
    var juegoSeleccionado by remember { mutableStateOf<Juego?>(null) }
    var dialogoAbierto by remember { mutableStateOf(false) }
    var dialogoNuevoJuego by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var generosDisponibles = repositorioJuegos.getGeneros()

    val generosSeleccionados = remember { mutableStateListOf<String>() }

    // Filtro los juegos por genero y nombre
    var juegosFiltrados = repositorioJuegos.getJuegos().filter {
        it.titulo.contains(filtro.text, ignoreCase = true) &&
                (generosSeleccionados.isEmpty() || generosSeleccionados.contains(it.genero))
    }.sortedBy { it.titulo }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Columna generos
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text("Filtros de Género", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                generosDisponibles.forEach { genero ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = generosSeleccionados.contains(genero),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    generosSeleccionados.add(genero)
                                } else {
                                    generosSeleccionados.remove(genero)
                                }
                            }
                        )
                        Text(genero, style = MaterialTheme.typography.body1)
                        if (generosSeleccionados.contains(genero)) {
                            IconButton(
                                onClick = {
                                    repositorioJuegos.deleteJuegosPorGenero(genero)
                                    generosSeleccionados.remove(genero)
                                    juegosFiltrados = repositorioJuegos.getJuegos().filter {
                                        it.titulo.contains(filtro.text, ignoreCase = true) &&
                                                (generosSeleccionados.isEmpty() || generosSeleccionados.contains(it.genero))
                                    }.sortedBy { it.titulo }
                                }
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Eliminar género")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(3f).fillMaxHeight()) {
                OutlinedTextField(
                    value = filtro,
                    onValueChange = { filtro = it },
                    label = { Text("Buscar juegos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar juego")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(juegosFiltrados) { juego ->
                        ListItem(
                            text = { Text(juego.titulo) },
                            secondaryText = {
                                Text("Género: ${juego.genero}, Precio: ${juego.precio}€, Fecha de lanzamiento: ${formatDate(juego.fechaLanzamiento)}")
                            },
                            modifier = Modifier.fillMaxWidth().clickable {
                                juegoSeleccionado = juego
                                dialogoAbierto = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = { dialogoNuevoJuego = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar juego")
                }
            }
        }

        // Editar o eliminar juego
        if (dialogoAbierto && juegoSeleccionado != null) {
            Dialog(onDismissRequest = { dialogoAbierto = false }) {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Editar juego", style = MaterialTheme.typography.h6)
                        Spacer(modifier = Modifier.height(8.dp))

                        var titulo by remember { mutableStateOf(juegoSeleccionado!!.titulo) }
                        var genero by remember { mutableStateOf(juegoSeleccionado!!.genero) }
                        var precio by remember { mutableStateOf(juegoSeleccionado!!.precio.toString()) }
                        var fechaLanzamiento by remember { mutableStateOf(juegoSeleccionado!!.fechaLanzamiento) }
                        var showDatePicker by remember { mutableStateOf(false) }
                        var showWarning by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (showWarning) {
                            Text(text = "El titulo introducido ya existe en la base de datos", color = Color.Red)
                        }
                        OutlinedTextField(
                            value = genero,
                            onValueChange = { genero = it },
                            label = { Text("Género") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = formatDate(fechaLanzamiento),
                            onValueChange = {  },
                            label = { Text("Fecha de lanzamiento") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false
                        )

                        IconButton(
                            onClick = {
                                showDatePicker = true
                            }
                        ) {
                            Icon(Icons.Outlined.DateRange, contentDescription = "Seleccionar fecha")
                        }
                        if (showDatePicker) {
                            DatePicker(
                                initDate = Date(),
                                onDismissRequest = { showDatePicker = false },
                                onDateSelect = {
                                    fechaLanzamiento = it
                                    showDatePicker = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Button(
                                onClick = {
                                    if (repositorioJuegos.getJuego(titulo) == null) {
                                        showWarning = false
                                        repositorioJuegos.updateJuego(
                                            juegoSeleccionado!!.titulo,
                                            titulo, genero.uppercase(), precio.toDouble(), fechaLanzamiento
                                        )
                                        dialogoAbierto = false
                                        juegosFiltrados = repositorioJuegos.getJuegos().filter {
                                            it.titulo.contains(filtro.text, ignoreCase = true) &&
                                                    (generosSeleccionados.isEmpty() || generosSeleccionados.contains(it.genero))
                                        }.sortedBy { it.titulo }
                                        generosDisponibles = repositorioJuegos.getGeneros()
                                    } else if (repositorioJuegos.getJuego(titulo) != null && juegoSeleccionado!!.titulo == titulo) {
                                        showWarning = false
                                        repositorioJuegos.updateJuego(
                                            juegoSeleccionado!!.titulo,
                                            titulo, genero.uppercase(), precio.toDouble(), fechaLanzamiento
                                        )
                                        dialogoAbierto = false
                                        juegosFiltrados = repositorioJuegos.getJuegos().filter {
                                            it.titulo.contains(filtro.text, ignoreCase = true) &&
                                                    (generosSeleccionados.isEmpty() || generosSeleccionados.contains(it.genero))
                                        }.sortedBy { it.titulo }
                                        generosDisponibles = repositorioJuegos.getGeneros()
                                    } else {
                                        showWarning = true
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Guardar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    repositorioJuegos.deleteJuegoPorTitulo(juegoSeleccionado!!.titulo)
                                    dialogoAbierto = false
                                    juegosFiltrados = repositorioJuegos.getJuegos().filter {
                                        it.titulo.contains(filtro.text, ignoreCase = true) &&
                                                (generosSeleccionados.isEmpty() || generosSeleccionados.contains(it.genero))
                                    }.sortedBy { it.titulo }
                                    generosDisponibles = repositorioJuegos.getGeneros()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        // Agregar un juego nuevo
        if (dialogoNuevoJuego) {
            Dialog(onDismissRequest = { dialogoNuevoJuego = false }) {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Agregar nuevo juego", style = MaterialTheme.typography.h6)
                        Spacer(modifier = Modifier.height(8.dp))

                        var titulo by remember { mutableStateOf("") }
                        var genero by remember { mutableStateOf("") }
                        var precio by remember { mutableStateOf("") }
                        var showDatePicker by remember { mutableStateOf(false) }
                        var fechaLanzamiento by remember { mutableStateOf(Date()) }
                        var showWarning by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (showWarning) {
                            Text(text = "El titulo introducido ya existe en la base de datos", color = Color.Red)
                        }
                        OutlinedTextField(
                            value = genero,
                            onValueChange = { genero = it },
                            label = { Text("Género") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = formatDate(fechaLanzamiento),
                            onValueChange = {  },
                            label = { Text("Fecha Lanzamiento") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                showDatePicker = true
                            }
                        ) {
                            Icon(Icons.Outlined.DateRange, contentDescription = "Seleccionar fecha")
                        }
                        if (showDatePicker) {
                            DatePicker(
                                initDate = Date(),
                                onDismissRequest = { showDatePicker = false },
                                onDateSelect = {
                                    fechaLanzamiento = it
                                    showDatePicker = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (repositorioJuegos.getJuego(titulo) == null) {
                                    showWarning = false
                                    repositorioJuegos.insertJuego(
                                        titulo,
                                        genero.uppercase(),
                                        precio.toDouble(),
                                        fechaLanzamiento
                                    )
                                    dialogoNuevoJuego = false
                                    juegosFiltrados = repositorioJuegos.getJuegos().filter {
                                        it.titulo.contains(filtro.text, ignoreCase = true) &&
                                                (generosSeleccionados.isEmpty() || generosSeleccionados.contains(it.genero))
                                    }.sortedBy { it.titulo }
                                    generosDisponibles = repositorioJuegos.getGeneros()
                                } else {
                                   showWarning = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = (titulo.isNotBlank() && genero.isNotBlank() && precio.isNotBlank() && isNumeric(precio))
                        ) {
                            Text("Agregar")
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Juegoteca",
        state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(800.dp, 600.dp)
        ),
        resizable = false
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            App()
        }
    }
}
