# RecycleHelper iOS App

An AI-powered recycling assistant that helps users identify whether items are recyclable or should go to landfill using on-device machine learning.

![Platform](https://img.shields.io/badge/platform-iOS-lightgrey)
![Swift](https://img.shields.io/badge/Swift-5.9-orange)
![iOS](https://img.shields.io/badge/iOS-15.0+-blue)
![License](https://img.shields.io/badge/license-MIT-green)

## 📱 Features

- **📷 Camera Capture**: Take photos of items directly from the app
- **🖼️ Photo Library**: Choose existing photos from your library
- **🤖 Real-time Classification**: On-device ML using Apple's Vision framework
- **♻️ Smart Categorization**: Identifies recyclable vs. landfill items
- **📊 Confidence Scores**: Shows how confident the AI is about its prediction
- **🔒 Privacy-First**: All processing happens on-device, no data sent to servers

## 🎯 How It Works

1. **Capture or Select**: Take a photo with the camera or choose from your photo library
2. **AI Analysis**: The app uses Apple's Vision framework to classify the image
3. **Get Results**: Receive instant feedback on whether the item is recyclable
4. **Learn More**: View confidence scores and detected object labels

## 🛠️ Technologies Used

- **SwiftUI**: Modern declarative UI framework
- **Vision Framework**: Apple's on-device machine learning for image classification
- **Core ML**: Machine learning models running locally on the device
- **AVFoundation**: Camera capture and photo processing
- **PhotosUI**: Photo library integration

## 📋 Requirements

- iOS 15.0 or later
- Xcode 15.0 or later
- Swift 5.9 or later
- iPhone or iPad with camera (for camera features)

## 🚀 Getting Started

### Installation

1. **Clone the repository**
   ```bash
   git clone [https://github.com/YOUR_USERNAME/RecycleHelper.git](https://github.com/djain2405/EcoSnap-RecycleHelper.git)
   cd final_iOS
   ```

2. **Open in Xcode**
   ```bash
   open RecycleHelper.xcodeproj
   ```

3. **Build and Run**
   - Select your target device or simulator
   - Press `Cmd + R` to build and run

### Running on Physical Device

**Note**: Photo library image classification works best on a **physical iOS device**. The iOS Simulator may encounter Vision framework limitations with photo library images.

To run on a physical device:
1. Connect your iPhone/iPad to your Mac
2. Select your device from the device list in Xcode
3. Ensure your Apple ID is added to Xcode (Preferences > Accounts)
4. Update the bundle identifier if needed
5. Build and run on your device

## 📁 Project Structure

```
RecycleHelper/
├── RecycleHelper/
│   ├── RecycleHelperApp.swift      # App entry point
│   ├── ContentView.swift            # Main home screen
│   ├── CameraView.swift             # Camera capture interface
│   ├── PhotoPicker.swift            # Photo library picker
│   └── RecycleClassifier.swift     # ML classification logic
├── RecycleHelper.xcodeproj/        # Xcode project
└── README.md                        # This file
```

## 🧠 How Classification Works

The app uses Apple's Vision framework with built-in image classification models to identify objects in photos. The classification pipeline:

1. **Image Normalization**: Photos are normalized to handle different orientations
2. **Vision Processing**: Uses `VNClassifyImageRequest` for object detection
3. **Label Mapping**: Maps detected labels to recycling categories
4. **Confidence Filtering**: Only shows results above a confidence threshold

### Recyclable Items Detected

The app recognizes common recyclable items including:
- Bottles (plastic, glass, water)
- Cans (aluminum, soda, tin)
- Paper and cardboard
- Containers and jars
- Cartons and boxes

### Non-Recyclable Items Detected

Common landfill items:
- Food waste (banana peels, apple cores, etc.)
- Plastic bags
- Styrofoam
- Greasy paper products
- Certain plastics

## 🎨 UI/UX

- **Clean Interface**: Minimalist design focusing on ease of use
- **Gradient Background**: Beautiful green-to-blue gradient
- **Animated Results**: Smooth transitions when showing classification results
- **Loading States**: Clear feedback during image processing
- **Accessibility**: Designed with VoiceOver support

## 🔧 Customization

### Adjusting Confidence Threshold

Edit `RecycleClassifier.swift`:
```swift
private let confidenceThreshold: Float = 0.3  // Adjust between 0.0 and 1.0
```

### Adding New Recyclable Keywords

Edit the `recyclableKeywords` array in `RecycleClassifier.swift`:
```swift
let recyclableKeywords = [
    "bottle", "can", "jar", "paper", "cardboard",
    // Add your keywords here
]
```

## 🐛 Known Issues

- **Simulator Limitation**: Photo library classification may fail in iOS Simulator with "Failed to create espresso context" error. This is a known Vision framework limitation. **Use a physical device for testing photo library features.**
- Camera features work fine in both simulator and physical devices

## 📚 Learning Resources

This app was created as an educational project demonstrating:
- SwiftUI best practices
- Vision framework integration
- Camera and photo library access
- On-device machine learning
- Modern iOS app architecture

---

# ♻️ Recycle Helper - Android Version

> **IEEE Workshop Demo - On-Device ML with TensorFlow Lite and Jetpack Compose**

✅ **BUILD STATUS: SUCCESS** - App is ready to run!

This is the Android version of the Recycle Helper app, matching the iOS version feature-for-feature.

---

## 📱 What This App Does

Take a photo or choose from gallery → Real AI classification using TensorFlow Lite MobileNet:
- **♻️ Likely Recyclable** (bottles, cans, paper, cardboard)
- **🗑️ Probably Landfill** (food, plastic bags, non-recyclables)
- **❓ Not Sure** (low confidence - check local guidelines)

### ✨ Features
- 📷 **Camera Capture** - Take photos directly in the app
- 🖼️ **Photo Picker** - Select images from your gallery
- 🤖 **Real ML Classification** - TensorFlow Lite with MobileNet V1 (4.1 MB)
- 📊 **Confidence Scores** - See how confident the AI is
- 🔒 **Privacy-First** - All processing happens on-device

---

## 🚀 Quick Start

### Prerequisites

- **Android Studio** (Latest version - Arctic Fox or newer)
- **Android Device** or Emulator (API 24+ / Android 7.0+)
- **Physical device recommended** (camera required)

### Setup Steps

1. **Open in Android Studio**
   ```bash
   File → Open → Select RecycleHelper folder
   ```

2. **Wait for Gradle Sync**
   - Android Studio will automatically download dependencies
   - This may take 2-5 minutes on first run

3. **Connect your Android device**
   - Enable USB Debugging on your phone
   - Connect via USB
   - Grant USB debugging permission when prompted

4. **Run the app**
   - Click the green Run button (▶️)
   - Select your device
   - Grant camera permission when asked

---

## 📁 Project Structure

```
RecycleHelper/
├── app/
│   ├── build.gradle.kts              # App-level dependencies
│   └── src/main/
│       ├── java/com/workshop/recyclehelper/
│       │   ├── MainActivity.kt       # App entry point
│       │   ├── RecycleHelperApp.kt   # Main app composable
│       │   ├── models/
│       │   │   └── RecycleModels.kt  # Data models
│       │   ├── ml/
│       │   │   └── RecycleClassifier.kt  # ML classification logic
│       │   └── ui/
│       │       ├── screens/
│       │       │   ├── HomeScreen.kt     # Home screen UI
│       │       │   └── CameraScreen.kt   # Camera screen UI
│       │       └── theme/
│       │           ├── Theme.kt          # App theme
│       │           └── Type.kt           # Typography
│       ├── AndroidManifest.xml       # App configuration & permissions
│       └── res/                      # Resources (strings, themes, etc.)
├── build.gradle.kts                  # Project-level config
└── settings.gradle.kts               # Project settings
```

---

## 🎨 Architecture

### Tech Stack

- **UI Framework:** Jetpack Compose (declarative UI like SwiftUI)
- **Camera:** CameraX (modern camera API)
- **ML Framework:** TensorFlow Lite (on-device inference)
- **Permissions:** Accompanist Permissions
- **Language:** Kotlin

### Component Breakdown

#### 1. **MainActivity.kt** (~40 lines)
- Entry point for the app
- Sets up Compose UI
- Like `SceneDelegate` in iOS

#### 2. **RecycleHelperApp.kt** (~100 lines)
- Root composable
- Handles navigation (Home ↔ Camera)
- Manages app state
- Handles camera permissions
- Like iOS `ContentView` with navigation

#### 3. **HomeScreen.kt** (~250 lines)
- Main landing screen
- Shows "Take Photo" button
- Displays classification results
- Loading overlay
- Like iOS `HomeScreen`

#### 4. **CameraScreen.kt** (~200 lines)
- Full-screen camera preview
- Circular capture button
- Uses CameraX for camera operations
- Like iOS `CameraView` with AVFoundation

#### 5. **RecycleClassifier.kt** (~250 lines)
- **Real ML classification** using TensorFlow Lite
- Loads MobileNet V1 model from assets
- Processes images at 224x224 resolution
- Maps ImageNet labels to recycling categories
- Like iOS `RecycleClassifier` with Vision framework

#### 6. **RecycleModels.kt** (~70 lines)
- Data models: `RecycleCategory`, `RecycleResult`
- Like iOS models (enums & structs)

---

### Key Learning Points

#### 1. **Jetpack Compose vs SwiftUI**

**SwiftUI:**
```swift
struct ContentView: View {
    var body: some View {
        Text("Hello")
    }
}
```

**Compose:**
```kotlin
@Composable
fun ContentView() {
    Text("Hello")
}
```

**Similarities:**
- Both are declarative UI frameworks
- State-driven (recompose when state changes)
- Composable/View hierarchies
- Preview support

#### 2. **CameraX vs AVFoundation**

**iOS (AVFoundation):**
```swift
let session = AVCaptureSession()
let camera = AVCaptureDevice.default()
let input = try AVCaptureDeviceInput(device: camera)
session.addInput(input)
session.startRunning()
```

**Android (CameraX):**
```kotlin
val cameraProvider = ProcessCameraProvider.getInstance()
val preview = Preview.Builder().build()
val imageCapture = ImageCapture.Builder().build()
cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
```

**CameraX is simpler:**
- Less boilerplate
- Automatic lifecycle handling
- Built-in use cases

#### 3. **TensorFlow Lite vs Core ML**

| Feature | Core ML (iOS) | TensorFlow Lite (Android) |
|---------|---------------|---------------------------|
| **Model Format** | `.mlmodel` | `.tflite` |
| **Framework** | Vision + Core ML | TF Lite Support |
| **Built-in Models** | Yes (Vision API) | No (must add manually) |
| **Size** | Larger | Smaller (quantized) |
| **Speed** | Fast (Neural Engine) | Fast (NNAPI) |

---

## 🧪 Experiments for Students

### Experiment 1: Confidence Threshold

**File:** `RecycleClassifier.kt` line 19

**Try these values:**
```kotlin
private const val CONFIDENCE_THRESHOLD = 0.5f  // More "sure" answers
private const val CONFIDENCE_THRESHOLD = 0.7f  // Balanced (default)
private const val CONFIDENCE_THRESHOLD = 0.9f  // More "not sure"
```

**Rebuild and test:**
- How does it affect "Not Sure" rate?
- Which threshold feels right?

### Experiment 2: Add Custom Keywords

**File:** `RecycleClassifier.kt` line 138

**Add your own items:**
```kotlin
val recyclableKeywords = listOf(
    "bottle", "can", "jar", "paper", "cardboard", "box",
    "cup",      // Add this!
    "mug",      // Add this!
    "notebook"  // Add this!
)
```

**Test:** Point camera at a cup - does it classify correctly?

### Experiment 3: UI Customization

**File:** `HomeScreen.kt`

**Change colors:**
- Line 30: Gradient background colors
- Line 107: Button color

**Change text:**
- Line 67: App title
- Line 72: Subtitle

### Experiment 4: Camera Settings

**File:** `CameraScreen.kt` line 179

**Try different capture modes:**
```kotlin
val imageCapture = ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)  // Fast
    // .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)  // Quality
    .build()
```

---

## 🔧 TensorFlow Lite Model

The app uses **real TensorFlow Lite inference** with MobileNet V1:
- **Model File**: `mobilenet_v1_1.0_224_quant.tflite` (4.1 MB)
- **Labels**: 1001 ImageNet classes
- **Location**: `app/src/main/assets/`
- **Format**: Quantized (uint8) for efficiency

### How It Works

1. **Image Preprocessing**: Resizes image to 224x224 pixels
2. **ByteBuffer Conversion**: Converts bitmap to uint8 RGB values
3. **Inference**: Runs TensorFlow Lite interpreter
4. **Label Mapping**: Maps ImageNet labels to recycling categories
5. **Results**: Returns category with confidence score

### Classification Pipeline

```
Photo/Camera → Resize (224x224) → Convert to ByteBuffer →
TFLite Model → Top Result → Map Label → Recyclable/Landfill
```

---

## 📊 iOS vs Android Comparison

| Feature | iOS (SwiftUI) | Android (Compose) |
|---------|---------------|-------------------|
| **UI Framework** | SwiftUI | Jetpack Compose |
| **Language** | Swift | Kotlin |
| **ML Framework** | Core ML + Vision | TensorFlow Lite |
| **Camera API** | AVFoundation | CameraX |
| **Permissions** | Info.plist | AndroidManifest.xml |
| **State Management** | @State, @Published | remember, mutableStateOf |
| **Preview** | #Preview | @Preview |
| **Navigation** | NavigationView | Composable switching |
| **Model Size** | ~14MB (MobileNetV2) | ~4MB (quantized) |

**Code Similarity:** ~90% identical in structure!

---

## 🐛 Troubleshooting

### Gradle Sync Failed
- Check internet connection
- File → Invalidate Caches → Restart
- Delete `.gradle` folder and re-sync

### Camera Not Working
- ✅ Running on physical device (not emulator)?
- ✅ Camera permission granted?
- ✅ AndroidManifest.xml has CAMERA permission?

### App Crashes on Launch
- Check Logcat for error messages
- Common issue: Missing AndroidManifest entries
- Try: Build → Clean Project → Rebuild

### Dependencies Not Found
- Check `build.gradle.kts` versions
- Update Gradle plugin if needed
- Sync project with Gradle files

---

## 🎯 For Production Apps

To make this production-ready:

1. **Train Custom Model**
   - Collect 500+ images per class (recyclable/landfill)
   - Use TensorFlow or AutoML
   - Export as `.tflite`

2. **Add Real Classification**
   - Replace simulated inference with actual TF Lite

3. **Improve UI**
   - Add history/stats
   - Add settings for threshold tuning
   - Add onboarding tutorial

4. **Localization**
   - Support multiple languages
   - Regional recycling rules

5. **Testing**
   - Unit tests for classifier
   - UI tests with Compose Testing
   - Test on various devices

---

## 📚 Resources

### Official Documentation
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [CameraX](https://developer.android.com/training/camerax)
- [TensorFlow Lite](https://www.tensorflow.org/lite)
- [Kotlin](https://kotlinlang.org/)

### Tutorials
- [Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [CameraX Codelab](https://developer.android.com/codelabs/camerax-getting-started)
- [TF Lite Codelab](https://codelabs.developers.google.com/codelabs/recognize-flowers-with-tensorflow-on-android)

### Datasets for Training
- **TACO Dataset** - Trash annotations (1500+ images)
- **TrashNet** - 2527 images of trash
- **Kaggle Recycling** - Multiple datasets

---

## ❓ FAQ

**Q: Does this use real ML or is it simulated?**

**A:** Real ML! The app uses TensorFlow Lite with MobileNet V1 for actual on-device inference.

**Q: Can I use this with iOS?**

**A:** Yes! Check the iOS version in the `RecycleHelper-iOS` folder. The architecture is nearly identical.

**Q: How do I test without a phone?**

**A:** Use Android Emulator with webcam passthrough (slower, but works for testing).

**Q: Is this production-ready?**

**A:** The code quality is production-ready, but you might need a custom-trained model for real use.

**Q: Can I publish this to Play Store?**

**A:** Yes, but add:
- Privacy policy
- Disclaimers about accuracy
- Proper model attribution
- Terms of service

---

## 🙏 Credits

**Created for:** IEEE Technical Workshop on On-Device ML

**Frameworks:** Jetpack Compose, CameraX, TensorFlow Lite

**License:** Educational use - modify freely!


---

## 🆚 Side-by-Side: iOS vs Android

### File Count
- iOS: ~600 lines across 4 files
- Android: ~900 lines across 8 files

### Learning Curve
- iOS: SwiftUI (easier if you know Swift)
- Android: Compose + Kotlin (slight learning curve)

### Model Integration
- iOS: Core ML is seamless (built-in models)
- Android: TF Lite requires manual setup

### Camera API
- iOS: AVFoundation (more boilerplate)
- Android: CameraX (cleaner, simpler)

**Both are great for teaching on-device ML! 🎉**

---

**Happy Coding! 🚀♻️📱**


## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Built with Apple's Vision framework
- Inspired by environmental sustainability efforts
- Created for IEEE Workshop on On-Device ML

## 📬 Contact

For questions or feedback, please open an issue on GitHub.

---

**Note**: This is an educational project. For accurate recycling information, always check with your local recycling facility as guidelines vary by location.
