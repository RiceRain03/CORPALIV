package com.example.corpaliv.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.corpaliv.navigation.Destino

@Composable
fun AppDrawer(
    destinoActual: Destino,
    drawerState: DrawerState,
    onDestinoSeleccionado: (Destino) -> Unit,
    contenido: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "Alba Lab",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "CORPALIV",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Destino.entries.forEach { destino ->
                    NavigationDrawerItem(
                        label = { Text(destino.titulo) },
                        icon = { Icon(destino.icono, contentDescription = null) },
                        selected = destino == destinoActual,
                        onClick = { onDestinoSeleccionado(destino) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = contenido
    )
}