package com.alvarlagerlof.koda.Projects

import android.app.Dialog
import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AlertDialog
import android.view.View
import com.alvarlagerlof.koda.QrCodeShare.QrViewer
import com.alvarlagerlof.koda.R
import kotlinx.android.synthetic.main.projects_sheet.view.*
import org.jetbrains.anko.support.v4.alert

/**
 * Created by alvar on 2016-07-03.
 */
class ProjectsBottomSheetFragment(var privateID: String,
                                  var publicID: String,
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
        val view = View.inflate(context, R.layout.projects_sheet, null)
        dialog.setContentView(view)

        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }


        view.share.setOnClickListener {
            if (publicID.startsWith("ny_")) {
                showCantShareDialog()
            } else {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, title + " på Koda.nu: https://koda.nu/arkivet/" + publicID)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
        }

        view.qr_share.setOnClickListener {
            if (publicID.startsWith("ny_")) {
                showCantShareDialog()
            } else {
                val intent = Intent(context, QrViewer::class.java)
                intent.putExtra("url", "http://koda.nu/arkivet/" + publicID)
                intent.putExtra("title", title)
                intent.putExtra("author", author)
                startActivity(intent)
            }
        }

        view.edit.setOnClickListener {
            context.startActivity(Intent(context, ProjectsEditActivity::class.java).putExtra("privateID", privateID))
        }

        view.delete.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle("Är du säker?")
                    .setMessage("Vill du ta bort $title? Denna operation går inte att ångra")
                    .setNegativeButton("AVBRYT") { _, _ -> }
                    .setPositiveButton("TA BORT") { _, _ ->
                        ProjectsDelete(context, privateID)
                        dismiss()
                    }
                    .create()
                    .show()
        }

    }



    internal fun showCantShareDialog() {

        alert(title = "Kan inte dela projektet", message = "Projektet är inte uppladdat på servern än.") {
            positiveButton("OK") {
                dismiss()
            }
        }.show()



    }


}










//internal lateinit var comments: LinearLayout
//internal lateinit var addToHomescreen: LinearLayout

//comments        = (LinearLayout) contentView.findViewById(R.id.comments);
//addToHomescreen = (LinearLayout) contentView.findViewById(R.id.add_to_homescreen);






/*comments.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), CommentsActivity.class);
               intent.putExtra("publicID", publicID);
               intent.putExtra("title", title);
               getContext().startActivity(intent);
           }
       });

       addToHomescreen.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent shortcutIntent = new Intent();
               shortcutIntent.setComponent(new ComponentName(getContext(), FullscreenPlay.class));
               shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               shortcutIntent.putExtra("publicID", publicID);
               shortcutIntent.putExtra("title", title);

               Intent addIntent = new Intent();
               addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
               addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
               addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getContext(), R.drawable.ic_play_black));
               addIntent.putExtra("duplicate", false);
               addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
               getContext().sendBroadcast(addIntent);

               Intent startMain = new Intent(Intent.ACTION_MAIN);
               startMain.addCategory(Intent.CATEGORY_HOME);
               startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(startMain);
           }
       });*/
