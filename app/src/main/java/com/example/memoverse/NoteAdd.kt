package com.example.memoverse

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.memoverse.ui.theme.MemoVerseTheme
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


public  var IMAGE_BYTEARRAY : ByteArray? = null

class NoteAdd : ComponentActivity() {
    private lateinit var databaseHelper: NoteDatabaseHelper
    private val context : Context = this@NoteAdd
    private val requestCodePermission = 123
    private val requestCodeGallery = 456
    private val requestCodeCamera = 789
    private val images = mutableStateOf<List<Note>>(emptyList())
    private val selectedImageUri = mutableStateOf<Uri?>(null)
    private val selectedImageBitmap = mutableStateOf<ImageBitmap?>(null)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = NoteDatabaseHelper(this)
        setContent {
            MemoVerseTheme {
                // A surface container using the 'background' color from the theme


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.White)
                    ) {
                        NoteAddScreen(context = this@NoteAdd, databaseHelper = databaseHelper)
                    }
                }
                requestPermissions()
            }
        }
    }


    private fun requestPermissions() {
        //val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == permissionGranted &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == permissionGranted

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), requestCodePermission)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            }
        }
    }




    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCodeGallery)
    }

    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCodeCamera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmapImage : Bitmap? = null
        if (resultCode == Activity.RESULT_OK && data!=null && data.data!=null) {
            when (requestCode) {
                requestCodeGallery -> {
                    val selectedImageUri = data?.data
                    var os = ByteArrayOutputStream()
                    var inputStream = selectedImageUri?.let {
                        this@NoteAdd?.contentResolver.openInputStream(
                            it
                        )
                    }
                    IMAGE_BYTEARRAY = inputStream?.readBytes()
                }

                requestCodeCamera -> {
                    bitmapImage = data?.extras?.get("data") as? Bitmap
                    if (bitmapImage != null) {
                        val uri = saveImageToGallery(bitmapImage)
                        //insertImageIntoDatabase(uriTObitmap(uri))
                    }
                }
            }
        }
    }




    fun toByteArray(imageBitmap: Bitmap?): ByteArray? {
        if (imageBitmap == null) {
            return null
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        try {
            byteArrayOutputStream.close()
        } catch (e: IOException) {

        }
        return byteArrayOutputStream.toByteArray()
    }


    private fun loadImageBitmap(uri: Uri?) : Bitmap?{
        val imageBitmap = uri?.let { ImageUtils.loadBitmapFromUri(contentResolver, it) }
        lifecycleScope.launch {
            selectedImageBitmap.value = imageBitmap?.asImageBitmap()
        }
        return imageBitmap?.asImageBitmap()?.asAndroidBitmap()
    }

    private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        // Here, implement the logic to save the image bitmap to the gallery
        // and return the image URI.
        // Note: Saving the image to the gallery requires additional code for handling file I/O and permissions.
        // You can refer to the Android documentation for more information on how to save images to the gallery.
        return null
    }


    object ImageUtils {
        fun loadBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
            return try {
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    @Composable
    fun NoteAddScreen(context: Context, databaseHelper: NoteDatabaseHelper) {
        var title by remember { mutableStateOf("") }
        var summary by remember { mutableStateOf("") }
        var error by remember { mutableStateOf("") }
        val context = LocalContext.current
        val currentDateString by remember { mutableStateOf(getCurrentDate()) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(10.dp)
        ) {
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(9.dp),
                elevation = 5.dp
            ){
                Box(modifier = Modifier
                    .background(color = colorResource(id = R.color.bodybg)).padding(10.dp)) {
                    Column {
                        Text(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.h1,
                            color = colorResource(id = R.color.mainbg),
                            text = "Memories of the Day"
                        )

                        OutlinedTextField(
                            value = title, onValueChange = { title = it },
                            label = { Text("Title", style = MaterialTheme.typography.body1) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.mainbg),
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = colorResource(id = R.color.mainbg),
                                unfocusedLabelColor = Color.Gray,
                                textColor = Color.Black
                            )

                        )


                        OutlinedTextField(
                            value = summary, onValueChange = { summary = it },
                            label = { Text("Memory I had was", style = MaterialTheme.typography.body1) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.mainbg),
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = colorResource(id = R.color.mainbg),
                                unfocusedLabelColor = Color.Gray,
                                textColor = Color.Black
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = Alignment.End)
                        ) {
                            Spacer(modifier = Modifier.width(150.dp))
                            IconButton(onClick = {
                                /* Open gallery and select picture*/
                                openGallery()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_image_24),
                                    contentDescription = "picture",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            IconButton(onClick = {
                                openCamera()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                                    contentDescription = "camera",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            //Image(painter = painterResource(id = R.drawable.ic_baseline_mic_24), contentDescription = "audio", modifier = Modifier.padding(10.dp))
                            //Image(painter = painterResource(id = R.drawable.ic_baseline_videocam_24), contentDescription = "video", modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Button(
                    onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    },
                    border = BorderStroke(1.dp, colorResource(id = R.color.mainbg)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(5.dp)
                        .width(150.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = colorResource(id = R.color.mainbg),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.body1
                    )
                }


                Button(
                    onClick = {
                        if (title.isNotEmpty() && summary.isNotEmpty()) {
                            val note = Note(
                                id = null,
                                date = currentDateString,
                                title = title,
                                summary = summary,
                                image = IMAGE_BYTEARRAY
                            )
                            databaseHelper.insertNote(note)
                            error = "Note added successfully!"
                            // Start LoginActivity using the current context
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )

                        } else {
                            error = "Please fill all fields"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.mainbg)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(5.dp)
                        .width(150.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.body1
                    )
                }


            }

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

        }

    }
}


fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(currentDate)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    MemoVerseTheme {
    }
}