package com.example.myfinances.model

data class Summary(
    val totalIncome: Long,
    val totalExpense: Long,
    val totalBalance: Long,
    val entries: List<FinancialMovement>
)
