package com.alexaat.stocktracker


import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File


fun Fragment.hideKeyboard() {
    val activity = this.activity
    if (activity is AppCompatActivity) {
        activity.hideKeyboard()
    }
}
fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

}


fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    }catch(e: Exception){
        return ""
    }finally {
        cursor?.let{
            it.close()
        }
    }
}


fun fileExists(context:Context, uriString:String):Boolean{
    if(uriString==""){
        return false
    }
    val uri = uriString.toUri()
    val path = getRealPathFromUri(context,uri)
    if(path==""){
    return false
    }
    val file = File(getRealPathFromUri(context,uri))
    if(file.exists()) {
        return true
    }
   return false
}


fun makeBarCode(text:String): Bitmap? {
    val multiFormatWriter = MultiFormatWriter()
    try {
        val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.EAN_13, 128, 128)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: Exception) {

    }
    return null
}





