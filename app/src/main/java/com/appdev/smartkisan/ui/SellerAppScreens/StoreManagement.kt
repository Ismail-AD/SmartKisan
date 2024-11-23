package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.Category
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.ui.OtherComponents.SingleCrop
import com.appdev.smartkisan.ui.SignUpProcess.SearchField
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.myGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreManagementScreen(controller: NavHostController) {
    var categories =
        listOf(
            Category("All", R.drawable.all_items),
            Category("Seeds", R.drawable.seed),
            Category("Fertilizers", R.drawable.fertlizers),
            Category("Medicine", R.drawable.herbal)
        )

    val productList = listOf(
        Product(
            5,
            "Herbal Medicine for Plants",
            400,
            350,
            R.drawable.seeds,
            4.7f,
            85,
            description = "This herbal plant medicine is specially formulated to enhance growth and protect your plants from common diseases. Made from natural ingredients, it boosts plant immunity and encourages healthy development without harsh chemicals. Ideal for organic farming and safe for all types of crops and garden plants.",
            10.0f, "g"
        )
    )
    var selected by remember {
        mutableIntStateOf(0)
    }

    var query by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Saad's Inventory",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onBackground
            )
        }, navigationIcon = {
            IconButton(onClick = {

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(23.dp)
                )
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { /* Handle add action */ },
            containerColor = myGreen,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add, // Replace with your add icon resource
                contentDescription = "Add", tint = Color.White
            )
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp)
            ) {
                SearchField(query = query,
                    placeholder = "Search Products",
                    modifier = Modifier
                        .fillMaxWidth(),
                    onFocusChange = { },
                    onTextChange = { text -> query = text }) {
                    focusManager.clearFocus()
                    query = ""
                }
                LazyRow(modifier = Modifier.padding(top = 10.dp)) {
                    itemsIndexed(categories) { index, category ->
                        FilterChip(
                            selected = selected == index,
                            onClick = { selected = index },
                            label = {
                                Text(
                                    text = category.name,
                                    modifier = Modifier.padding(
                                        end = 5.dp,
                                        top = 12.dp,
                                        bottom = 12.dp
                                    ),
                                    color = if (selected == index) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            },
                            leadingIcon = {
                                Row {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Image(
                                        painter = painterResource(id = category.image),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(end = 10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.LightGray.copy(alpha = 0.4f),
                                selectedContainerColor = Color(0xff238b45),
                            ),
                            border = null
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(top = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(productList) {
                        SingleCrop(it) {
                            controller.navigate(Routes.ProductDetailScreen.route)
                        }
                    }
                }
            }
        }
    }
}