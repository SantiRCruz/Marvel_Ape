package com.example.mapes.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun getBitmap(context:Context,url:String): Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context).data(url).build()
    val result = (loading.execute(request)as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}

fun String.decode64():Bitmap{
    val decodedString: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

}