package it.univaq.kebapp.ui.screen.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import it.univaq.kebapp.domain.model.Kebabbari
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    var selectedkebabbari by remember { mutableStateOf<Kebabbari?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // Permessi posizione
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                viewModel.updateLocationPermission(granted, location)

                // CENTRA sulla posizione appena ottenuta
                location?.let { loc ->
                    mapView?.controller?.animateTo(GeoPoint(loc.latitude, loc.longitude))
                    mapView?.controller?.setZoom(13.0)
                }
            }
        } else {
            viewModel.updateLocationPermission(false, null)
        }
    }

    // Richiesta automatica all'apertura
    LaunchedEffect(Unit) {
        if (!uiState.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Lista da mostrare
    val baseKebabbari = if (uiState.hasLocationPermission &&
        uiState.userLocation != null &&
        uiState.nearbyKebabbari.isNotEmpty()
    ) {
        uiState.nearbyKebabbari
    } else {
        uiState.allKebabbari
    }

    // Filtro ricerca
    val kebabbariToShow = remember(baseKebabbari, uiState.searchQuery) {
        if (uiState.searchQuery.isBlank()) {
            baseKebabbari
        } else {
            baseKebabbari.filter { kebab ->
                kebab.cnome.contains(uiState.searchQuery, ignoreCase = true) ||
                        kebab.ccomune.contains(uiState.searchQuery, ignoreCase = true) ||
                        kebab.cprovincia.contains(uiState.searchQuery, ignoreCase = true)
            }
        }
    }

    // Aggiorna i marker quando cambia la ricerca + CENTRA sulla prima città trovata
    LaunchedEffect(kebabbariToShow, uiState.userLocation) {
        mapView?.let { map ->
            map.overlays.clear()

            // Marker kebabbari
            kebabbariToShow.forEach { kebabbari ->
                val marker = Marker(map).apply {
                    position = GeoPoint(
                        kebabbari.clatitudine.toDouble(),
                        kebabbari.clongitudine.toDouble()
                    )
                    title = kebabbari.cnome
                    snippet = "${kebabbari.ccomune}, ${kebabbari.cprovincia}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    setOnMarkerClickListener { _, _ ->
                        selectedkebabbari = kebabbari
                        true
                    }
                }
                map.overlays.add(marker)
            }

            // Pin BLU della tua posizione
            uiState.userLocation?.let { loc ->
                val myMarker = Marker(map).apply {
                    position = GeoPoint(loc.latitude, loc.longitude)
                    title = "La tua posizione"
                    snippet = "Sei qui"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    // Marker BLU
                    icon = context.getDrawable(org.osmdroid.library.R.drawable.marker_default)
                }
                map.overlays.add(myMarker)
            }

            // CENTRA sulla prima città trovata quando cerchi
            if (uiState.searchQuery.isNotBlank() && kebabbariToShow.isNotEmpty()) {
                val firstResult = kebabbariToShow.first()
                map.controller.animateTo(
                    GeoPoint(
                        firstResult.clatitudine.toDouble(),
                        firstResult.clongitudine.toDouble()
                    )
                )
                map.controller.setZoom(14.0)
            }

            map.invalidate()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            if (uiState.hasLocationPermission &&
                                uiState.userLocation != null &&
                                uiState.nearbyKebabbari.isNotEmpty()
                            ) {
                                "Kebabbari entro 20 km (${kebabbariToShow.size})"
                            } else {
                                "Mappa Kebabbari (${kebabbariToShow.size})"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Indietro"
                            )
                        }
                    },
                    actions = {
                        // BOTTONE: Ricentra sulla TUA posizione
                        IconButton(
                            onClick = {
                                uiState.userLocation?.let { loc ->
                                    mapView?.controller?.animateTo(
                                        GeoPoint(loc.latitude, loc.longitude)
                                    )
                                    mapView?.controller?.setZoom(13.0)
                                } ?: run {
                                    // Se non hai posizione, richiedila
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Centra sulla mia posizione",
                                tint = if (uiState.userLocation != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )

                // Barra di ricerca
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Cerca per nome o città...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca"
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancella"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    ) { padding ->
        if (uiState.loadingMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = uiState.loadingMsg)
                }
            }
            return@Scaffold
        }

        if (uiState.errorMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        mapView = this
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Centro iniziale
                        if (uiState.userLocation != null) {
                            controller.setZoom(13.0)
                            controller.setCenter(
                                GeoPoint(
                                    uiState.userLocation.latitude,
                                    uiState.userLocation.longitude
                                )
                            )
                        } else {
                            controller.setZoom(13.0)
                            controller.setCenter(GeoPoint(42.4584, 14.2028))
                        }
                    }
                }
            )
        }

        if (selectedkebabbari != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedkebabbari = null },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedkebabbari!!.cnome,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${selectedkebabbari!!.ccomune}, ${selectedkebabbari!!.cprovincia}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = selectedkebabbari!!.cregione,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val lat = selectedkebabbari!!.clatitudine
                            val lng = selectedkebabbari!!.clongitudine
                            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${selectedkebabbari!!.cnome})")
                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }

                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
                                )
                                context.startActivity(browserIntent)
                            }

                            selectedkebabbari = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Ottieni indicazioni")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
