package com.gwallet.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF1269D3)
private val Pale = Color(0xFFF2F6FC)
private data class DemoTransaction(val id: Int, val title: String, val time: String, val amount: String, val group: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GWalletApp() }
    }
}

@Composable
private fun GWalletApp() {
    var tab by remember { mutableStateOf(0) }
    var showLogin by remember { mutableStateOf(false) }
    var transactions by remember {
        mutableStateOf(listOf(
            DemoTransaction(1, "Online payment • Demo", "2:52 AM", "− ₱1,800.00", "Today"),
            DemoTransaction(2, "Online payment • Demo", "1:47 AM", "− ₱1,000.00", "Today"),
            DemoTransaction(3, "Send money • Demo", "10:25 PM", "− ₱750.00", "Yesterday"),
            DemoTransaction(4, "ATM withdrawal • Sample", "7:15 AM", "− ₱3,218.00", "Nov 11, 2026"),
            DemoTransaction(5, "Send money • Demo", "6:31 PM", "− ₱300.00", "Nov 10, 2026"),
            DemoTransaction(6, "Demo cash in", "5:36 PM", "+ ₱500.00", "Nov 10, 2026")
        ))
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Pale)) {
        if (showLogin) {
            DemoLogin(onContinue = { showLogin = false })
        } else {
            Scaffold(
                containerColor = Pale,
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                        NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("Transactions") })
                        NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.QrCode, null) }, label = { Text("QR demo") })
                        NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.AccountCircle, null) }, label = { Text("Profile") })
                    }
                }
            ) { padding ->
                when (tab) {
                    0 -> HomeScreen(Modifier.padding(padding), transactions.size, onHistory = { tab = 1 })
                    1 -> HistoryScreen(Modifier.padding(padding), transactions, onDelete = { id -> transactions = transactions.filterNot { it.id == id } })
                    2 -> QrDemoScreen(Modifier.padding(padding))
                    else -> ProfileScreen(Modifier.padding(padding), onDemoLock = { showLogin = true })
                }
            }
        }
    }
}

@Composable
private fun DemoBanner() {
    Surface(color = Color(0xFFFFF2D8), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text("DEMO APP • Fictional balance and sample records only. Not connected to GCash or any bank.", color = Color(0xFF754B00), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, count: Int, onHistory: () -> Unit) {
    Column(modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("G", color = Blue, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Hello!", color = Blue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Welcome to G-Wallet Demo", color = Color.Gray, fontSize = 11.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("HELP", color = Blue, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            DemoBanner()
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blue)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("AVAILABLE DEMO BALANCE", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("₱ 5,000.00", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("Simulated funds • No real value", color = Color.White, fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(onClick = {}, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("+  Demo cash in") }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Send", "Load", "Transfer", "Bills").forEach { ActionTile(it, Modifier.weight(1f)) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Savings", "Cards", "Tap demo", "More").forEach { ActionTile(it, Modifier.weight(1f)) }
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Explore the Demo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Try the sample wallet layout and manage fictional transaction entries.", color = Color.Gray, fontSize = 13.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recent activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = onHistory) { Text("View all") }
            }
            Text("$count sample records • Tap Transactions below to view or delete them", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ActionTile(label: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✦", color = Blue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(label, color = Color(0xFF34445D), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text("Demo", color = Color.Gray, fontSize = 9.sp)
        }
    }
}

@Composable
private fun HistoryScreen(modifier: Modifier, transactions: List<DemoTransaction>, onDelete: (Int) -> Unit) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().background(Blue).padding(22.dp), contentAlignment = Alignment.Center) {
            Text("Transaction History", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { DemoBanner() }
        if (transactions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No demo records remaining") }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                transactions.groupBy { it.group }.forEach { (group, records) ->
                    item {
                        Text(group, modifier = Modifier.fillMaxWidth().background(Color(0xFFEAF1FA)).padding(horizontal = 20.dp, vertical = 14.dp), fontWeight = FontWeight.Bold, color = Color(0xFF263B5A), fontSize = 18.sp)
                    }
                    items(records, key = { it.id }) { tx ->
                        Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 17.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tx.time, color = Color.Gray, fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(tx.title, color = Color(0xFF172B4D), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(tx.amount, color = Color(0xFF172B4D), fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { onDelete(tx.id) }, contentPadding = PaddingValues(0.dp)) { Text("Delete demo", color = Color(0xFFB3261E), fontSize = 11.sp) }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFE6EAF0))
                    }
                }
            }
        }
    }
}

@Composable
private fun QrDemoScreen(modifier: Modifier) {
    Column(modifier.fillMaxSize().background(Color.White).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        DemoBanner()
        Spacer(Modifier.height(28.dp))
        Text("▦", color = Blue, fontSize = 100.sp)
        Text("QR Demo", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("QR scanning and payments are disabled in this prototype.", color = Color.Gray)
    }
}

@Composable
private fun ProfileScreen(modifier: Modifier, onDemoLock: () -> Unit) {
    Column(modifier.fillMaxSize().background(Color.White).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Profile", color = Blue, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        DemoBanner()
        Text("G-Wallet Demo User", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text("This profile is local to the demo. No phone number, MPIN, or biometric data is collected.", color = Color.Gray)
        OutlinedButton(onClick = onDemoLock, modifier = Modifier.fillMaxWidth()) { Text("Open demo welcome screen") }
    }
}

@Composable
private fun DemoLogin(onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFF0B2860)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("G", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Black)
        Text("G-WALLET DEMO", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Independent wallet interface prototype", color = Color.White.copy(alpha = .8f))
        Spacer(Modifier.height(32.dp))
        DemoBanner()
        Spacer(Modifier.height(24.dp))
        Text("Welcome to your demo wallet", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Continue to demo") }
        Text("No real login, PIN, phone number, or financial account is used.", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
    }
}
