package com.alvarlagerlof.koda

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View
import com.alvarlagerlof.koda.Play.FullscreenPlay
import com.alvarlagerlof.koda.Profile.ProfileActivity
import com.alvarlagerlof.koda.QrCodeShare.QrViewer
import kotlinx.android.synthetic.main.standard_sheet.view.*

/**
 * Created author alvar on 2016-07-03.
 */
class StandardBottomSheetFragment(var public_id: String,
                                  var title: String,
                                  var author: String) : BottomSheetDialogFragment() {



    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset < -0.15f) {
                dismiss()
            }
        }
    }


    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.standard_sheet, null)
        dialog.setContentView(view)

        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }



        view.profile.setOnClickListener {
            val sendIntent = Intent(context, ProfileActivity::class.java)
            sendIntent.putExtra("author", author)
            startActivity(sendIntent)
        }

        view.share.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, title + " på Koda.nu: https://koda.nu/arkivet/" + public_id)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        view.qr_share.setOnClickListener {
            val intent = Intent(context, QrViewer::class.java)
            intent.putExtra("url", "http://koda.nu/arkivet/" + public_id)
            intent.putExtra("title", title)
            intent.putExtra("author", author)
            startActivity(intent)
        }

        view.add_to_homescreen.setOnClickListener {
            val shortcutIntent = Intent()
            shortcutIntent.component = ComponentName(context, FullscreenPlay::class.java)
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            shortcutIntent.putExtra("public_id", public_id)
            shortcutIntent.putExtra("title", title)

            val addIntent = Intent()
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_play_black))
            addIntent.putExtra("duplicate", false)
            addIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
            context.sendBroadcast(addIntent)

            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }


    }

}