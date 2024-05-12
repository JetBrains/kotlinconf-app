import * as fs from 'node:fs/promises'

const response = await fetch("https://kotlinconf-app-prod.labs.jb.gg/conference");
const text = await response.text();
const json = JSON.parse(text)

const urls = json.speakers.map((s) => s.photoUrl)

urls.forEach(async (url) => {
    const path = "resources/cache/" + url.substring("https://".length);
    const lastSlash = path.lastIndexOf("/");
    const dir = path.substring(0, lastSlash);
    await fs.mkdir(dir, { recursive: true });

    console.log(`Downloading ${url} to ${path}`)
    try {
        const r = await fetch(url)
        let buffer = await r.arrayBuffer();
        await fs.writeFile(path, new DataView(buffer))
    } catch (e) {
        console.log(`FAIL: ${e} for ${url}`)
    }
});