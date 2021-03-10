# Review Relic Android Sdk
You can create your app on [Review Relic Admin Panel](https://reviewrelic.com/)

## Installation

Review Relic for Android supports API 16 and above.

##### Step 1.
Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

##### Step 2.
Add the dependency

```Gradle
dependencies {
        implementation 'com.github.reviewrelic-universe:android-sdk:1.0.0'
}
```

## Sample Apps

A project with some basic example is provided [here](https://github.com/reviewrelic-universe/android-sdk/tree/1.0.0/sample).

## Permissions

We include the INTERNET permission by default as we need it to make network requests:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
## Code Initialization

To initialize the Review Relic SDK in your app, use below snippet in your app's Application class or where ever you seems appropiaate:

#### Initialize SDK

```kotlin
ReviewRelic
    .setSecretKey("") /* Secret key obtained from admin panel of review relic */
    .setToken("") /* Token obtained from admin panel of review relic */            .setMerchantId("") /* Merchant Id obtained from admin panel of review relic */
    .enableLogging() /* Set to true if you want to see logs */
    .build()
```

#### Show Sheet

```kotlin
ReviewRelic.showSheet(
    transactionId = "",  /*Test Transaction Id*/
    thankYouMessage = "", /*Test Thank you message*/
    fragmentManager = null /* Pass fragment manager from your Activity/Fragment */
)
```

