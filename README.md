# Color Detective 🎨

A fun and educational Android app for kids aged 3-6 to learn colors through interactive camera-based gameplay.

## 📱 About the App

Color Detective is an engaging mobile game that helps young children learn colors by using their device's camera. Kids are given missions to find specific colors in their environment, making learning interactive and fun!

### Key Features

- **Real-time Color Detection**: Uses the phone camera to detect colors in real-time
- **Mission System**: Random color challenges like "Find something red!"
- **Visual Learning**: Color names are displayed in their actual colors (e.g., "red" appears in red)
- **Star Rewards**: Earn up to 5 stars by finding colors
- **Countdown Timer**: 30-second timer with visual countdown and sound alerts
- **Fun Sounds**: Playful sound effects when colors are found or time runs out
- **Smart Pause**: Timer automatically pauses when app goes to background or screen locks
- **Kid-Friendly UI**: Bright colors, bold fonts, and engaging animations

## 🎮 How to Play

1. **Launch the App**: Open Color Detective on your Android device
2. **Grant Camera Permission**: Allow camera access when prompted
3. **Read Your Mission**: The app will show a mission like "Find something red!"
4. **Find the Color**: Point your camera at objects matching the target color
5. **Earn Stars**: When you find the color, earn a star and hear a success sound!
6. **Complete Missions**: Find 5 colors to earn all stars and complete the game

### Tips for Kids

- Look for solid color objects (toys, books, clothes, etc.)
- The color can be anywhere in the camera view - you don't need to fill the whole screen
- When the timer turns red (last 5 seconds), you'll hear beeps to help you hurry!
- If you can't find the color in time, don't worry - a new color will appear automatically

## 🛠️ Technical Requirements

- **Android Version**: API Level 16 (Android 4.1) or higher
- **Hardware**: Device with camera support
- **Permissions**: Camera permission (requested on first launch)

## 🚀 Building and Running the App

### Prerequisites

- Android Studio (latest version recommended)
- Android SDK with API Level 16+
- Physical Android device or emulator with camera support

### Setup Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd sr-color-detective
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `sr-color-detective` folder
   - Wait for Gradle sync to complete

3. **Connect Your Device**
   - Enable USB debugging on your Android device:
     - Go to Settings → About Phone
     - Tap "Build Number" 7 times
     - Go back → Developer Options → Enable "USB Debugging"
   - Connect device via USB cable
   - Verify connection: `adb devices`

4. **Build and Install**

   **Option A: Using Android Studio**
   - Click the green "Run" button (or press Shift+F10)
   - Select your connected device
   - Click "OK"

   **Option B: Using Command Line**
   ```bash
   ./gradlew installDebug
   ```

   **Option C: Using Quick Install Script**
   ```bash
   ./quick-install.sh
   ```

5. **Launch the App**
   - The app will automatically launch after installation
   - Or find "Color Detective" in your app drawer and tap it

### Building APK

To build a debug APK:
```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## 📋 Configuration

Game settings can be customized in `app/src/main/res/values/integers.xml`:

- `max_stars`: Number of stars needed to complete (default: 5)
- `countdown_time_seconds`: Timer duration (default: 30)
- `countdown_warning_seconds`: When timer turns red (default: 5)
- `success_cooldown_milliseconds`: Delay between matches (default: 2000)

## 🐛 Troubleshooting

### App Crashes on Launch
- Ensure camera permission is granted
- Check that device has camera hardware
- Verify Android version is 4.1+ (API 16+)

### Camera Not Working
- Grant camera permission in Settings → Apps → Color Detective → Permissions
- Close other apps that might be using the camera
- Restart the app

### Device Not Detected (for development)
```bash
# Check ADB connection
adb devices

# Restart ADB if needed
adb kill-server
adb start-server
```

### Build Errors
- Sync Gradle: File → Sync Project with Gradle Files
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project

## 📱 Testing on Device

### View Logs
```bash
# Real-time logs
adb logcat | grep -i colordetective

# Errors only
adb logcat *:E | grep -i colordetective
```

### Common Commands
```bash
# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall app
adb uninstall com.sr.colordetective

# Launch app
adb shell am start -n com.sr.colordetective/.MainActivity

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

## 🏗️ Project Structure

```
sr-color-detective/
├── app/
│   ├── src/main/
│   │   ├── java/com/sr/colordetective/
│   │   │   ├── MainActivity.kt      # Main game logic
│   │   │   └── ColorAnalyzer.kt     # Color detection engine
│   │   ├── res/
│   │   │   ├── layout/               # UI layouts
│   │   │   ├── values/               # Strings, colors, configs
│   │   │   └── drawable/             # Icons and graphics
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 🎨 Technologies Used

- **Language**: Kotlin
- **Camera**: CameraX library
- **UI**: Material Design Components
- **Architecture**: Single Activity with real-time image analysis
- **Min SDK**: API 16 (Android 4.1)
- **Target SDK**: API 34

## 📝 Development Notes

- Color detection analyzes the entire camera frame, not just the center
- Timer automatically pauses when app goes to background
- 500ms cooldown prevents rapid-fire color matches
- All game settings are externalized in `integers.xml` for easy customization

## 🤝 Contributing

This is a personal project, but suggestions and improvements are welcome!

## 📄 License

This project is for educational purposes.

---

**Enjoy playing Color Detective! 🎨✨**
