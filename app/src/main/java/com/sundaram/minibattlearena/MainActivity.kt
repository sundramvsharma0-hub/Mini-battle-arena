package com.sundaram.minibattlearena

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(ArenaView())
    }

    inner class ArenaView : View(this) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private var px = 150f
        private var py = 0f
        private var vy = 0f
        private var grounded = false
        private var left = false
        private var right = false
        private var score = 0
        private var starX = 620f
        private var starY = 0f
        private var tick = 0
        private val platforms = mutableListOf<RectF>()
        private val sky = Color.rgb(16, 24, 54)

        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            val w = width.toFloat()
            val h = height.toFloat()
            val floor = h - 72f
            if (platforms.isEmpty() && w > 0) {
                platforms.add(RectF(w * .28f, floor - 115f, w * .48f, floor - 94f))
                platforms.add(RectF(w * .62f, floor - 205f, w * .82f, floor - 184f))
                starY = floor - 145f
            }
            c.drawColor(sky)
            p.color = Color.rgb(33, 48, 91)
            for (i in 0..18) {
                val sx = ((i * 137) % max(1, width)).toFloat()
                val sy = ((i * 71) % max(1, height / 2)).toFloat()
                c.drawCircle(sx, sy, if (i % 3 == 0) 3f else 1.8f, p)
            }
            p.color = Color.rgb(34, 48, 82)
            c.drawCircle(w * .82f, h * .23f, 55f, p)
            p.color = Color.rgb(52, 77, 112)
            c.drawRect(0f, floor, w, h, p)
            p.color = Color.rgb(68, 220, 190)
            c.drawRect(0f, floor, w, floor + 7f, p)
            for (r in platforms) {
                p.color = Color.rgb(73, 103, 151)
                c.drawRoundRect(r, 9f, 9f, p)
                p.color = Color.rgb(87, 226, 202)
                c.drawRect(r.left, r.top, r.right, r.top + 5f, p)
            }

            // Collectible star
            p.color = Color.rgb(255, 214, 75)
            c.drawCircle(starX, starY, 15f + (tick % 20) * .12f, p)
            p.color = Color.WHITE
            p.textSize = 27f
            c.drawText("★", starX - 11f, starY + 9f, p)

            // Friendly cartoon runner
            p.color = Color.rgb(40, 220, 235)
            c.drawRoundRect(px - 19f, py - 48f, px + 19f, py, 13f, 13f, p)
            p.color = Color.rgb(245, 194, 151)
            c.drawCircle(px, py - 59f, 15f, p)
            p.color = Color.rgb(35, 38, 58)
            c.drawArc(px - 16f, py - 75f, px + 16f, py - 48f, 180f, 180f, true, p)
            p.color = Color.WHITE
            c.drawCircle(px + 5f, py - 61f, 3f, p)
            p.color = Color.rgb(30, 40, 65)
            c.drawRoundRect(px - 15f, py - 4f, px - 2f, py + 7f, 4f, 4f, p)
            c.drawRoundRect(px + 2f, py - 4f, px + 15f, py + 7f, 4f, 4f, p)

            p.color = Color.WHITE
            p.textSize = 25f
            c.drawText("MINI BATTLE ARENA", 22f, 34f, p)
            p.textSize = 20f
            c.drawText("Stars: $score", 24f, 62f, p)

            // On-screen movement controls
            drawButton(c, 66f, h - 45f, "◀")
            drawButton(c, 146f, h - 45f, "▶")
            drawButton(c, w - 75f, h - 45f, "JUMP")
            tick++

            if (left) px -= 5f
            if (right) px += 5f
            px = max(25f, min(w - 25f, px))
            vy += 0.8f
            py += vy
            var landed = false
            val feet = py
            if (feet >= floor) {
                py = floor
                vy = 0f
                landed = true
            } else {
                for (r in platforms) {
                    if (vy >= 0f && px > r.left - 15f && px < r.right + 15f &&
                        feet >= r.top && feet - vy <= r.top + 8f) {
                        py = r.top
                        vy = 0f
                        landed = true
                        break
                    }
                }
            }
            grounded = landed
            if (kotlin.math.abs(px - starX) < 35f && kotlin.math.abs((py - 30f) - starY) < 38f) {
                score++
                starX = (90..max(91, width - 90)).random().toFloat()
                starY = (height / 3..max(height / 3 + 1, height - 180)).random().toFloat()
            }
            postInvalidateDelayed(16)
        }

        private fun drawButton(c: Canvas, cx: Float, cy: Float, label: String) {
            p.color = Color.argb(150, 220, 240, 255)
            c.drawCircle(cx, cy, 32f, p)
            p.color = Color.rgb(20, 35, 65)
            p.textSize = if (label == "JUMP") 14f else 30f
            c.drawText(label, cx - p.measureText(label) / 2f, cy + 10f, p)
        }

        override fun onTouchEvent(e: MotionEvent): Boolean {
            val w = width.toFloat()
            val h = height.toFloat()
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    left = false
                    right = false
                    for (i in 0 until e.pointerCount) {
                        val tx = e.getX(i)
                        val ty = e.getY(i)
                        if (ty > h - 100f && tx < 110f) left = true
                        else if (ty > h - 100f && tx < 205f) right = true
                        else if (ty > h - 100f && tx > w - 145f && grounded) {
                            vy = -17f
                            grounded = false
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_POINTER_UP -> {
                    left = false
                    right = false
                }
            }
            return true
        }
    }
}