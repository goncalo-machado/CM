package com.example.cm_project.ui

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import java.io.File


@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        CameraContent()
    } else {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Camera permission is required to use this feature.")
        }
    }
}

@Composable
fun CameraContent() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var imageName by remember { mutableStateOf("") }

    // Initialize the camera provider and clean up on disposal
    DisposableEffect(Unit) {
        val listener = {
            cameraProvider = cameraProviderFuture.get()
        }
        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    if (imageUri != null) {
        // Display the captured image
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)

            ) {
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                    )
                }

                Text(text = "Photo Captured!", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = imageName, // Bind the value to the state
                    onValueChange = { imageName = it }, // Update the state on user input
                    label = { Text("Enter a name") },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    imageUri?.let {
                        saveImageToGallery(it, imageName, context)
                    }
                    imageUri = null // Clear the captured image
                    imageName = ""  // Clear the name text field
                    showCamera = false // Optionally hide the camera preview
                }) {
                    Text("Save Photo")
                }
            }
        }
    } else if (showCamera) {
        CameraPreview(cameraProvider = cameraProvider,
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            onImageCaptured = { uri -> imageUri = uri })
    } else {
        SavedImagesScreen(context = LocalContext.current, onOpenCamera = { showCamera = true })
    }
}

@Composable
fun SavedImagesScreen(context: Context, onOpenCamera: () -> Unit) {
    val savedImages = remember { getSavedImages(context) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Title
            Text(
                text = "Gallery",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 60.dp)

            )

            // Display the saved images in a LazyColumn
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(savedImages.size) { index ->
                    val imageUri = savedImages[index]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Assuming image name is the file name, but you can use a different approach
                        Text(text = imageUri.lastPathSegment ?: "Unnamed")
                    }
                }
            }
        }

        // "Open Camera" button fixed at the bottom
        Button(
            onClick = onOpenCamera,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        ) {
            Text("Open Camera")
        }
    }
}

fun getSavedImages(context: Context): List<Uri> {
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME
    )

    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf("Pictures/CMProject/%") // Filter by app-specific folder

    val images = mutableListOf<Uri>()
    val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)

    cursor?.use {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            images.add(contentUri)
        }
    }

    return images
}

@Composable
fun CameraPreview(
    cameraProvider: ProcessCameraProvider?,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    onImageCaptured: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(cameraProvider) {
        cameraProvider?.let { provider ->
            val preview = androidx.camera.core.Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Capture button
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 65.dp)
    ) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Button(
            onClick = {
                captureImage(imageCapture, context, onImageCaptured)
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Capture Photo")
        }
    }
}

fun captureImage(
    imageCapture: ImageCapture, context: Context, onImageCaptured: (Uri) -> Unit
) {
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
        File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
    ).build()

    imageCapture.takePicture(outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri
                onImageCaptured(savedUri ?: Uri.EMPTY)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Error capturing image: ${exception.message}")
            }
        })
}

fun saveImageToGallery(imageUri: Uri, imageName: String, context: Context) {
    try {
        // Get the content resolver
        val contentResolver = context.contentResolver

        // Prepare the content values for the image
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpg")  // Image name
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")  // Image MIME type
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/CMProject"
            )  // Path in the gallery
            put(
                MediaStore.Images.Media.DATE_ADDED,
                System.currentTimeMillis() / 1000
            )  // Current time in seconds
            put(
                MediaStore.Images.Media.DATE_MODIFIED,
                System.currentTimeMillis() / 1000
            )  // Current time in seconds
        }

        // Insert the image into the MediaStore
        val imageUriForGallery =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        // If the URI is not null, write the image to the gallery
        imageUriForGallery?.let { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    inputStream.copyTo(outputStream!!)  // Copy the image from the input stream to the output stream
                }
            }
            Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e("CameraContent", "Error saving image to gallery: ${e.message}")
        Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
    }
}

