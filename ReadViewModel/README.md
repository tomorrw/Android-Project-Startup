# Read View Model

Inherit this view model and use it's states and methods.

## Usage
```kotlin
//View Model
class myViewModel: ReadViewModel<CustomData> {
    load: () -> Flow<CustomData>,
    refresh: () -> Flow<CustomData> = load,
    onDismiss: suspend () -> Unit = {},
    emptyCheck: (CustomData) -> Boolean = {false},
}

//View
DefaultReadView(
    viewModel: myViewModel,
    loader: @Composable () -> Unit = { Loader() },
    error: @Composable (String) -> Unit,//has a default component
    emptyState: @Composable () -> Unit, //has a default component
) { data ->
    //Your content
}
```

`💡 The load and refresh function could be suspend functions`

## Methods
```kotlin
Event {
    OnRefresh
    Load 
    ClearErrors
    LoadSilently 
}

//Usage
MyViewModel.on(Event.OnRefresh)
```

### onDataReception
another usefull method is `onDataReception(d: CustomData)` which could be called to change the data state.

## States
```kotlin
State<CustomData>(
isLoading: Boolean = false,
isRefreshing: Boolean = false,
isEmpty: Boolean = false,
viewData: Loadable<CustomData> = NotLoaded(),
error: String? = null,
)
```