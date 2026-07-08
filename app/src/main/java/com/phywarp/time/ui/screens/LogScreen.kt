package com.phywarp.time.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phywarp.time.data.local.TimerRecord
import com.phywarp.time.ui.TimerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogScreen(viewModel: TimerViewModel) {
    val records by viewModel.records.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("计时日志", style = MaterialTheme.typography.titleLarge)
            
            Row {
                val context = androidx.compose.ui.platform.LocalContext.current
                TextButton(onClick = { 
                    exportLogsToTxt(context, records)
                }) {
                    Text("导出 TXT")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.setSearchQuery(it)
            },
            placeholder = { Text("搜索任务标题...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        val groupedRecords = records.groupBy { it.dateString }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            groupedRecords.forEach { (date, recordsInDate) ->
                item {
                    DateHeader(date) {
                        viewModel.deleteRecordsByDate(date)
                    }
                }
                items(recordsInDate) { record ->
                    RecordItem(record) {
                        viewModel.deleteRecord(record.id)
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String, onClearAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(date, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        TextButton(onClick = onClearAll) {
            Text("清空当日", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RecordItem(record: TimerRecord, onDelete: () -> Unit) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startStr = timeFormatter.format(Date(record.startTime))
    val endStr = timeFormatter.format(Date(record.endTime))

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.title, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                Text(
                    "${record.type} | $startStr - $endStr",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                formatDuration(record.duration),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDelete) {
                // Icon for delete
                Text("×", fontSize = 24.sp, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "${h}h ${m}m ${s}s" else "${m}m ${s}s"
}

private fun exportLogsToTxt(context: Context, records: List<TimerRecord>) {
    val fileName = "timer_logs_${System.currentTimeMillis()}.txt"
    val content = StringBuilder()
    
    records.forEach { record ->
        val startStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(record.startTime))
        val endStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(record.endTime))
        content.append("标题: ${record.title}\n")
        content.append("类型: ${record.type}\n")
        content.append("开始: $startStr\n")
        content.append("结束: $endStr\n")
        content.append("耗时: ${formatDuration(record.duration)}\n")
        content.append("--------------------\n")
    }

    try {
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { it.write(content.toString().toByteArray()) }
        Toast.makeText(context, "日志已导出至: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "导出失败: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
