import SwiftUI

/// ContentView is the main screen of the app
/// Shows a clean home screen with a button to launch the camera
struct ContentView: View {

    // MARK: - State Properties

    /// The ML classifier that processes images
//    @StateObject private var classifier = RecycleClassifier()

    /// Controls whether to show the camera
    @State private var showCamera = false

    /// Controls whether to show the photo picker
    @State private var showPhotoPicker = false

    /// The captured image to display
    @State private var capturedImage: UIImage?

    /// Reference to camera coordinator for triggering capture
    @State private var cameraCoordinator: CameraView.Coordinator?

    // MARK: - Body

    var body: some View {
        NavigationView {
            ZStack {
                // Background gradient
                LinearGradient(
                    gradient: Gradient(colors: [Color.green.opacity(0.6), Color.blue.opacity(0.4)]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()

                VStack(spacing: 30) {
                    Spacer()

                    // App Icon/Logo
                    Text("♻️")
                        .font(.system(size: 100))

                    // Title
                    VStack(spacing: 10) {
                        Text("Recycle Helper")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                            .foregroundColor(.white)

                        Text("AI-Powered Recycling Assistant")
                            .font(.subheadline)
                            .foregroundColor(.white.opacity(0.9))
                    }

                    Spacer()

                    // Show result if available
                    if let result = classifier.result {
                        resultCard(result: result)
                            .transition(.scale.combined(with: .opacity))
                    }

                    // Show captured image thumbnail if available
                    if let image = capturedImage, classifier.result == nil {
                        VStack {
                            Image(uiImage: image)
                                .resizable()
                                .scaledToFit()
                                .frame(height: 200)
                                .cornerRadius(15)
                                .shadow(radius: 5)

                            Text("Processing...")
                                .foregroundColor(.white)
                                .font(.caption)
                        }
                        .transition(.scale)
                    }

                    Spacer()

                    // Action Buttons
                    VStack(spacing: 16) {
                        // Take Photo Button
                        Button(action: {
                            // Clear previous results
                            classifier.result = nil
                            capturedImage = nil
                            // Show camera
                            showCamera = true
                        }) {
                            HStack(spacing: 12) {
                                Image(systemName: "camera.fill")
                                    .font(.title2)

                                Text("Take Photo to Classify")
                                    .font(.headline)
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(
                                LinearGradient(
                                    gradient: Gradient(colors: [Color.blue, Color.blue.opacity(0.8)]),
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .cornerRadius(15)
                            .shadow(color: .black.opacity(0.3), radius: 10, x: 0, y: 5)
                        }
                        .disabled(classifier.isProcessing)
                        .opacity(classifier.isProcessing ? 0.6 : 1.0)

                        // Choose from Library Button
                        Button(action: {
                            // Clear previous results
                            classifier.result = nil
                            capturedImage = nil
                            // Show photo picker
                            showPhotoPicker = true
                        }) {
                            HStack(spacing: 12) {
                                Image(systemName: "photo.fill")
                                    .font(.title2)

                                Text("Choose from Photos")
                                    .font(.headline)
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(
                                LinearGradient(
                                    gradient: Gradient(colors: [Color.purple, Color.purple.opacity(0.8)]),
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .cornerRadius(15)
                            .shadow(color: .black.opacity(0.3), radius: 10, x: 0, y: 5)
                        }
                        .disabled(classifier.isProcessing)
                        .opacity(classifier.isProcessing ? 0.6 : 1.0)
                    }
                    .padding(.horizontal, 40)

                    // Workshop Info
                    VStack(spacing: 4) {
                        Text("IEEE Workshop Demo")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.7))

                        Text("On-Device ML with Core ML")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.7))
                    }
                    .padding(.bottom, 20)
                }

                // Loading overlay
                if classifier.isProcessing {
                    loadingView
                }
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showCamera) {
                CameraViewSheet(
                    cameraCoordinator: $cameraCoordinator,
                    onPhotoTaken: { image in
                        capturedImage = image
                        showCamera = false
                        // Classify the image
                        classifier.classify(image: image)
                    },
                    onCancel: {
                        showCamera = false
                    }
                )
            }
            .sheet(isPresented: $showPhotoPicker) {
                PhotoPicker(
                    onPhotoSelected: { image in
                        capturedImage = image
                        // Classify the image
                        classifier.classify(image: image)
                    },
                    onDismiss: {
                        showPhotoPicker = false
                    }
                )
            }
        }
        .animation(.easeInOut, value: classifier.result)
        .animation(.easeInOut, value: classifier.isProcessing)
        .animation(.easeInOut, value: capturedImage)
    }

    // MARK: - View Components

    /// Loading indicator shown during classification
    private var loadingView: some View {
        ZStack {
            Color.black.opacity(0.5)
                .ignoresSafeArea()

            VStack(spacing: 20) {
                ProgressView()
                    .scaleEffect(1.5)
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))

                Text("Analyzing...")
                    .font(.headline)
                    .foregroundColor(.white)

                Text("Using on-device AI")
                    .font(.caption)
                    .foregroundColor(.white.opacity(0.8))
            }
            .padding(40)
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .fill(Color.black.opacity(0.7))
            )
        }
    }

    /// Result card showing the classification
    @ViewBuilder
    private func resultCard(result: RecycleResult) -> some View {
        VStack(spacing: 16) {
            // Category with icon
            Text(result.category.displayName)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(Color(result.category.color))

            // Confidence level
            VStack(spacing: 8) {
                Text("Confidence: \(result.confidencePercentage)%")
                    .font(.subheadline)
                    .foregroundColor(.white)

                // Confidence bar
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Rectangle()
                            .fill(Color.white.opacity(0.3))
                            .frame(height: 6)
                            .cornerRadius(3)

                        Rectangle()
                            .fill(Color(result.category.color))
                            .frame(width: geometry.size.width * CGFloat(result.confidence), height: 6)
                            .cornerRadius(3)
                    }
                }
                .frame(height: 6)
            }

            // Helper message
            Text(result.category.message)
                .font(.caption)
                .multilineTextAlignment(.center)
                .foregroundColor(.white)
                .padding(.horizontal)

            Divider()
                .background(Color.white.opacity(0.3))

            // Technical details
            VStack(alignment: .leading, spacing: 6) {
                Text("🔍 Detected: \(result.detectedLabel)")
                    .font(.caption2)
                    .foregroundColor(.white.opacity(0.9))

                Text("Via Apple Vision Framework")
                    .font(.caption2)
                    .italic()
                    .foregroundColor(.white.opacity(0.7))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 20)
                .fill(Color.black.opacity(0.4))
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.white.opacity(0.3), lineWidth: 1)
                )
        )
        .padding(.horizontal, 30)
    }
}

// MARK: - Camera View Sheet

/// A sheet that presents the camera with a capture button
struct CameraViewSheet: View {
    @Binding var cameraCoordinator: CameraView.Coordinator?
    var onPhotoTaken: (UIImage) -> Void
    var onCancel: () -> Void

    var body: some View {
        ZStack {
            // Camera preview
            CameraView(
                coordinator: $cameraCoordinator,
                onPhotoTaken: onPhotoTaken
            )
            .ignoresSafeArea()

            // Camera UI Overlay
            VStack {
                // Top bar with cancel button
                HStack {
                    Button(action: onCancel) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title)
                            .foregroundColor(.white)
                            .shadow(color: .black.opacity(0.5), radius: 5)
                    }
                    .padding()

                    Spacer()
                }

                Spacer()

                // Instructions
                VStack(spacing: 8) {
                    Text("Point camera at an item")
                        .font(.headline)
                        .foregroundColor(.white)
                        .shadow(color: .black.opacity(0.7), radius: 3)

                    Text("Tap anywhere to capture")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.9))
                        .shadow(color: .black.opacity(0.7), radius: 3)
                }
                .padding(.bottom, 20)

                // Capture Button
                CaptureButton {
                    // Trigger camera capture via coordinator
                    cameraCoordinator?.capturePhoto()
                }
                .padding(.bottom, 40)
            }
        }
    }
}

// MARK: - Capture Button

/// The circular capture button (like iOS Camera app)
struct CaptureButton: View {
    var onCapture: () -> Void

    var body: some View {
        Button(action: onCapture) {
            ZStack {
                // Outer ring
                Circle()
                    .stroke(Color.white, lineWidth: 5)
                    .frame(width: 80, height: 80)

                // Inner circle
                Circle()
                    .fill(Color.white)
                    .frame(width: 65, height: 65)
            }
        }
    }
}

// MARK: - Preview

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
