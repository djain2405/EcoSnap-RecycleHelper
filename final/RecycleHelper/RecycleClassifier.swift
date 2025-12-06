import UIKit
import CoreML
import Vision
import Combine
import ImageIO

/// RecycleClassifier handles all ML model operations
/// It loads the model, processes images, and returns classification results
class RecycleClassifier: ObservableObject {

    // MARK: - Published Properties

    /// The current classification result (updates the UI automatically)
    @Published var result: RecycleResult?

    /// Whether the model is currently processing an image
    @Published var isProcessing = false

    // MARK: - Constants

    /// Confidence threshold: if prediction is below this, we say "Not Sure"
    /// WORKSHOP TIP: Have students experiment with different values (0.5, 0.7, 0.9)
    /// to see how it affects the "Not Sure" rate
    private let confidenceThreshold: Float = 0.3

    // MARK: - Classification Method

    /// Classifies an image to determine if it's recyclable or not
    /// - Parameter image: The photo taken by the user
    func classify(image: UIImage) {
        // Show loading state
        isProcessing = true
        result = nil

        // Process on background thread so UI stays responsive
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            guard let self = self else { return }

            // Get the classification result
            let classification = self.performClassification(image: image)

            // Update UI on main thread
            DispatchQueue.main.async {
                self.result = classification
                self.isProcessing = false
            }
        }
    }

    // MARK: - Private Methods

    /// Performs the actual ML classification using Vision framework
    /// - Parameter image: The image to classify
    /// - Returns: A RecycleResult with the prediction
    private func performClassification(image: UIImage) -> RecycleResult {

        print("🔍 Starting classification for image size: \(image.size), orientation: \(image.imageOrientation.rawValue)")

        // STEP 1: Normalize the image first to fix orientation issues
        // This works consistently for both camera and photo library
        guard let normalizedImage = normalizeImageOrientation(image) else {
            print("❌ Failed to normalize image orientation")
            return RecycleResult(
                category: .notSure,
                confidence: 0.0,
                detectedLabel: "Error: Could not normalize image"
            )
        }

        // STEP 2: Convert to CGImage
        guard let cgImage = normalizedImage.cgImage else {
            print("❌ Failed to get CGImage from normalized image")
            return RecycleResult(
                category: .notSure,
                confidence: 0.0,
                detectedLabel: "Error: Could not convert to CGImage"
            )
        }

        print("✅ Using normalized CGImage approach")

        // STEP 3: Create Vision classification request
        let request = VNClassifyImageRequest()

        // STEP 4: Perform the classification with retry logic
        // The simulator can be flaky with Vision framework
        let handler = VNImageRequestHandler(cgImage: cgImage, orientation: .up, options: [:])

        print("🔄 Performing Vision request...")
        do {
            try handler.perform([request])
            print("✅ Vision request completed successfully")
        } catch let error as NSError {
            print("❌ Vision request failed: \(error.localizedDescription)")
            print("❌ Error domain: \(error.domain), code: \(error.code)")

            // Check if this is the simulator espresso context error
            if error.localizedDescription.contains("espresso") {
                print("⚠️ This is a known simulator issue. The app will work on a real device.")
                return RecycleResult(
                    category: .notSure,
                    confidence: 0.0,
                    detectedLabel: "Simulator limitation: Vision framework requires a physical device for photo library images"
                )
            }

            return RecycleResult(
                category: .notSure,
                confidence: 0.0,
                detectedLabel: "Error: \(error.localizedDescription)"
            )
        }

        // STEP 5: Get the results
        guard let results = request.results as? [VNClassificationObservation],
              let topResult = results.first else {
            print("❌ No classification results returned")
            return RecycleResult(
                category: .notSure,
                confidence: 0.0,
                detectedLabel: "No results"
            )
        }

        print("✅ Top result: \(topResult.identifier) with confidence: \(topResult.confidence)")

        // STEP 6: Map the detected label to recyclable/landfill
        let category = self.mapLabelToCategory(
            label: topResult.identifier,
            confidence: topResult.confidence
        )

        return RecycleResult(
            category: category,
            confidence: topResult.confidence,
            detectedLabel: topResult.identifier
        )
    }

    /// Maps a MobileNet label to our recycling categories
    /// WORKSHOP TIP: This is a great place for students to customize!
    /// They can add more labels or change the mapping logic
    /// - Parameters:
    ///   - label: The label from the ML model (e.g., "water bottle", "banana")
    ///   - confidence: How confident the model is (0.0 to 1.0)
    /// - Returns: The recycling category
    private func mapLabelToCategory(label: String, confidence: Float) -> RecycleCategory {

        // If confidence is too low, we're not sure
        if confidence < confidenceThreshold {
            return .notSure
        }

        // Convert label to lowercase for easier matching
        let lowercaseLabel = label.lowercased()

        // RECYCLABLE items
        // These are common items that are typically recyclable
        let recyclableKeywords = [
            "bottle", "can", "jar", "paper", "cardboard", "box",
            "container", "newspaper", "magazine", "carton",
            "pop bottle", "soda can", "aluminum", "plastic bottle",
            "glass", "tin", "steel", "document"
        ]

        for keyword in recyclableKeywords {
            if lowercaseLabel.contains(keyword) {
                return .recyclable
            }
        }

        // LANDFILL items
        // These are common items that typically go to landfill
        let landfillKeywords = [
            "banana", "orange", "apple", "food", "meat",
            "plastic bag", "straw", "styrofoam", "ceramic",
            "mirror", "light bulb", "diaper", "tissue",
            "pizza box", "greasy", "chip bag"
        ]

        for keyword in landfillKeywords {
            if lowercaseLabel.contains(keyword) {
                return .landfill
            }
        }

        // If we don't recognize the item, say we're not sure
        // This is better than guessing!
        return .notSure
    }

    /// Normalizes the image orientation by redrawing it
    /// This fixes orientation issues from photos taken in different orientations
    /// Also converts image to a format that works better with Vision framework in simulators
    /// Works for both camera captures and photo library picks
    /// - Parameter image: The original UIImage
    /// - Returns: A new UIImage with normalized orientation (always .up) and compatible format
    private func normalizeImageOrientation(_ image: UIImage) -> UIImage? {
        // Always redraw the image to ensure it's in a compatible format
        // This is especially important for simulator compatibility
        print("🔄 Normalizing image orientation from \(image.imageOrientation.rawValue) to .up")

        // Create a graphics context and redraw the image
        // This converts the image to a bitmap format that Vision can handle reliably
        UIGraphicsBeginImageContextWithOptions(image.size, false, image.scale)
        defer { UIGraphicsEndImageContext() }

        image.draw(in: CGRect(origin: .zero, size: image.size))
        let normalizedImage = UIGraphicsGetImageFromCurrentImageContext()

        if normalizedImage != nil {
            print("✅ Image normalized successfully")
        } else {
            print("❌ Image normalization failed")
        }

        return normalizedImage
    }

    /// Converts UIImage.Orientation to CGImagePropertyOrientation for Vision framework
    /// - Parameter image: The UIImage to get orientation from
    /// - Returns: The CGImagePropertyOrientation value
    private func getCGImageOrientation(from image: UIImage) -> CGImagePropertyOrientation {
        switch image.imageOrientation {
        case .up:
            return .up
        case .down:
            return .down
        case .left:
            return .left
        case .right:
            return .right
        case .upMirrored:
            return .upMirrored
        case .downMirrored:
            return .downMirrored
        case .leftMirrored:
            return .leftMirrored
        case .rightMirrored:
            return .rightMirrored
        @unknown default:
            return .up
        }
    }
}

// MARK: - Data Models

/// Represents the three possible recycling categories
enum RecycleCategory {
    case recyclable
    case landfill
    case notSure

    /// User-friendly display name
    var displayName: String {
        switch self {
        case .recyclable: return "♻️ Likely Recyclable"
        case .landfill: return "🗑️ Probably Landfill"
        case .notSure: return "❓ Not Sure"
        }
    }

    /// Color for the UI
    var color: UIColor {
        switch self {
        case .recyclable: return UIColor.systemGreen
        case .landfill: return UIColor.systemGray
        case .notSure: return UIColor.systemOrange
        }
    }

    /// Helper message for the user
    var message: String {
        switch self {
        case .recyclable:
            return "This item appears to be recyclable. Please rinse and check local guidelines."
        case .landfill:
            return "This item likely goes in the landfill or compost."
        case .notSure:
            return "I'm not confident about this item. Please check your local recycling rules."
        }
    }
}

/// The complete result of a classification
struct RecycleResult: Equatable {
    /// The category: recyclable, landfill, or not sure
    let category: RecycleCategory

    /// How confident the model is (0.0 to 1.0)
    let confidence: Float

    /// The raw label from the ML model (for debugging/learning)
    let detectedLabel: String

    /// Formatted confidence as a percentage
    var confidencePercentage: Int {
        return Int(confidence * 100)
    }
}
