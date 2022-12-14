package com.boostcamp.dailyfilm.presentation.util

import android.graphics.*
import android.text.style.LineBackgroundSpan
import kotlin.math.abs
import kotlin.math.sign

class RoundedBackgroundSpan(
    private val backgroundColor: Int = Color.BLACK,
    private val padding: Int = 16,
    private val radius: Float = 20f
) : LineBackgroundSpan {

    private val rect = RectF()
    private val paint = Paint().apply { this.color = backgroundColor }
    private val paintStroke = Paint().apply { this.color = backgroundColor }
    private val path: Path = Path()

    private var prevWidth = -1f
    private var prevLeft = -1f
    private var prevRight = -1f
    private var prevBottom = -1f
    private var prevTop = -1f
    private var align = ALIGN_CENTER

    override fun drawBackground(
        canvas: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val width = p.measureText(text, start, end) + 2f * padding
        val shiftLeft: Float
        val shiftRight: Float


        when (align) {
            ALIGN_START -> {
                shiftLeft = 0f - padding
                shiftRight = width + shiftLeft
            }

            ALIGN_END -> {
                shiftLeft = right - width + padding
                shiftRight = (right + padding).toFloat()
            }
            else -> {
                shiftLeft = (right - width) / 2
                shiftRight = right - shiftLeft
            }
        }

        rect.set(shiftLeft, top.toFloat(), shiftRight, bottom.toFloat())


        if (lineNumber == 0) {
            canvas.drawRoundRect(rect, radius, radius, paint)
        } else {
            path.reset()
            val difference = width - prevWidth
            val diff = -sign(difference) * (2f * radius).coerceAtMost(abs(difference / 2f)) / 2f
            path.moveTo(
                prevLeft, prevBottom - radius
            )

            if (align != ALIGN_START) {
                path.cubicTo(//1
                    prevLeft, prevBottom - radius,
                    prevLeft, rect.top,
                    prevLeft + diff, rect.top
                )
            } else {
                path.lineTo(prevLeft, prevBottom + radius)
            }
            path.lineTo(
                rect.left - diff, rect.top
            )
            path.cubicTo(//2
                rect.left - diff, rect.top,
                rect.left, rect.top,
                rect.left, rect.top + radius
            )
            path.lineTo(
                rect.left, rect.bottom - radius
            )
            path.cubicTo(//3
                rect.left, rect.bottom - radius,
                rect.left, rect.bottom,
                rect.left + radius, rect.bottom
            )
            path.lineTo(
                rect.right - radius, rect.bottom
            )
            path.cubicTo(//4
                rect.right - radius, rect.bottom,
                rect.right, rect.bottom,
                rect.right, rect.bottom - radius
            )
            path.lineTo(
                rect.right, rect.top + radius
            )

            if (align != ALIGN_END) {
                path.cubicTo(//5
                    rect.right, rect.top + radius,
                    rect.right, rect.top,
                    rect.right + diff, rect.top
                )
                path.lineTo(
                    prevRight - diff, rect.top
                )
                path.cubicTo(//6
                    prevRight - diff, rect.top,
                    prevRight, rect.top,
                    prevRight, prevBottom - radius
                )

            } else {
                path.lineTo(prevRight, prevBottom - radius)
            }
            path.cubicTo(//7
                prevRight, prevBottom - radius,
                prevRight, prevBottom,
                prevRight - radius, prevBottom
            )

            path.lineTo(
                prevLeft + radius, prevBottom
            )

            path.cubicTo(//8
                prevLeft + radius, prevBottom,
                prevLeft, prevBottom,
                prevLeft, rect.top - radius
            )
            canvas.drawPath(path, paintStroke)

        }
        prevWidth = width
        prevLeft = rect.left
        prevRight = rect.right
        prevBottom = rect.bottom
        prevTop = rect.top
    }


    companion object {

        const val ALIGN_CENTER = 0
        const val ALIGN_START = 1
        const val ALIGN_END = 2

    }

}
