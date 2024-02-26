package com.app.storyapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.storyapp.api.BaseApi
import com.app.storyapp.databinding.ActivityAddStoryBinding
import com.app.storyapp.models.ResponseUploadStory
import com.app.storyapp.models.UserModel
import com.app.storyapp.viewModels.AddStoryModels
import com.app.storyapp.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        cameraOrGallery()
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
        if (it != null){
            Picasso.get().load(it).fit().into(bind.preview)
            val path = getPathFromUri(this, it)
            file = File(path!!)
            handleContent(file!!)
        }
    }

    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess){
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

        addStoryModel.getSession().observe(this){
            uploadContent(it, compressFile)
        }
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

    private fun uploadContent(userModel: UserModel, poto: File) {
        val token = "Bearer ${userModel.token}"

        bind.btnUpload.setOnClickListener {
            handleUpload(poto, token)
        }
    }

    private fun handleUpload(poto: File, token: String) {
        val dataDesc = bind.edDesc.text.toString()
        when {
            dataDesc.isEmpty() -> pesanError("Deskripsi masih Kosong...")
            !poto.exists() -> pesanError("Foto masih kosong...")
            else -> {
                val desc = dataDesc.toRequestBody("text/plain".toMediaType())
                val photo = poto.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", poto.name, photo)
                val callApi = BaseApi().getService().postStory(token, imageMultipart, desc)

                callApi.enqueue(object : Callback<ResponseUploadStory>{
                    override fun onResponse(
                        call: Call<ResponseUploadStory>,
                        response: Response<ResponseUploadStory>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error!!) {

                                pesanError(responseBody.message!!)
                                val i = Intent(this@AddStoryActivity, ListStoryActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)
                                finish()
                            }
                        } else {
                            pesanError("Error : ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseUploadStory>, t: Throwable) {
                        println("Error : ${t.message}")
                    }
                })
            }
        }
    }
}