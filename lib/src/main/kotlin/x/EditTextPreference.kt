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
        private set

    var hint: String? = null
        private set

    var boxStyle: Int = BOX_STYLE_OUTLINED
        private set

    var prefixText: String? = null
        private set

    var suffixText: String? = null
        private set

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

        a.recycle()
    }

    companion object {
        const val BOX_STYLE_OUTLINED = 0
        const val BOX_STYLE_FILLED = 1
    }
}

