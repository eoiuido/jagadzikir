package com.eoi.jagadzikir

// Every religious text in the app lives in this one file.
// The owner must check each line before release (see spec.md, section 5).

class Dzikir(
    val id: String,
    val name: String,
    val arabic: String,
    val latin: String,
    val meaning: String,
)

const val CUSTOM_ID = "custom"

// Shown in this order on the Home screen. The user's own text comes last (see Settings).
val DZIKIR = listOf(
    Dzikir(
        id = "tahlil",
        name = "Tahlil",
        arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ",
        latin = "Laa ilaaha illallah",
        meaning = "Tiada Tuhan selain Allah",
    ),
    Dzikir(
        id = "hamdalah",
        name = "Hamdalah",
        arabic = "الْحَمْدُ لِلَّهِ",
        latin = "Alhamdulillah",
        meaning = "Segala puji bagi Allah",
    ),
    Dzikir(
        id = "istighfar",
        name = "Istighfar",
        arabic = "أَسْتَغْفِرُ اللَّهَ",
        latin = "Astaghfirullah",
        meaning = "Aku memohon ampun kepada Allah",
    ),
    Dzikir(
        id = "sholawat",
        name = "Sholawat",
        arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
        latin = "Allahumma sholli 'ala Muhammad",
        meaning = "Ya Allah, limpahkanlah rahmat kepada Nabi Muhammad",
    ),
    Dzikir(
        id = "sholawat_sayyidina",
        name = "Sholawat",
        arabic = "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ",
        latin = "Allahumma sholli 'ala sayyidina Muhammad",
        meaning = "Ya Allah, limpahkanlah rahmat kepada junjungan kami, Nabi Muhammad",
    ),
)

fun dzikirById(id: String): Dzikir? = DZIKIR.firstOrNull { it.id == id }

// What the notification shows: (title, text). Null if the user's own text is still empty.
// For the user's own text, the first line is the title and the rest goes below it.
fun notificationText(s: Settings): Pair<String, String>? {
    if (s.dzikir != CUSTOM_ID) {
        val d = dzikirById(s.dzikir) ?: DZIKIR[3]
        return d.arabic to d.latin
    }
    val lines = s.customText.lines().map { it.trim() }.filter { it.isNotEmpty() }
    if (lines.isEmpty()) return null
    return lines.first() to lines.drop(1).joinToString(" ")
}

fun dzikirName(s: Settings): String =
    if (s.dzikir == CUSTOM_ID) "Isi sendiri" else (dzikirById(s.dzikir) ?: DZIKIR[3]).name
