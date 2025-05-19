package com.appdev.smartkisan.presentation.feature.onBoarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.OnBoardingItems
import com.appdev.smartkisan.presentation.Resuable.AnimationComposable
import com.appdev.smartkisan.presentation.Resuable.CustomButton
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BoardingTemplate(moveToNext: () -> Unit) {
    val items = OnBoardingItems.getData()
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Scaffold(bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            CustomButton(
                onClick = {
                    if (pagerState.currentPage + 1 < items.size) scope.launch {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    } else {
                        moveToNext()
                    }
                },
                text = if (pagerState.currentPage + 1 == items.size) "Get Started" else "Next",
                width = 0.88f
            )
        }
    }) { padding ->

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Fixed height for the skip button area
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                // Only show the skip button if not on the last page
                if (pagerState.currentPage + 1 != items.size) {
                    Card(onClick = {
                        moveToNext()
                    }, colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                        Text(
                            text = "Skip",
                            color = Color(0xff2E7D32),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }


            HorizontalPager(
                count = items.size,
                state = pagerState,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) { page ->
                BoardingScreen(items = items[page])
            }
            BottomSection(size = items.size, index = pagerState.currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 25.dp else 10.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "width"
    )

    Box(
        modifier = Modifier
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                color = if (isSelected) Color(0xff2E7D32) else Color.LightGray
            )
    )
}

@Composable
fun BottomSection(size: Int, index: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Indicators
            Indicators(size, index)
        }

    }
}

@Composable
fun BoxScope.Indicators(size: Int, index: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.align(Alignment.Center)
    ) {
        repeat(size) {
            Indicator(isSelected = it == index)
        }
    }
}

@Composable
fun BoardingScreen(items: OnBoardingItems) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Animation with background circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
                .background(Color(0xFF2BEC08))
        ) {
            AnimationComposable(
                resourceId = items.animationId,
                size = if (items.animationId != R.raw.newplantanimation) 230.dp else 190.dp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(id = items.title),
            fontSize = 28.sp,  // Increased font size
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            lineHeight = 35.sp
        )
        Spacer(modifier = Modifier.height(8.dp))


        Text(
            text = stringResource(id = items.desc),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp),
            letterSpacing = 1.sp,
        )
    }
}