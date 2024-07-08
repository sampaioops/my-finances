package com.example.myfinances.model


import java.util.UUID
import kotlin.math.absoluteValue

class FinancialMovement(
    val amount: Long,
    val type: MovementType,
    val cause: String,
    val dueDate: String,
    val id: UUID = UUID.nameUUIDFromBytes((amount.absoluteValue.toString() + dueDate.toString() + type.toString() + cause).toByteArray())
) {

}