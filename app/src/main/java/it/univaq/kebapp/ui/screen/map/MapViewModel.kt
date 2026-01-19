package it.univaq.kebapp.ui.screen.map

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.univaq.kebapp.common.Resource
import it.univaq.kebapp.domain.model.Kebabbari
import it.univaq.kebapp.domain.use_case.GetKebabbariUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUIState(
    val allKebabbari: List<Kebabbari> = emptyList(),
    val nearbyKebabbari: List<Kebabbari> = emptyList(),
    val loadingMsg: String? = null,
    val errorMsg: String? = null,
    val hasLocationPermission: Boolean = false,
    val userLocation: Location? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getKebabbariUseCase: GetKebabbariUseCase
) : ViewModel() {

    var uiState by mutableStateOf(MapUIState())
        private set

    init {
        loadKebabbari()
    }

    fun updateLocationPermission(granted: Boolean, location: Location?) {
        uiState = uiState.copy(
            hasLocationPermission = granted,
            userLocation = location
        )

        if (granted && location != null) {
            filterNearbyKebabbari(location)
        }
    }

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    private fun filterNearbyKebabbari(userLocation: Location) {
        val maxDistanceKm = 20.0   // ESTESO A 20 KM

        val nearby = uiState.allKebabbari.filter { kebabbari ->
            val kebabLocation = Location("").apply {
                latitude = kebabbari.clatitudine.toDouble()
                longitude = kebabbari.clongitudine.toDouble()
            }
            val distanceKm = userLocation.distanceTo(kebabLocation) / 1000.0
            distanceKm <= maxDistanceKm
        }

        uiState = uiState.copy(nearbyKebabbari = nearby)
    }

    private fun loadKebabbari() {
        viewModelScope.launch {
            getKebabbariUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(
                            loadingMsg = resource.message,
                            errorMsg = null
                        )
                    }
                    is Resource.Success -> {
                        uiState = uiState.copy(
                            loadingMsg = null,
                            errorMsg = null,
                            allKebabbari = resource.data
                        )
                        uiState.userLocation?.let { filterNearbyKebabbari(it) }
                    }
                    is Resource.Error -> {
                        uiState = uiState.copy(
                            loadingMsg = null,
                            errorMsg = resource.message
                        )
                    }
                }
            }
        }
    }
}
