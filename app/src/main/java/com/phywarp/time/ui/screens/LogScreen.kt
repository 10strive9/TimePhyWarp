package com.phywarp.time.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phywarp.time.data.local.AppDatabase
import com.phywarp.time.data.local.TimerRecord
import com.phywarp.time.viewmodel.TimerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var records by remember { mutableStateOf<List<TimerRecord>>(emptyList()) }

    // 加载记录
    LaunchedEffect(Unit) {
        val dao = AppDatabase.getDatabase(context).timerDao()
        dao.getAllRecords().collect { records = it }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题
        Text(
            text = "计时日志",
            fontSize = 32.sp,
            color = Color(0xFF00BFFF),
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (records.isEmpty()) {
            // 空状态
            Text(
                text = "还没有任何记录，快去开始计时吧！",
                color = Color(0xFF888888),
                fontSize = 18.sp
            )
        } else {
            // 按日期分组
            val groupedRecords = records.groupBy { it.dateString }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                groupedRecords.forEach { (date, recordsInDate) ->
                    item {
                        // 日期标题
                        Text(
                            text = date,
                            fontSize = 20.sp,
                            color = Color(0xFF00BFFF),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(recordsInDate) { record ->
                        RecordItem(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun RecordItem(
    record: TimerRecord,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x2200BFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = record.title,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = record.type,
                    color = Color(0xFF00BFFF)
                )
                Text(
                    text = "时长: ${formatDuration(record.duration)}",
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

// 格式化显示时长
private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
