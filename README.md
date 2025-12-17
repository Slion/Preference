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

### x.EnumListPreference

Easily build a list preference from an enum.

### x.SliderPreference

Preference using a [Material Slider](https://m2.material.io/components/sliders#usage).

## Release 

Increase the library version number from [`lib/build.gradle.kts`](lib/build.gradle.kts).

Generate your upload package by running:<br> 
`.\gradlew generateUploadPackage`

If signing hangs and fails on Windows, launch Kleopatra to make sure `gpg-agent` is running.

It outputs a file named `preference.zip` in the following folder `lib\build\distributions`. You can directly upload this ZIP file for publication on [Maven Central](https://central.sonatype.com/publishing).
