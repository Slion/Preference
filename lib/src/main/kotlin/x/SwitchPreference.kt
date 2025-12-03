/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package x

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Checkable
import android.widget.CompoundButton
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.PreferenceViewHolder
import androidx.preference.R
import androidx.preference.TwoStatePreference
import androidx.preference.R as PR

/**
 * A [androidx.preference.Preference] that provides a two-state toggleable option.
 *
 *
 * This preference will save a boolean value to [android.content.SharedPreferences].
 *
 * @attr name android:summaryOff
 * @attr name android:summaryOn
 * @attr name android:switchTextOff
 * @attr name android:switchTextOn
 * @attr name android:disableDependentsState
 */
@SuppressLint("RestrictedApi", "PrivateResource")
open class SwitchPreference @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.switchPreferenceStyle, defStyleRes: Int = 0
) : TwoStatePreference(context, attrs, defStyleAttr, defStyleRes) {
    private val mListener: Listener = Listener()

    // Switch text for on and off states
    private var mSwitchOn: CharSequence? = null
    private var mSwitchOff: CharSequence? = null

    var switchTextOn: CharSequence?
        /**
         * @return The text that will be displayed on the switch widget in the on state
         */
        get() = mSwitchOn
        /**
         * Set the text displayed on the switch widget in the on state.
         * This should be a very short string, one word if possible.
         *
         * @param onText Text to display in the on state
         */
        set(onText) {
            mSwitchOn = onText
            notifyChanged()
        }

    var switchTextOff: CharSequence?
        /**
         * @return The text that will be displayed on the switch widget in the off state
         */
        get() = mSwitchOff
        /**
         * Set the text displayed on the switch widget in the off state.
         * This should be a very short string, one word if possible.
         *
         * @param offText Text to display in the off state
         */
        set(offText) {
            mSwitchOff = offText
            notifyChanged()
        }

    /**
     * Construct a new SwitchPreference with the given style options.
     *
     * @param context      The [Context] that will style this preference
     * @param attrs        Style attributes that differ from the default
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     * resource that supplies default values for the view. Can be 0 to not
     * look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that supplies default values
     * for the view, used only if defStyleAttr is 0 or can not be found in the
     * theme. Can be 0 to not look for defaults.
     */
    /**
     * Construct a new SwitchPreference with the given style options.
     *
     * @param context      The [Context] that will style this preference
     * @param attrs        Style attributes that differ from the default
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     * resource that supplies default values for the view. Can be 0 to not
     * look for defaults.
     */
    /**
     * Construct a new SwitchPreference with the given style options.
     *
     * @param context The [Context] that will style this preference
     * @param attrs   Style attributes that differ from the default
     */
    /**
     * Construct a new SwitchPreference with default style options.
     *
     * @param context The [Context] that will style this preference
     */
    init {
        val a = context.obtainStyledAttributes(
            attrs,
            PR.styleable.SwitchPreferenceCompat, defStyleAttr, defStyleRes
        )

        setSummaryOn(
            TypedArrayUtils.getString(
                a, PR.styleable.SwitchPreferenceCompat_summaryOn,
                PR.styleable.SwitchPreferenceCompat_android_summaryOn
            )
        )

        setSummaryOff(
            TypedArrayUtils.getString(
                a, PR.styleable.SwitchPreferenceCompat_summaryOff,
                PR.styleable.SwitchPreferenceCompat_android_summaryOff
            )
        )

        this.switchTextOn = TypedArrayUtils.getString(
            a,
            PR.styleable.SwitchPreferenceCompat_switchTextOn,
            PR.styleable.SwitchPreferenceCompat_android_switchTextOn
        )

        this.switchTextOff = TypedArrayUtils.getString(
            a,
            PR.styleable.SwitchPreferenceCompat_switchTextOff,
            PR.styleable.SwitchPreferenceCompat_android_switchTextOff
        )

        disableDependentsState = TypedArrayUtils.getBoolean(
            a,
            R.styleable.SwitchPreferenceCompat_disableDependentsState,
            R.styleable.SwitchPreferenceCompat_android_disableDependentsState, false
        )

        a.recycle()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val switchView = holder.findViewById(R.id.switchWidget)
        syncSwitchView(switchView)
        syncSummaryView(holder)
    }



    /**
     * Set the text displayed on the switch widget in the on state.
     * This should be a very short string, one word if possible.
     *
     * @param resId The text as a string resource ID
     */
    fun setSwitchTextOn(resId: Int) {
        this.switchTextOn = context.getString(resId)
    }

    /**
     * Set the text displayed on the switch widget in the off state.
     * This should be a very short string, one word if possible.
     *
     * @param resId The text as a string resource ID
     */
    fun setSwitchTextOff(resId: Int) {
        this.switchTextOff = context.getString(resId)
    }

    /**
     * @param view
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun performClick(view: View) {
        super.performClick(view)
        syncViewIfAccessibilityEnabled(view)
    }

    private fun syncViewIfAccessibilityEnabled(view: View) {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (!accessibilityManager.isEnabled) {
            return
        }

        val switchView = view.findViewById<View?>(R.id.switchWidget)
        syncSwitchView(switchView)

        val summaryView = view.findViewById<View?>(android.R.id.summary)
        syncSummaryView(summaryView)
    }

    private fun syncSwitchView(view: View?) {
        if (view is SwitchCompat) {
            val switchView = view
            switchView.setOnCheckedChangeListener(null)
        }
        if (view is Checkable) {
            (view as Checkable).setChecked(mChecked)
        }
        if (view is SwitchCompat) {
            val switchView = view
            switchView.setTextOn(mSwitchOn)
            switchView.setTextOff(mSwitchOff)
            switchView.setOnCheckedChangeListener(mListener)
        }
    }

    private inner class Listener :
        CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (!callChangeListener(isChecked)) {
                // Listener didn't like it, change it back.
                // CompoundButton will make sure we don't recurse.
                buttonView.setChecked(!isChecked)
                return
            }

            this@SwitchPreference.setChecked(isChecked)
        }
    }
}
