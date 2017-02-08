package trac.portableexpensesdiary.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.raizlabs.android.dbflow.annotation.NotNull;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.currency.CurrencyRegisterDialog;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.model.User;

public class CurrencyUtils {

    private Activity activity;
    @NotNull
    private static boolean isShown;

    public CurrencyUtils(Activity activity) {
        this.activity = activity;
    }

    public void checkCurrencyForInit() {
        User currentUser =
                SessionManager.getInstance().getCurrentUser();

        if (currentUser.getCurrencyId() == null && !isShown) {
            new LoadCurrencyDialog().execute();
        }
    }

    public static void setIsShown(boolean isShown) {
        CurrencyUtils.isShown = isShown;
    }

    private class LoadCurrencyDialog extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(
                    activity,
                    R.style.ProgressDialogTheme
            );
            progressDialog.setMessage(activity.getString(R.string.progress_dialog_msg));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            CurrencyRegisterDialog dialog =
                    CurrencyRegisterDialog.newInstance();
            dialog.setCancelable(false);
            dialog.show(
                    activity.getFragmentManager(),
                    Constants.CURRENCY_REGISTER_DIALOG_TAG
            );

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
            setIsShown(true);
        }
    }
}