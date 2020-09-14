package com.wong.sign

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import java.io.ByteArrayOutputStream

class SignActivity : AppCompatActivity() {
    private var tuya: HandwritingBoardView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        tuya = findViewById(R.id.ll_sign_panel)
    }

    /**
     * 确定
     */
    fun onConfirmClick(view: View):Unit{
        if(tuya?.bitmap == null) return
        val intent: Intent = Intent()
        val baos:ByteArrayOutputStream = ByteArrayOutputStream()
        /*下面方法表示压缩图片，中间的值越小，压缩比例越大，失真也约厉害，100表示不压缩*/
        tuya!!.bitmap!!.compress(Bitmap.CompressFormat.PNG,100,baos)
        val byteArray:ByteArray = baos.toByteArray()
        intent.putExtra("bitmap",byteArray)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    /**
     * 取消
     */
    fun onCancelClick(view:View):Unit{
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /**
     * 撤销
     */
    fun undo(view:View):Unit{
        tuya?.undo()
    }

    /**
     * 重写
     */
    fun redo(view:View):Unit{
        tuya?.redo()
    }
}