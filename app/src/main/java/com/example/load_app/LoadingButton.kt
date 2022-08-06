package com.example.load_app

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import kotlin.math.roundToInt

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var fontSize = 55f
    private var currentProgress = 0f

    private var valueAnimator: ValueAnimator = ValueAnimator()
    private var buttonRect = Rect(0, 0, 0, 0)
    private var buttonAnimRect = Rect(0, 0, 0, 0)
    private val textRect = Rect()
    private val circleRect = RectF(0f, 0f, 0f, 0f)
    private var circleRadius = 0f

    private var loadingText = resources.getString(R.string.button_loading)
    private var text = resources.getString(R.string.download)
    private var buttonColor = resources.getColor(R.color.colorPrimary, null)
    private var animationColor = resources.getColor(R.color.colorPrimaryDark, null)
    private var textColor = resources.getColor(R.color.white, null)
    private var iconColor = resources.getColor(R.color.colorAccent, null)

    var isAnimated = false
        private set

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = fontSize
        typeface = Typeface.create("", Typeface.BOLD)
    }

    companion object {
        const val standardAnimTime = 1500L
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                getString(R.styleable.LoadingButton_text)?.let {
                    text = it
                }
                getString(R.styleable.LoadingButton_loadingText)?.let {
                    loadingText = it
                }
                animationColor =
                    getColor(R.styleable.LoadingButton_loadingColor, animationColor)
                iconColor =
                    getColor(R.styleable.LoadingButton_loadingIconColor, iconColor)
                textColor =
                    getColor(R.styleable.LoadingButton_textColor, textColor)
                buttonColor =
                    getColor(R.styleable.LoadingButton_buttonColor, buttonColor)
                fontSize = getFloat(R.styleable.LoadingButton_fontSize, fontSize)
                currentProgress = getFloat(R.styleable.LoadingButton_progress, currentProgress)
            } finally {
                recycle()
            }
        }
    }

    fun startAnimation(
        durationTime: Long = standardAnimTime
    ) {
        valueAnimator.cancel()
        isAnimated = true
        valueAnimator = ValueAnimator.ofFloat(
            currentProgress, 1f
        ).apply {
            duration = durationTime
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                currentProgress = it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                currentProgress = 0f
                isAnimated = false
            }
            doOnCancel {
                isAnimated = false
            }
            start()
        }
    }

    fun stopAnimation(){
        isAnimated = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = width
        heightSize = height
        buttonRect = Rect(0, 0, widthSize, heightSize)
        buttonAnimRect = Rect(0, 0, widthSize, heightSize)
        circleRadius = height / 4f
        circleRect.top = heightSize / 2f - circleRadius
        circleRect.bottom = heightSize / 2f + circleRadius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            if (isAnimated) {
                paint.color = buttonColor
                drawRect(buttonRect, paint)

                paint.color = animationColor
                buttonAnimRect.right = (widthSize.toFloat() * currentProgress).roundToInt()
                drawRect(buttonAnimRect, paint)

                paint.color = textColor
                paint.getTextBounds(loadingText, 0, loadingText.length, textRect)
                drawText(
                    loadingText,
                    widthSize / 2f,
                    heightSize / 2 - textRect.exactCenterY(),
                    paint
                )

                val loadingAngle = 360 * currentProgress
                paint.color = iconColor
                circleRect.left = widthSize / 2f + textRect.right / 2f + 4
                circleRect.right = circleRect.left + (2 * circleRadius)
                drawArc(circleRect, 0f, loadingAngle, true, paint)
            } else {
                paint.color = buttonColor
                drawRect(buttonRect, paint)

                paint.color = textColor
                paint.getTextBounds(text, 0, text.length, textRect)
                drawText(
                    text,
                    widthSize / 2f,
                    heightSize / 2 - textRect.exactCenterY(),
                    paint
                )
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