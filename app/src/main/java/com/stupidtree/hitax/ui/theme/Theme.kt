package com.stupidtree.hitax.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


private val DarkColors = darkColors(
    primary = Color(0x304ffe),
    primaryVariant = Color(0x3C8CE7),
    secondary = Color(0x3C8CE7),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF808080)
)

private val LightColors = lightColors(
    primary = Color(0x304ffe),
    primaryVariant = Color(0x3C8CE7),
    secondary = Color(0x3C8CE7),
    onPrimary = Color(0xFFffff),
    onSecondary = Color(0xFF4f4f4f)
)


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

@Composable
fun AppBar(title: Int) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        },
        title = {
            Text(text = stringResource(title))
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )
}
