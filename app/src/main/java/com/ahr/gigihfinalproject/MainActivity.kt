package com.ahr.gigihfinalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahr.gigihfinalproject.domain.model.UserTheme
import com.ahr.gigihfinalproject.presentation.NavGraphs
import com.ahr.gigihfinalproject.presentation.settings.SettingsViewModel
import com.ahr.gigihfinalproject.ui.theme.GigihFinalProjectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val userTheme by settingsViewModel.userTheme.collectAsState()

            LaunchedEffect(key1 = Unit) {
                settingsViewModel.getUserTheme()
            }

            val isDarkTheme = userTheme == UserTheme.Dark

            GigihFinalProjectTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}
