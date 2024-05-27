[![JetBrains incubator project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub) 

# KotlinConf App

This is the official KotlinConf App! We hope you enjoy(ed) the conference and sessions. This repository contains the source code of the application. 

All pieces of the application are implemented in *Kotlin*. Backend, frontend and mobile apps are Kotlin applications.
Yes, Kotlin is powering all parts of the story. Did I already say that? Okay, let's get to the details:

### Server

KotlinConf App is connecting to the server running in the cloud to get information about sessions,
speakers, favorites and votes. It is developed using [Ktor](https://ktor.io), an asynchronous Kotlin web framework.

The server polls [Sessionize](https://sessionize.com) service, which is used for planning the conference. 
Once in a while, it connects to APIs to get the latest information about sessions, speakers, and timeline. 
It then augments and republishes this information for clients to consume. 
It also provides a couple of extra APIs to save your favorites and accumulate votes.

### iOS, Android, Browser and Desktop Applications

All applications are developed within a single codebase using [Kotlin Multiplatform technology](https://kotlinlang.org/docs/multiplatform.html).
The UI is implemented using [Compose Multiplatform UI framework](https://www.jetbrains.com/lp/compose-multiplatform/).

## How to build and run

### Prerequisites
 
 * JDK >= 17 
 * Android Studio with Android SDK
 * XCode with iOS SDK
 * Create a file `local.properties` in the root directory of the project, pointing to your Android SDK installation. On Mac OS, the contents should be `sdk.dir=/Users/<your username>/Library/Android/sdk`. On other OSes, please adjust accordingly.

### Running the Android app

1. Open the project in Android Studio or [JetBrains Fleet](https://www.jetbrains.com/fleet/) and wait until the project finishes loading. 
2. In Android Studio, select the `androidApp` run configuration from the drop-down list within the [toolbar](https://developer.android.com/studio/intro#user-interface).
3. Click on the [run icon](https://developer.android.com/studio/run/rundebugconfig#running) to start the simulator.

### Running the iOS

1. Open the project in Android Studio or [JetBrains Fleet](https://www.jetbrains.com/fleet/) and wait until the project finishes loading.
2. In Android Studio, select the `KotlinConf` run configuration from the drop-down list within the [toolbar](https://developer.android.com/studio/intro#user-interface).
3. Click on the [run icon](https://developer.android.com/studio/run/rundebugconfig#running) to start the simulator.

### Running the desktop app

* Run `./gradlew :shared:run` to start the desktop application
### Running the backend

* Run `./gradlew :backend:run` to start the server
* All API will be available at `http://0.0.0.0:8080`

### Running the browser app

* To run the web app in the browser, run `./gradlew :shared:wasmJsBrowserRun`.
* Open `http://localhost:8000` in your browser after build to see the app.