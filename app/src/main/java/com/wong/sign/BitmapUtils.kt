package com.wong.sign

import android.graphics.Bitmap

object BitmapUtils {
    /**
     * 缩放图片
     */
    fun createScaleBitmap(src: Bitmap, dstWidth:Int, dstHeight:Int):Bitmap?{
        if( dstHeight <= 0 || dstWidth <= 0) return null
        /*如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false*/
        val dst: Bitmap = Bitmap.createScaledBitmap(src,dstWidth,dstHeight,false)
        if(src != dst){// 如果没有缩放，那么不回收
            src.recycle()// 释放Bitmap的native像素数组
        }
        return dst
    }
}