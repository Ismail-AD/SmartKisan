package com.appdev.smartkisan.ui.MainAppScreens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.NewsState
import com.appdev.smartkisan.ViewModel.NewsViewModel
import com.appdev.smartkisan.data.New
import com.appdev.smartkisan.ui.OtherComponents.NoDialogLoader
import com.appdev.smartkisan.ui.navigation.Routes
import kotlinx.serialization.json.Json

// Main root composable that hosts the news screen
@Composable
fun AgricultureNewsRoot(
    controller: NavHostController,
    newsViewModel: NewsViewModel = hiltViewModel()
) {
    val newsState by newsViewModel.newsState.collectAsStateWithLifecycle()



    AgricultureNewsScreen(
        uiState = newsState,
        onNewsClick = { news ->
            Log.d("AZQW", "${news}")
            val newsJson = Uri.encode(Json.encodeToString(news))
            controller.navigate(Routes.NewsDetails.route + "/$newsJson")
        }, onRefresh = {
            newsViewModel.refreshNews()
        },
        onBackPress = {
            controller.popBackStack()
        }
    )
}

// News screen showing list of news articles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgricultureNewsScreen(
    uiState: NewsState,
    onNewsClick: (New) -> Unit,
    onRefresh: () -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Agriculture News",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }, actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddings),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }

                uiState.statusMessage.isNotEmpty() && uiState.articles.isEmpty() -> {
                    EmptyStateMessage(message = uiState.statusMessage)
                }

                uiState.articles.isNotEmpty() -> {
                    NewsContent(
                        news = uiState.articles,
                        onNewsClick = onNewsClick
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    NoDialogLoader("Loading latest agriculture news..")
}

@Composable
fun EmptyStateMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NewsContent(
    news: List<New>,
    onNewsClick: (New) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(news) { article ->
            NewsCard(article = article, onNewsClick = onNewsClick)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewsCard(
    article: New,
    onNewsClick: (New) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNewsClick(article) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // or any other color
        ), border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // News Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.image)
                    .crossfade(true)
                    .build(),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.newsplaceholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = article.title ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Summary preview
                Text(
                    text = article.summary
                        ?: "Summary for this article is currently not available.",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date and author info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Publication date
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = article.publish_date?.split(" ")?.firstOrNull() ?: "", // Get only the date part
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Authors (if available)
                    if (!article.authors.isNullOrEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = article.authors?.firstOrNull()?.takeIf { it.isNotBlank() }
                                    ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}