package com.eoi.jagadzikir

import android.content.Context

// Times are stored as "minutes after midnight", e.g. 22:00 -> 1320.
data class Settings(
    val dzikir: String = "sholawat",
    val intervalMinutes: Int = 60,
    val quietEnabled: Boolean = true,
    val quietStart: Int = 22 * 60,
    val quietEnd: Int = 4 * 60 + 30,
    val running: Boolean = false,
)

// Saves the settings in a small private file on the phone. Nothing leaves the phone.
object Store {
    private const val FILE = "settings"

    fun load(context: Context): Settings {
        val p = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val d = Settings()
        return Settings(
            dzikir = p.getString("dzikir", d.dzikir) ?: d.dzikir,
            intervalMinutes = p.getInt("interval", d.intervalMinutes),
            quietEnabled = p.getBoolean("quietEnabled", d.quietEnabled),
            quietStart = p.getInt("quietStart", d.quietStart),
            quietEnd = p.getInt("quietEnd", d.quietEnd),
            running = p.getBoolean("running", d.running),
        )
    }

    fun save(context: Context, s: Settings) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putString("dzikir", s.dzikir)
            .putInt("interval", s.intervalMinutes)
            .putBoolean("quietEnabled", s.quietEnabled)
            .putInt("quietStart", s.quietStart)
            .putInt("quietEnd", s.quietEnd)
            .putBoolean("running", s.running)
            .apply()
    }
}
