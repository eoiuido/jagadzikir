package com.eoi.jagadzikir

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowInsets
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher

val GREEN = 0xFF1B7F4B.toInt()
private val GREEN_LIGHT = 0xFFEEF8F2.toInt()
private val BACKGROUND = 0xFFF7FAF8.toInt()
private val WHITE = 0xFFFFFFFF.toInt()
private val BORDER = 0xFFE3E8E5.toInt()
private val TEXT = 0xFF222222.toInt()
private val TEXT_SOFT = 0xFF444444.toInt()
private val GREY = 0xFF777777.toInt()
private val RED = 0xFFB3261E.toInt()
private val WARNING_BG = 0xFFFDECEA.toInt()
private val WARNING_TEXT = 0xFF8A1C14.toInt()

private const val REQUEST_NOTIFICATIONS = 1
private const val FEEDBACK_EMAIL = "eoi.uido@gmail.com"
private const val EOI_WEBSITE = "https://eoiuido.github.io"

// The whole app is this one screen (Home), plus the About page.
// Views are built in code: no layout library, which keeps the app tiny.
class MainActivity : Activity() {
    private lateinit var settings: Settings
    private lateinit var scroll: ScrollView
    private var showingAbout = false
    private var permissionDenied = false
    private var backCallback: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = Store.load(this)
        scroll = ScrollView(this).apply {
            setBackgroundColor(BACKGROUND)
            clipToPadding = true
        }
        // Android 15+ draws the app behind the status bar; keep content below it.
        if (Build.VERSION.SDK_INT >= 35) {
            scroll.setOnApplyWindowInsetsListener { v, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars())
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                insets
            }
        }
        setContentView(scroll)
    }

    override fun onResume() {
        super.onResume()
        // Coming back from the phone's settings: re-check permission and the alarm.
        if (settings.running) Reminders.schedule(this)
        render()
    }

    // ---- Actions ----

    private fun update(new: Settings) {
        settings = new
        Store.save(this, new)
        Reminders.schedule(this)
        render()
    }

    private fun onStartPressed() {
        if (notificationText(settings) == null) {
            AlertDialog.Builder(this)
                .setTitle("Isi sendiri")
                .setMessage("Tulis teks Anda dulu, dalam huruf Arab, Latin, atau keduanya.")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        if (Reminders.notificationsAllowed(this)) {
            start()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Izin notifikasi")
            .setMessage("JagaDzikir perlu izin notifikasi agar bisa mengingatkan Anda.")
            .setPositiveButton("Lanjut") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
                } else {
                    permissionDenied = true
                    render()
                }
            }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != REQUEST_NOTIFICATIONS) return
        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            start()
        } else {
            permissionDenied = true
            render()
        }
    }

    private fun start() {
        permissionDenied = false
        Reminders.ensureChannel(this)
        update(settings.copy(running = true))
    }

    private fun pickTime(isStart: Boolean) {
        val current = if (isStart) settings.quietStart else settings.quietEnd
        TimePickerDialog(this, { _, hour, minute ->
            val picked = hour * 60 + minute
            val newStart = if (isStart) picked else settings.quietStart
            val newEnd = if (isStart) settings.quietEnd else picked
            if (newStart == newEnd) {
                AlertDialog.Builder(this)
                    .setTitle("Jam tenang")
                    .setMessage("Jam mulai dan selesai tidak boleh sama.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                update(settings.copy(quietStart = newStart, quietEnd = newEnd))
            }
        }, current / 60, current % 60, true).show()
    }

    private fun openNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= 26) {
            Intent(AndroidSettings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(AndroidSettings.EXTRA_APP_PACKAGE, packageName)
        } else {
            appDetailsIntent()
        }
        startActivity(intent)
    }

    // Opens the settings of the reminder notifications themselves, where pop-up can be turned on or off.
    private fun openReminderSettings() {
        if (Build.VERSION.SDK_INT < 26) {
            openNotificationSettings()
            return
        }
        Reminders.ensureChannel(this)
        val intent = Intent(AndroidSettings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(AndroidSettings.EXTRA_APP_PACKAGE, packageName)
            .putExtra(AndroidSettings.EXTRA_CHANNEL_ID, Reminders.CHANNEL)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            openNotificationSettings()
        }
    }

    private fun appDetailsIntent() =
        Intent(AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))

    private fun showAbout(on: Boolean) {
        showingAbout = on
        render(keepScroll = false)
        if (Build.VERSION.SDK_INT >= 33) updateBackHandling()
    }

    // Android 13+: the Back gesture on the About page returns to Home.
    private fun updateBackHandling() {
        if (Build.VERSION.SDK_INT < 33) return
        val current = backCallback as OnBackInvokedCallback?
        if (showingAbout && current == null) {
            val callback = OnBackInvokedCallback { showAbout(false) }
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, callback)
            backCallback = callback
        } else if (!showingAbout && current != null) {
            onBackInvokedDispatcher.unregisterOnBackInvokedCallback(current)
            backCallback = null
        }
    }

    @Deprecated("Used on Android 12 and older only")
    override fun onBackPressed() {
        if (showingAbout) showAbout(false) else super.onBackPressed()
    }

    // ---- Screens ----

    private fun render(keepScroll: Boolean = true) {
        val y = if (keepScroll) scroll.scrollY else 0
        scroll.removeAllViews()
        scroll.addView(if (showingAbout) aboutPage() else homePage())
        scroll.post { scroll.scrollTo(0, y) }
    }

    private fun homePage(): View {
        val page = page()
        page.addView(label("JagaDzikir", 28, GREEN, bold = true))

        if (!Reminders.notificationsAllowed(this) && (permissionDenied || settings.running)) {
            page.addView(warningBox())
        }

        page.addView(section("Pilih dzikir"))
        for (d in DZIKIR) page.addView(dzikirCard(d, d.id == settings.dzikir))
        page.addView(customCard())

        page.addView(section("Setiap"))
        page.addView(intervalRow())

        page.addView(quietRow())
        if (settings.quietEnabled) {
            page.addView(timeRow())
            page.addView(label("Tidak ada pengingat di jam ini.", 13, GREY).withTop(6))
        }
        if (reminderTimes(settings).isEmpty()) {
            page.addView(label("Dengan jam tenang ini, tidak ada waktu tersisa untuk pengingat.", 13, RED).withTop(6))
        }

        if (settings.running) {
            val status = "Aktif · ${dzikirName(settings)} · setiap ${formatInterval(settings.intervalMinutes)}"
            page.addView(label(status, 15, GREEN, bold = true).apply { gravity = Gravity.CENTER }.withTop(32))
            page.addView(button("Hentikan", filled = false) { update(settings.copy(running = false)) }.withTop(10))
        } else {
            page.addView(button("Mulai Pengingat", filled = true) { onStartPressed() }.withTop(32))
        }
        page.addView(link("Tentang") { showAbout(true) }.withTop(8))
        return page
    }

    private fun aboutPage(): View {
        val page = page()
        page.addView(label("‹ Kembali", 16, GREEN).apply {
            setPadding(0, dp(8), 0, dp(8))
            setOnClickListener { showAbout(false) }
        })
        page.addView(label("JagaDzikir", 28, GREEN, bold = true).withTop(8))
        page.addView(body("Versi ${versionName()} · Dibuat oleh EOI"))
        page.addView(body("Tidak mengumpulkan data pribadi."))

        page.addView(section("Tampilan pengingat"))
        page.addView(body("Anda bisa memilih bagaimana pengingat tampil, sesuai kenyamanan Anda:"))
        page.addView(body("• Muncul sebentar di bagian atas layar, lalu tersimpan di bilah notifikasi."))
        page.addView(body("• Hanya tampil sebagai ikon kecil di bilah notifikasi, tanpa muncul di layar."))
        page.addView(body("Keduanya tetap tanpa suara. Untuk memilih, buka pengaturan di bawah, lalu nyalakan atau matikan \"Banner\" atau \"Pop-up\" (namanya bisa berbeda di tiap HP)."))
        page.addView(button("Atur Tampilan Pengingat", filled = false) { openReminderSettings() }.withTop(16))

        page.addView(section("Pengingat tidak muncul?"))
        page.addView(body("Beberapa HP menghemat baterai dengan menahan notifikasi. Agar pengingat selalu muncul:"))
        page.addView(body("1. Buka Pengaturan HP, lalu pilih Aplikasi."))
        page.addView(body("2. Cari dan pilih JagaDzikir, lalu pilih Baterai."))
        page.addView(body("3. Pilih \"Tidak dibatasi\" (atau matikan \"Optimalkan baterai\")."))
        page.addView(body("Pastikan juga notifikasi untuk JagaDzikir diizinkan. Pengingat kadang bisa terlambat beberapa menit. Ini normal di Android."))
        page.addView(button("Buka Pengaturan Aplikasi", filled = false) { startActivity(appDetailsIntent()) }.withTop(16))

        page.addView(section("Saran & masukan"))
        page.addView(body("Punya saran, atau menemukan kesalahan pada teks? Kami senang sekali mendengarnya."))
        page.addView(button("Kirim masukan", filled = false) { sendFeedback() }.withTop(12))
        page.addView(label(FEEDBACK_EMAIL, 13, GREY).apply {
            gravity = Gravity.CENTER
            setTextIsSelectable(true)
        }.withTop(6))

        page.addView(section("Aplikasi lain dari EOI"))
        page.addView(body("Kami juga memiliki beberapa aplikasi sehari-hari lain. Kunjungi:"))
        page.addView(button("Lihat Aplikasi EOI", filled = false) { openWebsite() }.withTop(12))
        page.addView(label(EOI_WEBSITE.removePrefix("https://"), 13, GREY).apply { gravity = Gravity.CENTER }.withTop(6))
        return page
    }

    // Opens the EOI website in the user's browser. The list of apps lives there,
    // so new EOI apps can be added without updating JagaDzikir.
    private fun openWebsite() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(EOI_WEBSITE)))
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this)
                .setTitle("Aplikasi lain dari EOI")
                .setMessage("Kunjungi $EOI_WEBSITE")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    // Opens the user's own email app. JagaDzikir itself sends nothing.
    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$FEEDBACK_EMAIL"))
            .putExtra(Intent.EXTRA_SUBJECT, "Masukan JagaDzikir ${versionName()}")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this)
                .setTitle("Saran & masukan")
                .setMessage("Silakan kirim email ke $FEEDBACK_EMAIL")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun dzikirCard(d: Dzikir, selected: Boolean): View {
        val card = cardShell(d.name, selected) { update(settings.copy(dzikir = d.id)) }
        card.addView(arabicLabel(d.arabic))
        card.addView(latinLabel(d.latin))
        card.addView(label(d.meaning, 13, GREY).withTop(2))
        return card
    }

    // The 6th choice: the user writes their own dzikir in one box (Arabic, Latin or both).
    private fun customCard(): View {
        val selected = settings.dzikir == CUSTOM_ID
        val card = cardShell("Isi sendiri (teks Arab, Latin, atau keduanya)", selected) {
            update(settings.copy(dzikir = CUSTOM_ID))
        }
        if (selected) {
            // Typing saves straight away, without redrawing the screen (that would close the keyboard).
            card.addView(input(settings.customText, "Tulis di sini") {
                settings = settings.copy(customText = it)
                Store.save(this, settings)
            }.withTop(10))
        } else if (settings.customText.isNotBlank()) {
            card.addView(label(settings.customText.trim(), 17, TEXT).withTop(6))
        }
        return card
    }

    private fun cardShell(name: String, selected: Boolean, onSelect: () -> Unit): LinearLayout {
        val card = column().apply {
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = rounded(if (selected) GREEN_LIGHT else WHITE, if (selected) GREEN else BORDER, 14)
            if (!selected) setOnClickListener { onSelect() }
        }
        val top = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        top.addView(label(name, 15, if (selected) GREEN else GREY, bold = true), LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        val radio = View(this).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setStroke(dp(if (selected) 6 else 2), if (selected) GREEN else 0xFFBBBBBB.toInt())
            }
        }
        top.addView(radio, LinearLayout.LayoutParams(dp(20), dp(20)))
        card.addView(top)
        return card.withTop(10)
    }

    private fun arabicLabel(str: String): View = label(str, 24, TEXT).apply { gravity = Gravity.RIGHT }.withTop(6)

    private fun latinLabel(str: String): View =
        label(str, 15, TEXT_SOFT).apply { setTypeface(typeface, Typeface.ITALIC) }.withTop(4)

    // Each line lines up on its own side: Arabic on the right, Latin on the left.
    private fun input(value: String, hintText: String, onChange: (String) -> Unit): EditText =
        EditText(this).apply {
            setText(value)
            hint = hintText
            textSize = 17f
            setTextColor(TEXT)
            gravity = Gravity.START or Gravity.TOP
            minLines = 2
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            filters = arrayOf(InputFilter.LengthFilter(200))
            background = rounded(WHITE, BORDER, 10)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) = onChange(s?.toString() ?: "")
            })
        }

    // Interval choices in rows of three (currently one row), so they fit on small phones.
    private fun intervalRow(): View {
        val grid = column()
        INTERVALS.chunked(3).forEachIndexed { r, rowItems ->
            val row = LinearLayout(this)
            rowItems.forEachIndexed { i, minutes ->
                val selected = minutes == settings.intervalMinutes
                val chip = label(formatInterval(minutes), 14, if (selected) WHITE else TEXT, bold = selected).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(10), 0, dp(10))
                    background = rounded(if (selected) GREEN else WHITE, if (selected) GREEN else BORDER, 20)
                    setOnClickListener { update(settings.copy(intervalMinutes = minutes)) }
                }
                row.addView(chip, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply { if (i > 0) marginStart = dp(8) })
            }
            grid.addView(row.withTop(if (r > 0) 8 else 0))
        }
        return grid.withTop(10)
    }

    private fun quietRow(): View {
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        row.addView(label("Jam tenang", 16, TEXT, bold = true), LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(Switch(this).apply {
            isChecked = settings.quietEnabled
            setOnCheckedChangeListener { _, on -> scroll.post { update(settings.copy(quietEnabled = on)) } }
        })
        return row.withTop(24)
    }

    private fun timeRow(): View {
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        row.addView(timeBox("Mulai", settings.quietStart) { pickTime(true) }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(label("–", 18, GREY).apply { setPadding(dp(10), 0, dp(10), 0) })
        row.addView(timeBox("Selesai", settings.quietEnd) { pickTime(false) }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        return row.withTop(10)
    }

    private fun timeBox(caption: String, minutes: Int, onClick: () -> Unit): View = column().apply {
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(dp(12), dp(12), dp(12), dp(12))
        background = rounded(WHITE, BORDER, 12)
        addView(label(caption, 12, GREY))
        addView(label(formatTime(minutes), 22, TEXT, bold = true))
        setOnClickListener { onClick() }
    }

    private fun warningBox(): View = column().apply {
        setPadding(dp(14), dp(14), dp(14), dp(14))
        background = rounded(WARNING_BG, WARNING_BG, 12)
        addView(label("Notifikasi dimatikan. Buka Pengaturan untuk mengizinkan.", 14, WARNING_TEXT))
        addView(label("Buka Pengaturan", 14, WARNING_TEXT, bold = true).apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setPadding(0, dp(8), 0, 0)
            setOnClickListener { openNotificationSettings() }
        })
    }.withTop(12)

    // ---- Small building blocks ----

    private fun page(): LinearLayout = column().apply {
        setPadding(dp(20), dp(16), dp(20), dp(48))
    }

    private fun column(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

    private fun label(str: String, sp: Int, color: Int, bold: Boolean = false): TextView = TextView(this).apply {
        text = str
        textSize = sp.toFloat()
        setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    private fun section(str: String): View = label(str, 16, TEXT, bold = true).withTop(24)

    private fun body(str: String): View = label(str, 15, TEXT_SOFT).apply {
        setLineSpacing(0f, 1.25f)
    }.withTop(8)

    private fun button(str: String, filled: Boolean, onClick: () -> Unit): View =
        label(str, 17, if (filled) WHITE else GREEN, bold = true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(16))
            background = rounded(if (filled) GREEN else WHITE, GREEN, 14)
            setOnClickListener { onClick() }
        }

    private fun link(str: String, onClick: () -> Unit): View = label(str, 15, GREY).apply {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        gravity = Gravity.CENTER
        setPadding(0, dp(12), 0, dp(12))
        setOnClickListener { onClick() }
    }

    private fun rounded(fill: Int, stroke: Int, radiusDp: Int) = GradientDrawable().apply {
        setColor(fill)
        setStroke(dp(2), stroke)
        cornerRadius = dp(radiusDp).toFloat()
    }

    private fun <T : View> T.withTop(marginDp: Int): T {
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { topMargin = dp(marginDp) }
        return this
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

    @Suppress("DEPRECATION")
    private fun versionName(): String = packageManager.getPackageInfo(packageName, 0).versionName ?: ""
}
