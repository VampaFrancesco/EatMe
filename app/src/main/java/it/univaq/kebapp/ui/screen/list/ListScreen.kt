package it.univaq.kebapp.ui.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
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
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel(),
    onNavigateToDetail: (Kebabbari) -> Unit = {}
) {
    val uiState = viewModel.uiState

    if (uiState.loadingMsg != null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text(
                    text = uiState.loadingMsg,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        return
    }

    if (uiState.errorMsg != null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = uiState.errorMsg,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Kebabbari (${uiState.kebabbari.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(uiState.kebabbari.size) { index ->
                val kebabbaro = uiState.kebabbari[index]
                KebabbariItem(
                    modifier = Modifier.fillMaxWidth(),
                    kebabbaro = kebabbaro,
                    onItemClick = { onNavigateToDetail(it) }
                )
            }
        }
    }
}

@Composable
fun KebabbariItem(
    modifier: Modifier = Modifier,
    kebabbaro: Kebabbari,
    onItemClick: (Kebabbari) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clickable { onItemClick(kebabbaro) }
    ) {
        Text(
            text = kebabbaro.cnome,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${kebabbaro.ccomune}, ${kebabbaro.cprovincia}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = kebabbaro.cregione,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
