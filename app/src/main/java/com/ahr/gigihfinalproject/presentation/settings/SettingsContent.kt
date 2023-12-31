package com.ahr.gigihfinalproject.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ahr.gigihfinalproject.R
import com.ahr.gigihfinalproject.domain.model.UserTheme
import com.ahr.gigihfinalproject.ui.theme.GigihFinalProjectTheme
import com.ahr.gigihfinalproject.util.emptyString

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    userTheme: UserTheme = UserTheme.Default,
    updateUserTheme: (UserTheme) -> Unit,
    userNotificationBaseWaterSetting: Boolean,
    updateUserNotificationBaseWaterSetting: (Boolean) -> Unit
) {

    val isDarkMode = userTheme == UserTheme.Dark
    val updateDarkMode: (Boolean) -> Unit = { state ->
        val userThemeState = if (state) UserTheme.Dark else UserTheme.Light
        updateUserTheme(userThemeState)
    }

    Column(
        modifier = modifier.padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingItem(
            title = stringResource(R.string.label_dark_mode),
            subtitle = stringResource(R.string.label_actived_dark_mode),
            state = isDarkMode,
            onStateChange = updateDarkMode,
            semantic = stringResource(R.string.desc_checkbox_dark_mode)
        )
        SettingItem(
            title = stringResource(R.string.label_notification),
            subtitle = stringResource(R.string.label_enable_tma_monitoring_notification),
            state = userNotificationBaseWaterSetting,
            onStateChange = updateUserNotificationBaseWaterSetting,
            semantic = stringResource(R.string.desc_checkbox_notification)
        )
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String = emptyString(),
    subtitle: String = emptyString(),
    state: Boolean = false,
    onStateChange: (Boolean) -> Unit = {},
    semantic: String = emptyString()
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = state,
            onCheckedChange = onStateChange,
            modifier = Modifier.semantics { contentDescription = semantic }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.6f
                    )
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewSettingItem() {
    GigihFinalProjectTheme {
        Surface {
            var state by remember { mutableStateOf(false) }
            SettingItem(
                modifier = Modifier.padding(16.dp),
                title = "Dark Mode",
                subtitle = "Enable dark mode",
                state = state,
                onStateChange = { state = it }
            )
        }
    }
}