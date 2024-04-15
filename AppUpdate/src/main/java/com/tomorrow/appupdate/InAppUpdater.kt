package com.tomorrow.appupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlin.system.exitProcess

data class StoreInfo(
    val name: String,
    val updateUrl: String?,
)

enum class UpdateType {
    Forced, Flexible, None
}

private fun AppUpdateManager.startUpdate(
    intentLauncher: ActivityResultLauncher<IntentSenderRequest>,
    updateInfo: AppUpdateInfo,
    updateType: Int
) {
    startUpdateFlowForResult(
        updateInfo, updateType, intentLauncher.starter(), 0
    )
}


private fun ActivityResultLauncher<IntentSenderRequest>.starter(): IntentSenderForResultStarter =
    IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValue, _, _ ->
        launch(
            IntentSenderRequest.Builder(intent).setFillInIntent(fillInIntent)
                .setFlags(flagsValue, flagsMask).build()
        )
    }

@Composable
fun InAppUpdater(
    storeInfo: StoreInfo? = null,
    updateType: UpdateType,
    title: String = "${storeInfo?.name ?: "App"} needs an update!",
    description: String = "A new update is available, please download the latest version!",
    ctaButtonText: String = "Update",
    dismissButtonText: String = "No Thanks",
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    titleTextStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
    descriptionTextStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
    ctaButtonTextStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.error),
    dismissButtonTextStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
    cornerRadius: Dp = 8.dp
) {
    val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(LocalContext.current)
    val intentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { if (it.resultCode == Activity.RESULT_CANCELED) exitProcess(0) })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isDialogVisible = rememberSaveable { mutableStateOf(false) }
    val isUpdateForced = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = updateType) {
        fun toggleDialog(updateType: UpdateType) {
            isDialogVisible.value = updateType != UpdateType.None
            isUpdateForced.value = updateType === UpdateType.Forced
        }

        if (updateType == UpdateType.None) return@LaunchedEffect

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdate(intentLauncher, it, updateType.toAppUpdateType())
            } else {
                toggleDialog(updateType)
            }
        }.addOnFailureListener { toggleDialog(updateType) }

    }

    if (isDialogVisible.value) {
        AlertDialog(
            onDismissRequest = {
                if (!isUpdateForced.value) {
                    isDialogVisible.value = false
                }
            },
            containerColor = backgroundColor,
            title = {
                Text(
                    text = title,
                    style = titleTextStyle
                )
            },
            text = {
                Text(
                    text = description,
                    style = descriptionTextStyle
                )
                Spacer(Modifier.height(24.dp))
            },
            confirmButton = {
                Text(
                    text = ctaButtonText,
                    modifier = Modifier
                        .clip(ButtonDefaults.textShape)
                        .clickable(onClick = { context.openUpdatePageInPlayStore(storeInfo?.updateUrl?.toUri()) })
                        .padding(8.dp),
                    style = ctaButtonTextStyle
                )
            },
            dismissButton = {
                if (!isUpdateForced.value) {
                    Text(
                        text = dismissButtonText,
                        modifier = Modifier
                            .clip(ButtonDefaults.textShape)
                            .clickable(onClick = { isDialogVisible.value = false })
                            .padding(8.dp),
                        style = dismissButtonTextStyle
                    )
                }
            },
            shape = RoundedCornerShape(cornerRadius),
            properties = DialogProperties(
                dismissOnBackPress = !isUpdateForced.value,
                dismissOnClickOutside = !isUpdateForced.value
            )
        )
    }
}

private fun Context.openUpdatePageInPlayStore(stringUrl: Uri?) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            stringUrl ?: "https://tomorrow.services/".toUri()
        )
    )
}

private fun Context.getAppName(): String {
    return this.applicationInfo.loadLabel(this.packageManager).toString()
}

private fun UpdateType.toAppUpdateType(): Int =
    if (this == UpdateType.Forced) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE