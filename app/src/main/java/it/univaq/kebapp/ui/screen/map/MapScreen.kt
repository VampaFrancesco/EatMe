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
import androidx.compose.material.icons.filled.LocationOn
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

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                viewModel.updateLocationPermission(granted, location)
            }
        } else {
            viewModel.updateLocationPermission(false, null)
        }
    }

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

    val kebabbariToShow = if (uiState.nearbyKebabbari.isNotEmpty()) {
        uiState.nearbyKebabbari
    } else {
        uiState.allKebabbari
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.nearbyKebabbari.isNotEmpty()) {
                            "Kebabbari vicini (${uiState.nearbyKebabbari.size})"
                        } else {
                            "Mappa Kebabbari (${uiState.allKebabbari.size})"
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
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

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        if (uiState.userLocation != null) {
                            controller.setZoom(12.0)
                            controller.setCenter(
                                GeoPoint(
                                    uiState.userLocation.latitude,
                                    uiState.userLocation.longitude
                                )
                            )
                        } else {
                            controller.setZoom(13.0)
                            controller.setCenter(GeoPoint(42.4584, 14.2028)) // Pescara
                        }

                        kebabbariToShow.forEach { kebabbari ->
                            val marker = Marker(this).apply {
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
                            overlays.add(marker)
                        }
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()

                    kebabbariToShow.forEach { kebabbari ->
                        val marker = Marker(mapView).apply {
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
                        mapView.overlays.add(marker)
                    }

                    if (uiState.userLocation != null) {
                        mapView.controller.setCenter(
                            GeoPoint(
                                uiState.userLocation.latitude,
                                uiState.userLocation.longitude
                            )
                        )
                    }

                    mapView.invalidate()
                }
            )

            if (uiState.hasLocationPermission &&
                uiState.userLocation != null &&
                uiState.nearbyKebabbari.isEmpty() &&
                uiState.allKebabbari.isNotEmpty()
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Nessun kebabbari entro 10 km dalla tua posizione",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
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
