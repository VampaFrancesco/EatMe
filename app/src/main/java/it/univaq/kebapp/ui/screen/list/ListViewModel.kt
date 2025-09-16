package it.univaq.kebapp.ui.screen.list

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


data class ListUIState(
    val kebabbari: List<Kebabbari> = emptyList(),
    val loadingMsg: String? = null,
    val errorMsg: String? = null

){

}

@HiltViewModel
class ListViewModel @Inject constructor(private val getKebabbariUseCase: GetKebabbariUseCase
): ViewModel() {
    var uiState by mutableStateOf(ListUIState())
        private set

    init{
        downloadKebabbari()
    }

    private fun downloadKebabbari(){
        viewModelScope.launch {
            getKebabbariUseCase().collect { resource -> when(resource){

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
                    kebabbari = resource.data
                )
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