package com.agvahealthcare.ventilator_ext.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.agvahealthcare.ventilator_ext.R;
import com.agvahealthcare.ventilator_ext.callback.OnModeChangeListener;
import com.agvahealthcare.ventilator_ext.callback.SimpleCallbackListener;
import com.agvahealthcare.ventilator_ext.callback.UserInteractionAwareCallback;
import com.agvahealthcare.ventilator_ext.model.VentMode;
import com.agvahealthcare.ventilator_ext.utility.callback.SingleValueCallbackListener;

import java.util.ArrayList;
import java.util.Objects;

import cdflynn.android.library.checkview.CheckView;


/**
 * Created by MOHIT MALHOTRA on 14-09-2018.
 */

public class DialogBoxFactory {

    private static AlertDialog dialogView;

    public static AlertDialog showDialog(Context ctx, String title, String message, SimpleCallbackListener listener) {
        return showDialog(ctx, title, message, null,  listener);
    }

    public static AlertDialog showDialog(Context ctx, String title, String message, String btnText, SimpleCallbackListener clickListener){

        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_alert, null, false);
        TextView tvTitle = view.findViewById(R.id.tvHead);
        TextView tvMsg = view.findViewById(R.id.tvDesc);
        Button btnOk = view.findViewById(R.id.btnOk);

        if(title != null && !title.isEmpty()) tvTitle.setText(title);
        if(message != null && !message.isEmpty()) tvMsg.setText(message);
        if(btnText != null && !btnText.isEmpty()) btnOk.setText(btnText);


        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(false);


        btnOk.setOnClickListener(v -> {
            if(clickListener != null) clickListener.doAction();
            dialog.cancel();
        });

        setDialogView(dialog, false);
        dialog.show();

        return dialog;
    }

    public static AlertDialog showSettingsSaved(Context ctx, boolean isAccepted){

        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_settings_response, null, false);
        CheckView cv = view.findViewById(R.id.cvTick);
        ImageView ivCross = view.findViewById(R.id.ivCross);
        TextView tvMsg = view.findViewById(R.id.tvMsg);
        TextView tvMsgDesc = view.findViewById(R.id.tvMsgDesc);

        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(true);

        dialog.setOnShowListener(d -> {
            if(isAccepted){
                tvMsg.setText("Success !");
                tvMsgDesc.setText("Settings saved");
                ivCross.setVisibility(View.GONE);
                cv.setVisibility(View.VISIBLE);
                cv.check();
            } else{
                tvMsg.setText("Failure !");
                tvMsgDesc.setText("Error in saving settings");
                cv.setVisibility(View.GONE);
                ivCross.setVisibility(View.VISIBLE);

            }

        });

        // to make the window background transparent
        if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        return dialog;
    }


    public static AlertDialog selectModeConfirmationDialog(Activity ctx, String message, VentMode newMode, OnModeChangeListener onModeChangeListener) {
        return selectModeConfirmationDialog(ctx, message, newMode, onModeChangeListener, null);

    }

    public static AlertDialog startVentConfirmationDialog(Activity ctx, String message){
        return startVentConfirmationDialog(ctx, message);
    }


    public static AlertDialog selectModeConfirmationDialog(Activity ctx, String message, VentMode newMode,OnModeChangeListener onModeChangeListener, SimpleCallbackListener onCancelListener){
        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_mode_confirmation, null, false);
        TextView tvConfMsg = view.findViewById(R.id.tvConfMessage);

        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNo = view.findViewById(R.id.btnNo);
        tvConfMsg.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();

        btnYes.setOnClickListener(v -> {
            onModeChangeListener.onModeChange(newMode);
            dialog.cancel();
        });
        btnNo.setOnClickListener((v)-> {
            if(onCancelListener != null) onCancelListener.doAction();
            dialog.cancel();
        });
        // to make the window background transparent
        final Window window = dialog.getWindow();
        if(window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setCallback(new UserInteractionAwareCallback(window.getCallback(), ctx));
        }
        dialog.setCancelable(false);
        setDialogView(dialog, true);

        dialog.show();
        return dialog;
    }

    public static AlertDialog showCommandDialog(Context ctx, SingleValueCallbackListener onSendListener){

        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_command, null, false);
        EditText etCommand = view.findViewById(R.id.etCmd);
        ImageButton btnSend = view.findViewById(R.id.btnSend);
        Button btnKillAll = view.findViewById(R.id.btnSuspendAll);
        Button btnFastBoot = view.findViewById(R.id.btnFastBoot);
        Button btnSelfTest = view.findViewById(R.id.btnSelfTest);


        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(true);


        btnSend.setOnClickListener(v -> {
            if(onSendListener != null && !TextUtils.isEmpty(etCommand.getText())) onSendListener.doAction(etCommand.getText().toString());
            dialog.cancel();
        });

        btnKillAll.setOnClickListener(v -> {
            if(onSendListener != null) onSendListener.doAction(ctx.getResources().getString(R.string.cmd_vent_killall));
            dialog.cancel();
        });

        btnFastBoot.setOnClickListener(v -> {
            if(onSendListener != null) onSendListener.doAction(ctx.getResources().getString(R.string.cmd_vent_fastboot));
            dialog.cancel();
        });

        btnSelfTest.setOnClickListener(v -> {
            if(onSendListener != null) onSendListener.doAction(ctx.getResources().getString(R.string.cmd_vent_selftest));
            dialog.cancel();
        });



        setDialogView(dialog,true);

        dialog.show();

        return dialog;
    }


    public static AlertDialog showTwoBtnDialog(Context ctx, String title, String message, SimpleCallbackListener onSuccessListener){
        return showTwoBtnDialog(ctx, title, message, onSuccessListener, null);

    }


    public static AlertDialog showTwoBtnDialog(Context ctx, String title, String message, SimpleCallbackListener onSuccessListener, SimpleCallbackListener onCancelListener){

        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_two_btn_design, null, false);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMsg = view.findViewById(R.id.tvMessage);
        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNo = view.findViewById(R.id.btnNo);


        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(false);

        tvTitle.setText(title);
        tvMsg.setText(message);
        btnYes.setOnClickListener((v)->{
            if(onSuccessListener != null) onSuccessListener.doAction();
            dialog.cancel();
        });
        btnNo.setOnClickListener((v)->{
            if(onCancelListener != null) onCancelListener.doAction();
            dialog.cancel();
        } );

        setDialogView(dialog,true);

        dialog.show();
        return dialog;
    }


    public static AlertDialog showO2CalibrateDialog(Context ctx, SimpleCallbackListener onAccept){

        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_o2_calibrate, null, false);
        View viewStep1 = view.findViewById(R.id.viewStep1);
        View viewStep2 = view.findViewById(R.id.viewStep2);
        View viewStep3 = view.findViewById(R.id.viewStep3);

        View.OnClickListener listener = (v)-> ((CheckBox)view.findViewWithTag("step" + v.getTag().toString())).setChecked(true);

        viewStep1.setOnClickListener(listener);
        viewStep2.setOnClickListener(listener);
        viewStep3.setOnClickListener(listener);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();

        CheckBox cbStep1 = view.findViewById(R.id.cbStep1);

        checkBoxes.add(cbStep1);

        CheckBox cbStep2 = view.findViewById(R.id.cbStep2);
        checkBoxes.add(cbStep2);

        CheckBox cbStep3 = view.findViewById(R.id.cbStep3);
        checkBoxes.add(cbStep3);

        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNo = view.findViewById(R.id.btnNo);
        View mainLayout = view.findViewById(R.id.layoutMain);
        View progressLayout = view.findViewById(R.id.layoutProgress);

        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(true);

        btnYes.setOnClickListener(v -> {

            boolean isAllClicked = true;

            for(CheckBox cb : checkBoxes){
                if(!cb.isChecked()) {
                    isAllClicked = false;
                    break;
                }
            }

            if(isAllClicked) {
                dialog.setCancelable(false);


                onAccept.doAction();

                if (progressLayout.getVisibility() != View.VISIBLE) {
                    mainLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        btnNo.setOnClickListener(v ->  dialog.cancel() );
        setDialogView(dialog, false);

        //setDialogViewMainActivity(dialog, false);

        dialog.show();

        return dialog;
    }


    public static AlertDialog showShutDownStatusDialog(String msg,Context ctx, SimpleCallbackListener onclickShutDown) {


        View view = LayoutInflater.from(ctx).inflate(R.layout.layoutdialog_shutdown_status,null,false);
        Button btnshutDown = view.findViewById(R.id.btnShutDown);
        Button btnCancelDialog = view.findViewById(R.id.btnCancelshutDown);


        TextView dialogMessage=view.findViewById(R.id.etCmd);
        dialogMessage.setText(msg);
        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(true);

        btnshutDown.setOnClickListener(v -> {
            if(onclickShutDown != null) onclickShutDown.doAction();
            dialog.cancel();
        });


        btnCancelDialog.setOnClickListener(v-> dialog.cancel());
/*=======
        if (dialog.isShowing()) {
            btnCancelDialog.setOnClickListener(v -> dialog.dismiss());
        }

       *//* btnCancelDialog.setOnClickListener(
                v -> dialog.cancel()
        );*//*

>>>>>>> Stashed changes*/

        setDialogView(dialog, true);

        dialog.show();


        return dialog;
    }


    public static AlertDialog showVentilationStatusDialog(String msg,Context ctx, SimpleCallbackListener onclickStandby){

        View view = LayoutInflater.from(ctx).inflate( R.layout.layout_dialog_vent_status, null, false);

        Button btnStandby = view.findViewById(R.id.btnStandby);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        TextView dialogMessage=view.findViewById(R.id.etCmd);
        dialogMessage.setText(msg);
        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(view).create();
        dialog.setCancelable(true);


        btnStandby.setOnClickListener(v -> {
            if(onclickStandby != null) onclickStandby.doAction();
            dialog.cancel();

        });

        btnCancel.setOnClickListener(v -> dialog.cancel() );

        setDialogView(dialog, true);

        dialog.show();

        return dialog;
    }


    public static void setDialogView(AlertDialog dialogView, boolean status) {
        DialogBoxFactory.dialogView = dialogView;
        // to make the window background transparent
        if(dialogView.getWindow() != null) dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(dialogView.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (Objects.requireNonNull(dialogView.getWindow()).getAttributes() != null){
            WindowManager.LayoutParams wmlp = dialogView.getWindow().getAttributes();
            if(status){
                wmlp.gravity = Gravity.BOTTOM | Gravity.START;
            }else {
                wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            }

            wmlp.height = 300;
            wmlp.width = 200;
            wmlp.dimAmount = 0.7F;
            wmlp.screenBrightness = 10.0F;
            wmlp.x = 250;//x position
            wmlp.y = 125;//y position
            dialogView.getWindow().setAttributes(wmlp);
        }


    }


    public static void setDialogViewMainActivity(AlertDialog dialogView, boolean status) {
        DialogBoxFactory.dialogView = dialogView;
        // to make the window background transparent
        if(dialogView.getWindow() != null) dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Objects.requireNonNull(dialogView.getWindow()).getAttributes() != null){
            WindowManager.LayoutParams wmlp = dialogView.getWindow().getAttributes();
            if(status){
                wmlp.gravity = Gravity.BOTTOM | Gravity.START;
            }else {
                wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            }

            wmlp.height = 300;
            wmlp.width = 200;
            wmlp.dimAmount = 0.0F;
            wmlp.screenBrightness = 5.0F;
            wmlp.x = 270;   //x position
            wmlp.y = 125;   //y position
            dialogView.getWindow().setAttributes(wmlp);
        }

    }


    public static void setDialogViewMainSystemDialog(AlertDialog dialogView, boolean status) {
        DialogBoxFactory.dialogView = dialogView;
        // to make the window background transparent
        if(dialogView.getWindow() != null) dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Objects.requireNonNull(dialogView.getWindow()).getAttributes() != null){
            WindowManager.LayoutParams wmlp = dialogView.getWindow().getAttributes();
            if(status){
                wmlp.gravity = Gravity.BOTTOM | Gravity.START;
            }else {
                wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            }

            wmlp.height = 300;
            wmlp.width = 200;
            wmlp.dimAmount = 0.0F;
            wmlp.screenBrightness = 5.0F;
            wmlp.x = 260;   //x position
            wmlp.y = 105;   //y position
            dialogView.getWindow().setAttributes(wmlp);
        }

    }



}