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

### Android Application

As you can imagine, the Android version is developed in Kotlin/JVM. What's interesting here is that this time
application utilizes Multiplatform support, which is an experimental feature in Kotlin 1.3.61. Data structures for retrieving data from the backend server and some date-time operations are shared across multiple projects.

### iOS Application

User interface of iOS version is written in Swift, all logic and data written in Kotlin in the common module. 
This way iOS part itself is responsible only for specifying how the application looks like and how it represents changes requested by logic. Kotlin and Swift are highly interoperable, so from Swift, you can easily use all classes and tools defined in the common module. 

## How to build and run

### Building the code

 * Make sure you have the Android SDK installed
 * Open the project in IntelliJ IDEA (2019.3 recommended)
 * Create a file `local.properties` in the root directory of the project, pointing to your Android SDK installation. On Mac OS, the contents should be `sdk.dir=/Users/<your username>/Library/Android/sdk`. On other OSes, please adjust accordingly.
 * Run `./gradlew build`

### Running the backend
 
 * Run `./gradlew backend:run` from the command line or from Gradle tool window
 * The backend will start serving on localhost:8080, with data stored in a local H2 database

### Running the Android app

 * Create a run configuration of type "Android App"
 * Select module "app" in the run configuration settings
 * Run the configuration
 * Select the emulator or connected device, as normal

### Running the iOS

To run iOS version you need to generate fat framework first:
 * For simulator `./gradlew debugFatFramework`
 * For device `./gradlew releaseFatFramework`
  
Next you should install all pods with running:
```
cd iosApp
pod install
```

Next you can open `iosApp/KotlinConf.xcworkspace`, select a device XCode and hit run.

