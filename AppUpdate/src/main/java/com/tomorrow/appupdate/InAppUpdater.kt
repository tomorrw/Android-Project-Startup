package com.tomorrow.appupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
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
    Forced, Flexible, None,
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
    appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(LocalContext.current),
    storeInfo: StoreInfo? = null,
    updateType: UpdateType,
    customAlertDialog: @Composable (
        title: String,
        description: String,
        ctaButtonText: String,
        onCTAClick: () -> Unit,
        isDismissible: Boolean,
        isDismissibleOnBack: Boolean,
        onDismiss: () -> Unit,
        dismissButtonText: String,
    ) -> Unit,
) {
    val intentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { if (it.resultCode == Activity.RESULT_CANCELED) exitProcess(0) })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isForceUpdateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val isFlexibleUpdateDialogVisible = rememberSaveable { mutableStateOf(false) }



    LaunchedEffect(key1 = updateType) {
        fun toggleDialog(updateType: UpdateType) {
            isFlexibleUpdateDialogVisible.value =
                updateType == UpdateType.Flexible
            isForceUpdateDialogVisible.value = updateType == UpdateType.Forced
        }

        fun checkUpdateType() {
            if (updateType == UpdateType.None) return

            appUpdateManager.appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    appUpdateManager.startUpdate(intentLauncher, it, updateType.toAppUpdateType())
                } else {
                    toggleDialog(updateType)
                }
            }.addOnFailureListener { toggleDialog(updateType) }
        }

        checkUpdateType()
    }

    when {
        isForceUpdateDialogVisible.value ->
            customAlertDialog(
                title = "${storeInfo?.name ?: "App"} needs an update!",
                description = "A new update is available, please download the latest version!",
                ctaButtonText = "Update",
                onCTAClick = { context.openUpdatePageInPlayStore(storeInfo?.updateUrl?.toUri()) },
                isDismissible = false,
                isDismissibleOnBack = false,
                onDismiss = { exitProcess(0) },
                dismissButtonText = "Close App",
            )

        isFlexibleUpdateDialogVisible.value ->
            customAlertDialog(
                title = "${storeInfo?.name ?: "App"} needs an update!",
                description = "A new update is available, please download the latest version!",
                ctaButtonText = "Update",
                onCTAClick = { context.openUpdatePageInPlayStore(storeInfo?.updateUrl?.toUri()) },
                isDismissible = true,
                onDismiss = { isFlexibleUpdateDialogVisible.value = false },
                dismissButtonText = "No Thanks",
                isDismissibleOnBack = true
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