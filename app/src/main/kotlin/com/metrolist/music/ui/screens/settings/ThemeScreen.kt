package com.metrolist.music.ui.screens.settings

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.metrolist.music.R
import com.metrolist.music.constants.DarkModeKey
import com.metrolist.music.constants.PureBlackKey
import com.metrolist.music.constants.PureBlackMiniPlayerKey
import com.metrolist.music.ui.component.Material3SettingsGroup
import com.metrolist.music.ui.component.Material3SettingsItem
import com.metrolist.music.ui.theme.DefaultThemeColor
import com.metrolist.music.utils.rememberEnumPreference
import com.metrolist.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(navController: NavController) {
    val (darkMode, onDarkModeChange) = rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val (pureBlack, onPureBlackChangeRaw) = rememberPreference(PureBlackKey, defaultValue = false)
    val (_, onPureBlackMiniPlayerChange) = rememberPreference(PureBlackMiniPlayerKey, defaultValue = false)

    val onPureBlackChange: (Boolean) -> Unit = { enabled ->
        onPureBlackChangeRaw(enabled)
        onPureBlackMiniPlayerChange(enabled)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.theme_colors)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            // Theme Mode Controls
            Material3SettingsGroup(
                title = stringResource(R.string.theme_mode),
                items = listOf(
                    Material3SettingsItem(
                        leadingContent = {
                            ModeCircle(
                                darkMode = darkMode,
                                pureBlack = pureBlack,
                                targetMode = DarkMode.AUTO,
                                targetPureBlack = pureBlack,
                                showIcon = true,
                                onClick = null
                            )
                        },
                        title = { Text(stringResource(R.string.theme_mode_adaptive)) },
                        description = { Text(stringResource(R.string.theme_mode_adaptive_desc)) },
                        onClick = { onDarkModeChange(DarkMode.AUTO) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            ModeCircle(
                                darkMode = darkMode,
                                pureBlack = pureBlack,
                                targetMode = DarkMode.OFF,
                                targetPureBlack = false,
                                showIcon = false,
                                onClick = null
                            )
                        },
                        title = { Text(stringResource(R.string.theme_mode_light)) },
                        description = { Text(stringResource(R.string.theme_mode_light_desc)) },
                        onClick = { onDarkModeChange(DarkMode.OFF); onPureBlackChange(false) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            ModeCircle(
                                darkMode = darkMode,
                                pureBlack = pureBlack,
                                targetMode = DarkMode.ON,
                                targetPureBlack = false,
                                showIcon = false,
                                onClick = null
                            )
                        },
                        title = { Text(stringResource(R.string.theme_mode_dark)) },
                        description = { Text(stringResource(R.string.theme_mode_dark_desc)) },
                        onClick = { onDarkModeChange(DarkMode.ON); onPureBlackChange(false) }
                    ),
                    Material3SettingsItem(
                        leadingContent = {
                            ModeCircle(
                                darkMode = darkMode,
                                pureBlack = pureBlack,
                                targetMode = DarkMode.ON,
                                targetPureBlack = true,
                                showIcon = false,
                                onClick = null
                            )
                        },
                        title = { Text(stringResource(R.string.theme_mode_oled)) },
                        description = { Text(stringResource(R.string.theme_mode_oled_desc)) },
                        onClick = { onDarkModeChange(DarkMode.ON); onPureBlackChange(true) }
                    )
                )
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ModeCircle(
    darkMode: DarkMode,
    pureBlack: Boolean,
    targetMode: DarkMode,
    targetPureBlack: Boolean,
    showIcon: Boolean,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isSystemDark = isSystemInDarkTheme()
    val isSelected = darkMode == targetMode && pureBlack == targetPureBlack
    
    val effectiveDark = when (targetMode) {
        DarkMode.AUTO -> isSystemDark
        DarkMode.ON -> true
        DarkMode.OFF -> false
    }
    
    val modeColorScheme = if (targetMode == DarkMode.AUTO && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        rememberDynamicColorScheme(seedColor = DefaultThemeColor, isDark = effectiveDark, style = PaletteStyle.TonalSpot)
    }
    
    val fillColor = if (targetPureBlack) Color.Black else modeColorScheme.surface
    
    val borderWidth by animateDpAsState(targetValue = if (isSelected) 3.dp else 0.dp, label = "borderWidth")
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1f, label = "scale")
    
    val interactionSource = remember { MutableInteractionSource() }
    
    val contentDesc = when {
        targetPureBlack -> stringResource(R.string.cd_pure_black_mode)
        targetMode == DarkMode.OFF -> stringResource(R.string.cd_light_mode)
        targetMode == DarkMode.ON -> stringResource(R.string.cd_dark_mode)
        else -> stringResource(R.string.cd_system_mode)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(fillColor)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, MaterialTheme.colorScheme.inversePrimary, CircleShape) else Modifier)
            .then(if (onClick != null) Modifier.clickable(interactionSource = interactionSource, indication = ripple(), onClick = onClick) else Modifier)
            .semantics { contentDescription = contentDesc },
        contentAlignment = Alignment.Center
    ) {
        when {
            showIcon -> Icon(painterResource(R.drawable.sync), contentDescription = null, tint = modeColorScheme.onSurface, modifier = Modifier.size(20.dp))
            isSelected -> {
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn() + scaleIn(initialScale = 0.3f),
                    exit = fadeOut() + scaleOut(targetScale = 0.3f)
                ) {
                    Icon(painterResource(R.drawable.check), contentDescription = null, tint = MaterialTheme.colorScheme.inversePrimary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
