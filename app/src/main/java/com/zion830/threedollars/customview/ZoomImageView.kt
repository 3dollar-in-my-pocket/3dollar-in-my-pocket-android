package com.zion830.threedollars.customview

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.widget.AppCompatImageView;

class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var mode = Mode.NONE
    private val viewMatrix: Matrix = Matrix()
    private val lastPoint = PointF() // 마지막 좌표
    private var matrixValue = FloatArray(9)
    private var minScale = 0.5f // 축소 최대 비율
    private var maxScale = 2f // 확대 최대 비율
    private var saveScale = 1f // 현재 이미지 비율
    private var right = 0f
    private var bottom = 0f
    private var originalBitmapWidth = 0f
    private var originalBitmapHeight = 0f
    private var mScaleDetector: ScaleGestureDetector? = null

    // 더블 클릭 처리를 위한 속성
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0

    init {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        matrixValue = FloatArray(9)
        imageMatrix = viewMatrix
        scaleType = ScaleType.MATRIX
    }

    fun setZoomScale(min: Float, max: Float) {
        minScale = min
        maxScale = max
    }

    private fun fitCenter() {
        val drawable = drawable
        val bmWidth = drawable?.intrinsicWidth ?: 0
        val bmHeight = drawable?.intrinsicHeight ?: 0
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        val scale = if (width > height) height / bmHeight else width / bmWidth // 이미지 비율 계산
        viewMatrix.setScale(scale, scale)
        saveScale = 1f
        originalBitmapWidth = scale * bmWidth
        originalBitmapHeight = scale * bmHeight
        val redundantYSpace = height - originalBitmapHeight
        val redundantXSpace = width - originalBitmapWidth
        viewMatrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2) // 중앙으로 이동
        imageMatrix = viewMatrix
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        fitCenter()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector!!.onTouchEvent(event)
        viewMatrix.getValues(matrixValue)
        val x = matrixValue[Matrix.MTRANS_X]
        val y = matrixValue[Matrix.MTRANS_Y]
        val currentPoint = PointF(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                clickCount++
                lastPoint.set(currentPoint)
                mode = Mode.DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastPoint.set(currentPoint)
                mode = Mode.ZOOM
            }
            MotionEvent.ACTION_MOVE -> if (mode == Mode.ZOOM || mode == Mode.DRAG && saveScale > minScale) {
                var deltaX = currentPoint.x - lastPoint.x
                var deltaY = currentPoint.y - lastPoint.y
                val scaleWidth = Math.round(originalBitmapWidth * saveScale).toFloat()
                val scaleHeight = Math.round(originalBitmapHeight * saveScale).toFloat()
                var limitX = false
                var limitY = false
                if (!(scaleWidth < width && scaleHeight < height)) {
                    if (scaleWidth < width) {
                        deltaX = 0f
                        limitY = true
                    } else if (scaleHeight < height) {
                        deltaY = 0f
                        limitX = true
                    } else {
                        limitX = true
                        limitY = true
                    }
                }
                if (limitY) {
                    if (y + deltaY > 0) {
                        deltaY = -y
                    } else if (y + deltaY < -bottom) {
                        deltaY = -(y + bottom)
                    }
                }
                if (limitX) {
                    if (x + deltaX > 0) {
                        deltaX = -x
                    } else if (x + deltaX < -right) {
                        deltaX = -(x + right)
                    }
                }
                if (saveScale > 1.0f) {
                    viewMatrix.postTranslate(deltaX, deltaY) // 확대할 경우에만 드래그 가능
                }
                lastPoint[currentPoint.x] = currentPoint.y
            }
            MotionEvent.ACTION_UP -> {
                val time = System.currentTimeMillis() - startTime
                duration = duration + time
                if (clickCount == 2) {
                    if (duration <= MAX_DURATION) {
                        fitCenter()
                    }
                    clickCount = 0
                    duration = 0
                }
                mode = Mode.NONE
            }
            MotionEvent.ACTION_POINTER_UP -> mode = Mode.NONE
        }
        imageMatrix = viewMatrix
        invalidate()
        return true
    }

    private enum class Mode {
        NONE, DRAG, ZOOM
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = Mode.ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = saveScale * scaleFactor
            if (newScale < maxScale && newScale > minScale) {
                saveScale = newScale
                val width = width.toFloat()
                val height = height.toFloat()
                val scaledBitmapWidth = originalBitmapWidth * saveScale
                val scaledBitmapHeight = originalBitmapHeight * saveScale
                right = scaledBitmapWidth - width
                bottom = scaledBitmapHeight - height
                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    viewMatrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2)
                } else {
                    viewMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                }
            }
            return true
        }
    }

    companion object {
        private const val MAX_DURATION = 200
    }
}