package com.appdev.smartkisan.ui.MainAppScreens

import android.widget.RatingBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.ui.OtherComponents.CustomRatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetails(controller:NavHostController,
    product: Product = Product(
        5,
        "Herbal Medicine for Plants",
        400,
        350,
        R.drawable.imageseed,
        3.5f,
        85,
        description = "This herbal plant medicine is specially formulated to enhance growth and protect your plants from common diseases. Made from natural ingredients, it boosts plant immunity and encourages healthy development without harsh chemicals. Ideal for organic farming and safe for all types of crops and garden plants.",
        10.0f, "g"
    )
) {
    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            Card(onClick = {
                  controller.navigateUp()
            }, shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(start = 10.dp)) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        })
    }) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddings),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    Modifier
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Image(
                        painter = painterResource(id = product.image),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(370.dp), contentScale = ContentScale.FillBounds
                    )
                }
                Text(
                    text = product.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.discountPrice > 0) {
                        Text(
                            text = "Rs. " + product.discountPrice.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSystemInDarkTheme()) Color(0xFF28B42F) else Color(
                                0xFF2E7D32
                            )
                        )
                        Text(
                            text = "Rs. " + product.price.toString(),
                            fontSize = 18.sp,
                            color = Color.Gray.copy(alpha = 0.8f),
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = "Rs. " + product.price.toString(),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSystemInDarkTheme()) Color(0xFF28B42F) else Color(
                                0xFF2E7D32
                            )
                        )
                    }
                }
                Text(
                    text = "Details",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,modifier = Modifier
                        .padding(top = 20.dp)
                )
                Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 2.dp,modifier = Modifier
                    .padding(top = 4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quantity",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = product.quantity.toString() + "/" + product.unit,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 2.dp,modifier = Modifier
                    .padding(top = 2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ratings",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        CustomRatingBar(rating = product.ratings)
                        Text(
                            text = "(" + product.ratings + ")",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    text = "Description",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,modifier = Modifier
                        .padding(top = 20.dp)
                )
                Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 2.dp,modifier = Modifier
                    .padding(top = 4.dp))
                Text(
                    text = product.description,
                    color = MaterialTheme.colorScheme.onBackground, modifier = Modifier .padding(top = 10.dp)
                )
            }
        }
    }
}

