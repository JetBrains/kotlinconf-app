// const KOTLIN_CONF_CACHE = 'kotlin-conf-cache';
// const staticUrlsToCache = [];
//
// self.addEventListener('install', function (event) {
//     event.waitUntil(
//         caches.open(KOTLIN_CONF_CACHE)
//             .then(cache =>
//                 Promise.all(
//                     staticUrlsToCache.map(file =>
//                         cache.add(file)
//                             .catch(_ => console.error(`Can't load ${file} to cache`))
//                     )
//                 )
//             ).then(_ => console.log("Offline mode is Ready!"))
//     );
// });
//
// self.addEventListener('fetch', event => {
//     if (event.request === "no-cache") return;
//
//     event.respondWith(
//         caches.match(event.request)
//             .then(response =>
//                 response ?? fetch(event.request)
//                     .then(response => {
//                         if (response == null || response.status !== 200) {
//                             return caches.match(event.request)
//                                 .then(cacheResponse => 
//                                   cacheResponse ?? response
//                                 );
//                         }
//
//                         const responseToCache = response.clone();
//
//                         caches.open(KOTLIN_CONF_CACHE)
//                             .then(cache => cache.put(event.request, responseToCache));
//
//                         return response;
//                     })
//                     .catch(error => caches.match(event.request))
//             )
//     )
// });
//
// self.addEventListener('activate', event => {
//     const cacheAllowlist = [KOTLIN_CONF_CACHE];
//
//     event.waitUntil(
//         caches.keys().then(cacheNames =>
//             Promise.all(
//                 cacheNames.map(cacheName => {
//                     if (cacheAllowlist.indexOf(cacheName) === -1) {
//                         return caches.delete(cacheName);
//                     }
//                 })
//             )
//         )
//     );
// });

const map = new Map()

self.addEventListener('message', function(event) {
    console.log('Service worker received a message: ', event.data);

    if (event.data.command === 'register-notification') {
        const id = setTimeout(() => {
          self.registration.showNotification(event.data.title, { body: event.data.body });
          map.delete(event.data.notificationId)
        }, event.data.delay)
        map.set(event.data.notificationId, id)
    }
    if (event.data.command === 'cancel-notification') {
        const id = map.get(event.data.notificationId)
        if (id != undefined) {
            clearTimeout(id)
            map.delete(event.data.notificationId)
        }
    }
  });
