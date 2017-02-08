package trac.portableexpensesdiary.expense;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.util.Date;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingEnum;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DateUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class StartExpenseTrackingActivity extends BaseActivity
        implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    private CardView dateOfReceivingIncomeLayout;
    private RadioButton monthlyBtn;
    private RadioButton yearlyBtn;
    private AntiMonkeyButton receivingIncomeDateBtn;
    private SlideDateTimeListener dateOfReceivingIncomeListener;
    private Date dateOfReceivingIncome;

    private CardView incomeLayout;
    private TextInputLayout incomeInputLayout;
    private EditText incomeAmountTxt;
    private TextView incomeAmountCurrencyTxtView;

    private CardView balanceLayout;
    private LinearLayout balanceInfoLayout;
    private TextInputLayout balanceInputLayout;
    private EditText balanceAmountTxt;
    private TextView balanceAmountCurrencyTxtView;
    private TextView currentIncomeAmountTxtView;
    private TextView currentAmountTxtView;

    private ExpenseTracking currentExpenseTracking;
    private Currency currentCurrency;

    private String editBalanceTag;
    private int incomeMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_expense_tracking);
        setTitle(
                getString(
                        R.string.review_expense_title
                )
        );
        editBalanceTag =
                getIntent().getStringExtra(Constants.EDIT_BALANCE_TAG);

        initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOfReceivingIncome) {
            Date date = dateOfReceivingIncome;

            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(dateOfReceivingIncomeListener)
                    .setInitialDate(date)
                    .setIs24HourTime(true)
                    .build()
                    .show();

        } else if (v.getId() == R.id.saveBtn) {
            saveData();
        } else {
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.monthlyBtn && isChecked) {
            incomeMethod = ExpenseTrackingEnum.INCOME_METHOD_MONTHLY;
        } else if(buttonView.getId() == R.id.yearlyBtn && isChecked){
            incomeMethod = ExpenseTrackingEnum.INCOME_METHOD_YEARLY;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (currentExpenseTracking != null) {

            if (!s.toString().equals("") && s.toString().matches(RoundUtils.DECIMAL_REGEX)) {
                float additionalIncome =
                        RoundUtils.stringToFloat(s.toString());

                currentAmountTxtView.setText(
                        RoundUtils.floatToStringWithCurrency(
                                additionalIncome
                        )
                );
            } else {
                currentAmountTxtView.setText(
                        RoundUtils.floatToStringWithCurrency(
                                0.00f
                        )
                );
            }
        } else {
            currentAmountTxtView.setText(
                    s.toString()
            );
        }
    }

    private void initViews() {
        dateOfReceivingIncomeLayout =
                (CardView) findViewById(R.id.dateOfReceivingIncomeLayout);
        monthlyBtn =
                (RadioButton) findViewById(R.id.monthlyBtn);
        yearlyBtn =
                (RadioButton) findViewById(R.id.yearlyBtn);
        receivingIncomeDateBtn =
                (AntiMonkeyButton) findViewById(R.id.btnOfReceivingIncome);

        incomeLayout =
                (CardView) findViewById(R.id.incomeLayout);
        incomeInputLayout =
                (TextInputLayout) findViewById(R.id.incomeTxtLayout);
        incomeAmountTxt =
                (EditText) findViewById(R.id.incomeAmount);
        incomeAmountCurrencyTxtView =
                (TextView) findViewById(R.id.incomeAmountCurrencyTxt);

        balanceLayout =
                (CardView) findViewById(R.id.balanceLayout);
        balanceInputLayout =
                (TextInputLayout) findViewById(R.id.balanceTxtLayout);
        balanceAmountTxt =
                (EditText) findViewById(R.id.balanceAmount);
        balanceAmountCurrencyTxtView =
                (TextView) findViewById(R.id.balanceAmountCurrencyTxt);

        balanceInfoLayout =
                (LinearLayout) findViewById(R.id.balanceInfoLayout);
        currentIncomeAmountTxtView =
                (TextView) findViewById(R.id.currentIncomeAmount);
        currentAmountTxtView =
                (TextView) findViewById(R.id.currentAmount);

        AntiMonkeyButton saveBtn =
                (AntiMonkeyButton) findViewById(R.id.saveBtn);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) findViewById(R.id.cancelBtn);

        setLayoutVisibility();

        monthlyBtn.setOnCheckedChangeListener(this);
        yearlyBtn.setOnCheckedChangeListener(this);
        balanceAmountTxt.addTextChangedListener(this);
        receivingIncomeDateBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void setLayoutVisibility() {
        currentExpenseTracking =
                ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
        currentCurrency = Currency.getCurrency(
                SessionManager.getInstance().getCurrentUser().getCurrencyId()
        );

        if (currentExpenseTracking == null) {
            balanceInfoLayout.setVisibility(View.GONE);
        } else if (editBalanceTag != null) {
            dateOfReceivingIncomeLayout.setVisibility(View.GONE);
            incomeLayout.setVisibility(View.GONE);
        } else {
            balanceLayout.setVisibility(View.GONE);
        }

        setDateOfReceivingIncomeLayout();
        setIncomeLayout();
        setBalanceLayout();
        setBalanceInfoLayout();
    }

    private void setDateOfReceivingIncomeLayout() {
        if (currentExpenseTracking != null) {
            incomeMethod = currentExpenseTracking.getIncomeMethod();

            if(incomeMethod == ExpenseTrackingEnum.INCOME_METHOD_MONTHLY) {
                monthlyBtn.setChecked(true);
            } else {
                yearlyBtn.setChecked(true);
            }

            dateOfReceivingIncome =
                    new Date(currentExpenseTracking.getDateOfReceivingIncome());
            setDateOfReceivingIncome(dateOfReceivingIncome);
        } else {
            monthlyBtn.setChecked(true);
        }

        initDateTimePickerListener();
    }

    private void setDateOfReceivingIncome(Date dateOfReceivingIncome) {
        this.dateOfReceivingIncome = dateOfReceivingIncome;
        receivingIncomeDateBtn.setText(
                DateUtils.dateToString(
                        dateOfReceivingIncome.getTime()
                )
        );
    }

    private void initDateTimePickerListener() {
        dateOfReceivingIncomeListener = new SlideDateTimeListener() {

            @Override
            public void onDateTimeSet(Date date) {
                setDateOfReceivingIncome(date);
            }

            @Override
            public void onDateTimeCancel() {
            }
        };
    }

    private void setIncomeLayout() {
        if (currentExpenseTracking != null) {
            incomeAmountTxt.setText(
                    RoundUtils.floatToString(
                            currentExpenseTracking.getIncome()
                    )
            );
            incomeAmountCurrencyTxtView.setText(
                    currentCurrency.getCurrencyName()
            );
        }
    }

    private void setBalanceLayout() {
        if (currentExpenseTracking != null) {
            balanceAmountTxt.setText(
                    RoundUtils.floatToString(
                            currentExpenseTracking.getCurrentAmount()
                    )
            );
            balanceAmountCurrencyTxtView.setText(
                    currentCurrency.getCurrencyName()
            );
            setBalanceInfoLayout();
        }
    }

    private void setBalanceInfoLayout() {
        if (currentExpenseTracking != null) {
            currentIncomeAmountTxtView.setText(
                    RoundUtils.floatToStringWithCurrency(
                            currentExpenseTracking.getIncome()
                    )
            );
            currentAmountTxtView.setText(
                    RoundUtils.floatToStringWithCurrency(
                            currentExpenseTracking.getCurrentAmount()
                    )
            );
        }
    }

    private void saveData() {
        if (areFieldsValidated()) {
            String totalIncome =
                    incomeAmountTxt.getText().toString();
            String currentAmount =
                    balanceAmountTxt.getText().toString();

            boolean isCurrentExpenseTrackingBeenEdited = false;
            if (currentExpenseTracking != null) {
                isCurrentExpenseTrackingBeenEdited = true;
            }

            ExpenseManager.getInstance().setExpenseTracking(
                    RoundUtils.stringToFloat(totalIncome),
                    RoundUtils.stringToFloat(currentAmount),
                    incomeMethod,
                    dateOfReceivingIncome.getTime()
            );

            String dialogMessage = getString(R.string.expenses_tracking_registered);

            if (isCurrentExpenseTrackingBeenEdited &&
                    currentExpenseTracking.getTrackingState() == ExpenseTrackingEnum.STARTED) {
                dialogMessage = getString(R.string.expenses_tracking_edited);
            }
                new SweetAlertDialog(
                        this,
                        SweetAlertDialog.SUCCESS_TYPE
                )
                        .setTitleText(
                                getString(R.string.title_success)
                        )
                        .setContentText(
                                dialogMessage
                        )
                        .setConfirmText(
                                getString(R.string.ok_btn)
                        )
                        .setConfirmClickListener(
                                new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        finish();
                                    }
                                }
                        ).show();
        }
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;

        incomeInputLayout.setError(null);
        balanceInputLayout.setError(null);

        String totalIncome =
                incomeAmountTxt.getText().toString();
        String currentAmount =
                balanceAmountTxt.getText().toString();

        if (dateOfReceivingIncome == null) {
            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(
                            getString(R.string.error_title)
                    )
                    .setContentText(
                            getString(R.string.expense_start_dates_not_selected_err)
                    )
                    .show();

            areValidated = false;
        }

        if (dateOfReceivingIncome != null &&
                (!DateUtils.isSelectedDateSameAsCurrentDay(dateOfReceivingIncome) &&
                        dateOfReceivingIncome.before(new Date()))) {
            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(
                            getString(R.string.error_title)
                    )
                    .setContentText(
                            getString(R.string.receiving_income_err)
                    ).show();

            areValidated = false;
        }

        if (totalIncome.isEmpty()) {
            incomeInputLayout.setError(
                    getString(R.string.empty_field_err)
            );
            incomeInputLayout.requestFocus();
            areValidated = false;
        }

        if (currentAmount.isEmpty() &&
                balanceInputLayout.getVisibility() == View.VISIBLE) {
            balanceInputLayout.setError(
                    getString(R.string.empty_field_err)
            );
            balanceInputLayout.requestFocus();
            areValidated = false;
        }

        return areValidated;
    }
}