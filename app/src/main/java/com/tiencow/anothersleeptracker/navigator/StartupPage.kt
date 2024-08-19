/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.tiencow.anothersleeptracker.navigator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tiencow.anothersleeptracker.data_store.DataStoreHelper

import androidx.compose.runtime.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun PermissionWrapper(dataStoreHelper: DataStoreHelper) {
    var hasSeenStartupPage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        hasSeenStartupPage = dataStoreHelper.getHasSeenStartupPage()
        isLoading = false
    }

    when {
        isLoading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "My bad code :(",
                    textAlign = TextAlign.Center,
                )
            }
        }
        hasSeenStartupPage -> {
            Navigation()
        }
        else -> {
            StartupPage(
                onContinue = {
                    hasSeenStartupPage = true
                    MainScope().launch {
                        dataStoreHelper.setHasSeenStartupPage(true)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartupPage(
    onContinue: () -> Unit
) {
    val permissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Hello",
            style = TextStyle(
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
            ) {
                Text(
                    "- If you would like to enable\nreminder notifications: ",
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = { permissionState.launchPermissionRequest() },
            ) {
                Text(
                    "Request",
                    textAlign = TextAlign.Center,
                )
            }
        }

        Text(
            "- To delete entries on the Previous Sleeps screen, swipe left",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp)
        )
        Spacer(modifier = Modifier.height(17.dp))
        Text(
            "- You may need to disable battery optimizations for background notifications",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp)
        )
        Spacer(modifier = Modifier.height(17.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.padding(),
        ) {
            Text(
                "Continue",
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun RationaleDialog(onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Permission Required") },
        text = { Text("The app needs this permission to function properly. Please grant the permission.") },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("OK")
            }
        }
    )
}