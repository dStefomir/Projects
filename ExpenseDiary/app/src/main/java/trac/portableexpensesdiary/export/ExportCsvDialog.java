package trac.portableexpensesdiary.export;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.filterdialog.FilterDialog;
import trac.portableexpensesdiary.utils.CsvUtils;
import trac.portableexpensesdiary.utils.DateUtils;
import trac.portableexpensesdiary.utils.PermissionUtils;

public class ExportCsvDialog extends FilterDialog implements View.OnClickListener {

    private AntiMonkeyButton fromDateBtn;
    private AntiMonkeyButton toDateBtn;
    private static FragmentManager supportFragmentManager;

    public ExportCsvDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_export_csv,
                container
        );

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onClick(View v) {
        Date fromDate;
        Date toDate;

        switch (v.getId()) {

            case R.id.btnFromDate:
                fromDate = this.fromDate;
                new SlideDateTimePicker
                        .Builder(supportFragmentManager)
                        .setListener(fromDateListener)
                        .setInitialDate(fromDate)
                        .build()
                        .show();

                break;

            case R.id.btnToDate:
                toDate = this.toDate;
                new SlideDateTimePicker
                        .Builder(supportFragmentManager)
                        .setListener(toDateListener)
                        .setInitialDate(toDate)
                        .build()
                        .show();

                break;

            case R.id.cancelBtn:
                dismiss();

                break;

            case R.id.btnExportCsv:
                exportCsvFile();

                break;
        }
    }

    private void initViews(View view) {
        fromDateBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnFromDate);
        toDateBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnToDate);
        AntiMonkeyButton exportBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnExportCsv);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) view.findViewById(R.id.cancelBtn);

        setPreconditions();
        setFromDateBtn();
        setToDateBtn();
        initDateTimePickerListener();

        fromDateBtn.setOnClickListener(this);
        toDateBtn.setOnClickListener(this);
        exportBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    public static ExportCsvDialog newInstance(FragmentManager fragmentManager) {
        ExportCsvDialog manualExpenseRegisterDialog = new ExportCsvDialog();

        Bundle args = new Bundle();
        supportFragmentManager = fragmentManager;
        manualExpenseRegisterDialog.setArguments(args);

        return manualExpenseRegisterDialog;
    }

    private void setFromDateBtn() {
        fromDateBtn.setText(
                getString(R.string.from_date).concat(
                        DateUtils.dateToString(
                                fromDate
                        )
                )
        );
    }

    private void setToDateBtn() {
        toDateBtn.setText(
                getString(R.string.to_date).concat(
                        DateUtils.dateToString(
                                toDate
                        )
                )
        );
    }

    private void exportCsvFile() {
        if (this.fromDate.before(this.toDate)) {
            if (PermissionUtils.checkForStoragePermission(getActivity())) {
                new ExportCsvTask().execute();
            }
        } else {
            new SweetAlertDialog(
                    getActivity(),
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(getString(R.string.error_title))
                    .setContentText(getString(R.string.review_expense_dates_err))
                    .show();
        }
    }

    private class ExportCsvTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(
                    getActivity(),
                    R.style.ProgressDialogTheme
            );
            progressDialog.setMessage(getActivity().getString(R.string.progress_dialog_msg));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return CsvUtils.exportCsvFile(
                    fromDate.getTime(),
                    toDate.getTime()
            );
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);

            if (aVoid) {
                progressDialog.dismiss();

                new SweetAlertDialog(
                        getActivity(),
                        SweetAlertDialog.SUCCESS_TYPE
                )
                        .setTitleText(getString(R.string.title_success))
                        .setContentText(getString(R.string.csv_exported_successfully))
                        .show();
            } else {
                progressDialog.dismiss();

                new SweetAlertDialog(
                        getActivity(),
                        SweetAlertDialog.ERROR_TYPE
                )
                        .setTitleText(getString(R.string.error_title))
                        .setContentText(getString(R.string.csv_exported_err))
                        .show();
            }
        }
    }
}