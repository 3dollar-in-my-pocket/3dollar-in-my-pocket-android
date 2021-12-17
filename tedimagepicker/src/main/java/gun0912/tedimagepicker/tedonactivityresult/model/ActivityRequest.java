package gun0912.tedimagepicker.tedonactivityresult.model;

import android.content.Intent;

import gun0912.tedimagepicker.tedonactivityresult.listener.OnActivityResultListener;

public class ActivityRequest {


    private Intent intent;
    private OnActivityResultListener listener;

    public ActivityRequest(Intent intent,
                           OnActivityResultListener listener) {
        this.intent = intent;
        this.listener = listener;
    }

    public Intent getIntent() {
        return intent;
    }

    public OnActivityResultListener getListener() {
        return listener;
    }
}
