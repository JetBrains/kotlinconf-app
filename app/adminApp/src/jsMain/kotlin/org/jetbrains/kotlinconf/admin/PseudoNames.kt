// ABOUTME: Maps an opaque user UUID to a stable, friendly pseudo-name (e.g. "Brave Otter").
// ABOUTME: Deterministic and storage-free — same UUID always yields the same name, like Google Docs anons.
package org.jetbrains.kotlinconf.admin

/**
 * Derives a readable name from a UUID so the admin can talk about a user without the raw id.
 * The mapping is a pure function of the id (FNV-1a hash into two word lists), so it is stable
 * across reloads and needs no persistence. Names can collide; the UUID stays the real key.
 */
fun pseudoName(userId: String): String {
    val h = fnv1a(userId)
    val adjective = ADJECTIVES[((h ushr 16) and 0xFFFF) % ADJECTIVES.size]
    val animal = ANIMALS[(h and 0xFFFF) % ANIMALS.size]
    return "$adjective $animal"
}

// FNV-1a, 32-bit. Pure Int math (wraps mod 2^32), so it is identical on every Kotlin target —
// unlike String.hashCode(), which we deliberately avoid relying on for a stable contract.
private fun fnv1a(s: String): Int {
    var hash = -0x7ee3623b // 0x811C9DC5
    for (c in s) {
        hash = hash xor c.code
        hash *= 0x01000193
    }
    return hash
}

private val ADJECTIVES = listOf(
    "Amber",
    "Brave",
    "Bright",
    "Calm",
    "Clever",
    "Cosmic",
    "Curious",
    "Daring",
    "Eager",
    "Electric",
    "Fearless",
    "Gentle",
    "Golden",
    "Happy",
    "Honest",
    "Jolly",
    "Keen",
    "Kind",
    "Lively",
    "Lucky",
    "Mellow",
    "Merry",
    "Mighty",
    "Noble",
    "Plucky",
    "Proud",
    "Quiet",
    "Rapid",
    "Rusty",
    "Sharp",
    "Shy",
    "Silent",
    "Silver",
    "Sleepy",
    "Smooth",
    "Snappy",
    "Sneaky",
    "Spry",
    "Sunny",
    "Swift",
    "Tidy",
    "Vivid",
    "Witty",
    "Zany",
    "Zesty",
    "Bold",
    "Cozy",
    "Frosty",
)

private val ANIMALS = listOf(
    "Aardvark",
    "Badger",
    "Beaver",
    "Bison",
    "Cheetah",
    "Cobra",
    "Crane",
    "Dolphin",
    "Eagle",
    "Falcon",
    "Ferret",
    "Finch",
    "Fox",
    "Gecko",
    "Gibbon",
    "Hare",
    "Heron",
    "Ibex",
    "Jaguar",
    "Koala",
    "Lemur",
    "Lynx",
    "Magpie",
    "Marmot",
    "Mole",
    "Moose",
    "Newt",
    "Ocelot",
    "Otter",
    "Owl",
    "Panda",
    "Panther",
    "Puffin",
    "Quokka",
    "Rabbit",
    "Raccoon",
    "Raven",
    "Robin",
    "Seal",
    "Shark",
    "Sloth",
    "Sparrow",
    "Stoat",
    "Tapir",
    "Toucan",
    "Walrus",
    "Weasel",
    "Wombat",
)
