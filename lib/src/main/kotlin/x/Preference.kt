/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package x

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.PreferenceViewHolder

/**
 * Basic preference adding the following features:
 * - Explicit breadcrumb
 * - Title and summary swap
 * - Multiple line summary
 *
 * See: https://stackoverflow.com/questions/6729484/android-preference-summary-how-to-set-3-lines-in-summary
 */
class Preference : androidx.preference.Preference {

    // Mode constants
    companion object {
        const val MODE_NORMAL = 0
        const val MODE_SCROLLABLE = 1
        const val MODE_SELECTABLE = 2  // Selectable implies scrollable
    }

    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int) : super(ctx, attrs, defStyle) {
        //Timber.d("constructor 3")
        construct(ctx,attrs)
    }
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        //Timber.d("constructor 2")
        construct(ctx,attrs)
    }
    constructor(ctx: Context) : super(ctx) {
        //Timber.d("constructor 1")
    }

    @SuppressLint("RestrictedApi")
    fun construct(ctx: Context, attrs: AttributeSet?) {
        //Timber.d("construct")
        val a = context.obtainStyledAttributes(attrs, x.R.styleable.Preference)
        breadcrumb = TypedArrayUtils.getText(a, x.R.styleable.Preference_breadcrumb,0) ?: ""
        displayedTitle = TypedArrayUtils.getText(a, x.R.styleable.Preference_displayedTitle,0) ?: ""
        swapTitleSummary = a.getBoolean(x.R.styleable.Preference_swapTitleSummary, false)
        isSingleLineSummary = a.getBoolean(x.R.styleable.Preference_singleLineSummary, false)
        summaryMaxLines = a.getInt(x.R.styleable.Preference_summaryMaxLines, 100)
        titleMaxLines = a.getInt(x.R.styleable.Preference_titleMaxLines, 1)

        // Get marquee repeat limits (-1 = infinite)
        summaryMarqueeRepeatLimit = a.getInt(x.R.styleable.Preference_summaryMarqueeRepeatLimit, -1)
        titleMarqueeRepeatLimit = a.getInt(x.R.styleable.Preference_titleMarqueeRepeatLimit, -1)

        // Get mode attributes and set corresponding flags
        val summaryModeValue = a.getInt(x.R.styleable.Preference_summaryMode, MODE_NORMAL)
        when (summaryModeValue) {
            MODE_SCROLLABLE -> {
                summaryScrollable = true
                summaryTextSelectable = false
            }
            MODE_SELECTABLE -> {
                // Selectable implies scrollable
                summaryScrollable = true
                summaryTextSelectable = true
            }
            else -> {
                // MODE_NORMAL or not set
                summaryScrollable = false
                summaryTextSelectable = false
            }
        }

        val titleModeValue = a.getInt(x.R.styleable.Preference_titleMode, MODE_NORMAL)
        when (titleModeValue) {
            MODE_SCROLLABLE -> {
                titleScrollable = true
                titleTextSelectable = false
            }
            MODE_SELECTABLE -> {
                // Selectable implies scrollable
                titleScrollable = true
                titleTextSelectable = true
            }
            else -> {
                // MODE_NORMAL or not set
                titleScrollable = false
                titleTextSelectable = false
            }
        }

        // Get ellipsize modes (0=END, 1=START, 2=MIDDLE, 3=MARQUEE, -1=NONE)
        // Only apply ellipsize if not in scrollable or selectable mode
        if (summaryScrollable || summaryTextSelectable) {
            summaryEllipsize = null
        } else {
            val summaryEllipsizeValue = a.getInt(x.R.styleable.Preference_summaryEllipsize, 0)
            summaryEllipsize = when (summaryEllipsizeValue) {
                1 -> TextUtils.TruncateAt.START
                2 -> TextUtils.TruncateAt.MIDDLE
                3 -> TextUtils.TruncateAt.MARQUEE
                -1 -> null
                else -> TextUtils.TruncateAt.END
            }
        }

        if (titleScrollable || titleTextSelectable) {
            titleEllipsize = null
        } else {
            val titleEllipsizeValue = a.getInt(x.R.styleable.Preference_titleEllipsize, 0)
            titleEllipsize = when (titleEllipsizeValue) {
                1 -> TextUtils.TruncateAt.START
                2 -> TextUtils.TruncateAt.MIDDLE
                3 -> TextUtils.TruncateAt.MARQUEE
                -1 -> null
                else -> TextUtils.TruncateAt.END
            }
        }

        // Get title drawables
        titleDrawableStart = a.getResourceId(x.R.styleable.Preference_titleDrawableStart, 0)
        titleDrawableEnd = a.getResourceId(x.R.styleable.Preference_titleDrawableEnd, 0)
        titleDrawableTop = a.getResourceId(x.R.styleable.Preference_titleDrawableTop, 0)
        titleDrawableBottom = a.getResourceId(x.R.styleable.Preference_titleDrawableBottom, 0)

        // Get title drawable padding (default 8dp)
        titleDrawablePadding = a.getDimensionPixelSize(x.R.styleable.Preference_titleDrawablePadding, titleDrawablePadding)

        // Get title text color (0 means not set, use theme default)
        titleTextColor = a.getColor(x.R.styleable.Preference_titleTextColor, 0)

        // Get summary drawables
        summaryDrawableStart = a.getResourceId(x.R.styleable.Preference_summaryDrawableStart, 0)
        summaryDrawableEnd = a.getResourceId(x.R.styleable.Preference_summaryDrawableEnd, 0)
        summaryDrawableTop = a.getResourceId(x.R.styleable.Preference_summaryDrawableTop, 0)
        summaryDrawableBottom = a.getResourceId(x.R.styleable.Preference_summaryDrawableBottom, 0)

        // Get summary drawable padding (default 8dp)
        summaryDrawablePadding = a.getDimensionPixelSize(x.R.styleable.Preference_summaryDrawablePadding, summaryDrawablePadding)

        // Get summary text color (0 means not set, use theme default)
        summaryTextColor = a.getColor(x.R.styleable.Preference_summaryTextColor, 0)

        // Get all caps options (default: false)
        isAllCapsTitle = a.getBoolean(x.R.styleable.Preference_allCapsTitle, false)
        isAllCapsSummary = a.getBoolean(x.R.styleable.Preference_allCapsSummary, false)

        // Get padding dimensions
        val paddingVerticalAttr = a.getDimensionPixelSize(x.R.styleable.Preference_paddingVertical, -1)
        val paddingHorizontalAttr = a.getDimensionPixelSize(x.R.styleable.Preference_paddingHorizontal, -1)

        paddingTop = a.getDimensionPixelSize(x.R.styleable.Preference_paddingTop,
            if (paddingVerticalAttr != -1) paddingVerticalAttr else -1)
        paddingBottom = a.getDimensionPixelSize(x.R.styleable.Preference_paddingBottom,
            if (paddingVerticalAttr != -1) paddingVerticalAttr else -1)
        paddingStart = a.getDimensionPixelSize(x.R.styleable.Preference_paddingStart,
            if (paddingHorizontalAttr != -1) paddingHorizontalAttr else -1)
        paddingEnd = a.getDimensionPixelSize(x.R.styleable.Preference_paddingEnd,
            if (paddingHorizontalAttr != -1) paddingHorizontalAttr else -1)

        // Get layout override dimensions
        minHeight = a.getDimensionPixelSize(x.R.styleable.Preference_minHeight, -1)
        horizontalPadding = a.getDimensionPixelSize(x.R.styleable.Preference_horizontalPadding, -1)
        verticalPadding = a.getDimensionPixelSize(x.R.styleable.Preference_verticalPadding, -1)

        a.recycle()
        if (breadcrumb.isEmpty()) {
            breadcrumb = title ?: summary ?: ""
        }
    }

    var breadcrumb: CharSequence = ""

    // Needed if you want to sort by title but display something else
    var displayedTitle: CharSequence = ""

    // Use this to swap texts of title and summary
    // Needed as preferences can only be sorted by titles but wanted them sorted by summary
    var swapTitleSummary = false

    // Control single line summary behavior
    var isSingleLineSummary = false

    // Maximum lines for summary text
    var summaryMaxLines = 100

    // Maximum lines for title text
    var titleMaxLines = 1

    // Ellipsize mode for summary (default: END)
    var summaryEllipsize: TextUtils.TruncateAt? = TextUtils.TruncateAt.END

    // Ellipsize mode for title (default: END)
    var titleEllipsize: TextUtils.TruncateAt? = TextUtils.TruncateAt.END

    // Marquee repeat limit for summary (-1 = infinite, default: -1)
    var summaryMarqueeRepeatLimit = -1

    // Marquee repeat limit for title (-1 = infinite, default: -1)
    var titleMarqueeRepeatLimit = -1

    // Enable vertical scrolling for summary (default: false)
    var summaryScrollable = false

    // Enable vertical scrolling for title (default: false)
    var titleScrollable = false

    // Enable text selection for summary (default: false)
    var summaryTextSelectable = false

    // Enable text selection for title (default: false)
    var titleTextSelectable = false

    // Drawables for title text
    var titleDrawableStart: Int = 0
    var titleDrawableEnd: Int = 0
    var titleDrawableTop: Int = 0
    var titleDrawableBottom: Int = 0

    // Padding between title drawables and text (default 8dp)
    var titleDrawablePadding: Int = (8 * context.resources.displayMetrics.density).toInt()

    // Custom text color for title (0 means not set, use theme default)
    var titleTextColor: Int = 0

    // Drawables for summary text
    var summaryDrawableStart: Int = 0
    var summaryDrawableEnd: Int = 0
    var summaryDrawableTop: Int = 0
    var summaryDrawableBottom: Int = 0

    // Padding between summary drawables and text (default 8dp)
    var summaryDrawablePadding: Int = (8 * context.resources.displayMetrics.density).toInt()

    // Custom text color for summary (0 means not set, use theme default)
    var summaryTextColor: Int = 0

    // Control all caps for title (default: false)
    var isAllCapsTitle: Boolean = false

    // Control all caps for summary (default: false)
    var isAllCapsSummary: Boolean = false

    // Padding properties for the preference item content
    var paddingTop: Int = -1
    var paddingBottom: Int = -1
    var paddingStart: Int = -1
    var paddingEnd: Int = -1

    // Layout override properties for fine-grained spacing control
    var minHeight: Int = -1
    var horizontalPadding: Int = -1
    var verticalPadding: Int = -1

    /**
     * Set the title text color from a color resource.
     * @param colorResId Color resource ID (e.g., R.color.my_color)
     */
    fun setTitleTextColorResource(colorResId: Int) {
        titleTextColor = androidx.core.content.ContextCompat.getColor(context, colorResId)
        notifyChanged()
    }

    /**
     * Set the title text color from a theme attribute.
     * @param attrResId Theme attribute resource ID (e.g., R.attr.colorPrimary, android.R.attr.textColorPrimary)
     */
    fun setTitleTextColorFromTheme(attrResId: Int) {
        val typedValue = android.util.TypedValue()
        if (context.theme.resolveAttribute(attrResId, typedValue, true)) {
            titleTextColor = if (typedValue.type >= android.util.TypedValue.TYPE_FIRST_COLOR_INT &&
                typedValue.type <= android.util.TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color value
                typedValue.data
            } else if (typedValue.resourceId != 0) {
                // It's a resource reference
                androidx.core.content.ContextCompat.getColor(context, typedValue.resourceId)
            } else {
                0
            }
            notifyChanged()
        }
    }

    /**
     * Clear the custom title text color and use the default theme color.
     */
    fun clearTitleTextColor() {
        titleTextColor = 0
        notifyChanged()
    }

    /**
     * Set the summary text color from a color resource.
     * @param colorResId Color resource ID (e.g., R.color.my_color)
     */
    fun setSummaryTextColorResource(colorResId: Int) {
        summaryTextColor = androidx.core.content.ContextCompat.getColor(context, colorResId)
        notifyChanged()
    }

    /**
     * Set the summary text color from a theme attribute.
     * @param attrResId Theme attribute resource ID (e.g., R.attr.colorPrimary, android.R.attr.textColorPrimary)
     */
    fun setSummaryTextColorFromTheme(attrResId: Int) {
        val typedValue = android.util.TypedValue()
        if (context.theme.resolveAttribute(attrResId, typedValue, true)) {
            summaryTextColor = if (typedValue.type >= android.util.TypedValue.TYPE_FIRST_COLOR_INT &&
                typedValue.type <= android.util.TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color value
                typedValue.data
            } else if (typedValue.resourceId != 0) {
                // It's a resource reference
                androidx.core.content.ContextCompat.getColor(context, typedValue.resourceId)
            } else {
                0
            }
            notifyChanged()
        }
    }

    /**
     * Clear the custom summary text color and use the default theme color.
     */
    fun clearSummaryTextColor() {
        summaryTextColor = 0
        notifyChanged()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        // Find the RelativeLayout that contains title and summary (hardcoded padding in preference_material.xml)
        // The layout structure is: LinearLayout -> RelativeLayout (with title/summary) + LinearLayout (widget_frame)
        // So we need to find the RelativeLayout that contains the title
        val titleView = holder.findViewById(android.R.id.title)
        val relativeLayout = titleView?.parent as? android.widget.RelativeLayout

        // Check if theme defines custom vertical padding for preferences
        if (relativeLayout != null) {
            val tv = android.util.TypedValue()
            if (context.theme.resolveAttribute(R.attr.preferencePaddingVertical, tv, true)) {
                // Theme specifies padding - use it
                val themePadding = tv.getDimension(context.resources.displayMetrics).toInt()
                relativeLayout.setPaddingRelative(
                    relativeLayout.paddingStart,
                    themePadding,
                    relativeLayout.paddingEnd,
                    themePadding
                )
            }
            // If attribute not defined in theme, leave the default padding from layout
        }

        // Apply layout overrides for spacing control
        if (minHeight != -1) {
            holder.itemView.minimumHeight = minHeight
        }

        if (horizontalPadding != -1) {
            // Override the horizontal padding from theme
            val currentTop = holder.itemView.paddingTop
            val currentBottom = holder.itemView.paddingBottom
            holder.itemView.setPaddingRelative(horizontalPadding, currentTop, horizontalPadding, currentBottom)
        }

        // Apply vertical padding to the content area (RelativeLayout with title/summary)
        if (verticalPadding != -1) {
            if (relativeLayout != null) {
                val currentStart = relativeLayout.paddingStart
                val currentEnd = relativeLayout.paddingEnd
                relativeLayout.setPaddingRelative(currentStart, verticalPadding, currentEnd, verticalPadding)
            }
        }

        // Apply padding to the item view
        if (paddingTop != -1 || paddingBottom != -1 || paddingStart != -1 || paddingEnd != -1) {
            val currentTop = if (paddingTop != -1) paddingTop else holder.itemView.paddingTop
            val currentBottom = if (paddingBottom != -1) paddingBottom else holder.itemView.paddingBottom
            val currentStart = if (paddingStart != -1) paddingStart else holder.itemView.paddingStart
            val currentEnd = if (paddingEnd != -1) paddingEnd else holder.itemView.paddingEnd
            holder.itemView.setPaddingRelative(currentStart, currentTop, currentEnd, currentBottom)
        }

        val summary = holder.findViewById(android.R.id.summary) as TextView
        val title = holder.findViewById(android.R.id.title) as TextView

        // Configure summary
        summary.isSingleLine = isSingleLineSummary
        summary.maxLines = summaryMaxLines
        summary.ellipsize = summaryEllipsize
        summary.isAllCaps = isAllCapsSummary

        // Enable scrolling for summary if requested
        if (summaryScrollable) {
            if (isSingleLineSummary || summaryMaxLines == 1) {
                // Single line: enable horizontal scrolling
                summary.setHorizontallyScrolling(true)
                summary.setHorizontalScrollBarEnabled(true)
            } else {
                // Multi-line: enable vertical scrolling
                summary.setVerticalScrollBarEnabled(true)
            }
            summary.movementMethod = android.text.method.ScrollingMovementMethod.getInstance()
        }

        // Enable text selection for summary if requested
        summary.setTextIsSelectable(summaryTextSelectable)

        // Marquee requires specific configuration
        if (summaryEllipsize == TextUtils.TruncateAt.MARQUEE) {
            summary.isSingleLine = true
            summary.isSelected = true
            summary.marqueeRepeatLimit = summaryMarqueeRepeatLimit
        }

        // Configure title
        title.maxLines = titleMaxLines
        title.ellipsize = titleEllipsize
        title.isAllCaps = isAllCapsTitle

        // Enable scrolling for title if requested
        if (titleScrollable) {
            if (titleMaxLines == 1) {
                // Single line: enable horizontal scrolling
                title.setHorizontallyScrolling(true)
                title.setHorizontalScrollBarEnabled(true)
            } else {
                // Multi-line: enable vertical scrolling
                title.setVerticalScrollBarEnabled(true)
            }
            title.movementMethod = android.text.method.ScrollingMovementMethod.getInstance()
        }

        // Enable text selection for title if requested
        title.setTextIsSelectable(titleTextSelectable)

        // Marquee requires specific configuration
        if (titleEllipsize == TextUtils.TruncateAt.MARQUEE) {
            title.isSingleLine = true
            title.isSelected = true
            title.marqueeRepeatLimit = titleMarqueeRepeatLimit
        }

        if (swapTitleSummary) {
            // Just do it
            val tt = title.text
            title.text = summary.text
            summary.text = tt
        }

        // Set actual title if not empty
        if (displayedTitle.isNotEmpty()) {
            title.text = displayedTitle
        }


        // Apply custom title text color if set
        if (titleTextColor != 0) {
            title.setTextColor(titleTextColor)
        }

        // Apply drawables to title
        title.setCompoundDrawablesRelativeWithIntrinsicBounds(titleDrawableStart, titleDrawableTop, titleDrawableEnd, titleDrawableBottom)
        title.compoundDrawablePadding = titleDrawablePadding

        // Apply custom summary text color if set
        if (summaryTextColor != 0) {
            summary.setTextColor(summaryTextColor)
        }

        // Apply drawables to summary
        summary.setCompoundDrawablesRelativeWithIntrinsicBounds(summaryDrawableStart, summaryDrawableTop, summaryDrawableEnd, summaryDrawableBottom)
        summary.compoundDrawablePadding = summaryDrawablePadding

        // Override the copy listener to avoid duplicate copy messages
        holder.itemView.setOnCreateContextMenuListener(if (isCopyingEnabled) x.OnPreferenceCopyListener(this) else null)
        holder.itemView.isLongClickable = isCopyingEnabled
    }
}