# later.md: JagaDzikir ideas for after version 1

## iPhone version
- Needs a separate small app written in Apple's language (Swift); the Android code cannot run on iPhone.
- The owner has an iPhone but no Mac. Without a Mac, testing on the iPhone is only possible through TestFlight, which needs the **Apple Developer Program (US$99/year)**. Publishing on the App Store needs it too.
- Building can happen on a cloud Mac (for example GitHub's macOS build machines).
- iPhones keep at most 64 scheduled notifications. With 15-minute reminders JagaDzikir can need up to 96 per day, so the iPhone version needs a different scheduling approach (for example scheduling only the next few hours and refreshing when the app opens).
- Decide after the Android launch.


## Web version (PWA), discussed 28 Oct 2026, not chosen for now
- Would be a separate app (JagaDzikir is native Android, not Expo).
- Web reminders need a server sending web push, so JagaDzikir would no longer be "no server, collects nothing": the Privacy Policy and Play data answers would change.
- iPhone: works only after "Add to Home Screen" (iOS 16.4+); Apple controls whether it is silent.
- Lighter alternative for sharing before Play launch: a JagaDzikir page at eoi-apps.my.id/jagadzikir ("Segera hadir di Google Play"), later the download page.
- Never share the APK in public posts (APK scams in Indonesia; Google developer verification for sideloaded apps in Indonesia).
