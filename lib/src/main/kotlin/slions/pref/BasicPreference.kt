/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package slions.pref

import android.annotation.SuppressLint
import android.content.Context
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

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val summary = holder.findViewById(android.R.id.summary) as TextView
        val title = holder.findViewById(android.R.id.title) as TextView
        // Enable multiple line support
        summary.isSingleLine = false
        summary.maxLines = 100 // Just need to be high enough I guess

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