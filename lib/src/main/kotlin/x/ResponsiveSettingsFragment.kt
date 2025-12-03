package x


import android.annotation.SuppressLint
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceHeaderFragmentCompat
import slions.ihs
import timber.log.Timber
import x.BasicPreference


/**
 * Give us single pane on narrow screens and two pane settings on wider screens.
 */
class ResponsiveSettingsFragment : PreferenceHeaderFragmentCompat() {

    // The root of our preference tree, notably shown in the left pane
    lateinit var iPreferenceFragmentRoot: PreferenceFragmentBase

    // Keep track of the settings fragment we are currently showing
    // Notably used to remain in the proper settings page after screen rotation
    var iPreference: Preference? = null

    // Breadcrumbs management
    private var iTitleStack: ArrayList<String> = ArrayList<String>()

    /**
     * Called when we need to create our root preference fragment
     */
    override fun onCreatePreferenceHeader(): PreferenceFragmentCompat {
        Timber.d("$ihs onCreatePreferenceHeader")

        (requireActivity() as? PreferenceActivityBase)?.let {
            Timber.d("$ihs Found compatible activity")
            iPreferenceFragmentRoot = it.onCreatePreferenceHeader()
        }

        // Load our title stack with our root title
        iTitleStack.add(iPreferenceFragmentRoot.title(requireContext()))

        // Provide left pane headers fragment
        return iPreferenceFragmentRoot
    }

    /**
     * Called when we need to create our initial detail fragment.
     * As it stands that's always called after configuration change even when we are restoring states.
     * I reckon it has to do with the way we use the framework.
     * If we were doing it properly it should not be called when restoring states after configuration change.
     */
    override fun onCreateInitialDetailFragment(): Fragment? {
        Timber.d("$ihs onCreateInitialDetailFragment")

        // Patch our breadcrumbs, needed until user selects a preference screen
        slidingPaneLayout.doOnLayout {
            // If we restored the detail pane or user selected one just bail out to avoid stacking the title twice
            if (iPreference!=null) {
                return@doOnLayout
            }
            // Only after layout are the panel states valid
            // We need this to make sure the title breadcrumbs makes sense when onPreferenceStartFragment as not been called yet.
            // Basically when the activity first started without restoring the detail pane
            Timber.d("$ihs doOnLayout - isSlideable: ${slidingPaneLayout.isSlideable}, isOpen: ${slidingPaneLayout.isOpen}")
            if (slidingPaneLayout.isSlideable) {
                // Only showing one pane
                if (slidingPaneLayout.isOpen) {
                    // Showing only detail fragment
                    (childFragmentManager.findFragmentById(androidx.preference.R.id.preferences_detail) as? PreferenceFragmentBase)?.apply {
                        iTitleStack.add(title(requireContext()))
                    }
                } else {
                    // Showing only header fragment
                    resetBreadcrumbs()
                }
            } else {
                // Showing two panes
                (childFragmentManager.findFragmentById(androidx.preference.R.id.preferences_detail) as? PreferenceFragmentBase)?.apply {
                    iTitleStack.add(title(requireContext()))
                }
            }
        }

        return super.onCreateInitialDetailFragment()
    }

    /**
     * Called when user selects a preference screen.
     * Also called through [PreferenceActivityBase.startFragment] when restoring states after configuration change.
     */
    @SuppressLint("MissingSuperCall")
    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        //super.onPreferenceStartFragment(caller, pref)
        Timber.d("$ihs onPreferenceStartFragment")

        if (caller.id == androidx.preference.R.id.preferences_header) {
            // A preference was selected in our root/header
            // That means our breadcrumbs need to be reset to the root level
            resetBreadcrumbs()
        }

        iPreference = pref
        Timber.d("$ihs Fragment name: ${iPreference?.fragment}")

        // No actual fragment specified, just a back action
        // Notably used when deleting custom configuration
        if (pref.fragment == "back") {
            if (childFragmentManager.backStackEntryCount >=1) {
                // Go back to previous fragment if any
                childFragmentManager.popBackStack()
                // Adjust and update our breadcrumb
                popBreadcrumbs()
                (activity as? PreferenceActivityBase)?.updateTitleOnLayout()
            }

            return true
        }

        // TODO: Do we still need to use either AbstractSettingsFragment or addOnBackStackChangedListener
        // Stack our breadcrumb if any, otherwise just stack our title
        (if (pref is BasicPreference && pref.breadcrumb.isNotEmpty()) pref.breadcrumb else pref.title)?.let {
            iTitleStack.add(it.toString())
        }

        // Trigger title update on next layout
        (activity as? PreferenceActivityBase)?.updateTitleOnLayout()


        // Launch specified fragment
        // NOTE: This code is taken from the super implementation to which we added the animations
        if (caller.id == androidx.preference.R.id.preferences_header) {
            Timber.d("onPreferenceStartFragment: caller is header")
            // Opens the preference header.
            openPreferenceHeader(pref)
            return true
        }
        if (caller.id == androidx.preference.R.id.preferences_detail) {
            Timber.d("onPreferenceStartFragment: caller is detail")
            // Opens an preference in detail pane.
            val frag = childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                pref.fragment!!
            )
            frag.arguments = pref.extras

            childFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(x.R.anim.slide_in_from_right,
                    x.R.anim.slide_out_to_left,
                    x.R.anim.slide_in_from_left,
                    x.R.anim.slide_out_to_right)
                replace(androidx.preference.R.id.preferences_detail, frag)
                addToBackStack(null)
            }
            return true
        }

        return false
    }

    /**
     * Taken from base class to add animations
     */
    private fun openPreferenceHeader(header: Preference) {
        if (header.fragment == null) {
            if (header.intent == null) return
            // TODO: Change to use WindowManager ActivityView API
            header.intent?.let {
                startActivity(it)
            }
            return
        }
        val fragment = header.fragment?.let {
            childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                it
            )
        }

        fragment?.apply {
            arguments = header.extras
        }

        // Clear back stack
        if (childFragmentManager.backStackEntryCount > 0) {
            val entry = childFragmentManager.getBackStackEntryAt(0)
            childFragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        childFragmentManager.commit {
            setReorderingAllowed(true)
            // Don't do animation when the pane is not open
            // The opening of the pane itself is the animation
            if (slidingPaneLayout.isOpen) {
                // Define animations when opening settings from our root left pane
                setCustomAnimations(x.R.anim.slide_in_from_right,
                    x.R.anim.slide_out_to_left,
                    x.R.anim.slide_in_from_left,
                    x.R.anim.slide_out_to_right)
            }
            replace(androidx.preference.R.id.preferences_detail, fragment!!)
            slidingPaneLayout.openPane()
        }

    }

    /**
     *
     */
    private fun resetBreadcrumbs() {
        Timber.d("$ihs resetBreadcrumbs: ${iTitleStack.count()}")
        // Only keep our root title
        while (iTitleStack.count()>1) {
                //iTitleStack.removeLast()
                iTitleStack.removeAt(iTitleStack.size - 1)            
        }
    }

    /**
     * Called by the activity whenever we go back
     */
    fun popBreadcrumbs() {
        Timber.d("$ihs popBreadcrumbs: ${iTitleStack.count()}")
        if (iTitleStack.count()>1) {
                //iTitleStack.removeLast()
                iTitleStack.removeAt(iTitleStack.size - 1)
        }
    }

    /**
     * Provide proper title according to current mode:
     * - Split screen mode shows breadcrumbs
     * - Full screen mode shows only current page title
     */
    fun title(): String {

        Timber.d("$ihs titles: $iTitleStack")

        return if (iPreferenceFragmentRoot.isVisible && !slidingPaneLayout.isSlideable && iTitleStack.count()>1) {
            // We effectively disabled that algorithm using 100 for full crumb at start and end
            // We are simply using TextView ellipsis in the middle instead
            // Build our breadcrumbs
            var title = ""
            val sep = " > "
            val short = "…"
            // The last crumb index that should be displayed at the beginning of our title
            val lastFirst = 100
            // The first crumb index that should be displayed at the end of our title
            val firstLast = iTitleStack.lastIndex - 100
            // Build our title, it will look like: First > Second > … > … > Before last > Last
            iTitleStack.forEachIndexed { index, crumb ->
                  if (index==0) {
                      title = crumb
                  } else if (index>lastFirst && index<firstLast) {
                      title += "$sep$short"
                  } else {
                      title += "$sep$crumb"
                  }
            }
            title
        } else {
            iTitleStack.last()
        }
    }
}
