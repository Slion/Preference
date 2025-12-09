package x
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.preference.Preference

/**
 * Copied from AndroidX.Preference and converted to Kotlin.
 * We needed this to avoid showing duplicate messages when copying a preference.
 * Looks like the latest code of AndroidX fixed that but was never released it seems.
 */
class OnPreferenceCopyListener(
    private val mPreference: Preference
) : View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    @SuppressLint("PrivateResource")
    override fun onCreateContextMenu(
        menu: ContextMenu, v: View?,
        menuInfo: ContextMenuInfo?
    ) {
        val summary = mPreference.summary
        if (!mPreference.isCopyingEnabled || TextUtils.isEmpty(summary)) {
            return
        }
        menu.setHeaderTitle(summary)
        menu.add(Menu.NONE, Menu.NONE, Menu.NONE, androidx.preference.R.string.copy)
            .setOnMenuItemClickListener(this)
    }
    @SuppressLint("PrivateResource")
    override fun onMenuItemClick(item: MenuItem): Boolean {
        val clipboard = mPreference.context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager
        val summary = mPreference.summary
        val clip = ClipData.newPlainText("Preference", summary)
        clipboard.setPrimaryClip(clip)
        // T has a clipboard overlay that automatically shows copied text, so only show a Toast
        // below T.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Toast.makeText(
                mPreference.context,
                mPreference.context.getString(androidx.preference.R.string.preference_copied, summary),
                Toast.LENGTH_SHORT
            ).show()
        }
        return true
    }
}
