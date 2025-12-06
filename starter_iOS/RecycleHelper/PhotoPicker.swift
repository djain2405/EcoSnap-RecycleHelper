import SwiftUI
import PhotosUI

/// PhotoPicker wraps the system photo picker for selecting images from the photo library
struct PhotoPicker: UIViewControllerRepresentable {

    // MARK: - Properties

    /// Callback that gets triggered when a photo is selected
    var onPhotoSelected: (UIImage) -> Void

    /// Callback for when the picker is dismissed
    var onDismiss: () -> Void

    // MARK: - UIViewControllerRepresentable Methods

    func makeUIViewController(context: Context) -> PHPickerViewController {
        // Configure the picker to only show images
        var configuration = PHPickerConfiguration()
        configuration.filter = .images
        configuration.selectionLimit = 1 // Only allow selecting one image

        let picker = PHPickerViewController(configuration: configuration)
        picker.delegate = context.coordinator

        return picker
    }

    func updateUIViewController(_ uiViewController: PHPickerViewController, context: Context) {
        // No updates needed
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(onPhotoSelected: onPhotoSelected, onDismiss: onDismiss)
    }

    // MARK: - Coordinator

    /// Coordinator to handle photo picker delegate methods
    class Coordinator: NSObject, PHPickerViewControllerDelegate {
        var onPhotoSelected: (UIImage) -> Void
        var onDismiss: () -> Void

        init(onPhotoSelected: @escaping (UIImage) -> Void, onDismiss: @escaping () -> Void) {
            self.onPhotoSelected = onPhotoSelected
            self.onDismiss = onDismiss
        }

        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            // Dismiss the picker first
            onDismiss()

            // Check if user selected a photo
            guard let result = results.first else {
                print("📷 No photo selected")
                return
            }

            // Load the image
            let itemProvider = result.itemProvider

            if itemProvider.canLoadObject(ofClass: UIImage.self) {
                itemProvider.loadObject(ofClass: UIImage.self) { [weak self] image, error in
                    if let error = error {
                        print("❌ Error loading image: \(error.localizedDescription)")
                        return
                    }

                    if let image = image as? UIImage {
                        print("✅ Photo loaded successfully! Size: \(image.size)")
                        DispatchQueue.main.async {
                            self?.onPhotoSelected(image)
                        }
                    }
                }
            }
        }
    }
}
