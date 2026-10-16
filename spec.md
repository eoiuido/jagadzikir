# spec.md: JagaDzikir (EOI app #1)

Status: **APPROVED by the owner on 12 Oct 2026.** The religious texts in section 5 must still be checked line by line before release.

---

## 1. Premise

- **Who:** Muslims in Indonesia with busy days.
- **Problem:** During a busy day it is easy to forget to recite dzikir and sholawat.
- **Promise:** Pick one dzikir and how often. JagaDzikir quietly shows it on your phone, all day, with no sound, no ads and no cost. Forever.

---

## 2. User flow, screen by screen

The app has **2 screens**. All text in the app is in **Indonesian**.

### Screen 1: Home (the only screen most people will use)

From top to bottom:

1. **Pick one dzikir** (only one can be chosen at a time). Each choice shows the Arabic, the Latin and a short Indonesian meaning in small text, so the user knows what they are choosing.
   1. Laa ilaaha illallah (selected by default)
   2. Alhamdulillah
   3. Astaghfirullah
   4. Allahumma sholli 'ala Muhammad
   5. Allahumma sholli 'ala sayyidina Muhammad
   6. **Isi sendiri (teks Arab, Latin, atau keduanya)**: one text box for the user's own dzikir. In the notification, the first line is the title and the rest goes below it. If they tap "Mulai Pengingat" with the box empty, a short message asks them to write something first.
2. **How often** ("Setiap"): 15 menit · 30 menit · 1 jam. The default is **30 menit**.
3. **Quiet hours** ("Jam tenang"): a switch, **on by default**, from **22:00 to 04:30**. Tapping a time lets the user change it.
4. **Big button:**
   - When reminders are off: **"Mulai Pengingat"** (Start reminders)
   - When reminders are on: a short line such as "Aktif · Sholawat · setiap 1 jam", and a button **"Hentikan"** (Stop)
5. A small link at the bottom: **"Tentang"** (About), which opens Screen 2.

**What happens when the user taps "Mulai Pengingat" for the first time:**
1. A short sentence appears first: *"JagaDzikir perlu izin notifikasi agar bisa mengingatkan Anda."* (JagaDzikir needs notification permission to remind you.)
2. Then the phone's own permission box appears.
3. If the user taps **Allow**, the reminders start and the screen shows "Aktif".
4. If the user taps **Don't allow**, see "Permission denied" in section 4.

**If the user changes the dzikir, interval or quiet hours while reminders are on,** the app updates the schedule straight away. They do not need to stop and start again.

### Screen 2: Tentang (About)

- App name, version and "Dibuat oleh EOI" (made by EOI).
- One line: "Gratis selamanya. Tanpa iklan. Tidak mengumpulkan data pribadi." (Free forever. No ads. Collects no personal data.)
- A short help section: **"Pengingat tidak muncul?"** (Reminders not showing?), with simple steps for turning off battery optimization for the app (see section 4).
- **Saran & masukan**: "Kirim masukan" opens the user's own email app, addressed to eoi.uido@gmail.com. The app itself sends nothing.
- **Aplikasi lain dari EOI**: one sentence about EOI and a "Lihat Aplikasi EOI" button that opens https://eoiuido.github.io in the browser. The list of EOI apps lives on that website, so it can grow without updating JagaDzikir.
- **Donasi (tidak wajib)**: "JagaDzikir gratis selamanya dan tanpa iklan. Jika aplikasi ini membantu Anda, Anda bisa mendukung kami melalui ..." The "..." is filled in later, **after checking Google Play's rules** about donations inside apps.

### The notification

- **Title:** Arabic text, e.g. اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ
- **Text below:** Latin, e.g. Allahumma sholli 'ala Muhammad
- **Own text:** the first line is the title, the other lines go below it.
- **No sound. No vibration.** It pops up at the top of the screen for a few seconds, then stays in the notification list (owner's request, 16 Oct 2026).
- Pop-up is the user's choice: Tentang has a **"Tampilan pengingat"** section with an "Atur Tampilan Pengingat" button that opens the phone's settings for these notifications, where "Banner" / "Pop-up" can be turned on or off. Some phone brands turn it off by default.
- Tapping it opens the app. Nothing else is needed.

---

## 3. Settings saved on the phone

These are saved only on the phone. Nothing is sent anywhere.

| Setting | Possible values | Default |
|---|---|---|
| Chosen dzikir | tahlil, hamdalah, istighfar, sholawat, sholawat_sayyidina, custom | tahlil |
| Own text | up to 200 characters | empty |
| Interval | 15, 30, 60 minutes | 30 |
| Quiet hours on? | yes / no | yes |
| Quiet hours start | a time | 22:00 |
| Quiet hours end | a time | 04:30 |
| Reminders running? | yes / no | no |

**How a setting reaches the notification:** the user taps a choice on the Home screen → the app saves it on the phone → if reminders are running, the app cancels the old schedule and sets a new one.

**How the schedule works (simple version):** the app works out every reminder time in one day, leaving out quiet hours. For example, every 1 hour from 04:30 to 22:00 gives 05:30, 06:30 … 21:30. It then tells the phone to "show this every day at 05:30", "every day at 06:30", and so on. The phone remembers these by itself, so the app does not need to be open. The most this can be is 48 times a day (every 30 minutes with quiet hours off). That is under the iPhone's limit of 64.

---

## 4. Rules and edge cases

| Situation | What the app does |
|---|---|
| **Permission denied** | Reminders stay off. The Home screen shows a friendly box: "Notifikasi dimatikan. Buka Pengaturan untuk mengizinkan." (Notifications are off. Open Settings to allow them.) There is a button that opens the phone's settings for this app. |
| **Permission turned off later** (in phone settings) | Next time the app opens, it notices and shows the same box. |
| **App closed / swiped away** | Reminders keep coming, because the phone holds the schedule, not the app. |
| **Phone restarts** | The phone restores the schedule automatically. This **must be tested** on the owner's phone. |
| **Battery saver / battery optimization** | Some Android phones (Xiaomi, Oppo, Vivo, Samsung) may delay or block reminders. The "Pengingat tidak muncul?" help section explains, in 3 simple steps, how to allow JagaDzikir. A reminder may sometimes arrive a few minutes late. This is normal for Android and acceptable. |
| **Quiet hours cross midnight** (e.g. 22:00 to 04:30) | Handled correctly: nothing between 22:00 and 04:30. |
| **Quiet start = quiet end** | Not allowed. The app shows "Jam mulai dan selesai tidak boleh sama." |
| **Quiet hours leave no reminder times** (e.g. quiet 05:00 to 04:30) | The app shows a short warning that no reminders would appear. |
| **User travels to another time zone** | Reminders follow the phone's local time. |
| **No internet** | No effect. The app never uses the internet. |

**Out of scope for version 1** (any new ideas go in `later.md`): more than one dzikir at a time, sounds, counters or statistics, accounts, English language, widgets.

---

## 5. Reminder texts: PLEASE CHECK EVERY LINE

Please check the Arabic, the Latin spelling and the meaning before release. If anything should be changed (for example, a longer sholawat or a different spelling), write the correction next to it.

| # | Name in app | Arabic (notification title) | Latin (notification text) | Indonesian meaning (shown in app only) | Owner OK? |
|---|---|---|---|---|---|
| 1 | Tahlil | لَا إِلَٰهَ إِلَّا اللَّهُ | Laa ilaaha illallah | Tiada Tuhan selain Allah | ☐ |
| 2 | Hamdalah | الْحَمْدُ لِلَّهِ | Alhamdulillah | Segala puji bagi Allah | ☐ |
| 3 | Istighfar | أَسْتَغْفِرُ اللَّهَ | Astaghfirullah | Aku memohon ampun kepada Allah | ☐ |
| 4 | Sholawat | اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ | Allahumma sholli 'ala Muhammad | Ya Allah, limpahkanlah rahmat kepada Nabi Muhammad | ☐ |
| 5 | Sholawat | اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ | Allahumma sholli 'ala sayyidina Muhammad | Ya Allah, limpahkanlah rahmat kepada junjungan kami, Nabi Muhammad | ☑ (16 Oct) |
| 6 | Isi sendiri | (written by the user) | (written by the user) | — | — |

---

## Decisions from the interview (12 Oct 2026)

- Interval: 30 minutes to 3 hours (changed on 16 Oct 2026 to 15 minutes, 30 minutes or 1 hour).
- Only **one** dzikir can be active at a time.
- Quiet hours: on by default, 22:00 to 04:30, and the user can change them.
- Notification: Arabic title + Latin text.
- App language: Indonesian only.
- App name: **JagaDzikir**.
- Icon: its own look (not EOI style): white crescent and star on green.
- No sound, no vibration.

## Technical change (12 Oct 2026, decided by the owner)

- JagaDzikir is **Android only** (Indonesian market), built as a **native Android app in Kotlin with no extra libraries**, to keep it tiny (target: under 0.5 MB) and fast on cheap phones.
- This replaces the React Native / Expo plan in the brief **for this app only**. Future EOI apps will use React Native.
- Everything else in this spec stays the same: screens, settings, rules, texts, app ID `com.eoi.jagadzikir`.

## Changes requested by the owner (16 Oct 2026)

- New dzikir list in the owner's order (6 items, the last one is the user's own text).
- Donasi placeholder in Tentang (optional, gentle wording; details later after checking Google Play's rules).
- iPhone version: moved to `later.md`.
- Own text uses one box; feedback by email added to Tentang; icon is a crescent and star (owner's request).
- Tested by the owner: reminders still arrive after a phone restart and with battery saver on.
- 15 menit added and 30 menit made the default; Laa ilaaha illallah is the default dzikir; both sholawat choices are titled "Sholawat"; wording is "dzikir dan sholawat" (owner's request).

## Small suggestions from me (tell me if you disagree)

- Showing the Indonesian meaning in small text on the Home screen, but not in the notification.
- The default interval is 1 hour (changed on 16 Oct 2026 to 30 minutes).
- Interval choices are 15 min, 30 min and 1 h.
