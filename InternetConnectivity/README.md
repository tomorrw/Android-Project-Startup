# Internet Connectivity

Simply drop the `ConnectivityStatus` and you will be notified of changes in internet connectivity.

## Usage

```kotlin
ConnectivityStatus()
```

### Connectivity Wrapper 
You can also wrap your content with the `ConnectivityStatusWrapper` to have the status displayed at the top of your content.

```kotlin
ConnectivityStatusWrapper(
    modifier: Modifier = Modifier,
    color: : Color = MaterialTheme.colorScheme.surface,
    content: @Composable (ColumnScope) -> Unit
)
```