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

val DZIKIR = listOf(
    Dzikir(
        id = "sholawat",
        name = "Sholawat",
        arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
        latin = "Allahumma sholli 'ala Muhammad",
        meaning = "Ya Allah, limpahkanlah rahmat kepada Nabi Muhammad",
    ),
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
)

fun dzikirById(id: String): Dzikir = DZIKIR.firstOrNull { it.id == id } ?: DZIKIR[0]
