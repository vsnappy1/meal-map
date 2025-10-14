package com.randos.mealmap.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.randos.mealmap.ui.theme.iconButtonColors
import com.randos.mealmap.utils.findActivity
import java.io.File

@Composable
fun RecipeImage(
    modifier: Modifier = Modifier,
    imagePath: String?,
    onImagePick: (String) -> Unit,
    onCameraCapture: (String) -> Unit
) {
    var showImageSourceDialog by remember { mutableStateOf(false) }
    if (showImageSourceDialog) {
        ImageCapture(
            onDismiss = {
                showImageSourceDialog = false
            },
            onImagePick = {
                onImagePick(it)
                showImageSourceDialog = false
            },
            onCameraCapture = {
                onCameraCapture(it)
                showImageSourceDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .height(150.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (imagePath == null) {
                TileBackground(
                    iconSize = 24.dp,
                    spaceBetweenRow = 4.dp
                )
                Text(
                    modifier = Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.small
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable {
                            showImageSourceDialog = true
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    text = "Add Image"
                )
            } else {
                AsyncImage(
                    model = imagePath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(35.dp)
                        .align(Alignment.BottomEnd),
                    onClick = {
                        showImageSourceDialog = true
                    },
                    colors = iconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecipeImagePreview() {
    MaterialTheme {
        RecipeImage(
            imagePath = null,
            onImagePick = {},
            onCameraCapture = {}
        )
    }
}

@Composable
private fun ImageCapture(onDismiss: () -> Unit, onImagePick: (String) -> Unit, onCameraCapture: (String) -> Unit) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val imagePicker = imagePickerLauncher(onImagePick)
    val cameraLauncher = cameraLauncher(context, capturedImageUri, onCameraCapture)
    val permissionLauncher = permissionLauncher(context, capturedImageUri, cameraLauncher)

    ImageSourceDialog(
        onDismiss = onDismiss,
        onGalleryClick = {
            imagePicker.launch("image/*")
        },
        onCameraClick = {
            val uri = createImageUri(context)
            capturedImageUri = uri
            requestCameraPermission(
                context = context,
                permissionLauncher = permissionLauncher,
                onGranted = {
                    cameraLauncher.launch(uri)
                },
                onDenied = {
                    Toast.makeText(
                        context,
                        "Go to app settings and enable camera permission",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onDismiss()
                }
            )
        }
    )
}

@Composable
private fun ImageSourceDialog(onDismiss: () -> Unit, onGalleryClick: () -> Unit, onCameraClick: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Choose Image Source", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Gallery")
                }
                Button(onClick = onCameraClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Camera")
                }
            }
        }
    }
}

@Composable
private fun permissionLauncher(
    context: Context,
    capturedImageUri: Uri?,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
): ManagedActivityResultLauncher<String, Boolean> = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        capturedImageUri?.let { cameraLauncher.launch(it) }
    } else {
        Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun cameraLauncher(
    context: Context,
    capturedImageUri: Uri?,
    onCameraCapture: (String) -> Unit
): ManagedActivityResultLauncher<Uri, Boolean> =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            capturedImageUri?.let { onCameraCapture(it.toString()) }
        } else {
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

@Composable
private fun imagePickerLauncher(onImagePick: (String) -> Unit): ManagedActivityResultLauncher<String, Uri?> =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagePick(it.toString()) }
    }

private fun createImageUri(context: Context): Uri {
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "meal_map_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}

private fun requestCameraPermission(
    context: Context,
    onGranted: () -> Unit,
    onDenied: () -> Unit = {},
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val cameraPermission = Manifest.permission.CAMERA
    when {
        ContextCompat.checkSelfPermission(context, cameraPermission) ==
            PackageManager.PERMISSION_GRANTED -> {
            // Already granted
            onGranted()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            context.findActivity(),
            cameraPermission
        ) -> {
            // User previously denied → show rationale UI
            onDenied()
        }

        else -> {
            // Request permission
            permissionLauncher.launch(cameraPermission)
        }
    }
}
