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

        a.recycle()
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
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