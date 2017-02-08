package trac.portableexpensesdiary.basecomponents;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import trac.portableexpensesdiary.expense.SessionManager;

public class BaseActivity extends AppCompatActivity {

    private static boolean isAuthenticationScreenActive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(SessionManager.getInstance().getCurrentUser() == null &&
                !isAuthenticationScreenActive) {
            Intent intent =
                    getBaseContext().getPackageManager().getLaunchIntentForPackage(
                            getBaseContext().getPackageName()
                    );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public static void setIsAuthenticationScreenActive(boolean isAuthenticationScreenActive) {
        BaseActivity.isAuthenticationScreenActive = isAuthenticationScreenActive;
    }
}