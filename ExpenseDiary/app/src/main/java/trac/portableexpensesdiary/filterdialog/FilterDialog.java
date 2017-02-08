package trac.portableexpensesdiary.filterdialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.util.Calendar;
import java.util.Date;

import trac.portableexpensesdiary.MainActivity;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.expense.ReviewExpensesActivity;
import trac.portableexpensesdiary.interfaces.DateFilterInterface;
import trac.portableexpensesdiary.utils.DateUtils;

public class FilterDialog extends DialogFragment implements View.OnClickListener {

    private AntiMonkeyButton fromDateBtn;
    private AntiMonkeyButton toDateBtn;
    protected Date fromDate;
    protected Date toDate;
    protected SlideDateTimeListener fromDateListener;
    protected SlideDateTimeListener toDateListener;

    private static FragmentManager supportFragmentManager;

    public FilterDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_filter,
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
                        .setIs24HourTime(true)
                        .build()
                        .show();

                break;

            case R.id.btnToDate:
                toDate = this.toDate;
                new SlideDateTimePicker
                        .Builder(supportFragmentManager)
                        .setListener(toDateListener)
                        .setInitialDate(toDate)
                        .setIs24HourTime(true)
                        .build()
                        .show();

                break;

            case R.id.btnConfirm:
                if (areFieldsValidated()) {
                    DateFilterInterface filterInterface;

                    try {
                        filterInterface = (MainActivity) getActivity();
                    } catch (Exception e) {
                        filterInterface = (ReviewExpensesActivity) getActivity();
                    }

                    filterInterface.setFilterDates(
                            this.fromDate,
                            this.toDate
                    );

                    this.dismiss();
                }

                break;

            case R.id.btnCancel:
                dismiss();
        }
    }

    protected void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        fromDateBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnFromDate);
        toDateBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnToDate);
        AntiMonkeyButton confirmBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnConfirm);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnCancel);

        setPreconditions();
        setFromDateBtn();
        setToDateBtn();
        initDateTimePickerListener();

        fromDateBtn.setOnClickListener(this);
        toDateBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    public static FilterDialog newInstance(FragmentManager fragmentManager) {
        FilterDialog filterDialog = new FilterDialog();

        Bundle args = new Bundle();
        supportFragmentManager = fragmentManager;
        filterDialog.setArguments(args);

        return filterDialog;
    }

    protected void initDateTimePickerListener() {
        fromDateListener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                fromDate = date;
                setFromDateBtn();
            }
        };

        toDateListener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                toDate = date;
                setToDateBtn();
            }
        };
    }

    private void setFromDateBtn() {
        fromDateBtn.setText(
                getString(R.string.from_date).concat(
                        DateUtils.dateToString(
                                fromDate.getTime()
                        )
                )
        );
    }

    private void setToDateBtn() {
        toDateBtn.setText(
                getString(R.string.to_date).concat(
                        DateUtils.dateToString(
                                toDate.getTime()
                        )
                )
        );
    }

    protected void setPreconditions() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(
                Calendar.MONTH,
                -1
        );
        calendar.set(
                Calendar.HOUR,
                0
        );
        calendar.set(
                Calendar.MINUTE,
                0
        );
        calendar.set(
                Calendar.SECOND,
                1
        );

        fromDate = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.set(
                Calendar.HOUR,
                23
        );
        calendar.set(
                Calendar.MINUTE,
                59
        );
        calendar.set(
                Calendar.SECOND,
                59
        );

        toDate = calendar.getTime();
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;

        if (toDate.before(fromDate)) {
            areValidated = false;

            new SweetAlertDialog(
                    getActivity(),
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(getString(R.string.error_title))
                    .setContentText(getString(R.string.to_date_err))
                    .show();
        }

        if (toDate.compareTo(fromDate) == 0) {
            areValidated = false;

            new SweetAlertDialog(
                    getActivity(),
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(getString(R.string.error_title))
                    .setContentText(getString(R.string.from_date_err))
                    .show();
        }

        return areValidated;
    }
}