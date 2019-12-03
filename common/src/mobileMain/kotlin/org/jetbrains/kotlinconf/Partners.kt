package org.jetbrains.kotlinconf

class Partner(
    val key: String,
    val title: String,
    val url: String,
    val mapName: String
)

object Partners {
    private val partners = listOf(
        Partner("android", "Android", "https://developer.android.com/", "partner table-android"),
        Partner("47", "47 degrees", "https://www.47deg.com", "partner table-47"),
        Partner("freenow", "FREE NOW", "https://free-now.com/", "partner table-freenow"),
        Partner("bitrise", "Bitrise", "https://www.bitrise.io/", "partner table-bitrise"),
        Partner("instill", "Instill", "https://www.instil.co", "partner table-instil"),
        Partner("gradle", "Gradle", "https://gradle.com/", "partner table-gradle"),
        Partner("n26", "N26", "https://n26.com/", "partner table-n26"),
        Partner("kodein", "Kodein", "https://kodein.net", "partner table-kodeinkoders"),
        Partner("data2viz", "Data2Viz", "https://data2viz.io/", "partner table-data2viz"),
        Partner("pleo", "Pleo", "https://www.pleo.io", "partner table-pleo"),
        Partner("shape", "Shape", "https://www.shape.dk/", "partner table-shape"),
        Partner("badoo", "Badoo", "https://badoo.com/en/", "partner table-badoo"),
        Partner("jetbrains", "JetBrains", "https://www.jetbrains.com", "partner table-jetbrains")
    )

    private val descriptions = mutableMapOf(
        "android" to "Android is the world's most popular mobile platform with more than 2.5 billion monthly active devices worldwide. Development on the platform is increasingly becoming Kotlin-first. Learn more about writing Android apps faster with Kotlin. https://developer.android.com/kotlin",
        "47" to "47 Degrees is a global consulting firm specializing in enterprise platform modernization, microservices architectures, mobile application development, and big data solutions, all using proven functional programming expertise.",
        "freenow" to "FREE NOW was founded as mytaxi in June 2009 and was the world’s first taxi app that established a direct connection between a passenger and a taxi driver. With 14 million passengers and more than 100,000 drivers, FREE NOW is one of the leading ride-hailing providers in Europe. Since February 2019, the service is part of the FREE NOW group, the ride-hailing joint venture of BMW and Daimler. FREE NOW today works with more than 700 employees in 26 offices and is available in over 100 European cities.",
        "bitrise" to "Bitrise helps you build and operate better apps, faster. By effortlessly combining services and tools mobile developers love, we make development easier, more scalable and take away the fear of change that causes development processes to stagnate.",
        "instill" to "Instil is a software engineering consultancy based in Belfast, UK. We specialise in the development of bespoke, business-critical software and developer training to clients globally.",
        "gradle" to "Gradle dramatically improves the software development process in terms of speed, reliability, and developer productivity to enable success in digital transformation.",
        "n26" to "N26 is Europe’s first Mobile Bank with a full European banking license. We have 3.5 million customers across 24 markets. Our team of over 1,300 employees in 4 locations is concentrated on reinventing the banking experience for the digital generation.",
        "kodein" to "Kodein Koders is the first startup in Europe to be entirely dedicated to Kotlin, anywhere Kotlin goes! From the ground up, we invested into making the Kodein Framework the first Open-Source Kotlin/Multiplatform Framework.",
        "data2viz" to "Data2viz is a company that creates libraries and tools for data-visualizations. We base our solutions on Kotlin, deeply convinced by the vast benefits it can provide to manipulate data and render the visualizations on different platforms: mobiles, web, and desktop.",
        "pleo" to "Pleo is a fundamentally new way to manage company expenses. Offering smart payment cards to employees, Pleo enables everyone to buy whatever they need for work, all the while making sure the company remains in full control of spending.",
        "shape" to "Shape is an award winning digital product studio. We are a team of 70+ devoted in-house developers, designers and strategists that combine innovation with digital craftsmanship to deliver lasting products for mobile and beyond.",
        "badoo" to "We're the team behind some of the world’s biggest dating brands, including Badoo, Bumble, Lumen and Chappy, and our products are used by over 500 million people worldwide.\n\nAs one of the leading tech companies in Europe, we’re constantly growing, investing and innovating to provide the best technology and user experience for our community, because we believe that happiness is better shared.\n\nOur Engineering Team is made up of over 280 Agile people, and each team releases new and improved features every week! They are fast, innovative and creative, and their work impacts hundreds of millions of people every day. To find out more, you can check out our tech blog here https://badootech.badoo.com/, and peruse our OpenSource projects here https://github.com/badoo.",
        "jetbrains" to "JetBrains is the proud creator of the Kotlin programming language. We also create professional software development tools for coding in Java, Kotlin, C#, C++, Ruby, Python, PHP, JavaScript and more languages.\n"
    )

    fun partner(name: String): Partner? = partners.find { name == it.key }

    fun partnerByRoomName(name: String): Partner? = partners.find { it.mapName == name }

    fun descriptionByName(name: String): String = descriptions[name] ?: ""
}