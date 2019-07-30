package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.*
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.R
import kotlin.math.max


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }
    private val paint: Paint = Paint().apply { isAntiAlias = true }
    val path = Path()
    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor =
                a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()
            with(paintBorder) {
                color = borderColor
                style = Paint.Style.STROKE
                strokeWidth = borderWidth.toFloat()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val halfWidth: Float = (width / 2).toFloat()
        val halfHeight: Float = (height / 2).toFloat()
        val radius: Float = max(halfWidth, halfHeight)

        path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.drawCircle(halfWidth, halfHeight, radius, paintBorder)
    }

    fun getBorderColor() = borderColor

    private fun setBorderColorInt(color: Int) {
        paintBorder.color = color
        invalidate()
    }

    fun setBorderColor(@ColorRes colorRes: Int) {
        setBorderColorInt(resources.getColor(colorRes, context.theme))
    }

    fun setBorderColor(hex: String) {
        val color: Int? = hex.toIntOrNull(16)
        if (color != null)
            setBorderColorInt(color)
    }

    @Dimension(unit = Dimension.DP)
    fun getBorderWidth(): Int{
        return borderWidth
    }

    fun setBorderWidth(@Dimension(unit = Dimension.DP) dp:Int) {
        borderWidth = dp
    }
}