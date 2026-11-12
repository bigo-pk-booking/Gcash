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
    var showLogin by remember { mutableStateOf(true) }
    var transactions by remember {
        mutableStateOf(listOf(
            DemoTransaction(1, "ATM withdrawal • 7-Eleven", "Transaction entry", "− ₱2,800.00", "Recent")
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
                        NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.QrCode, null) }, label = { Text("QR") })
                        NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.AccountCircle, null) }, label = { Text("Profile") })
                    }
                }
            ) { padding ->
                when (tab) {
                    0 -> HomeScreen(Modifier.padding(padding), transactions.size, onHistory = { tab = 1 })
                    1 -> HistoryScreen(Modifier.padding(padding), transactions, onDelete = { id -> transactions = transactions.filterNot { it.id == id } })
                    2 -> QrDemoScreen(Modifier.padding(padding))
                    else -> ProfileScreen(Modifier.padding(padding), onWalletLock = { showLogin = true })
                }
            }
        }
    }
}

@Composable
private fun PersonalWalletNotice() {
    Surface(color = Color(0xFFFFF2D8), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text("PERSONAL WALLET • Fictional balance and sample records only. Not connected to any bank or payment service.", color = Color(0xFF754B00), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))
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
                Text("Welcome to Gcàsh", color = Color.Gray, fontSize = 11.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("HELP", color = Blue, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            PersonalWalletNotice()
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blue)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("AVAILABLE BALANCE", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("₱ 457.00", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("Personal wallet prototype • Fictional balance", color = Color.White, fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(onClick = {}, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("+  Cash in") }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Send", "Load", "Transfer", "Bills").forEach { ActionTile(it, Modifier.weight(1f)) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Savings", "Cards", "Tap", "More").forEach { ActionTile(it, Modifier.weight(1f)) }
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Explore Gcàsh", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Manage your personal wallet interface and sample entries.", color = Color.Gray, fontSize = 13.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recent activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = onHistory) { Text("View all") }
            }
            Text("$count transaction entry • Tap Transactions below to view or delete it", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ActionTile(label: String, modifier: Modifier) {
    val symbol = when (label) {
        "Send" -> "➤"
        "Load" -> "📱"
        "Transfer" -> "⇄"
        "Bills" -> "▤"
        "Savings" -> "▣"
        "Cards" -> "▭"
        "Tap" -> "⌁"
        else -> "•••"
    }
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, color = Blue, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            Text(label, color = Color(0xFF34445D), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HistoryScreen(modifier: Modifier, transactions: List<DemoTransaction>, onDelete: (Int) -> Unit) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().background(Blue).padding(22.dp), contentAlignment = Alignment.Center) {
            Text("Transaction History", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { PersonalWalletNotice() }
        if (transactions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No transaction records") }
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
                                TextButton(onClick = { onDelete(tx.id) }, contentPadding = PaddingValues(0.dp)) { Text("Delete", color = Color(0xFFB3261E), fontSize = 11.sp) }
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
        PersonalWalletNotice()
        Spacer(Modifier.height(28.dp))
        Text("▦", color = Blue, fontSize = 100.sp)
        Text("QR", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("QR scanning and payments are disabled in this prototype.", color = Color.Gray)
    }
}

@Composable
private fun ProfileScreen(modifier: Modifier, onWalletLock: () -> Unit) {
    Column(modifier.fillMaxSize().background(Color.White).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Profile", color = Blue, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        PersonalWalletNotice()
        Text("Gcàsh User", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text("This profile is stored locally. No phone number, MPIN, or biometric data is collected.", color = Color.Gray)
        OutlinedButton(onClick = onWalletLock, modifier = Modifier.fillMaxWidth()) { Text("Lock wallet") }
    }
}

@Composable
private fun DemoLogin(onContinue: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().background(Color(0xFF0B2860)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Gcàsh", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Enter your 4-digit PIN", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            repeat(4) { index ->
                Box(
                    Modifier.size(15.dp).background(
                        if (index < pin.length) Color.White else Color.White.copy(alpha = .3f),
                        RoundedCornerShape(50)
                    )
                )
            }
        }
        if (error) {
            Spacer(Modifier.height(12.dp))
            Text("Incorrect PIN. Please try again.", color = Color(0xFFFFD1D1), fontSize = 13.sp)
        }
        Spacer(Modifier.height(26.dp))
        val keys = listOf("1","2","3","4","5","6","7","8","9","", "0", "⌫")
        keys.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                row.forEach { key ->
                    Box(Modifier.weight(1f).padding(5.dp), contentAlignment = Alignment.Center) {
                        if (key.isNotEmpty()) {
                            FilledTonalButton(
                                onClick = {
                                    error = false
                                    if (key == "⌫") {
                                        if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                    } else if (pin.length < 4) {
                                        pin += key
                                        if (pin.length == 4) {
                                            if (pin == "1995") onContinue()
                                            else { pin = ""; error = true }
                                        }
                                    }
                                },
                                modifier = Modifier.size(72.dp),
                                shape = RoundedCornerShape(36.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Blue
                                )
                            ) { Text(key, fontSize = 24.sp, fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Personal wallet prototype • Sample information only", color = Color.White.copy(alpha = .8f), fontSize = 11.sp)
    }
}
