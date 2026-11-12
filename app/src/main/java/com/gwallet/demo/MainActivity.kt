package com.gwallet.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF0878D1)
private data class DemoTransaction(val id: Int, val title: String, val detail: String, val amount: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WalletScreen() }
    }
}

@Composable
private fun WalletScreen() {
    var transactions by remember {
        mutableStateOf(listOf(
            DemoTransaction(1, "Sample transfer", "Demo record • Today", "− ₱250.00"),
            DemoTransaction(2, "Demo payment", "Demo record • Yesterday", "− ₱120.00"),
            DemoTransaction(3, "Sample cash in", "Demo record • Nov 10", "+ ₱500.00")
        ))
    }
    var selected by remember { mutableStateOf<DemoTransaction?>(null) }
    Surface(color = Color(0xFFF3F7FB), modifier = Modifier.fillMaxSize()) {
        Column {
            Column(Modifier.fillMaxWidth().background(Blue).padding(22.dp)) {
                Text("G-WALLET DEMO", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Independent wallet interface prototype", color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
                Spacer(Modifier.height(22.dp))
                Text("SIMULATED BALANCE", color = Color.White.copy(alpha = .85f), fontSize = 12.sp)
                Text("₱ 5,000.00", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("Demo only • Not real money", color = Color.White, fontSize = 12.sp)
            }
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Send (demo)", "Receive (demo)", "Pay (demo)").forEach { label ->
                    Card(Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Box(Modifier.padding(vertical = 18.dp, horizontal = 5.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(label, color = Blue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Transaction history", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${transactions.size} demo records", color = Color.Gray, fontSize = 12.sp)
            }
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions, key = { it.id }) { tx ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tx.title, fontWeight = FontWeight.SemiBold)
                                Text(tx.detail, color = Color.Gray, fontSize = 12.sp)
                                Text(tx.amount, color = Blue, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { selected = tx }) { Text("Delete", color = Color(0xFFB3261E)) }
                        }
                    }
                }
            }
            Text("This is a fictional demo. No real payments or GCash data are used.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.fillMaxWidth().padding(12.dp))
        }
    }
    selected?.let { tx ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("Delete demo record?") },
            text = { Text("This removes only “${tx.title}” from this demo screen. It does not affect any real wallet or financial record.") },
            confirmButton = { TextButton(onClick = { transactions = transactions.filterNot { it.id == tx.id }; selected = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { selected = null }) { Text("Cancel") } }
        )
    }
}
