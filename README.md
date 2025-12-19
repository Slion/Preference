# Preference

[![Build](https://github.com/Slion/Preference/actions/workflows/ci.yml/badge.svg)](https://github.com/Slion/Preference/actions/workflows/ci.yml)

Slions Preference is an Android Kotlin library which builds upon [Jetpack Preference](https://developer.android.com/jetpack/androidx/releases/preference) and [Material Components](https://github.com/material-components/material-components-android). Notably used by [Fulguris](http://fulguris.slions.net).

## Setup

This library is published on Maven Central as [`net.slions.android:preference`](https://central.sonatype.com/artifact/net.slions.android/preference). You can use it in your own Android project by adding it to your depenencies:

```
dependencies {
    implementation "net.slions.android:preference:0.0.5"
}
```

## Usage

Make sure your preference fragments are derived from `x.PreferenceFragmentBase`.
This will notably take care of using a compatible `preferenceTheme` if none was specified in your theme styles. Failing to do so or using an incompatible `preferenceTheme` can lead to crashes.

### XML Namespace Aliases

For cleaner and more concise XML preference files, you can use shorter namespace aliases instead of the verbose default names:

**Recommended aliases:**

```xml
<PreferenceScreen xmlns:a="http://schemas.android.com/apk/res/android"
    xmlns:x="http://schemas.android.com/apk/res-auto">
```

- `a` - Alias for `android` namespace (`http://schemas.android.com/apk/res/android`)
- `x` - Alias for `app` namespace (`http://schemas.android.com/apk/res-auto`)

**Example usage:**

```xml
<PreferenceScreen xmlns:a="http://schemas.android.com/apk/res/android"
    xmlns:x="http://schemas.android.com/apk/res-auto">
    
    <x.PreferenceCategory
        a:title="Settings"
        x:iconSpaceReserved="false">
        
        <Preference
            a:key="my_pref"
            a:title="My Preference"
            a:summary="Configure something"
            x:iconSpaceReserved="false" />
            
    </x.PreferenceCategory>
    
</PreferenceScreen>
```

This approach significantly reduces verbosity compared to using full `android:` and `app:` prefixes throughout your preference XML files.

### Using PreferenceFragmentExtra

Instead of creating a dedicated fragment class for each preference screen, you can use `PreferenceFragmentExtra` which receives its configuration through XML extras. This is particularly useful for simple preference screens that don't require custom logic.

**XML Declaration:**

```xml
<Preference
    a:key="my_settings"
    a:title="My Settings"
    a:summary="Configure my settings"
    a:fragment="x.PreferenceFragmentExtra"
    x:iconSpaceReserved="false">
    <extra
        a:name="screen"
        a:value="@xml/my_preferences" />
    <extra
        a:name="title"
        a:value="@string/my_settings_title" />
</Preference>
```

**Required Extras:**

- `screen` - Reference to the preference XML resource (e.g., `@xml/my_preferences`)
  - The value will be resolved by Android to a full resource path like `res/xml/my_preferences.xml`
- `title` - String or string resource reference for the fragment title (e.g., `@string/my_title` or `"My Title"`)

**Programmatic Usage:**

You can also create instances programmatically:

```kotlin
val fragment = PreferenceFragmentExtra.newInstance(
    screen = "res/xml/my_preferences.xml",
    title = "My Settings"
)
```

Or using the apply pattern:

```kotlin
val fragment = PreferenceFragmentExtra().apply {
    arguments = Bundle().apply {
        putString(PreferenceFragmentExtra.EXTRA_SCREEN, "res/xml/my_preferences.xml")
        putString(PreferenceFragmentExtra.EXTRA_TITLE, "My Settings")
    }
}
```

**Note:** When using XML extras with resource references (like `@xml/...` or `@string/...`), Android automatically resolves them to their full resource path format before passing to the fragment.

## Customisation

You can define your own `preferenceTheme` attribute in your theme styles. Like so:
`<item name="preferenceTheme">@style/PreferenceThemeOverlay.Slions.Custom</item>`

## Features

Run and explore the demo application and code documentation for more information.

### Package Naming

All custom preference classes in this library use the package name `x` for maximum brevity in XML declarations. This unconventional choice is intentional and serves a specific purpose.

**Why `x`?**

Preference XML files can become verbose and difficult to read when using full package paths. By using the single-letter package name `x`, we achieve:

- **Conciseness:** `<x.Preference>` vs `<net.slions.android.preference.Preference>`
- **Readability:** Less visual clutter in your XML files
- **Consistency:** Matches our namespace alias convention (`xmlns:x="http://schemas.android.com/apk/res-auto"`)

**XML Usage:**

```xml
<PreferenceScreen xmlns:a="http://schemas.android.com/apk/res/android"
    xmlns:x="http://schemas.android.com/apk/res-auto">
    
    <x.Preference
        a:key="my_basic_pref"
        a:title="Basic Preference"
        x:iconSpaceReserved="false" />
        
    <x.EnumListPreference
        a:key="my_enum_pref"
        a:title="Enum Preference" />
        
    <x.SliderPreference
        a:key="my_slider_pref"
        a:title="Slider Preference" />
        
</PreferenceScreen>
```

**Available Classes:**

All custom preference classes can be referenced using the `x.` prefix:
- `x.Preference`
- `x.EnumListPreference`
- `x.SliderPreference`
- `x.PreferenceFragmentBase`
- `x.PreferenceFragmentExtra`

### x.Preference

Extend the basic preference with convenient features and tricks.

#### Title and Summary Drawables

Add compound drawables to title and summary text with customizable padding:

```xml
<x.Preference
    a:title="Title with Drawables"
    a:summary="Summary with Drawables"
    x:titleDrawableStart="@drawable/ic_star"
    x:titleDrawableEnd="@drawable/ic_arrow"
    x:titleDrawablePadding="8dp"
    x:summaryDrawableStart="@drawable/ic_info"
    x:summaryDrawableEnd="@drawable/ic_check"
    x:summaryDrawablePadding="4dp" />
```

**Available attributes:**
- Title: `titleDrawableStart`, `titleDrawableEnd`, `titleDrawableTop`, `titleDrawableBottom`, `titleDrawablePadding`
- Summary: `summaryDrawableStart`, `summaryDrawableEnd`, `summaryDrawableTop`, `summaryDrawableBottom`, `summaryDrawablePadding`

#### Text Colors

Customize title and summary text colors using color values or theme attributes:

```xml
<x.Preference
    a:title="Colored Title"
    a:summary="Colored Summary"
    x:titleTextColor="?attr/colorPrimary"
    x:summaryTextColor="#FF0000" />
```

**Programmatic usage:**

```kotlin
preference.setTitleTextColorFromTheme(R.attr.colorPrimary)
preference.setSummaryTextColorResource(R.color.my_color)
preference.titleTextColor = 0xFF0000FF.toInt()  // Direct color value
```

#### Item Spacing (Padding)

Adjust the internal spacing of preference content using padding attributes:

**Padding attributes** (control spacing *inside* items):
```xml
<!-- Individual padding -->
<x.Preference
    a:title="Custom Padding"
    x:paddingTop="20dp"
    x:paddingBottom="20dp"
    x:paddingStart="24dp"
    x:paddingEnd="24dp" />

<!-- Convenient shorthand for vertical or horizontal -->
<x.Preference
    a:title="Vertical Padding"
    x:paddingVertical="16dp" />  <!-- Sets both top and bottom -->

<x.Preference
    a:title="Horizontal Padding"
    x:paddingHorizontal="32dp" />  <!-- Sets both start and end -->
```

**Common use cases:**

```xml
<!-- Create compact item layout -->
<x.Preference
    a:title="Compact item"
    x:paddingVertical="4dp"
    x:paddingHorizontal="8dp" />

<!-- Create spacious item layout -->
<x.Preference
    a:title="Spacious item"
    x:paddingVertical="24dp"
    x:paddingHorizontal="32dp" />
```

**Available attributes:**
- Padding: `paddingTop`, `paddingBottom`, `paddingStart`, `paddingEnd`, `paddingVertical`, `paddingHorizontal`

**Note:** The shorthand attributes (`paddingVertical`, `paddingHorizontal`) are applied first, then individual directional attributes override them if specified.

#### Theme Overlays for Screen-Wide Spacing

For consistent spacing across an entire preference screen, use **theme overlays** instead of setting attributes on individual preferences:

**Built-in theme overlays:**

1. **PreferenceThemeOverlay.Slions** (default) - Standard spacing
   - Uses default Android values (48dp height, 16dp padding)
2. **PreferenceThemeOverlay.Slions.Compact** - Moderately reduced spacing
   - `minHeight`: 36dp (vs default 48dp)
   - `horizontalPadding`: 12dp (vs default 16dp)
   - `preferenceVerticalPadding`: 6dp (vs default 16dp)
3. **PreferenceThemeOverlay.Slions.Dense** - Ultra-compact spacing
   - `minHeight`: 24dp (vs default 48dp)
   - `horizontalPadding`: 8dp (vs default 16dp)
   - `preferenceVerticalPadding`: 0dp (vs default 16dp)

**Custom theme attributes:**

You can also set these attributes in your own theme:

```xml
<style name="MyCustomPreferenceTheme" parent="PreferenceThemeOverlay.Slions">
    <!-- Control vertical padding for all preferences -->
    <item name="preferenceVerticalPadding">8dp</item>
    <!-- Control vertical padding for category items -->
    <item name="preferenceCategoryVerticalPadding">4dp</item>
</style>
```

**Usage in your fragment:**

```kotlin
class MyPreferenceFragment : PreferenceFragmentBase() {

    override fun titleResourceId() = R.string.my_settings_title

    override fun themeOverride(): Int {
        // Apply compact theme overlay to this entire screen
        return x.R.style.PreferenceThemeOverlay_Slions_Compact
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.my_preferences, rootKey)
    }
}
```

**Benefits:**
- ✅ Apply spacing to entire screen with one line of code
- ✅ No need to set attributes on individual preferences
- ✅ Consistent spacing automatically inherited by all items
- ✅ Easy to maintain and modify
- ✅ Clean XML files without repetitive attributes

**When to use which approach:**

| Approach | Use Case |
|----------|----------|
| **Theme overlay** | Consistent spacing for entire screen, quick setup |
| **Per-item padding attributes** | Fine-grained control, specific items need different spacing |
| **Per-item layout overrides** | Individual preferences need custom minimum height |

#### Layout Overrides for Dense Layouts

To **reduce** spacing below theme defaults (which the margin/padding attributes cannot do), use these layout override attributes that directly control the underlying layout parameters:

**Layout override attributes:**
```xml
<!-- Reduce minimum height (default is usually 48dp from theme) -->
<x.Preference
    a:title="Compact Item"
    x:minHeight="24dp" />

<!-- Reduce horizontal padding (overrides listPreferredItemPaddingLeft/Right/Start/End) -->
<x.Preference
    a:title="Less Side Padding"
    x:horizontalPadding="8dp" />

<!-- Reduce vertical content padding (space around title/summary, default is 16dp) -->
<x.Preference
    a:title="Tight Content"
    x:verticalPadding="4dp" />
```

**Create ultra-compact dense lists:**
```xml
<x.PreferenceCategory
    a:title="Dense Information"
    x:minHeight="0dp"
    x:verticalPadding="2dp">
    
    <x.Preference
        a:title="Dense Item 1"
        a:summary="Very compact"
        x:minHeight="20dp"
        x:verticalPadding="2dp" />
        
    <x.Preference
        a:title="Dense Item 2"
        a:summary="Fits more items on screen"
        x:minHeight="20dp"
        x:verticalPadding="2dp" />
        
</x.PreferenceCategory>
```

**Key differences:**
- **Padding attributes**: Add space on top of defaults (good for increasing spacing)
- **Layout override attributes**: Replace theme defaults (good for reducing spacing below defaults)

**Available layout override attributes:**
- `minHeight` - Override minimum height (replaces `?android:attr/listPreferredItemHeightSmall`)
- `horizontalPadding` - Override left/right padding from theme
- `verticalPadding` - Override top/bottom padding of the content area containing title and summary

### x.EnumListPreference

Easily build a list preference from an enum.

### EditTextPreference with Material Design 3

The library automatically provides Material Design 3 dialogs for all `EditTextPreference` instances when you extend `PreferenceFragmentBase`.

**Features:**
- ✅ Material Design 3 dialogs with rounded corners
- ✅ Proper elevation and shadows
- ✅ Material buttons
- ✅ Smooth animations
- ✅ Summary placeholder (`%s` shows current value)
- ✅ Simple summary provider support

**Usage:**

Simply use the standard androidx `EditTextPreference` in your XML:

```xml
<EditTextPreference
    a:key="user_name"
    a:title="User Name"
    a:summary="Current: %s"
    a:defaultValue="John Doe"
    a:dialogTitle="Enter Name"
    a:dialogMessage="Please enter your full name"
    x:useSimpleSummaryProvider="true" />

<!-- Email input -->
<EditTextPreference
    a:key="email"
    a:title="Email Address"
    a:summary="%s"
    a:defaultValue="user@example.com"
    a:dialogTitle="Email"
    a:dialogMessage="Enter your email address"
    x:useSimpleSummaryProvider="true" />

<!-- Password (hidden) -->
<EditTextPreference
    a:key="password"
    a:title="Password"
    a:summary="Tap to change password"
    a:dialogTitle="Change Password" />

<!-- With message -->
<EditTextPreference
    a:key="bio"
    a:title="Biography"
    a:summary="Tap to edit your bio"
    a:dialogTitle="Your Biography"
    a:dialogMessage="Tell us about yourself..." />
```

**How it works:**

`PreferenceFragmentBase` automatically intercepts `EditTextPreference` dialogs and displays them using `MaterialEditTextPreferenceDialogFragmentCompat`, which provides Material Design 3 styling.

**No additional code needed!** Just extend `PreferenceFragmentBase` and use standard `EditTextPreference` in your XML.

**Available attributes:**
- `a:key` - Preference storage key
- `a:title` - Title displayed in preference list
- `a:summary` - Summary text (use `%s` to show current value)
- `a:defaultValue` - Default text value
- `a:dialogTitle` - Dialog header text
- `a:dialogMessage` - Optional message shown in dialog (above the input field)
- `x:useSimpleSummaryProvider` - Automatically use preference value as summary

### x.SliderPreference

Preference using a [Material Slider](https://m2.material.io/components/sliders#usage).

