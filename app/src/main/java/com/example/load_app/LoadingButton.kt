package com.example.load_app

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

private const val TAG = "LoadingButton"
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingText = ""
    private var completeText = resources.getString(R.string.download)
    private var currentProgress = 0f

    private var valueAnimator: ValueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Completed -> {
                invalidate()
            }
            ButtonState.Clicked -> {
                invalidate()
            }
            ButtonState.Loading -> {
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofFloat(
                    currentProgress, 1f
                ).apply {
                    duration = 1000
                    addUpdateListener {
                        currentProgress = it.animatedValue as Float
                        invalidate()
                    }
                    doOnEnd {
                        currentProgress = 0f
                    }
                    start()
                }
            }
        }
    }

    var progress : Float by Delegates.observable(0f) { p, old, new ->
        ValueAnimator.ofFloat(
            currentProgress, new
        ).apply {
            duration = (1000 * new).toLong()
            addUpdateListener {
                currentProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                loadingText = getString(R.styleable.LoadingButton_text) ?: ""
                currentProgress = getFloat(R.styleable.LoadingButton_progress, 0f)
            } finally {
                recycle()
            }
        }
    }

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = Color.WHITE
    }

    private val paint = Paint().apply {
        color = drawColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            canvas.drawColor(backgroundColor)
            when(buttonState){
                ButtonState.Completed -> {
                    drawText(
                        completeText,
                        widthSize.toFloat() / 2, (textPaint.textSize + heightSize.toFloat()) / 2, textPaint
                    )
                }
                ButtonState.Clicked -> {

                }
                ButtonState.Loading -> {
                    drawRect(
                        0f, 0f,
                        widthSize.toFloat() * currentProgress,
                        heightSize.toFloat(), paint
                    )
                    drawText(
                        loadingText,
                        widthSize.toFloat() / 2, (textPaint.textSize + heightSize.toFloat()) / 2, textPaint
                    )
                }
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}