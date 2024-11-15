package com.appdev.smartkisan.ui.OtherComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleCrop(product: Product, onClick: () -> Unit) {
    Card(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .height(230.dp),
        shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(
            containerColor = if(isSystemInDarkTheme()) Color(0xFF4E2643) else Color.LightGray.copy(alpha = 0.4f)
        )

    ) {
        Column {
            Box(
                Modifier
                    .padding(start = 7.dp, top = 7.dp, end = 7.dp)
            ) {
                Image(
                    painter = painterResource(id = product.image),
                    contentDescription = "",
                    modifier = Modifier.clip(RoundedCornerShape(6.dp))
                )
            }
            Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp, top = 7.dp)) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if(isSystemInDarkTheme()) Color(0xFF28B42F) else Color(0xFF2E7D32), maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
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
                            color = Color.Gray.copy(alpha = 0.8f),
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = product.ratings.toString(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

        }
    }
}