package com.kekadoc.test.twitch.streams

import android.app.ActivityManager
import android.content.Context
import android.util.TypedValue
import androidx.annotation.Dimension

fun Context.dpToPx(@Dimension(unit = Dimension.DP) dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}
