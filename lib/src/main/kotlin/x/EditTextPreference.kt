/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package x

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.EditTextPreference as AndroidXEditTextPreference

/**
 * Extended EditTextPreference that adds support for inputType attribute.
 * This allows configuring the keyboard type and text behavior directly in XML.
 *
 * Usage:
 * ```xml
 * <x.EditTextPreference
 *     a:key="email"
 *     a:title="Email"
 *     x:inputType="textEmailAddress" />
 *
 * <x.EditTextPreference
 *     a:key="password"
 *     a:title="Password"
 *     x:inputType="textPassword" />
 * ```
 */
class EditTextPreference : AndroidXEditTextPreference {

    var inputType: Int = InputType.TYPE_CLASS_TEXT

    var hint: String? = null

    var boxStyle: Int = BOX_STYLE_OUTLINED

    var prefixText: String? = null

    var suffixText: String? = null

    var startIconDrawable: Int = 0

    var endIconDrawable: Int = 0

    var errorText: String? = null

    var textGravity: Int = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL

    /**
     * Set a validator to check input before saving.
     * The validator receives the input text and returns null if valid,
     * or an error message string if invalid.
     */
    var validator: ((String?) -> String?)? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
        init(context, attrs, androidx.preference.R.attr.editTextPreferenceStyle, 0)
    }

    constructor(context: Context) : super(context) {
        init(context, null, androidx.preference.R.attr.editTextPreferenceStyle, 0)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.EditTextPreference,
            defStyleAttr,
            defStyleRes
        )

        // Read hint from XML attribute
        hint = a.getString(R.styleable.EditTextPreference_hint)

        // Read inputType from XML attribute
        inputType = a.getInt(R.styleable.EditTextPreference_inputType, InputType.TYPE_CLASS_TEXT)

        // Also check for android:inputType if x:inputType is not specified
        if (inputType == InputType.TYPE_CLASS_TEXT) {
            inputType = a.getInt(
                R.styleable.EditTextPreference_android_inputType,
                InputType.TYPE_CLASS_TEXT
            )
        }

        // Read boxStyle attribute (0 = outlined, 1 = filled)
        boxStyle = a.getInt(R.styleable.EditTextPreference_boxStyle, BOX_STYLE_OUTLINED)

        // Read prefix and suffix text
        prefixText = a.getString(R.styleable.EditTextPreference_prefixText)
        suffixText = a.getString(R.styleable.EditTextPreference_suffixText)

        // Read start and end icon drawables
        startIconDrawable = a.getResourceId(R.styleable.EditTextPreference_startIconDrawable, 0)
        endIconDrawable = a.getResourceId(R.styleable.EditTextPreference_endIconDrawable, 0)

        // Read error text (can be set in XML for static validation)
        errorText = a.getString(R.styleable.EditTextPreference_errorText)

        // Read text gravity (alignment)
        textGravity = a.getInt(
            R.styleable.EditTextPreference_textGravity,
            android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
        )

        a.recycle()

        // Set up automatic summary formatting with prefix/suffix if they are defined
        if (prefixText != null || suffixText != null) {
            summaryProvider = SummaryProvider<AndroidXEditTextPreference> { preference ->
                val value = preference.text
                if (value.isNullOrEmpty()) {
                    // No value - show placeholder or nothing
                    null
                } else {
                    // Format with prefix and/or suffix
                    buildString {
                        prefixText?.let { append(it) }
                        append(value)
                        suffixText?.let { append(it) }
                    }
                }
            }
        }
    }

    companion object {
        const val BOX_STYLE_OUTLINED = 0
        const val BOX_STYLE_FILLED = 1
    }
}

