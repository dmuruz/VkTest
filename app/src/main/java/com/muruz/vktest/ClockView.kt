package com.muruz.vktest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var width = 0
    private var height = 0
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private val calendar = Calendar.getInstance()

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
        centerX = width / 2f
        centerY = height / 2f
        radius = (min(width, height) / 2f) * 0.9f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calendar.timeInMillis = System.currentTimeMillis()
        drawClockFace(canvas)
        drawHourHand(canvas)
        drawMinuteHand(canvas)
        drawSecondHand(canvas)
        postInvalidateDelayed(1000)
    }

    private fun drawClockFace(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.textSize = radius / 4
        paint.strokeWidth = 8f
        canvas.drawCircle(centerX, centerY, radius, paint)

        val labelRadius = radius * 0.75f
        for (i in 1..12) {
            val angle = Math.PI / 6 * (i - 3)
            val x = (centerX + cos(angle) * labelRadius).toFloat()
            val y = (centerY + sin(angle) * labelRadius).toFloat()
            val number = if (i == 12) "12" else (i % 12).toString()
            canvas.drawText(number, x, y, paint)
        }
    }

    private fun drawHand(canvas: Canvas, angle: Double, handLength: Float) {
        val handRadius = radius * handLength
        val handX = (centerX + cos(angle - Math.PI / 2) * handRadius).toFloat()
        val handY = (centerY + sin(angle - Math.PI / 2) * handRadius).toFloat()
        canvas.drawLine(centerX, centerY, handX, handY, paint)
    }

    private fun drawHourHand(canvas: Canvas) {
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val hourAngle = Math.PI * (hours + calendar.get(Calendar.MINUTE) / 60.0) / 6
        drawHand(canvas, hourAngle, 0.5f)
    }

    private fun drawMinuteHand(canvas: Canvas) {
        val minutes = calendar.get(Calendar.MINUTE)
        val minuteAngle = Math.PI * (minutes + calendar.get(Calendar.SECOND) / 60.0) / 30
        drawHand(canvas, minuteAngle, 0.8f)
    }

    private fun drawSecondHand(canvas: Canvas) {
        val seconds = calendar.get(Calendar.SECOND)
        val secondAngle = Math.PI * seconds / 30
        drawHand(canvas, secondAngle, 0.9f)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putLong("time", calendar.timeInMillis)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        if (newState is Bundle) {
            val time = newState.getLong("time")
            calendar.timeInMillis = time
            newState = newState.getParcelable("superState")
        }
        super.onRestoreInstanceState(newState)
    }
}
