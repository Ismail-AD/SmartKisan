package com.appdev.smartkisan.ui.MainAppScreens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.data.New

// News Detail Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    dateTimePair: Pair<String, String>?,
    news: New,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "News Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddings ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(horizontal = 16.dp)
        ) {
            item {
                // Image (handle null)
                news.image?.let { imageUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = news.title ?: "News image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                // Title (handle null)
                Text(
                    text = news.title ?: "Untitled",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Publication info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dateTimePair?.let { dateTime->
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = dateTime.first,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        if (!news.authors.isNullOrEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = news.authors.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        }
                    }
                    dateTimePair?.let { dateTime->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateTime.second,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary section (handle null)
                news.summary?.let { summary ->
                    Text(
                        text = "Summary",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Divider(
                        color = Color.Gray.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.3f), thickness = (1.5).dp, modifier = Modifier
                            .padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Full article content (handle null)
                news.text?.let { fullText ->
                    Text(
                        text = "Full Article",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Divider(
                        color = Color.Gray.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.3f), thickness = (1.5).dp, modifier = Modifier
                            .padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = fullText,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Source link section (handle null)
//                news.url?.let { url ->
//                    OutlinedButton(
//                        onClick = { /* Handle URL click - open in browser */ },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("View Original Article")
//                    }
//                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}