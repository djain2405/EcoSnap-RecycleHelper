package com.workshop.recyclehelper.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.workshop.recyclehelper.models.RecycleCategory
import com.workshop.recyclehelper.models.RecycleResult
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * RecycleClassifier - Handles all ML model operations
 *
 * This class:
 * 1. Loads the TensorFlow Lite MobileNet model
 * 2. Processes images using real on-device ML inference
 * 3. Returns classification results
 *
 * Uses TensorFlow Lite with MobileNet V1 for efficient on-device classification
 */
class RecycleClassifier(private val context: Context) {

    companion object {
        private const val TAG = "RecycleClassifier"

        /**
         * Confidence threshold: if prediction is below this, we say "Not Sure"
         * WORKSHOP TIP: Have students experiment with different values (0.5, 0.7, 0.9)
         * to see how it affects the "Not Sure" rate
         */
        private const val CONFIDENCE_THRESHOLD = 0.3f

        // MobileNet model parameters
        private const val MODEL_FILE = "mobilenet_v1_1.0_224_quant.tflite"
        private const val LABELS_FILE = "labels.txt"
        private const val IMAGE_SIZE = 224 // MobileNet input size
    }

    // TensorFlow Lite interpreter
    private var interpreter: Interpreter? = null

    // Labels loaded from file
    private val labels = mutableListOf<String>()

    /**
     * Initialize the classifier
     * Loads the TensorFlow Lite model and labels
     */
    init {
        try {
            Log.d(TAG, "🔄 Loading TensorFlow Lite model...")

            // Load the TFLite model from assets
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)
            interpreter = Interpreter(modelBuffer)

            // Load labels from assets
            loadLabels()

            Log.d(TAG, "✅ Classifier initialized with ${labels.size} labels")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error initializing classifier: ${e.message}", e)
        }
    }

    /**
     * Load labels from assets file
     */
    private fun loadLabels() {
        try {
            context.assets.open(LABELS_FILE).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    labels.clear()
                    reader.forEachLine { line ->
                        labels.add(line.trim())
                    }
                }
            }
            Log.d(TAG, "✅ Loaded ${labels.size} labels")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading labels: ${e.message}", e)
        }
    }

    /**
     * Classify an image using TensorFlow Lite
     *
     * STEPS:
     * 1. Preprocess the bitmap to 224x224
     * 2. Convert to ByteBuffer for TFLite
     * 3. Run inference
     * 4. Get top result
     * 5. Map label to recyclable/landfill
     * 6. Return RecycleResult
     *
     * @param bitmap The image to classify
     * @return RecycleResult with classification
     */
    fun classify(bitmap: Bitmap): RecycleResult {
        Log.d(TAG, "📸 Starting classification...")

        if (interpreter == null) {
            Log.e(TAG, "❌ Interpreter not initialized")
            return RecycleResult(
                category = RecycleCategory.NOT_SURE,
                confidence = 0.0f,
                detectedLabel = "Error: Model not loaded"
            )
        }

        try {
            // STEP 1: Preprocess the image
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)

            // STEP 2: Convert to ByteBuffer (quantized model expects uint8)
            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

            // STEP 3: Prepare output buffer (1001 classes for MobileNet)
            val outputBuffer = Array(1) { ByteArray(labels.size) }

            // STEP 4: Run inference
            interpreter?.run(inputBuffer, outputBuffer)

            // STEP 5: Find the top result
            val probabilities = outputBuffer[0]
            var maxIndex = 0
            var maxProbability = probabilities[0].toInt() and 0xFF

            for (i in 1 until probabilities.size) {
                val probability = probabilities[i].toInt() and 0xFF
                if (probability > maxProbability) {
                    maxProbability = probability
                    maxIndex = i
                }
            }

            // Convert quantized probability to float (0-255 -> 0.0-1.0)
            val confidence = maxProbability / 255.0f

            // Get the label
            val detectedLabel = if (maxIndex < labels.size) labels[maxIndex] else "Unknown"

            Log.d(TAG, "🔍 Detected: $detectedLabel with confidence: $confidence")

            // STEP 6: Map to category
            val category = mapLabelToCategory(detectedLabel, confidence)

            Log.d(TAG, "✅ Classification complete: ${category.displayName}")

            return RecycleResult(
                category = category,
                confidence = confidence,
                detectedLabel = detectedLabel
            )

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error during classification: ${e.message}", e)
            return RecycleResult(
                category = RecycleCategory.NOT_SURE,
                confidence = 0.0f,
                detectedLabel = "Error: ${e.message}"
            )
        }
    }

    /**
     * Convert bitmap to ByteBuffer for TFLite inference
     * MobileNet quantized model expects uint8 values [0-255]
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(1 * IMAGE_SIZE * IMAGE_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                val value = intValues[pixel++]
                // Extract RGB values and add to buffer
                byteBuffer.put(((value shr 16) and 0xFF).toByte())
                byteBuffer.put(((value shr 8) and 0xFF).toByte())
                byteBuffer.put((value and 0xFF).toByte())
            }
        }

        return byteBuffer
    }


    /**
     * Map a detected label to recycling category
     *
     * WORKSHOP TIP: This is where students can customize!
     * They can add more labels or change the mapping logic
     *
     * @param label The label from the ML model (e.g., "water bottle", "banana")
     * @param confidence How confident the model is (0.0 to 1.0)
     * @return The recycling category
     */
    private fun mapLabelToCategory(label: String, confidence: Float): RecycleCategory {
        // If confidence is too low, we're not sure
        if (confidence < CONFIDENCE_THRESHOLD) {
            return RecycleCategory.NOT_SURE
        }

        // Convert label to lowercase for easier matching
        val lowercaseLabel = label.lowercase()

        // RECYCLABLE items
        // These are common items that are typically recyclable
        val recyclableKeywords = listOf(
            "bottle", "can", "jar", "paper", "cardboard", "box",
            "container", "newspaper", "magazine", "carton",
            "aluminum", "plastic bottle", "glass", "tin", "steel", "packet"
        )

        for (keyword in recyclableKeywords) {
            if (lowercaseLabel.contains(keyword)) {
                return RecycleCategory.RECYCLABLE
            }
        }

        // LANDFILL items
        // These are common items that typically go to landfill
        val landfillKeywords = listOf(
            "banana", "orange", "apple", "food", "meat",
            "plastic bag", "straw", "styrofoam", "ceramic",
            "mirror", "light bulb", "diaper", "tissue",
            "wrapper", "greasy", "chip bag", "pizza"
        )

        for (keyword in landfillKeywords) {
            if (lowercaseLabel.contains(keyword)) {
                return RecycleCategory.LANDFILL
            }
        }

        // If we don't recognize the item, say we're not sure
        // This is better than guessing!
        return RecycleCategory.NOT_SURE
    }

    /**
     * Clean up resources when done
     * Closes the TensorFlow Lite interpreter
     */
    fun close() {
        interpreter?.close()
        interpreter = null
        Log.d(TAG, "🔒 Classifier closed")
    }
}
