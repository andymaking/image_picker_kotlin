package com.example.pickimages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.pickimages.ui.theme.PickImagesTheme
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil.annotation.ExperimentalCoilApi


@OptIn(ExperimentalCoilApi::class)
class MainActivity : ComponentActivity() {

    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, handle image loading
            } else {
                // Permission denied, handle accordingly
            }
        }

    private fun pickImage() {
        // Use any method to pick an image from the phone's storage.
        // For example, you can use an Intent to open the image picker.
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected image
            val selectedImageUri: Uri? = data?.data
            // Update the imageUri to trigger image loading in the composable
            if (selectedImageUri != null) {
                imageUri = selectedImageUri
                setContent { MainScreen() } // Trigger recomposition to update the ImageFromPhoneStorage composable
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickImagesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
    }

    private fun hasReadExternalStoragePermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current

        // Request the necessary permissions
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (hasReadExternalStoragePermission()) {
            ImageFromPhoneStorage()
        } else {
            // Handle permission not granted scenario
            Text(text = "Permission required to load images.")
        }

        ImageFromPhoneStorage()
    }

    @Composable
    fun ImageFromPhoneStorage() {
//        var imageUri by remember { mutableStateOf<Uri?>(null) }

        // Launch the image picker when the user clicks on a button
        // Use any method to trigger this (button click, FAB click, etc.).
        // For simplicity, we'll use a Text composable as a button here.
        Text(
            text = "Select Image",
            modifier = Modifier
                .padding(16.dp)
                .clickable { pickImage() },
        )

        // Load the image using Coil
        val painter = imageUri?.let { uri ->
            rememberImagePainter(
                data = uri,
                builder = {
                    scale(Scale.FILL)
                    crossfade(true)
                }
            )
        }

        Image(
            painter = painter ?: rememberImagePainter(ColorDrawable(Color.LightGray.toArgb())),
            contentDescription = "Image from phone storage",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }

}


// Handle the result of the image picker


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PickImagesTheme {
        Greeting("Android")
    }
}