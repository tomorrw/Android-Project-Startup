# App Update
Displays a popup to the user when updateType is enabled with the option to update the app.

## Usage
```kotlin
InAppUpdater (
    storeInfo: StoreInfo? = null,
    updateType: UpdateType,
    title: String = "${storeInfo?.name ?: "App"} needs an update!",
    description: String = "A new update is available, please download the latest version!",
    ctaButtonText: String = "Update",
    dismissButtonText: String = "No Thanks",
    style: AppUpdaterStyle = // Default Style
)
```

### Parameters
```kotlin
StoreInfo(
    name: String,
    updateUrl: String?,
)

enum class UpdateType {
    Forced, Flexible, None,
}

AppUpdaterStyle(
    backgroundColor: Color,
    titleTextStyle: TextStyle,
    descriptionTextStyle: TextStyle,
    ctaButtonTextStyle: TextStyle,
    dismissButtonTextStyle: TextStyle,
    cornerRadius: Dp = 8.dp
)
```
