package com.appdev.smartkisan.presentation.Resuable

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.Product
import com.appdev.smartkisan.presentation.theme.myGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedItem(
    context: Context,
    product: com.appdev.smartkisan.domain.model.Product,
    modifier: Modifier = Modifier,
    onUpdate: (com.appdev.smartkisan.domain.model.Product) -> Unit = {},
    onDelete: (pid: Long) -> Unit = {},
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = modifier
            .height(140.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp) // Make space for the overlapping image
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .background(
                        if (isSystemInDarkTheme()) Color(0xDF0E3636) else Color.LightGray.copy(
                            alpha = 0.4f
                        ), RoundedCornerShape(10.dp)
                    )
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 93.dp)
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color(0xFF28B42F) else Color(0xFF2E7D32),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (product.discountPrice > 0) {
                            Text(
                                text = "Rs. " + product.discountPrice.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Rs. " + product.price.toString(),
                                fontSize = 15.sp,
                                color = Color.Gray.copy(alpha = 0.9f),
                                textDecoration = TextDecoration.LineThrough
                            )
                        } else {
                            Text(
                                text = "Rs. " + product.price.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = product.brandName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Card(
                        onClick = {
                            onUpdate(product)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = myGreen)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_text_new_),
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                    }
                    Card(
                        onClick = {
                            onDelete(product.id)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = myGreen)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                    }
                }
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(110.dp)
                    .offset(x = (-18).dp)
                    .align(Alignment.CenterStart)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrls.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = "Product first Image",
                    placeholder = painterResource(R.drawable.placholder),
                    error = painterResource(R.drawable.placholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}