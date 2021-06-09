package com.example.capstonebangkit

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class UploadActivity() : AppCompatActivity(), View.OnClickListener {
    private var selectImageButton: Button? = null
    private var uploadServerButton: Button? = null

    val SELECT_MULTIPLE_IMAGES = 1
    var selectedImagesPaths // Paths of the image(s) selected by the user.
            : ArrayList<String>? = null
    var imagesSelected = false // Whether the user selected at least an image or not.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@UploadActivity, arrayOf(Manifest.permission.INTERNET), 2);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@UploadActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);
        }
        setContentView(R.layout.activity_upload)

        selectImageButton = findViewById<View>(R.id.btn_select_image) as? Button
        selectImageButton!!.setOnClickListener(this)
        uploadServerButton = findViewById<View>(R.id.btn_upload_and_connect) as? Button
        uploadServerButton!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_select_image -> selectImage()
            R.id.btn_upload_and_connect -> connectServer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //                   Toast.makeText(getApplicationContext(), "Access to Storage Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Access to Storage Permission Denied.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            2 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //                Toast.makeText(getApplicationContext(), "Access to Internet Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Access to Internet Permission Denied.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }



    private fun connectServer() {
        val responseText: TextView = findViewById(R.id.tv_upload_response_status)
        if (imagesSelected == false) { // This means no image is selected and thus nothing to upload.
            responseText.text = "No Image Selected to Upload. Select Image(s) and Try Again."
            return
        }
        responseText.text = "Sending the Files. Please Wait ..."
        val ipv4AddressView: EditText = findViewById(R.id.et_ip_address)
        val ipv4Address = ipv4AddressView.text.toString()
        val portNumberView: EditText = findViewById(R.id.et_port_number)
        val portNumber = portNumberView.text.toString()
        val matcher: Matcher = IP_ADDRESS.matcher(ipv4Address)
        if (!matcher.matches()) {
            responseText.text = "Invalid IPv4 Address. Please Check Your Inputs."
            return
        }
        val postUrl = "https://prefab-pride-312605.et.r.appspot.com/"
        val multiPartBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        for (i in selectedImagesPaths!!.indices) {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val stream = ByteArrayOutputStream()
            try {
                // Read BitMap by file path.
                val bitmap = BitmapFactory.decodeFile(selectedImagesPaths!![i], options)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            } catch (e: Exception) {
                responseText.text = "Please Make Sure the Selected File is an Image."
                return
            }
            val byteArray = stream.toByteArray()
            multiPartBodyBuilder.addFormDataPart(
                "image$i",
                "Android_Flask_$i.jpg", RequestBody.create("image/*jpg".toMediaTypeOrNull(), byteArray)
            )
        }
        val postBodyImage: RequestBody = multiPartBodyBuilder.build()

//        RequestBody postBodyImage = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
//                .build();
        postRequest(postUrl, postBodyImage)
    }

    private fun postRequest(postUrl: String?, postBody: RequestBody?) {
        val client = OkHttpClient()

        val request=  Request.Builder()
            .url(postUrl!!)
            .post(postBody!!)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Cancel the post on failure.
                call.cancel()
                Log.d("FAIL", e.message!!)

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread {
                    val responseText: TextView = findViewById(R.id.tv_upload_response_status)
                    responseText.text = "Failed to Connect to Server. Please Try Again."
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread {
                    val responseText: TextView = findViewById(R.id.tv_upload_response_status)
                    try {
                        responseText.text = "Server's Response\n" + response.body!!.string()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            SELECT_MULTIPLE_IMAGES
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == SELECT_MULTIPLE_IMAGES && resultCode == RESULT_OK && null != data) {
                // When a single image is selected.
                var currentImagePath: String
                selectedImagesPaths = ArrayList()
                val numSelectedImages: TextView = findViewById(R.id.btn_select_image)
                if (data.data != null) {
                    val uri = data.data
                    currentImagePath = UploadActivity.Companion.getPath(applicationContext, uri)!!
                    Log.d("ImageDetails", "Single Image URI : $uri")
                    Log.d("ImageDetails", "Single Image Path : $currentImagePath")
                    selectedImagesPaths!!.add(currentImagePath)
                    imagesSelected = true
                    numSelectedImages.text =
                        "Number of Selected Images : " + selectedImagesPaths!!.size
                } else {
                    // When multiple images are selected.
                    // Thanks tp Laith Mihyar for this Stackoverflow answer : https://stackoverflow.com/a/34047251/5426539
                    if (data.clipData != null) {
                        val clipData = data.clipData
                        for (i in 0 until clipData!!.itemCount) {
                            val item = clipData.getItemAt(i)
                            val uri = item.uri
                            currentImagePath =
                                UploadActivity.Companion.getPath(applicationContext, uri)!!
                            selectedImagesPaths!!.add(currentImagePath)
                            Log.d("ImageDetails", "Image URI $i = $uri")
                            Log.d("ImageDetails", "Image Path $i = $currentImagePath")
                            imagesSelected = true
                            numSelectedImages.text =
                                "Number of Selected Images : " + selectedImagesPaths!!.size
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't Picked any Image.", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(
                applicationContext,
                selectedImagesPaths!!.size.toString() + " Image(s) Selected.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val IP_ADDRESS = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))"
        )

        // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
        fun getPath(context: Context?, uri: Uri?): String? {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (UploadActivity.Companion.isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (UploadActivity.Companion.isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return UploadActivity.Companion.getDataColumn(context, contentUri, null, null)
                } else if (UploadActivity.Companion.isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if (("image" == type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if (("video" == type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if (("audio" == type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return UploadActivity.Companion.getDataColumn(
                        context,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                }
            } else if ("content".equals(uri?.scheme, ignoreCase = true)) {
                return UploadActivity.Companion.getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri?.scheme, ignoreCase = true)) {
                return uri?.path
            }
            return null
        }

        fun getDataColumn(
            context: Context?, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                cursor = context?.contentResolver?.query(
                    (uri)!!, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        fun isExternalStorageDocument(uri: Uri?): Boolean {
            return ("com.android.externalstorage.documents" == uri?.authority)
        }

        fun isDownloadsDocument(uri: Uri?): Boolean {
            return ("com.android.providers.downloads.documents" == uri?.authority)
        }

        fun isMediaDocument(uri: Uri?): Boolean {
            return ("com.android.providers.media.documents" == uri?.authority)
        }
    }
}
