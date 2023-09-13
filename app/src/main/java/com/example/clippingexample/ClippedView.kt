package com.example.clippingexample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.View

class ClippedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }
    private val path = Path()
    private val text = resources.getString(R.string.clipping)
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)
    private val circleRadius = resources.getDimension(R.dimen.circleRadius)
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        drawQuickRejectExample(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas) {
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        canvas.drawColor(Color.WHITE)
        paint.color = Color.RED
        canvas.drawLine(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom, paint)
        paint.color = Color.GREEN
        canvas.drawCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, paint)
        paint.color = Color.BLUE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(text, clipRectRight, textOffset, paint)
    }

    /**
     * The general idea behind this is that we first define a clipped region and then we only draw
     * in that clipped region, to get desired shape. We don't draw out everything and clip the
     * unwanted regions as it is less efficient
     */

    private fun drawBackAndUnclippedRectangle(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne, rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawDifferenceClippingExample(canvas: Canvas) {
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo, rowOne)
        // Use the subtraction of two clipping rectangles to create a frame.
        // We create smaller clipped rectangle than that of drawClippedRectangle()
        canvas.clipRect(
            2 * rectInset,
            2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )

        // We remove out a smaller rectangle out of clipped rectangle, so now the clipped area is frame
        if (VERSION.SDK_INT < VERSION_CODES.O)
        // This method was deprecated in API level 26
            canvas.clipRect(
                4 * rectInset,
                4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                Region.Op.DIFFERENCE
            )
        else {
            canvas.clipOutRect(
                4 * rectInset,
                4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
        }
        // draws the shape in clipped region
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCircularClippingExample(canvas: Canvas) {
        canvas.save()
        // translate the canvas
        canvas.translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(
            circleRadius, clipRectBottom - circleRadius,
            circleRadius, Path.Direction.CCW
        )
        if (VERSION.SDK_INT < VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            // clips out a path, since we added a circle to the path, clips a circle
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()
        // translate the canvas
        canvas.translate(columnTwo, rowTwo)

        // define a clipping region leaving some offset on right side
        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )

        // define a clipping region leaving some offset on left side
        if (VERSION.SDK_INT < VERSION_CODES.O) {
            // This method was deprecated in API level 26
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,
                clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            // simple clip method to draws over the intersection of clipping regions
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,
                clipRectBottom
            )
        }
        // draw the drawing on the intersection of both the clippings
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()

        // add circle to path
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius, Path.Direction.CCW
        )

        // add rectangle to path
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset, Path.Direction.CCW
        )

        // clip the path
        // since path contains the intersection of both circle and rectangle
        // clipped region is defined by their combination
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowThree)
        path.rewind()

        // draw a rounded rectangle from a rectangular shape
        path.addRoundRect(
            rectF, clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowFour)

        // define slightly smaller clipped rectangle
        // this is similar to difference clipping without having an inner region clipped
        canvas.clipRect(
            2 * rectInset,
            2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawTranslatedTextExample(canvas: Canvas) {
    }

    private fun drawSkewedTextExample(canvas: Canvas) {
    }

    private fun drawQuickRejectExample(canvas: Canvas) {
    }
}