package com.app.storyapp

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.app.storyapp.databinding.ActivityAddStoryBinding
import com.app.storyapp.viewModels.AddStoryModels
import com.app.storyapp.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private val addStoryModel by viewModels<AddStoryModels> {
        ViewModelsFactory.getInstance(this)
    }
    private lateinit var bind: ActivityAddStoryBinding
    private var imageUri: Uri? = null
    private var file: File? = null
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocationData { _, _ ->  }
            }
        }
        cameraOrGallery()
        getMyLocationData { _, _ ->  }
    }

    private fun getMyLocationData(callback: (lat:Double, lon:Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                loc?.let {
                    val lat: Double = it.latitude
                    val lon: Double = it.longitude
                    callback(lat, lon)
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun cameraOrGallery() {
        bind.gallery.setOnClickListener {
            val i = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            resultLauncherGallery.launch(i)
        }

        bind.camera.setOnClickListener {
            imageUri = getImageUri(this)
            resultLauncherCamera.launch(imageUri)
        }
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            Picasso.get().load(it).fit().into(bind.preview)
            val path = getPathFromUri(this, it)
            file = File(path!!)
            handleContent(file!!)
        }
    }

    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            imageUri?.let {
                println("Image Uri = $it")
                Picasso.get().load(it).fit().into(bind.preview)
                val path = getPathFromUri(this, it)
                file = File(path!!)
                handleContent(file!!)
            }
        }
    }

    private fun handleContent(file: File) {
        val compressFile = Compressor(this).compressToFile(file)
        uploadContent(compressFile)
    }

    private fun getPathFromUri(context: Context, it: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(it, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(columnIndex)
        }
        return realPath
    }

    private fun getImageUri(context: Context): Uri? {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpeg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri
    }

    private fun pesanError(s: String) {
        Toast.makeText(this@AddStoryActivity, s, Toast.LENGTH_SHORT).show()
    }

    private fun uploadContent(poto: File) {
        getMyLocationData{
            lat, lon ->
            bind.btnUpload.setOnClickListener {
                handleUpload(poto, lat, lon)
            }
        }

    }

    private fun handleUpload(poto: File, lat: Double, lon: Double) {

        println("ini $lat dan $lon, truss ini foto $poto")
        val dataDesc = bind.edDesc.text.toString()
        when {
            dataDesc.isEmpty() -> pesanError("Deskripsi masih Kosong...")
            !poto.exists() -> pesanError("Foto masih kosong...")
            else -> {
                val desc = dataDesc.toRequestBody("text/plain".toMediaType())
                val photo = poto.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part
                        = MultipartBody.Part.createFormData("photo", poto.name, photo)
                val latData = lat.toString()
                val lonData = lon.toString()
                val reqLat = latData.toRequestBody("text/plain".toMediaType())
                val reqLon = lonData.toRequestBody("text/plain".toMediaType())

                lifecycleScope.launch {
                    try {
                        addStoryModel.postStory(desc, imageMultipart, reqLat, reqLon)
                        // Berhasil mengunggah
                        pesanError("Berhasil Memposting...")
                        val i = Intent(this@AddStoryActivity, ListStoryActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        finish()
                    } catch (e: Exception){
                        pesanError("Gagal Memposting... ${e.message}")
                    }
                }
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}