package com.sundaram.minibattlearena

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.max
import kotlin.math.min

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ArenaView())
    }

    inner class ArenaView : View(this) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var x = 180f
        private var y = 400f
        private var score = 0
        private var coinX = 650f
        private var coinY = 300f
        private var ground = 0f
        private var jumping = false
        private var jumpVelocity = 0f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            ground = h - 90f
            canvas.drawColor(Color.rgb(22, 32, 57))
            paint.color = Color.rgb(45, 65, 95)
            canvas.drawRect(0f, ground, w, h, paint)
            paint.color = Color.YELLOW
            canvas.drawCircle(coinX, coinY, 22f, paint)
            paint.color = Color.CYAN
            canvas.drawRoundRect(x - 28, y - 48, x + 28, y + 8, 18f, 18f, paint)
            paint.color = Color.WHITE
            paint.textSize = 34f
            canvas.drawText("MINI BATTLE ARENA", 30f, 48f, paint)
            canvas.drawText("Stars: $score", 30f, 92f, paint)
            paint.textSize = 23f
            canvas.drawText("Tap left/right to move • Tap upper area to jump", 30f, h - 25f, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                if (event.y < height * 0.65f && !jumping) {
                    jumping = true
                    jumpVelocity = -23f
                } else {
                    x += if (event.x < width / 2f) -24f else 24f
                }
                x = max(35f, min(width - 35f, x))
                if (kotlin.math.abs(x - coinX) < 55f && kotlin.math.abs(y - coinY) < 70f) {
                    score++
                    coinX = (100..(max(200, width - 100))).random().toFloat()
                    coinY = (150..(max(180, height - 180))).random().toFloat()
                }
                invalidate()
            }
            return true
        }
    }
}
