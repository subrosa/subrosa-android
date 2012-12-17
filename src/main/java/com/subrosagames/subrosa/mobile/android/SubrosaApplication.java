package com.subrosagames.subrosa.mobile.android;

import android.app.Application;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 *
 */
@ReportsCrashes(formKey = "dEhTdmtWNFpsM2dXZk4zSHA5ajY5bGc6MQ")
public class SubrosaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
