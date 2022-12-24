package com.example.mapes.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun getBitmap(context:Context,url:String): Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context).data(url).build()
    val result = (loading.execute(request)as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}