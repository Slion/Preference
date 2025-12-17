/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package x

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

import androidx.preference.PreferenceViewHolder

/**
 *
 * See: https://stackoverflow.com/questions/6729484/android-preference-summary-how-to-set-3-lines-in-summary
 */
class PreferenceCategory : androidx.preference.PreferenceCategory {
    var summaryMaxLines: Int = 10
    var isSingleLineSummary: Boolean = false
    var isAllCapsSummary: Boolean = false

    var titleMaxLines: Int = 1
    var isAllCapsTitle: Boolean = true

    // Padding properties for the category item content
    var paddingTop: Int = -1
    var paddingBottom: Int = -1
    var paddingStart: Int = -1
    var paddingEnd: Int = -1

    // Layout override properties for fine-grained spacing control
    var minHeight: Int = -1
    var horizontalPadding: Int = -1
    var verticalPadding: Int = -1

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        construct(attrs, defStyleAttr, 0)
    }

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        construct(attrs, androidx.preference.R.attr.preferenceCategoryStyle, 0)
    }

    constructor(ctx: Context) : super(ctx) {}

    private fun construct(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.PreferenceCategory,
            defStyleAttr,
            defStyleRes
        )

        isSingleLineSummary = a.getBoolean(R.styleable.PreferenceCategory_singleLineSummary, false)
        summaryMaxLines = a.getInt(R.styleable.PreferenceCategory_summaryMaxLines, 10)
        isAllCapsSummary = a.getBoolean(R.styleable.PreferenceCategory_allCapsSummary, false)

        isSingleLineTitle = a.getBoolean(R.styleable.PreferenceCategory_singleLineTitle, true)
        titleMaxLines = a.getInt(R.styleable.PreferenceCategory_titleMaxLines, 1)
        isAllCapsTitle = a.getBoolean(R.styleable.PreferenceCategory_allCapsTitle, true)

        // Get padding dimensions
        val paddingVerticalAttr = a.getDimensionPixelSize(R.styleable.PreferenceCategory_paddingVertical, -1)
        val paddingHorizontalAttr = a.getDimensionPixelSize(R.styleable.PreferenceCategory_paddingHorizontal, -1)

        paddingTop = a.getDimensionPixelSize(R.styleable.PreferenceCategory_paddingTop,
            if (paddingVerticalAttr != -1) paddingVerticalAttr else -1)
        paddingBottom = a.getDimensionPixelSize(R.styleable.PreferenceCategory_paddingBottom,
            if (paddingVerticalAttr != -1) paddingVerticalAttr else -1)
        paddingStart = a.getDimensionPixelSize(R.styleable.PreferenceCategory_paddingStart,
            if (paddingHorizontalAttr != -1) paddingHorizontalAttr else -1)
        paddingEnd = a.getDimensionPixelSize(R.styleable.PreferenceCategory_paddingEnd,
            if (paddingHorizontalAttr != -1) paddingHorizontalAttr else -1)

        // Get layout override dimensions
        minHeight = a.getDimensionPixelSize(R.styleable.PreferenceCategory_minHeight, -1)
        horizontalPadding = a.getDimensionPixelSize(R.styleable.PreferenceCategory_horizontalPadding, -1)
        verticalPadding = a.getDimensionPixelSize(R.styleable.PreferenceCategory_verticalPadding, -1)

        a.recycle()
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        // Find the RelativeLayout that contains title and summary (hardcoded padding in preference_material.xml)
        // The layout structure is: LinearLayout -> RelativeLayout (with title/summary) + LinearLayout (widget_frame)
        // So we need to find the RelativeLayout that contains the title
        val titleView = holder.findViewById(android.R.id.title)
        val relativeLayout = titleView?.parent as? android.widget.RelativeLayout

        // Check if theme defines custom vertical padding for preference categories
        if (relativeLayout != null) {
            val tv = android.util.TypedValue()
            if (context.theme.resolveAttribute(R.attr.preferenceCategoryPaddingVertical, tv, true)) {
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
        summary.isSingleLine = isSingleLineSummary
        summary.maxLines = summaryMaxLines
        summary.isAllCaps = isAllCapsSummary

        val title = holder.findViewById(android.R.id.title) as TextView
        title.isSingleLine = isSingleLineTitle
        title.maxLines = titleMaxLines
        title.isAllCaps = isAllCapsTitle
    }
}