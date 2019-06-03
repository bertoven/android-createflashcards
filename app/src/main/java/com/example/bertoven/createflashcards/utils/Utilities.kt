package com.example.bertoven.createflashcards.utils

import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView

fun dpToPx(context: Context, dps: Int): (Int) {
    val density = context.resources.displayMetrics.densityDpi
    return dps * (density / DisplayMetrics.DENSITY_DEFAULT)
}

fun pxToDp(context: Context, px: Int): (Int) {
    val density = context.resources.displayMetrics.densityDpi
    return px / (density / DisplayMetrics.DENSITY_DEFAULT)
}

fun expandView(v: View) {
    v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    val targetHeight = v.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            v.layoutParams.height = if (interpolatedTime == 1F)
                LinearLayout.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms
    a.duration = pxToDp(v.context, targetHeight).toLong()
    v.startAnimation(a)
}

fun collapseView(v: View) {
    val initialHeight = v.measuredHeight

    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1F) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
            }
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = pxToDp(v.context, initialHeight).toLong()
    v.startAnimation(a)
}

fun makeLinkSpan(text: CharSequence, listener: View.OnClickListener): SpannableString {
    val clickableText = SpannableString(text)
    clickableText.setSpan(ClickableString(listener), 0, text.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return clickableText
}

fun makeLinksFocusable(tv: TextView) {
    val m = tv.movementMethod
    if (m == null || m !is LinkMovementMethod) {
        if (tv.linksClickable) {
            tv.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}

private class ClickableString(private val mListener: View.OnClickListener) : ClickableSpan() {
    override fun onClick(widget: View?) {
        mListener.onClick(widget)
    }
}