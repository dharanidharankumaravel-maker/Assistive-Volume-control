package com.volumepilot.service.overlay

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.volumepilot.domain.model.OverlayPosition
import kotlin.math.abs

/**
 * Dedicated container view hosting the floating volume overlay.
 *
 * Intercepts touch gestures when the widget is collapsed, enabling real-time
 * dragging across the screen and smooth magnetic snapping to the nearest screen edge.
 *
 * When the overlay panel is expanded:
 * - Touch events inside pass through to Compose children (sliders, tabs, buttons).
 * - Tapping outside the expanded widget triggers ACTION_OUTSIDE and automatically dismisses it.
 */
class OverlayContainerView(
    context: Context,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
    private val isExpandedProvider: () -> Boolean,
    private val onDismissOutside: () -> Unit,
    private val onPositionChanged: (OverlayPosition) -> Unit
) : FrameLayout(context) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var snapAnimator: ValueAnimator? = null

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // If user tapped outside while expanded, dismiss the expanded panel
        if (ev.actionMasked == MotionEvent.ACTION_OUTSIDE) {
            if (isExpandedProvider()) {
                onDismissOutside()
                return true
            }
        }

        // If panel is expanded, let child views (sliders, tabs, buttons) handle touches
        if (isExpandedProvider()) {
            return false
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                snapAnimator?.cancel()
                snapAnimator = null
                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = ev.rawX
                initialTouchY = ev.rawY
                isDragging = false
                // Return false to allow Compose child to receive ACTION_DOWN for tap / long-press gestures
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.rawX - initialTouchX
                val dy = ev.rawY - initialTouchY
                if (abs(dx) > touchSlop || abs(dy) > touchSlop) {
                    isDragging = true
                    // Intercept touch stream. Child receives ACTION_CANCEL,
                    // and future MOVE/UP events go directly to onTouchEvent.
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Detect touches outside the window when expanded and auto-dismiss
        if (ev.actionMasked == MotionEvent.ACTION_OUTSIDE) {
            if (isExpandedProvider()) {
                onDismissOutside()
                return true
            }
        }

        if (isExpandedProvider()) {
            return false
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                snapAnimator?.cancel()
                snapAnimator = null
                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = ev.rawX
                initialTouchY = ev.rawY
                isDragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (ev.rawX - initialTouchX).toInt()
                val dy = (ev.rawY - initialTouchY).toInt()

                if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
                    isDragging = true
                }

                if (isDragging) {
                    val screenWidth = resources.displayMetrics.widthPixels
                    val screenHeight = resources.displayMetrics.heightPixels
                    val defaultSizePx = (48 * resources.displayMetrics.density).toInt()
                    val viewWidth = if (width > 0) width else defaultSizePx
                    val viewHeight = if (height > 0) height else defaultSizePx

                    val newX = initialX + dx
                    val newY = initialY + dy

                    layoutParams.x = newX.coerceIn(0, (screenWidth - viewWidth).coerceAtLeast(0))
                    layoutParams.y = newY.coerceIn(0, (screenHeight - viewHeight).coerceAtLeast(0))

                    try {
                        if (isAttachedToWindow) {
                            windowManager.updateViewLayout(this, layoutParams)
                        }
                    } catch (e: Exception) {
                        // View may be detached or being removed
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    snapToEdge()
                    return true
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun snapToEdge() {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val defaultSizePx = (48 * resources.displayMetrics.density).toInt()
        val viewWidth = if (width > 0) width else defaultSizePx
        val viewHeight = if (height > 0) height else defaultSizePx

        val centerX = layoutParams.x + (viewWidth / 2f)
        val isLeft = centerX < (screenWidth / 2f)

        // Dock closely along the edge with minimal inset
        val edgeMarginPx = (4 * resources.displayMetrics.density).toInt()
        val targetX = if (isLeft) {
            edgeMarginPx
        } else {
            (screenWidth - viewWidth - edgeMarginPx).coerceAtLeast(0)
        }

        val topMarginPx = (48 * resources.displayMetrics.density).toInt()
        val bottomMarginPx = (48 * resources.displayMetrics.density).toInt()
        val maxY = (screenHeight - viewHeight - bottomMarginPx).coerceAtLeast(topMarginPx)
        val targetY = layoutParams.y.coerceIn(topMarginPx, maxY)
        layoutParams.y = targetY

        val startX = layoutParams.x
        snapAnimator?.cancel()

        if (startX != targetX) {
            snapAnimator = ValueAnimator.ofInt(startX, targetX).apply {
                duration = 200L
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    if (isAttachedToWindow) {
                        layoutParams.x = animator.animatedValue as Int
                        try {
                            windowManager.updateViewLayout(this@OverlayContainerView, layoutParams)
                        } catch (e: Exception) {
                            // View may be detached
                        }
                    }
                }
                start()
            }
        } else {
            try {
                if (isAttachedToWindow) {
                    windowManager.updateViewLayout(this, layoutParams)
                }
            } catch (e: Exception) {
                // View may be detached
            }
        }

        val newPosition = OverlayPosition(targetX, targetY, isLeft)
        onPositionChanged(newPosition)
    }

    fun cancelAnimation() {
        snapAnimator?.cancel()
        snapAnimator = null
    }
}
