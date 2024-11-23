package com.appdev.smartkisan.ui.OtherComponents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchField(
    query: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    TextField(
        value = query,
        onValueChange = { input ->
            onTextChange(input)
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = if (isSystemInDarkTheme()) Color(0xDF0E3636) else Color(
                0xFFE4E7EE
            ),
            unfocusedContainerColor = if (isSystemInDarkTheme()) Color(0xDF0E3636) else Color(
                0xFFE4E7EE
            )
        ),
        placeholder = {
            Text(
                text = placeholder
            )
        },
        modifier = modifier.onFocusChanged { focusState ->
            onFocusChange(focusState.isFocused)
            isFocused = focusState.isFocused
        },
        singleLine = true, leadingIcon = {
            if (isFocused) {
                IconButton(onClick = {
                    onBackClick()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = ""
                )
            }
        }, shape = RoundedCornerShape(10.dp)
    )
}