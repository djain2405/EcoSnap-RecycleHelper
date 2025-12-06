import SwiftUI
import AVFoundation

/// CameraView handles the camera preview and photo capture
/// This is a UIViewControllerRepresentable that wraps AVFoundation camera functionality
struct CameraView: UIViewControllerRepresentable {

    // MARK: - Properties

    /// Binding to expose the coordinator for external control
    @Binding var coordinator: Coordinator?

    /// Callback that gets triggered when a photo is taken
    /// The UIImage is passed back to the parent view
    var onPhotoTaken: (UIImage) -> Void

    // MARK: - Initializers

    /// Create camera view with coordinator binding
    init(coordinator: Binding<Coordinator?>, onPhotoTaken: @escaping (UIImage) -> Void) {
        self._coordinator = coordinator
        self.onPhotoTaken = onPhotoTaken
    }

    // MARK: - UIViewControllerRepresentable Methods

    /// Creates the camera view controller
    func makeUIViewController(context: Context) -> CameraViewController {
        let controller = CameraViewController()
        controller.photoCallback = onPhotoTaken

        // Store coordinator reference
        context.coordinator.controller = controller

        // Pass coordinator back to parent
        DispatchQueue.main.async {
            self.coordinator = context.coordinator
        }

        return controller
    }

    /// Updates the view controller (not needed for this simple camera)
    func updateUIViewController(_ uiViewController: CameraViewController, context: Context) {
        // No updates needed for this simple implementation
    }

    /// Creates the coordinator to manage communication
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    /// Coordinator class to bridge SwiftUI and UIKit
    class Coordinator {
        var controller: CameraViewController?

        func capturePhoto() {
            print("📸 Coordinator: capturePhoto() called")
            controller?.capturePhoto()
        }
    }
}

// MARK: - CameraViewController

/// The actual UIKit view controller that manages the camera session
class CameraViewController: UIViewController {

    // MARK: - Properties

    /// The AVFoundation capture session that manages camera input/output
    private var captureSession: AVCaptureSession?

    /// The preview layer that displays what the camera sees
    private var previewLayer: AVCaptureVideoPreviewLayer?

    /// The output that captures still photos
    private var photoOutput: AVCapturePhotoOutput?

    /// Callback to send captured photos back to SwiftUI
    var photoCallback: ((UIImage) -> Void)?

    /// Gesture recognizer for tap-to-capture
    private var tapGesture: UITapGestureRecognizer?

    // MARK: - Lifecycle Methods

    override func viewDidLoad() {
        super.viewDidLoad()
        print("📷 CameraViewController: viewDidLoad")
        setupCamera()
        setupTapGesture()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("📷 CameraViewController: viewWillAppear")
        // Start the camera when the view appears
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            self?.captureSession?.startRunning()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        print("📷 CameraViewController: viewWillDisappear")
        // Stop the camera when the view disappears to save battery
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            self?.captureSession?.stopRunning()
        }
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        // Make sure the preview layer fills the entire view
        previewLayer?.frame = view.bounds
    }

    // MARK: - Camera Setup

    /// Sets up the entire camera pipeline: input -> session -> output -> preview
    private func setupCamera() {
        print("📷 Setting up camera...")

        // 1. Create a new capture session
        let session = AVCaptureSession()
        session.sessionPreset = .photo // High quality photo preset

        // 2. Get the back camera device
        guard let camera = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back) else {
            print("❌ Could not access camera")
            return
        }

        // 3. Create an input from the camera device
        guard let input = try? AVCaptureDeviceInput(device: camera) else {
            print("❌ Could not create camera input")
            return
        }

        // 4. Add the input to the session
        if session.canAddInput(input) {
            session.addInput(input)
        }

        // 5. Create a photo output to capture still images
        let output = AVCapturePhotoOutput()
        if session.canAddOutput(output) {
            session.addOutput(output)
        }
        self.photoOutput = output

        // 6. Create a preview layer so we can see what the camera sees
        let preview = AVCaptureVideoPreviewLayer(session: session)
        preview.videoGravity = .resizeAspectFill // Fill the screen
        preview.frame = view.bounds
        view.layer.addSublayer(preview)

        // Store references
        self.previewLayer = preview
        self.captureSession = session

        // 7. Start the session on a background thread (camera operations can be slow)
        DispatchQueue.global(qos: .userInitiated).async {
            session.startRunning()
            print("✅ Camera session started")
        }
    }

    /// Sets up tap gesture to capture photo
    private func setupTapGesture() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(handleTap))
        view.addGestureRecognizer(tap)
        self.tapGesture = tap
        print("✅ Tap gesture recognizer added")
    }

    /// Handles tap gesture
    @objc private func handleTap() {
        print("👆 Screen tapped!")
        capturePhoto()
    }

    // MARK: - Public Methods

    /// Captures a photo from the camera
    /// This method is called when the user taps the capture button or taps the screen
    func capturePhoto() {
        print("📸 capturePhoto() called")

        guard let photoOutput = photoOutput else {
            print("❌ Photo output not available")
            return
        }

        // Provide haptic feedback
        let generator = UIImpactFeedbackGenerator(style: .medium)
        generator.impactOccurred()
        print("📳 Haptic feedback triggered")

        // Create photo settings
        let settings = AVCapturePhotoSettings()

        // Add flash animation
        animateFlash()

        // Take the photo
        photoOutput.capturePhoto(with: settings, delegate: self)
        print("📸 Photo capture triggered")
    }

    /// Animates a camera flash effect
    private func animateFlash() {
        print("✨ Flash animation started")
        let flashView = UIView(frame: view.bounds)
        flashView.backgroundColor = .white
        flashView.alpha = 0
        view.addSubview(flashView)

        UIView.animate(withDuration: 0.1, animations: {
            flashView.alpha = 1
        }) { _ in
            UIView.animate(withDuration: 0.2, animations: {
                flashView.alpha = 0
            }) { _ in
                flashView.removeFromSuperview()
                print("✨ Flash animation completed")
            }
        }
    }
}

// MARK: - AVCapturePhotoCaptureDelegate

/// Extension to handle photo capture callbacks
extension CameraViewController: AVCapturePhotoCaptureDelegate {

    /// This gets called when a photo is successfully captured
    func photoOutput(_ output: AVCapturePhotoOutput,
                    didFinishProcessingPhoto photo: AVCapturePhoto,
                    error: Error?) {

        print("📸 Photo processing callback received")

        // Check for errors
        if let error = error {
            print("❌ Error capturing photo: \(error.localizedDescription)")
            return
        }

        // Get the image data from the photo
        guard let imageData = photo.fileDataRepresentation(),
              let image = UIImage(data: imageData) else {
            print("❌ Could not convert photo to UIImage")
            return
        }

        print("✅ Photo captured successfully! Size: \(image.size)")

        // Send the image back to SwiftUI via the callback
        DispatchQueue.main.async { [weak self] in
            print("📤 Sending photo to SwiftUI callback")
            self?.photoCallback?(image)
        }
    }
}
