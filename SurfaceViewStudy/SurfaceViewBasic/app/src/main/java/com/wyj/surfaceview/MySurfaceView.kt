package com.wyj.surfaceview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MySurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var drawingFlag: Boolean = false

    private val normalScope by lazy(LazyThreadSafetyMode.NONE) { CoroutineScope(Dispatchers.Default) }

    private var canvas: Canvas? = null
    private var path: Path? = null
    private var paint: Paint? = null
    private var drawingJob: Job? = null

    companion object {
        const val TAG = "MySurfaceView"
        const val TIME_IN_FRAME = 30
    }

    init {
        holder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
        keepScreenOn = true
        path = Path()
        paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 5f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawingFlag = true
        drawingJob = normalScope.launch {
            val startTime = System.currentTimeMillis()
            while (drawingFlag) {
                drawContent()
            }
            var intervalTime: Int = (System.currentTimeMillis() - startTime).toInt()
            while (intervalTime <= TIME_IN_FRAME) {
                intervalTime = (System.currentTimeMillis() - startTime).toInt()
                Thread.yield()
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        drawingFlag = false
        drawingJob?.cancel()
    }

    private fun drawContent() {
        try {
            canvas = holder.lockCanvas()?.apply {
                drawColor(Color.WHITE)
                drawPath(path!!, paint!!)
            }

        } catch (e: Exception) {
            Log.d(TAG, "drawContent: ${e.message}")
        } finally {
            canvas?.let {
                holder.unlockCanvasAndPost(it)
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val result = event?.let { event ->
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path?.moveTo(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    path?.lineTo(x, y)
                }
            }
            return@let true
        } ?: super.onTouchEvent(event)
        return result
    }

}