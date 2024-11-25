package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.appdev.smartkisan.ui.OtherComponents.ExpandedItem
import com.appdev.smartkisan.ui.OtherComponents.SingleCrop
import com.appdev.smartkisan.ui.OtherComponents.SearchField
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.myGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreManagementScreen(controller: NavHostController) {

    var expanded by remember { mutableStateOf(false) }
    val items = listOf("All", "Seeds", "Fertilizers", "Medicine")
    var selectedOption by remember { mutableStateOf("All") }
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
        ),
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
        ),
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
        ),Product(
                5,
        "Herbal Medicine for Plants",
        400,
        350,
        R.drawable.seeds,
        4.7f,
        85,
        description = "This herbal plant medicine is specially formulated to enhance growth and protect your plants from common diseases. Made from natural ingredients, it boosts plant immunity and encourages healthy development without harsh chemicals. Ideal for organic farming and safe for all types of crops and garden plants.",
        10.0f, "g"
    ),Product(
            5,
            "Herbal Medicine for Plants",
            400,
            350,
            R.drawable.seeds,
            4.7f,
            85,
            description = "This herbal plant medicine is specially formulated to enhance growth and protect your plants from common diseases. Made from natural ingredients, it boosts plant immunity and encourages healthy development without harsh chemicals. Ideal for organic farming and safe for all types of crops and garden plants.",
            10.0f, "g"
        ),
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
            onClick = { controller.navigate(Routes.AddProductScreen.route) },
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
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .border(
                            (1.5).dp, if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                                0xFFE4E7EE
                            ), RoundedCornerShape(5.dp)
                        )
                ) {
                    TextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        shape = RoundedCornerShape(5.dp),
                        containerColor = MaterialTheme.colorScheme.background,
                        border = BorderStroke(
                            1.dp, if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                                0xFFE4E7EE
                            )
                        )
                    ) {
                        items.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                onClick = {
                                    selectedOption = item
                                    expanded = false
                                },
                                modifier = Modifier.background(
                                    if (selectedOption == item) if (isSystemInDarkTheme()) Color(
                                        0xFF114646
                                    ) else Color(
                                        0xFFE4E7EE
                                    ) else Color.Transparent
                                )
                            )
                            if (index != items.lastIndex && item != selectedOption) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = Color.Gray.copy(alpha = if (isSystemInDarkTheme()) 0.6f else 0.2f)
                                )
                            }
                        }

                    }
                }


                LazyColumn(
                    modifier = Modifier.padding(top = 15.dp), verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    itemsIndexed(productList) { index , item ->
                        ExpandedItem(item) {
//                            controller.navigate(Routes.ProductDetailScreen.route)
                        }
                        if(index==productList.lastIndex){
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}