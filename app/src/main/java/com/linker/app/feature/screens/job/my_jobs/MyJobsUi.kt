package com.linker.app.feature.screens.job.my_jobs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linker.app.core.data.models.HomeJobDto
import com.linker.app.core.util.enums.JobStatusEnum
import com.linker.app.core.util.enums.JobTypeEnum
import com.linker.app.ui.theme.LocalJobFinderTheme
import java.math.BigDecimal

import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.linker.app.ui.components.AppendError
import com.linker.app.ui.components.AppendLoading
import com.linker.app.ui.components.EmptyListState
import com.linker.app.ui.components.FullScreenError
import com.linker.app.ui.components.FullScreenLoading
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJobsUi(
    state: MyJobsUiState,
    onEvent: (MyJobsEvent) -> Unit
) {
    val jobs = state.jobsPagingDataFlow.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Jobs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(MyJobsEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = jobs.loadState.refresh is LoadState.Loading && jobs.itemCount > 0,
            onRefresh = { jobs.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- PAGING STATE HANDLING LOGIC ---
            val refreshState = jobs.loadState.refresh
            val appendState = jobs.loadState.append
            
            // 1. Initial Error State
            // Occurs when the first fetch fails and there's no cached data from Room.
            if (refreshState is LoadState.Error && jobs.itemCount == 0) {
                FullScreenError(
                    message = refreshState.error.localizedMessage ?: "Failed to load jobs",
                    onRetry = { jobs.retry() }
                )
            } 
            // 2. Initial Loading State
            // Center loader for initial cold start when database is empty.
            else if (refreshState is LoadState.Loading && jobs.itemCount == 0) {
                FullScreenLoading(modifier = Modifier.align(Alignment.Center))
            }
            // 3. Empty Success State
            // Occurs when the data successfully fetched, but returned 0 results.
            else if (jobs.itemCount == 0 && refreshState !is LoadState.Loading) {
                EmptyListState(
                    title = "You haven't posted any jobs yet.",
                    description = "Post a job to see it listed here.",
                    icon = Icons.Default.WorkHistory,
                    modifier = Modifier.align(Alignment.Center)
                )
            } 
            // 4. Data Population & Append States
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        count = jobs.itemCount,
                        key = { jobs[it]?.id ?: it }
                    ) { index ->
                        val job = jobs[index]
                        if (job != null) {
                            MyJobCardItem(job = job, onClick = { onEvent(MyJobsEvent.OnJobClicked(job)) })
                        }
                    }
                    
                    // 5. Append Loading State
                    // Occurs when the user scrolls to the bottom and we request the next page.
                    if (appendState is LoadState.Loading) {
                        item { AppendLoading() }
                    } 
                    // 6. Append Error State
                    // Occurs when the pagination request fails. Provides an inline retry button.
                    else if (appendState is LoadState.Error) {
                        item {
                            AppendError(
                                message = appendState.error.localizedMessage ?: "Failed to load more",
                                onRetry = { jobs.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyJobCardItem(
    job: HomeJobDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = job.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = job.status.name,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (job.status == JobStatusEnum.OPEN) Color(0xFF388E3C) else Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.05f))
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.jobType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Text(
                    text = "₹${job.payAmount}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyJobsPreview() {
    LocalJobFinderTheme {
        MyJobsUi(
            state = MyJobsUiState(),
            onEvent = {}
        )
    }
}
