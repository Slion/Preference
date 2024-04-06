package slions

import android.content.res.Resources

// TODO: Move those to a separate utility library

/**
 * Convert integer from dp to px
 */
val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Covert integer from px to dp
 */
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()