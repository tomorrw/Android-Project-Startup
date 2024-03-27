# App Update
Displays a popup to the user when updateType is enabled with the option to update the app.

## Usage
```kotlin
InAppUpdater (
appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(LocalContext.current),
storeInfo = appInfo,
updateType = updateType,
customAlertDialog = { title, description, ctaButtonText, onCTAClick, isDismissible, isDismissibleOnBack, onDismiss, dismissButtonText ->
            // custom Alert Dialog Goes Here
    }
)
```

### Parameters
```kotlin
StoreInfo(
    val name: String,
    val updateUrl: String?,
)

enum class UpdateType {
    Forced, Flexible, None,
}
```
