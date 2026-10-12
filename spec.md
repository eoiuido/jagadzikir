# spec.md: JagaDzikir (EOI app #1)

Status: **APPROVED by the owner on 12 Oct 2026.** The religious texts in section 5 must still be checked line by line before release.

---

## 1. Premise

- **Who:** Muslims in Indonesia with busy days.
- **Problem:** During a busy day it is easy to forget to recite sholawat and dzikir.
- **Promise:** Pick one dzikir and how often. JagaDzikir quietly shows it on your phone, all day, with no sound, no ads and no cost. Forever.

---

## 2. User flow, screen by screen

The app has **2 screens**. All text in the app is in **Indonesian**.

### Screen 1: Home (the only screen most people will use)

From top to bottom:

1. **Pick one dzikir** (only one can be chosen at a time). Each choice shows the Arabic, the Latin and a short Indonesian meaning in small text, so the user knows what they are choosing.
   - Sholawat (selected by default)
   - Laa ilaaha illallah
   - Alhamdulillah
   - Astaghfirullah
2. **How often** ("Setiap"): 30 menit · 1 jam · 1,5 jam · 2 jam · 3 jam. The default is **1 jam**.
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

### The notification

- **Title:** Arabic text, e.g. اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ
- **Text below:** Latin, e.g. Allahumma sholli 'ala Muhammad
- **No sound. No vibration.** It appears quietly on the screen and in the notification list.
- Tapping it opens the app. Nothing else is needed.

---

## 3. Settings saved on the phone

These are saved only on the phone. Nothing is sent anywhere.

| Setting | Possible values | Default |
|---|---|---|
| Chosen dzikir | sholawat, tahlil, hamdalah, istighfar | sholawat |
| Interval | 30, 60, 90, 120, 180 minutes | 60 |
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
| 1 | Sholawat | اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ | Allahumma sholli 'ala Muhammad | Ya Allah, limpahkanlah rahmat kepada Nabi Muhammad | ☐ |
| 2 | Tahlil | لَا إِلَٰهَ إِلَّا اللَّهُ | Laa ilaaha illallah | Tiada Tuhan selain Allah | ☐ |
| 3 | Hamdalah | الْحَمْدُ لِلَّهِ | Alhamdulillah | Segala puji bagi Allah | ☐ |
| 4 | Istighfar | أَسْتَغْفِرُ اللَّهَ | Astaghfirullah | Aku memohon ampun kepada Allah | ☐ |

---

## Decisions from the interview (12 Oct 2026)

- Interval: 30 minutes to 3 hours.
- Only **one** dzikir can be active at a time.
- Quiet hours: on by default, 22:00 to 04:30, and the user can change them.
- Notification: Arabic title + Latin text.
- App language: Indonesian only.
- App name: **JagaDzikir**.
- Icon: its own look (not EOI style). Design still to be done.
- No sound, no vibration.

## Technical change (12 Oct 2026, decided by the owner)

- JagaDzikir is **Android only** (Indonesian market), built as a **native Android app in Kotlin with no extra libraries**, to keep it tiny (target: under 0.5 MB) and fast on cheap phones.
- This replaces the React Native / Expo plan in the brief **for this app only**. Future EOI apps will use React Native.
- Everything else in this spec stays the same: screens, settings, rules, texts, app ID `com.eoi.jagadzikir`.

## Small suggestions from me (tell me if you disagree)

- Showing the Indonesian meaning in small text on the Home screen, but not in the notification.
- The default interval is 1 hour.
- Interval choices are 30 min, 1 h, 1.5 h, 2 h and 3 h.
