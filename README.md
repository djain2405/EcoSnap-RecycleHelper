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
   git clone https://github.com/YOUR_USERNAME/RecycleHelper.git
   cd RecycleHelper
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
