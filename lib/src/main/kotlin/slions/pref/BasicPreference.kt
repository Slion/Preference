/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package slions.pref

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

/**
 * Basic preference adding the following features:
 * - Explicit breadcrumb
 * - Title and summary swap
 * - Multiple line summary
 *
 * See: https://stackoverflow.com/questions/6729484/android-preference-summary-how-to-set-3-lines-in-summary
 */
class BasicPreference :
    Preference {

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
        val a = context.obtainStyledAttributes(attrs, R.styleable.BasicPreference)
        breadcrumb = TypedArrayUtils.getText(a, R.styleable.BasicPreference_breadcrumb,0) ?: ""
        displayedTitle = TypedArrayUtils.getText(a, R.styleable.BasicPreference_displayedTitle,0) ?: ""
        swapTitleSummary = a.getBoolean(R.styleable.BasicPreference_swapTitleSummary, false)
        isSingleLineSummary = a.getBoolean(R.styleable.BasicPreference_isSingleLineSummary, false)
        summaryMaxLines = a.getInt(R.styleable.BasicPreference_summaryMaxLines, 100)
        titleMaxLines = a.getInt(R.styleable.BasicPreference_titleMaxLines, 1)

        // Get marquee repeat limits (-1 = infinite)
        summaryMarqueeRepeatLimit = a.getInt(R.styleable.BasicPreference_summaryMarqueeRepeatLimit, -1)
        titleMarqueeRepeatLimit = a.getInt(R.styleable.BasicPreference_titleMarqueeRepeatLimit, -1)

        // Get mode attributes and set corresponding flags
        val summaryModeValue = a.getInt(R.styleable.BasicPreference_summaryMode, MODE_NORMAL)
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

        val titleModeValue = a.getInt(R.styleable.BasicPreference_titleMode, MODE_NORMAL)
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
            val summaryEllipsizeValue = a.getInt(R.styleable.BasicPreference_summaryEllipsize, 0)
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
            val titleEllipsizeValue = a.getInt(R.styleable.BasicPreference_titleEllipsize, 0)
            titleEllipsize = when (titleEllipsizeValue) {
                1 -> TextUtils.TruncateAt.START
                2 -> TextUtils.TruncateAt.MIDDLE
                3 -> TextUtils.TruncateAt.MARQUEE
                -1 -> null
                else -> TextUtils.TruncateAt.END
            }
        }

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

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val summary = holder.findViewById(android.R.id.summary) as TextView
        val title = holder.findViewById(android.R.id.title) as TextView

        // Configure summary
        summary.isSingleLine = isSingleLineSummary
        summary.maxLines = summaryMaxLines
        summary.ellipsize = summaryEllipsize

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

    }
}