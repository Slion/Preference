package slions.pref.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import slions.pref.ResponsiveSettingsFragment
import slions.pref.PreferenceFragmentBase
import timber.log.Timber

private const val TITLE_TAG = "settingsActivityTitle"

/**
 * TODO: Move core features to base class in library
 */
class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    lateinit var responsive: ResponsiveSettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load our layout
        setContentView(slions.pref.R.layout.activity_settings)
        // Setup our toolbar
        setSupportActionBar(findViewById(slions.pref.R.id.settings_toolbar))
        //title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        responsive = ResponsiveSettingsFragment(HeaderFragment())

        // Load our preference entry point
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(slions.pref.R.id.settings, responsive)
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.title_activity_settings)
            }
        }
    }

    /**
     *
     */
    override fun onResume() {
        Timber.d("onResume")
        super.onResume()
/*
        // At this stage our preferences have been created
        try {
            // Start specified fragment if any
            if (iFragmentClassName == null) {
                val className = intent.extras!!.getString(SETTINGS_CLASS_NAME)
                val classType = Class.forName(className!!)
                startFragment(classType)
            } else {
                val classType = Class.forName(iFragmentClassName)
                startFragment(classType)
            }
            // Prevent switching back to that settings page after screen rotation
            //intent = null
        }
        catch(ex: Exception) {
            // Just ignore
        }
        */


        updateTitleOnLayout()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment!!
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    /**
     *
     */
    fun updateTitleOnLayout(aGoingBack: Boolean = false) {
        findViewById<View>(android.R.id.content).doOnLayout {
            if (aGoingBack) {
                // This code path is not actually being used
                // TODO: clean it up
                updateTitle()
            } else {
                title = responsive.title()
            }
        }
    }

    /**
     * Update activity title as define by the current fragment
     * Also still does not work properly on wide screens.
     * TODO: This is not actually being used anymore, consider a clean up
     */
    private fun updateTitle()
    {
        // TODO: could just be defensive, test rotation without it and remove if not needed
        if (responsive.view==null) {
            // Prevent crash upon screen rotation
            return
        }

        if (!responsive.slidingPaneLayout.isOpen /*|| !responsive.slidingPaneLayout.isSlideable*/) {
            setTitle("Settings")
        } else {
            // Make sure title is also set properly when coming back from second level preference screen
            // Notably needed for portrait and landscape configuration settings
            updateTitle(currentFragment())
            //title = responsive.iPreference?.title
        }
    }

    /**
     * Update activity title as defined by the given [aFragment].
     */
    private fun updateTitle(aFragment : Fragment?)
    {
        Timber.d("updateTitle")
        // Needed to update title after language change
        (aFragment as? PreferenceFragmentBase)?.let {
            Timber.d("updateTitle done")
            title = it.title()
        }
    }

    /**
     * Fetch the currently loaded settings fragment.
     */
    private fun currentFragment() : Fragment? {

        return if (responsive.childFragmentManager.fragments.isNotEmpty() && ((responsive.slidingPaneLayout.isOpen && responsive.slidingPaneLayout.isSlideable) /*||responsive.childFragmentManager.backStackEntryCount>0*/)) {
            responsive.childFragmentManager.fragments.last()
        } else if (responsive.childFragmentManager.fragments.isNotEmpty() && responsive.slidingPaneLayout.isOpen && !responsive.slidingPaneLayout.isSlideable) {
            responsive.childFragmentManager.fragments.first()
        } else {
            supportFragmentManager.findFragmentById(R.id.settings)
        }

    }

    /**
     *
     */
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        doOnBackPressed()
    }

    /**
     *
     */
    private fun doOnBackPressed() {

        // Deploy workaround to make sure we exit this activity when user hits back from top level fragments
        // You can reproduce that issue by disabling that workaround and going in a nested settings fragment such as Look & Feel > Portrait
        // Then hit back button twice won't exit the settings activity. You can't exit the settings activity anymore.
        val doFinish = (responsive.childFragmentManager.backStackEntryCount==0 && (!responsive.slidingPaneLayout.isOpen || !responsive.slidingPaneLayout.isSlideable))
        //val doFinish = !responsive.slidingPaneLayout.isOpen
        super.onBackPressed()
        if (doFinish) {
            finish()
        } else {
            // Do not update title if we exit the activity to avoid showing broken title before exit
            responsive.popBreadcrumbs()
            updateTitleOnLayout()
        }
    }

    class HeaderFragment : PreferenceFragmentBase() {

        override fun titleResourceId(): Int {
            return R.string.title_activity_settings
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)
        }
    }

    class MessagesFragment : PreferenceFragmentBase() {

        override fun titleResourceId(): Int {
            return R.string.messages_header
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.messages_preferences, rootKey)
        }
    }

    class SyncFragment : PreferenceFragmentBase() {

        override fun titleResourceId(): Int {
            return R.string.sync_header
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.sync_preferences, rootKey)
        }
    }
}