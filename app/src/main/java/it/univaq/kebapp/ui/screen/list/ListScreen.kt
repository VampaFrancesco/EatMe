package it.univaq.kebapp.ui.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.univaq.kebapp.domain.model.Kebabbari


@Composable
fun ListScreen(modifier: Modifier = Modifier, viewModel: ListViewModel = hiltViewModel()){

    val uiState = viewModel.uiState

    if(uiState.loadingMsg != null){
        Box(modifier = modifier, contentAlignment = Alignment.Center){
            Text(text = uiState.loadingMsg)
        }
        return
        }

    if(uiState.errorMsg != null){
        Box(modifier = modifier, contentAlignment = Alignment.Center){
            Text(text = uiState.errorMsg)
        }
        return
    }

    Column(modifier = modifier){
        Text(
            text = "Kebabbari",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(uiState.kebabbari.size){ index ->



                val kebabbaro = uiState.kebabbari[index]
                KebabbariItem(
                    modifier = Modifier.fillMaxWidth(),
                    kebabbaro = kebabbaro,
                    onItemClick = {
                        
                    }
                )
            }
        }
    }
}


@Composable
fun KebabbariItem(
    modifier: Modifier = Modifier,
    kebabbaro: Kebabbari,
    onItemClick: (Kebabbari) -> Unit = {

    }
){
    Column(
        modifier = modifier
            .padding(16.dp)
            .clickable{
                onItemClick
            }
    )
    {
        Text(
            text = kebabbaro.id.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "${kebabbaro.ccomune}, ${kebabbaro.cprovincia}, ${kebabbaro.cregione}",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    
}



