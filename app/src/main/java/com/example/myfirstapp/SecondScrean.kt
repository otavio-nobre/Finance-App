package com.example.myfirstapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class ItemFinanceiro(
    val Description: String,
    val value: Double,
    val isInvestment: Boolean,
    val monthlyReturnRate: Double = 0.0
)

@Composable
fun SecondScreen(navController: NavController) {
    var incomeInput by remember { mutableStateOf("") }
    var Description by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }
    var rateInput by remember { mutableStateOf("") }
    var isInvestmentSelected by remember { mutableStateOf(false) }

    val listofitens = remember { mutableStateListOf<ItemFinanceiro>() }

    val primaryGreen = Color(0xFF1E5631)
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryGreen,
        focusedLabelColor = primaryGreen,
        cursorColor = primaryGreen
    )

    val income = incomeInput.toDoubleOrNull() ?: 0.0
    val totalExpenses = listofitens.filter { !it.isInvestment }.sumOf { it.value }
    val totalInvested = listofitens.filter { it.isInvestment }.sumOf { it.value }
    val remainingBalance = income - totalExpenses - totalInvested

    val totalMonthlyYield = listofitens.filter { it.isInvestment }.sumOf {
        it.value * (it.monthlyReturnRate / 100.0)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Financial Dashboard",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen
        )

        OutlinedTextField(
            value = incomeInput,
            onValueChange = { incomeInput = it },
            label = { Text("Monthly Income ($)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Remaining Balance", fontSize = 14.sp, color = primaryGreen)
                Text(
                    text = "$ %.2f".format(remainingBalance),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (remainingBalance >= 0) primaryGreen else Color.Red
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Expenses", fontSize = 12.sp, color = Color.Red)
                    Text(
                        text = "$ %.2f".format(totalExpenses),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Invested / Yield", fontSize = 12.sp, color = Color(0xFF0D47A1))
                    Text(
                        text = "$ %.2f".format(totalInvested),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "+$ %.2f/mo".format(totalMonthlyYield),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        OutlinedTextField(
            value = Description,
            onValueChange = { Description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        OutlinedTextField(
            value = valueInput,
            onValueChange = { valueInput = it },
            label = { Text("Value ($)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        if (isInvestmentSelected) {
            OutlinedTextField(
                value = rateInput,
                onValueChange = { rateInput = it },
                label = { Text("Monthly Return Rate (%)") },
                modifier = Modifier.fillMaxWidth(),
                colors = customTextFieldColors
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Category: ${if (isInvestmentSelected) "Investment" else "Expense"}")
            Switch(
                checked = isInvestmentSelected,
                onCheckedChange = { isInvestmentSelected = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = primaryGreen
                )
            )
        }

        Button(
            onClick = {
                val valueDouble = valueInput.toDoubleOrNull() ?: 0.0
                val rateDouble = rateInput.toDoubleOrNull() ?: 0.0

                if (Description.isNotBlank() && valueDouble > 0) {
                    val index = listofitens.indexOfFirst {
                        it.Description.equals(Description.trim(), ignoreCase = true) &&
                                it.isInvestment == isInvestmentSelected
                    }

                    if (index != -1) {
                        val itemAntigo = listofitens[index]
                        listofitens[index] = itemAntigo.copy(
                            value = itemAntigo.value + valueDouble,
                            monthlyReturnRate = if (isInvestmentSelected) rateDouble else 0.0
                        )
                    } else {
                        listofitens.add(
                            ItemFinanceiro(
                                Description = Description.trim(),
                                value = valueDouble,
                                isInvestment = isInvestmentSelected,
                                monthlyReturnRate = if (isInvestmentSelected) rateDouble else 0.0
                            )
                        )
                    }

                    Description = ""
                    valueInput = ""
                    rateInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Text("+ Add Item", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        listofitens.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.Description, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = if (item.isInvestment) {
                                "Investment (${item.monthlyReturnRate}%/mo)"
                            } else "Expense",
                            fontSize = 12.sp,
                            color = if (item.isInvestment) Color(0xFF0D47A1) else Color.Red
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$ %.2f".format(item.value),
                                fontWeight = FontWeight.Bold,
                                color = if (item.isInvestment) Color(0xFF0D47A1) else Color.Red
                            )
                            if (item.isInvestment && item.monthlyReturnRate > 0) {
                                val estimatedYield = item.value * (item.monthlyReturnRate / 100.0)
                                Text(
                                    text = "+$ %.2f".format(estimatedYield),
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { listofitens.remove(item) }) {
                            Text("X", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Text("Back")
        }
    }
}