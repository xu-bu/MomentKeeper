package com.example.momentkeeper.ui.sidebar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.momentkeeper.model.Journal

@Composable
fun InfoSidebar(
    title: String,
    onTitleChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    eventDescription: String,
    onEventChange: (String) -> Unit,
    savedJournals: List<Journal>,
    onSelectJournal: (Journal) -> Unit,
    onDismissFocus: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Journal Info",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .clickable { onDismissFocus() }
                .padding(bottom = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 当前手帐信息（可编辑）
        Text(
            text = "标题",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            placeholder = { Text("我的手帐") }
        )

        Text(
            text = "时间",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = date,
            onValueChange = onDateChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            placeholder = { Text("yyyy-MM-dd") }
        )

        Text(
            text = "事件",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = eventDescription,
            onValueChange = onEventChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .padding(bottom = 16.dp),
            maxLines = 4,
            placeholder = { Text("记录这一刻...") }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 已保存手帐列表
        Text(
            text = "已保存的手帐",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .clickable { onDismissFocus() }
                .padding(bottom = 8.dp)
        )
        if (savedJournals.isEmpty()) {
            Text(
                text = "暂无保存的手帐，点击右上角保存当前手帐。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable { onDismissFocus() }
                    .padding(vertical = 8.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                items(savedJournals) { journal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectJournal(journal) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = journal.title.ifEmpty { "未命名手帐" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = journal.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
