package trac.portableexpensesdiary;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.fabric.sdk.android.Fabric;

public class PortableExpensesDiaryApp extends MultiDexApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        setContext(this);

        Fabric.with(
                this,
                new Crashlytics()
        );
        Fabric.with(
                this,
                new Answers()
        );

        FlowManager.init(
                new FlowConfig.Builder(this).build()
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        setContext(null);
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        PortableExpensesDiaryApp.context = context;
    }
}
