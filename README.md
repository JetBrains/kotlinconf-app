## KotlinConf Schedule Application

Welcome to the KotlinConf! We hope you enjoy the conference and session, and while you are likely using
the official KotlinConf schedule application on your mobile phone, let us tell you something about how it is implemented.

All pieces of the application are implemented in *Kotlin*. Backend, frontend and mobile apps are Kotlin applications.
Yes, Kotlin is powering all parts of the story. Did I already said that? Okay, let's get to the details:

### Server

KotlinConf Schedule Application is connecting to the server running in the cloud to get information about sessions,
speakers, favorites and votes. It is developed using [Ktor](http://ktor.io), an asynchronous Kotlin web framework.

The server polls [Sessionize](https://sessionize.com) service we use for planning the conference. 
Once in a while it connects to APIs to get latest information about sessions, speakers, and timeline. 
It then augments and republishes this information for clients to consume. 
It also provides couple of extra APIs to save your favorites and accumulate votes.


### Web Page

During the KotlinConf keynote we showed a web page connected to the same the server and displaying live
information about voting process. This page is developed using Kotlin/JS and React framework. It connects to
the server using a WebSocket and receives updates on votes for the given session. 

### Android Application

Obviously, Android application is developed in Kotlin/JVM. What's interesting here is that this time
application utilizes Multiplatform support coming as experimental feature in Kotlin 1.2. Data structures for 
retrieving data from the backend server and some date-time operations are shared across multiple projects.

### iOS Application

Believe it or not, iOS application is developed in Kotlin/Native. While it is still an early stage in supporting iOS 
platform natively with Kotlin, it is already a fully functional, connected application, interoperating with iOS 
native frameworks and otherwise indistinguishable from Objective C or Swift application. While there are many
rough edges and no multiplatform support ready for this kind of application, it's already showing the huge potential
in enabling development of native applications for all platforms.

     