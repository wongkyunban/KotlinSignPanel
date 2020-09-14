package com.wong.sign

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class HandwritingBoardView : View {
    companion object {
        const val TOUCH_TOLERANCE: Int = 4// 触点之间的公差，此变量用于控制，当路径在绘制过程中，移动距离至少等于4才绘制，减轻频繁的绘制的情况。
    }

    private var mPaint: Paint? = null// 用于描绘路径的画笔
    private var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null// 保存每一次绘制出来的图形
    private var mPath: Path? = null// 每一次画出来的路径
    private var mX: Float = 0F
    private var mY: Float = 0F
    private var drawPath: DrawPath? = null// 记录Path路径的对象
    private var savePath: MutableList<DrawPath> = ArrayList()// 保存Path路径的集合,用List集合来模拟栈
    private var screenWidth: Int = 0// 手写板的宽
    private var screenHeight: Int = 0// 手写板的高
    private var mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)// 画布画笔
    var mPenSize: Float = 10F
        // 画笔默认大小
        set(value) {
            field = if (value <= 0) 10F else value
            if(mPaint != null){
                mPaint!!.strokeWidth = value
            }
        }
    var mPenColor: Int = Color.BLACK
        // 画笔默认颜色
        set(value) {
            if(mPaint != null){
                mPaint!!.color = value
            }
            field = value
        }
    var mPanelColor: Int = Color.TRANSPARENT// 背景色


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeJoin = Paint.Join.ROUND// 设置外边缘
        mPaint!!.strokeCap = Paint.Cap.ROUND// 形状
        mPaint!!.strokeWidth = mPenSize// 画笔大小
        mPaint!!.color = mPenColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!) // 保存每一次绘制出来的图形
        mCanvas!!.drawColor(mPanelColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawColor(mPanelColor)
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint) // 将前面已经画过得显示出来
        if (mPath != null) {
            canvas.drawPath(mPath!!, mPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x: Float = event!!.x
        val y: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {// 按下
                mPath = Path()// 每次点击时，就重新绘制一个新的路径Path
                drawPath = DrawPath()// 每一次记录的路径对象是不一样的
                drawPath!!.path = mPath
                drawPath!!.paint = mPaint
                touchStart(x, y)// 处理坐标点
                invalidate()// 刷新视图
            }
            MotionEvent.ACTION_MOVE -> {// 移动
                touchMove(x, y)
                invalidate()// 刷新视图
            }
            MotionEvent.ACTION_UP -> {// 松开前最后一个点
                touchUp(x, y)
                invalidate()// 刷新视图
            }
        }
        return true

    }

    private fun touchStart(x: Float, y: Float) {
        mPath!!.moveTo(x, y)
        mX = x// 记录当前x坐标
        mY = y// 记录当前y坐标
    }

    private fun touchMove(x: Float, y: Float) {
        val dx: Float = abs(x - mX)
        val dy: Float = abs(mY - y)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {// 横坐标或纵坐标移动的距离的绝对值大于或等于TOUCH_TOLERANCE时，才描绘路径
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x// 记录当前x坐标
            mY = y// 记录当前y坐标
        }
    }

    private fun touchUp(x: Float, y: Float) {
        mPath!!.lineTo(mX, mY)
        mCanvas!!.drawPath(mPath!!, mPaint!!)
        savePath.add(drawPath!!)// 将一条完整的路径保存下来
        mPath = null// 重新置空
    }

    /**
     * 撤销
     */
    fun undo() {
        /*清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉*/
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas!!.setBitmap(mBitmap)// 重新设置画布，相当于清空画布
        if (savePath.size > 0) {
            savePath.removeAt(savePath.size - 1)// 移除最后一个path
            for (drawPath: DrawPath in savePath) {
                mCanvas!!.drawPath(drawPath.path!!, drawPath.paint!!)
            }
            invalidate()// 刷新页面
        }
    }

    /**
     * 重写
     */
    fun redo() {
        savePath.clear()
        mCanvas!!.drawColor(mPanelColor, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    /**
     * 返回Bitmap
     */
    val bitmap: Bitmap?
        get() = if (savePath.size == 0) null else mBitmap

    // 嵌套类：不能访问外部成员
    class DrawPath {
        var path: Path? = null
        var paint: Paint? = null
    }
}