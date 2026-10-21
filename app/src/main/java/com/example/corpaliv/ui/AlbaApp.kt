package com.example.corpaliv.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.corpaliv.navigation.Destino
import com.example.corpaliv.ui.components.AppDrawer
import com.example.corpaliv.ui.screens.PantallaCatalogo
import com.example.corpaliv.ui.screens.PantallaContacto
import com.example.corpaliv.ui.screens.PantallaCuenta
import com.example.corpaliv.ui.screens.PantallaDonaciones
import com.example.corpaliv.ui.screens.PantallaInicio
import com.example.corpaliv.ui.screens.PantallaPrograma
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbaApp() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoActual = Destino.desdeRuta(entradaActual?.destination?.route)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun irA(destino: Destino) {
        navController.navigate(destino.ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    AppDrawer(
        destinoActual = destinoActual,
        drawerState = drawerState,
        onDestinoSeleccionado = { destino ->
            scope.launch { drawerState.close() }
            irA(destino)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(destinoActual.titulo) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú lateral")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    Destino.entries.filter { it.enBarraInferior }.forEach { destino ->
                        NavigationBarItem(
                            selected = destino == destinoActual,
                            onClick = { irA(destino) },
                            icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destino.INICIO.ruta,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destino.INICIO.ruta) { PantallaInicio(onIrA = { irA(it) }) }
                composable(Destino.PROGRAMAS.ruta) { PantallaPrograma() }
                composable(Destino.CATALOGO.ruta) { PantallaCatalogo() }
                composable(Destino.DONACIONES.ruta) { PantallaDonaciones() }
                composable(Destino.CONTACTO.ruta) { PantallaContacto() }
                composable(Destino.CUENTA.ruta) { PantallaCuenta() }
            }
        }
    }
}