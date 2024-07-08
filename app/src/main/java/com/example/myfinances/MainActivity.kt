package com.example.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfinances.model.FinancialMovement
import com.example.myfinances.ui.theme.MyFinancesTheme
import java.time.LocalDate

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.myfinances.model.MovementType
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale



class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinancesTheme {
                Scaffold() { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                    {
                        val isLoading by viewModel.isLoading.collectAsState()
                        if (isLoading) {
                            LoadingComponent()
                        } else {
                            viewModel.agentsData.collectAsState().value.let { summary ->
                                HomeView(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }


                    }
                }
            }
        }
    }
}


//TODO: Organize the composable functions

@Composable
fun HomeView(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val summary by viewModel.agentsData.collectAsState()
    val cuurentDate by viewModel.currentDate.collectAsState()


    Column(modifier = modifier) {
        SummaryCard(
            totalIncome = summary.totalIncome,
            totalExpense = summary.totalExpense,
            totalBalance = summary.totalBalance,
            currentDate = cuurentDate,
            viewModel = viewModel
        )

        LazyColumn {
            items(summary.financialMovements) { financialMovement ->
                FinancialMovementCard(
                    financialMovement = financialMovement,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    totalIncome: Long,
    totalExpense: Long,
    totalBalance: Long,
    currentDate: LocalDate = LocalDate.now(),
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium, // Usando shapes do tema
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Resumo do mês de ${
                    currentDate.month.getDisplayName(
                        TextStyle.FULL,
                        Locale("pt", "BR")
                    )
                } de ${currentDate.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            SummaryItem(label = "Total de créditos", value = totalIncome)
            SummaryItem(label = "Total de débitos", value = totalExpense)
            SummaryItem(label = "Saldo", value = totalBalance, isTotal = true)
            MonthControlButtons(viewModel = viewModel)
        }

    }
}


@Composable
fun SummaryItem(label: String, value: Long, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = if (isTotal) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = formatCentsToReal(value),
            modifier = Modifier.weight(1f),
            style = if (isTotal) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium
        )
    }
    if (!isTotal) Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun FinancialMovementCard(financialMovement: FinancialMovement, modifier: Modifier = Modifier) {
    val backgroundColor =
        if (financialMovement.type == MovementType.CREDIT) Color(0xFF81C784) else MaterialTheme.colorScheme.errorContainer
    val textColor =
        if (financialMovement.type == MovementType.CREDIT) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = financialMovement.description,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(color = textColor)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatCentsToReal(financialMovement.amount),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
                Text(
                    text = "Vencimento: ${financialMovement.dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                )
            }
        }
    }
}

fun formatCentsToReal(cents: Long): String {
    val amountInReal = cents / 100.0
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return numberFormat.format(amountInReal)
}

@Composable
fun LoadingComponent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

}

@Composable
fun MonthControlButtons(viewModel: MainViewModel) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

        Button(onClick = {
            viewModel.minusMonth()
        }) {
            Text("<")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { viewModel.fetchSummary(viewModel.currentDate.value) }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Atualizar")
        }

        Spacer(modifier = Modifier.width(8.dp))


        Button(onClick = {
            viewModel.plusMonth()
        }) {
            Text(">")
        }
    }


}
