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

        val prefs = getSharedPreferences("gwallet_diagnostics", MODE_PRIVATE)
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val report = buildString {
                append("Thread: ").append(thread.name).append("\\n")
                append(throwable.stackTraceToString())
            }
            prefs.edit().putString("last_crash", report).commit()
            previousHandler?.uncaughtException(thread, throwable)
        }

        val savedCrash = prefs.getString("last_crash", null)
        setContent {
            if (savedCrash != null) {
                CrashDiagnosticScreen(
                    report = savedCrash,
                    onClear = {
                        prefs.edit().remove("last_crash").apply()
                        recreate()
                    }
                )
            } else {
                GWalletApp()
            }
        }
    }
}

@Composable
private fun CrashDiagnosticScreen(report: String, onClear: () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue)) {
        Column(
            Modifier.fillMaxSize().background(Pale).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("G-Wallet • Crash Diagnostic", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("A previous app crash was detected. Copy or screenshot this report so the issue can be diagnosed.", color = Color.DarkGray)
            Surface(Modifier.weight(1f).fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(12.dp)) {
                Text(report, Modifier.padding(12.dp), fontSize = 11.sp, color = Color(0xFF202938))
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) { Text("Clear report and retry") }
        }
    }
}

@Composable
private fun GWalletApp() {
    var tab by remember { mutableStateOf(0) }
    var showLogin by remember { mutableStateOf(true) }
    var showOffline by remember { mutableStateOf(false) }
    var transactions by remember {
        mutableStateOf(listOf(
            DemoTransaction(20, "Mobile Legend Shop", "Today", "− ₱1,000.00", "Today"),
            DemoTransaction(21, "Mobile Legend Shop", "Today", "− ₱1,000.00", "Today"),
            DemoTransaction(22, "Mobile Legend Shop", "Today", "− ₱800.00", "Today"),
            DemoTransaction(2, "Send Money", "10:25 PM", "− ₱750.00", "Yesterday"),
            DemoTransaction(3, "ATM Withdrawal", "7:15 AM", "− ₱3,218.00", "Nov 11, 2026"),
            DemoTransaction(4, "Send Money", "6:31 PM", "− ₱300.00", "Nov 10, 2026"),
            DemoTransaction(5, "Send Money", "5:36 PM", "+ ₱500.00", "Nov 10, 2026"),
            DemoTransaction(6, "Cash in from PayMongo", "4:57 PM", "+ ₱4,353.94", "Nov 10, 2026"),
            DemoTransaction(7, "Send Money", "8:53 AM", "− ₱150.00", "Nov 10, 2026"),
            DemoTransaction(8, "Cash in from PayMongo", "4:00 PM", "+ ₱2,710.41", "Nov 6, 2026"),
            DemoTransaction(9, "Online Payment - Web Pay", "2:50 PM", "− ₱1,000.00", "Nov 6, 2026"),
            DemoTransaction(10, "Cash in from Netbank", "2:49 PM", "+ ₱1,000.00", "Nov 6, 2026"),
            DemoTransaction(11, "Online Payment - Web Pay", "1:32 PM", "− ₱2,000.00", "Nov 6, 2026"),
            DemoTransaction(12, "Online Payment - Web Pay", "12:57 PM", "− ₱1,037.43", "Nov 6, 2026"),
            DemoTransaction(13, "Pay via Scanned QR", "12:36 PM", "− ₱2,767.52", "Nov 6, 2026"),
            DemoTransaction(14, "Send Money", "12:35 PM", "+ ₱5,100.00", "Nov 6, 2026")
        ))
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Pale)) {
        if (showLogin) {
            DemoLogin(onContinue = { showLogin = false })
        } else {
            if (showOffline) {
                AlertDialog(
                    onDismissRequest = { showOffline = false },
                    title = { Text("No internet connection") },
                    text = { Text("Please check your internet connection and try again.") },
                    confirmButton = { TextButton(onClick = { showOffline = false }) { Text("OK") } }
                )
            }
            Scaffold(
                containerColor = Pale,
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                        NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("Transactions") })
                        NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.QrCode, null) }, label = { Text("Camera") })
                        NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.AccountCircle, null) }, label = { Text("Profile") })
                    }
                }
            ) { padding ->
                when (tab) {
                    0 -> HomeScreen(Modifier.padding(padding), transactions.size, onHistory = { tab = 1 }, onOffline = { showOffline = true })
                    1 -> HistoryScreen(Modifier.padding(padding), transactions)
                    2 -> QrDemoScreen(Modifier.padding(padding), onOffline = { showOffline = true })
                    else -> ProfileScreen(Modifier.padding(padding), onWalletLock = { showLogin = true }, onOffline = { showOffline = true })
                }
            }
        }
    }
}

@Composable
private fun WalletMark(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = Color(0xFF183B65),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * .18f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(w * .18f, h * .25f),
            size = androidx.compose.ui.geometry.Size(w * .64f, h * .5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * .08f)
        )
        drawRoundRect(
            color = Color(0xFF35A6A0),
            topLeft = androidx.compose.ui.geometry.Offset(w * .55f, h * .39f),
            size = androidx.compose.ui.geometry.Size(w * .32f, h * .22f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * .06f)
        )
        drawCircle(
            color = Color.White,
            radius = w * .035f,
            center = androidx.compose.ui.geometry.Offset(w * .68f, h * .5f)
        )
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, count: Int, onHistory: () -> Unit, onOffline: () -> Unit) {
    Column(modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            WalletMark(Modifier.size(42.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Hello!", color = Blue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Welcome to G-Wallet", color = Color.Gray, fontSize = 11.sp)
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onOffline) { Text("HELP", color = Blue, fontWeight = FontWeight.Bold) }
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blue)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("SAMPLE BALANCE", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("₱ 457.00", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("DEMO BALANCE • Not real money", color = Color.White, fontSize = 11.sp)
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(onClick = onOffline, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("+  Cash in") }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Send", "Load", "Transfer", "Bills").forEach { ActionTile(it, Modifier.weight(1f), onOffline) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Savings", "Cards", "Tap", "More").forEach { ActionTile(it, Modifier.weight(1f), onOffline) }
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Explore your demo wallet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Manage your personal wallet interface and sample entries.", color = Color.Gray, fontSize = 13.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recent activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = onHistory) { Text("View all") }
            }
            Text("$count transaction entries", color = Color.Gray, fontSize = 12.sp)
            Text("DEMO ONLY • Sample data • No real payments", color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ActionTile(label: String, modifier: Modifier, onOffline: () -> Unit) {
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
    Card(onClick = onOffline, modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, color = Blue, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            Text(label, color = Color(0xFF34445D), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HistoryScreen(modifier: Modifier, transactions: List<DemoTransaction>) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().background(Blue).padding(22.dp), contentAlignment = Alignment.Center) {
            Text("Demo Transaction History", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text("Sample date • Nov 13, 2026", color = Color(0xFF203B63), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("DEMO ONLY • Fictional sample history", color = Color.Gray, fontSize = 9.sp)
        }
        Text("Today", modifier = Modifier.fillMaxWidth().background(Color(0xFFF4F7FC)).padding(horizontal = 20.dp, vertical = 10.dp), fontWeight = FontWeight.SemiBold, color = Color(0xFF263B5A), fontSize = 14.sp)
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
                            Text(tx.amount, color = Color(0xFF172B4D), fontWeight = FontWeight.SemiBold)
                        }
                        HorizontalDivider(color = Color(0xFFE6EAF0))
                    }
                }
            }
        }
    }
}

@Composable
private fun QrDemoScreen(modifier: Modifier, onOffline: () -> Unit) {
    Column(modifier.fillMaxSize().background(Color.White).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.QrCode, contentDescription = null, tint = Blue, modifier = Modifier.size(88.dp))
        Text("Camera", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Open camera to scan a code", color = Color.Gray)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onOffline) { Text("Open Camera") }
        Text("DEMO ONLY • Sample data • No real payments", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 24.dp))
    }
}

@Composable
private fun ProfileScreen(modifier: Modifier, onWalletLock: () -> Unit, onOffline: () -> Unit) {
    Column(modifier.fillMaxSize().background(Color.White).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Profile", color = Blue, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("G-Wallet User", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text("Demo profile only. No payments or real financial records are created.", color = Color.Gray)
        OutlinedButton(onClick = onWalletLock, modifier = Modifier.fillMaxWidth()) { Text("Lock wallet") }
        OutlinedButton(onClick = onOffline, modifier = Modifier.fillMaxWidth()) { Text("Check connection") }
        Text("DEMO ONLY • Sample data • No real payments", color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
private fun DemoLogin(onContinue: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("+639-9678091036") }
    var showNotice by remember { mutableStateOf(false) }

    if (showNotice) {
        AlertDialog(
            onDismissRequest = { showNotice = false },
            title = { Text("Wallet notice") },
            text = {
                Text("This is an independent wallet interface. This message is a sample and does not indicate a real account status.")
            },
            confirmButton = {
                TextButton(onClick = { showNotice = false }) { Text("OK") }
            }
        )
    }

    Box(
        Modifier.fillMaxSize().background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(Color(0xFF8E123A), Color(0xFF650D32), Color(0xFF350D2A))
            )
        )
    ) {
        Column(
            Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 24.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WalletMark(Modifier.size(66.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Merry", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                    Text("G-Wallet", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(42.dp))
            Surface(
                color = Color(0xFF176FE5),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 8.dp
            ) {
                TextField(
                    value = phoneNumber,
                    onValueChange = { value -> phoneNumber = value.take(16) },
                    singleLine = true,
                    modifier = Modifier.width(300.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White
                    ),
                    placeholder = { Text("+639-XXXXXXXXX", color = Color(0xFFDCEBFF)) }
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 12.dp
        ) {
            Column(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Enter your PIN", color = Color(0xFF173B78), fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                    repeat(4) { index ->
                        Box(
                            Modifier.size(13.dp).background(
                                if (index < pin.length) Blue else Color(0xFFB7C8E0),
                                RoundedCornerShape(50)
                            )
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
                keys.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        row.forEach { key ->
                            Box(Modifier.weight(1f).padding(vertical = 2.dp), contentAlignment = Alignment.Center) {
                                if (key.isNotEmpty()) {
                                    TextButton(
                                        onClick = {
                                            if (key == "⌫") {
                                                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                            } else if (pin.length < 4) {
                                                pin += key
                                                if (pin.length == 4) {
                                                    pin = ""
                                                    showNotice = true
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(54.dp)
                                    ) {
                                        Text(key, color = Color(0xFF173B78), fontSize = 25.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = { showNotice = true }) { Text("Help Center", color = Blue) }
                    TextButton(onClick = { showNotice = true }) { Text("Forgot PIN?", color = Blue) }
                }
                Text(
                    "Independent wallet interface • Sample data only",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}
