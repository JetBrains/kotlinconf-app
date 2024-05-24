const KOTLIN_CONF_CACHE = 'kotlin-conf-cache';
const staticUrlsToCache = [
    './',
    './kotlin-app-wasm-js.js',
    './kotlin-app-js.js',
    './135.js',
    './273.js',
    './8433c6b69bfa201b0895.wasm',
    './KotlinConfApp-shared-wasm-js.wasm',
    './manifest.json',
    './images/AppIcon@3x.png',
    './images/AppIcon~ios-marketing.png',
    './images/splash-screen.svg',
    './images/splash-screen.png',
    './images/bird_splash-screen.svg',
    './images/bird_splash-screen.png',
    './images/menu_banner.svg',
    './font/jetbrains_sans_bold.ttf',
    './font/jetbrains_sans_semibold.ttf',
    './font/jetbrains_sans_regular.ttf',
    './files/app-description.md',
    './files/app-privacy-policy.md',
    './files/app-terms.md',
    './files/code-of-conduct.md',
    './files/first-floor-dark.svg',
    './files/first-floor.svg',
    './files/ground-floor-dark.svg',
    './files/ground-floor.svg',
    './files/visitors-privacy-policy.md',
    './files/visitors-terms.md',
    './values/strings.commonMain.cvr',
    './drawable/about_conf_bottom_banner.xml',
    './drawable/about_conf_top_banner.xml',
    './drawable/academy.png',
    './drawable/android.png',
    './drawable/arrow_right.xml',
    './drawable/back.xml',
    './drawable/bookmark.xml',
    './drawable/bookmark_active.xml',
    './drawable/btsystems.xml',
    './drawable/close.xml',
    './drawable/cloud_inject.xml',
    './drawable/cup.xml',
    './drawable/cup_active.xml',
    './drawable/express.png',
    './drawable/foundation.xml',
    './drawable/google.xml',
    './drawable/gradle.xml',
    './drawable/kodein.xml',
    './drawable/kt_weekly.xml',
    './drawable/light.xml',
    './drawable/location.xml',
    './drawable/location_active.xml',
    './drawable/lunch.xml',
    './drawable/lunch_active.xml',
    './drawable/menu.xml',
    './drawable/menu_active.xml',
    './drawable/menu_banner.xml',
    './drawable/mercari.xml',
    './drawable/monta.xml',
    './drawable/mytalks.xml',
    './drawable/mytalks_active.xml',
    './drawable/notifications_bird.xml',
    './drawable/pretix.xml',
    './drawable/privacy_policy_bird.xml',
    './drawable/schedule_day_1_banner.xml',
    './drawable/schedule_day_2_banner.xml',
    './drawable/schedule_day_3_banner.xml',
    './drawable/schedule_party_section_bird.xml',
    './drawable/search.xml',
    './drawable/sentry.xml',
    './drawable/shape.xml',
    './drawable/slack.xml',
    './drawable/smilehappy.xml',
    './drawable/smilehappy_active.xml',
    './drawable/smileneutral.xml',
    './drawable/smileneutral_active.xml',
    './drawable/smilesad.xml',
    './drawable/smilesad_active.xml',
    './drawable/speakers.xml',
    './drawable/speakers_active.xml',
    './drawable/stickermule.xml',
    './drawable/time.xml',
    './drawable/time_active.xml',
    './drawable/touchlab.xml',
    './drawable/uber.xml',
    './drawable/worldline.xml',
    './drawable/x.xml',
    './drawable-dark/about.xml',
    './drawable-dark/academy.png',
    './drawable-dark/android.png',
    './drawable-dark/btsystems.xml',
    './drawable-dark/closing.xml',
    './drawable-dark/cloud_inject.xml',
    './drawable-dark/express.png',
    './drawable-dark/foundation.xml',
    './drawable-dark/google.xml',
    './drawable-dark/gradle.xml',
    './drawable-dark/kodein.xml',
    './drawable-dark/kt_weekly.xml',
    './drawable-dark/mercari.xml',
    './drawable-dark/monta.xml',
    './drawable-dark/pretix.xml',
    './drawable-dark/sentry.xml',
    './drawable-dark/shape.xml',
    './drawable-dark/stickermule.xml',
    './drawable-dark/touchlab.xml',
    './drawable-dark/uber.xml',
    './drawable-dark/worldline.xml'
];

self.addEventListener('install', function (event) {
    event.waitUntil(
        caches.open(KOTLIN_CONF_CACHE)
            .then(cache =>
                Promise.all(
                    staticUrlsToCache.map(file =>
                        cache.add(file)
                            .catch(_ => console.error(`Can't load ${file} to cache`))
                    )
                )
            ).then(_ => console.log("Offline mode is Ready!"))
    );
});

self.addEventListener('fetch', event => {
    if (event.request === "no-cache") return;

    event.respondWith(
        caches.match(event.request)
            .then(response =>
                response ?? fetch(event.request)
                    .then(response => {
                        if (response == null || response.status !== 200) {
                            return caches.match(event.request)
                                .then(cacheResponse => 
                                  cacheResponse ?? response
                                );
                        }

                        const responseToCache = response.clone();

                        caches.open(KOTLIN_CONF_CACHE)
                            .then(cache => cache.put(event.request, responseToCache));

                        return response;
                    })
                    .catch(error => caches.match(event.request))
            )
    )
});

self.addEventListener('activate', event => {
    const cacheAllowlist = [KOTLIN_CONF_CACHE];

    event.waitUntil(
        caches.keys().then(cacheNames =>
            Promise.all(
                cacheNames.map(cacheName => {
                    if (cacheAllowlist.indexOf(cacheName) === -1) {
                        return caches.delete(cacheName);
                    }
                })
            )
        )
    );
});

const map = new Map()

self.addEventListener('message', function(event) {
    console.log('Service worker received a message: ', event.data);

    if (event.data.command === 'register-notification') {
        const id = setTimeout(() => {
          self.registration.showNotification(event.data.title, { body: event.data.body });
          map.delete(event.data.title)
        }, event.data.delay)
        map.set(event.data.title, id)
    }
    if (event.data.command === 'cancel-notification') {
        const id = map.get(event.data.title)
        if (id != undefined) {
            clearTimeout(id)
            map.delete(event.data.title)
        }
    }
  });