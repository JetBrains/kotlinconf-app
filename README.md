[![JetBrains incubator project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub) 

# KotlinConf App

This is the official KotlinConf App! We hope you enjoy(ed) the conference and sessions. This repository contains the source code of the application. 

All pieces of the application are implemented in *Kotlin*. Backend, frontend and mobile apps are Kotlin applications.
Yes, Kotlin is powering all parts of the story. Did I already say that? Okay, let's get to the details:

### Server

KotlinConf App is connecting to the server running in the cloud to get information about sessions,
speakers, favorites and votes. It is developed using [Ktor](http://ktor.io), an asynchronous Kotlin web framework.

The server polls [Sessionize](https://sessionize.com) service, which is used for planning the conference. 
Once in a while it connects to APIs to get latest information about sessions, speakers, and timeline. 
It then augments and republishes this information for clients to consume. 
It also provides couple of extra APIs to save your favorites and accumulate votes.

### Web Page

During the KotlinConf keynote we showed a web page connected to the same server, displaying live
information about the voting process. This page is developed using [Kotlin/JS and React framework](https://github.com/jetbrains/create-react-kotlin-app). It connects to
the server using a WebSocket and receives updates on votes for the given session. 

### Android Application

As you can imagine, the Android version is developed in Kotlin/JVM. What's interesting here is that this time
application utilizes Multiplatform support, which is an experimental feature in Kotlin 1.2. Data structures for 
retrieving data from the backend server and some date-time operations are shared across multiple projects.

### iOS Application

The best part is that the iOS version is developed in Kotlin/Native. While still at an early stage in supporting iOS 
platform natively with Kotlin, it is already a fully functional, connected application, interoperating with iOS 
native frameworks, and otherwise indistinguishable from Objective C or Swift application. While there are still
rough edges and no multiplatform support ready for this kind of application, it's already showing the huge potential
in enabling development of native applications for all platforms.

## How to build and run

### Building the code

 * Make sure you have the Android SDK installed
 * Open the project in IntelliJ IDEA (2017.3 EAP recommended)
 * Create a file `local.properties` in the root directory of the project, pointing to your Android SDK installation. On Mac OS,
the contents should be `sdk.dir=/Users/<your username>/Library/Android/sdk`. On other OSes, please adjust accordingly.
 * Run `./gradlew build`

### Running the backend
 
 * Run `./gradlew backend:run` from the command line or from Gradle toolwindow
 * The backend will start serving on localhost:8080, with data stored in a local H2 database

Or deploy to Heroku with this button:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

### Running the Android app

 * Create a run configuration of type "Android App"
 * Select module "app" in the run configuration settings
 * Run the configuration
 * Select the emulator or connected device, as normal

### Running the Web client

 * Make sure the backend is running on localhost:8080
 * Run `npm run serve` in the 'web' directory to run webpack development server
