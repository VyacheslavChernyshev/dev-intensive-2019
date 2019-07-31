package ru.skillbranch.devintensive.ui.custom

import android.R.attr.strokeColor
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.R
import android.graphics.RectF
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
    }

    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH

    private lateinit var mBitmapShader: Shader
    private var mShaderMatrix: Matrix = Matrix()

    private var mBitmapDrawBounds: RectF = RectF()
    private var mStrokeBounds: RectF = RectF()

    private lateinit var mBitmap: Bitmap

    private var mBitmapPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private var mStrokePaint: Paint = Paint(ANTI_ALIAS_FLAG)

    private var mInitialized: Boolean = false

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            mBorderColor =
                a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()
        }

        mStrokePaint.color = mBorderColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = mBorderWidth.toFloat()

        mInitialized = true

        setupBitmap()
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        if (!mInitialized) {
            return
        }
        mBitmap = getBitmapFromDrawable(drawable)

        mBitmapShader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        val dx: Float
        val dy: Float
        val scale: Float

        if (mBitmap.width < mBitmap.height) {
            scale = mBitmapDrawBounds.width() / mBitmap.width.toFloat()
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - mBitmap.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap.height.toFloat()
            dx = mBitmapDrawBounds.left - mBitmap.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun drawStroke(canvas: Canvas) {
        if (mStrokePaint.strokeWidth > 0f) {
            canvas.drawOval(mStrokeBounds, mStrokePaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }


    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawStroke(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = mStrokePaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        mStrokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    fun getBorderColor() = mBorderColor

    private fun setBorderColorInt(@ColorInt color: Int) {
        if (color == mBorderColor) {
            invalidate()
            return
        }
        mBorderColor = color
        mStrokePaint.color = mBorderColor
        invalidate()
    }

    fun setBorderColor(@ColorRes colorRes: Int) {
        setBorderColorInt(context.getColor(colorRes))
    }

    fun setBorderColor(hex: String) {
        mStrokePaint.color = Color.parseColor(hex)
        invalidate()
    }

    @Dimension(unit = Dimension.DP)
    fun getBorderWidth(): Int {
        return mBorderWidth
    }

    fun setBorderWidth(@Dimension(unit = Dimension.DP) borderWidth: Int) {
        if (borderWidth == mBorderWidth) {
            return
        }
        mBorderWidth = borderWidth
        mStrokePaint.strokeWidth = mBorderWidth.toFloat()
    }
}