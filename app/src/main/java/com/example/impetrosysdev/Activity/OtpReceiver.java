package com.example.impetrosysdev.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpReceiver extends BroadcastReceiver {

    private OtpRecieverListner otpRecieverListner;

    public OtpReceiver() {

    }
public void initListner(OtpRecieverListner otpRecieverListner){
        this.otpRecieverListner = otpRecieverListner;
}
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Status status = bundle.getParcelable(SmsRetriever.EXTRA_STATUS);  // Use getParcelable to retrieve Status
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:  // Use CommonStatusCodes.SUCCESS
                            String message = (String) bundle.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            if (message != null) {
                                Pattern pattern = Pattern.compile("\\d{6}");
                                Matcher matcher = pattern.matcher(message);

                                if (matcher.find()) {
                                    String myOtp = matcher.group(0);
                                    if (this.otpRecieverListner != null) {
                                        this.otpRecieverListner.onOtpSuccess(myOtp);
                                    } else {
                                        if (this.otpRecieverListner != null) {
                                            this.otpRecieverListner.onOtpTimeout();
                                        }
                                    }
                                }
                            }
                            break;

                        case CommonStatusCodes.TIMEOUT:
                            if (this.otpRecieverListner != null) {
                                this.otpRecieverListner.onOtpTimeout();
                            }
                            break;

                    }
                }
            }
        }
    }

    public interface OtpRecieverListner {
        void onOtpSuccess(String otp);

        void onOtpTimeout();
    }
}
