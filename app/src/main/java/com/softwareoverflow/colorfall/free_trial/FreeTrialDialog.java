package com.softwareoverflow.colorfall.free_trial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;

public class FreeTrialDialog extends Dialog {

    private Context origin;

    private TextView playFree;
    private View dialogView;

    public FreeTrialDialog(@NonNull Context context, final ViewGroup parent) {
        super(context, R.style.Theme_AppCompat_Light);

        dialogView= ((Activity) context).getLayoutInflater()
                .inflate(R.layout.popup_free_trial, parent);

        if(dialogView.getParent()!=null)
            ((ViewGroup)dialogView.getParent()).removeView(dialogView);

        ImageView background = dialogView.findViewById(R.id.dialog_popup_bg);
        background.setClipToOutline(true);

        playFree = dialogView.findViewById(R.id.play_free_version);
        TextView upgrade = dialogView.findViewById(R.id.upgrade_to_pro);

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug2", "UPGRADING!");
                //start the upgrade process
            }
        });

        this.setCancelable(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(dialogView);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        this.create();

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                parent.removeView(dialogView);
            }
        });
    }

    public void setPlayFreeListener(View.OnClickListener playFreeListener){
         playFree.setOnClickListener(playFreeListener);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }


}
