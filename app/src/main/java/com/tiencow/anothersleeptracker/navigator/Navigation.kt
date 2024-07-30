@file:OptIn(ExperimentalMaterial3Api::class)

package com.tiencow.anothersleeptracker.navigator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tiencow.anothersleeptracker.StopwatchViewModel
import com.tiencow.anothersleeptracker.TimeEntryEvent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: StopwatchViewModel = hiltViewModel()

    val isRunning by viewModel.isRunning.collectAsState(initial = false)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute == Screens.Stopwatch.route || currentRoute == Screens.PreviousLogs.route) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Another Sleep Tracker")
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screens.Settings.route) {
                                launchSingleTop = true
                            }
                        }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            } else if (currentRoute == Screens.About.route) {
                TopAppBar(
                    title = { Text("About") },
                    navigationIcon = {
                        IconButton(onClick = {navController.navigateUp()}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            } else if (currentRoute == Screens.Settings.route) {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = {navController.navigateUp()}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute == Screens.Stopwatch.route || currentRoute == Screens.PreviousLogs.route) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screens.Stopwatch.route,
                        onClick = {
                            navController.navigate(Screens.Stopwatch.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screens.PreviousLogs.route,
                        onClick = {
                            navController.navigate(Screens.PreviousLogs.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Previous Sleeps"
                            )
                        },
                        label = { Text("Previous Sleeps") },
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute != Screens.Settings.route) {
                FloatingActionButton(onClick = {
                    if (isRunning) {
                        viewModel.onEvent(TimeEntryEvent.StopStopwatch)
                    } else {
                        viewModel.onEvent(TimeEntryEvent.StartStopwatch)
                    }
                }) {
                    if (isRunning) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Stopwatch.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(Screens.Stopwatch.route) {
                HomePage(viewModel = viewModel)
            }
            composable(Screens.PreviousLogs.route) {
                PreviousLogsPage(viewModel = viewModel)
            }
            composable(Screens.Settings.route) {
                SettingsPage(navController = navController)
            }
            composable(Screens.About.route) {
                AboutScreen()
            }
        }
    }
}