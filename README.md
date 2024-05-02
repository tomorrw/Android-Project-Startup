
# Android Project Startup

This is a collection of utils to kick start your android apps.

| Package Name | Description                |
| :--------    | :------------------------- |
| [`InternetConnectivity`](InternetConnectivity) | Check Internet Connectivity |
| [`Navigation`](Navigation) | Navigation Components |
| [`AppUpdate`](AppUpdate) | Checks App Update |
| [`ReadViewModel`](ReadViewModel) | ViewModel with custom states and events |
| [`RequestPermission`](RequestPermission) | Pop up requesting permissions |



## Install Package

```kotlin
implementation("com.github.tomorrw.Android-Project-Startup:InternetConnectivity:$version")
implementation("com.github.tomorrw.Android-Project-Startup:Navigation:$version")
implementation("com.github.tomorrw.Android-Project-Startup:AppUpdate:$version")
implementation("com.github.tomorrw.Android-Project-Startup:ReadViewModel:$version")
implementation("com.github.tomorrw.Android-Project-Startup:RequestPermission:$version")
```
`❗️ Don't Forget to add`
```kotlin
maven {
    name = it
    url = uri("https://maven.pkg.github.com/tomorrw/Android-Project-Startup")
    credentials {
        username = envValues['USERNAME']
        password = envValues['TOKEN']
    }
}
```
