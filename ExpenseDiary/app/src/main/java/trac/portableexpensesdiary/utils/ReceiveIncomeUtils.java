package trac.portableexpensesdiary.utils;

import android.app.Activity;

import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.MainActivity;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.expense.ExpenseManager;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.interfaces.UiNotifierInterface;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.ExpenseTrackingEnum;

public class ReceiveIncomeUtils {

    public static void checkActiveExpenseTracking(final Activity activity) {
        if (ExpenseManager.getInstance().isExpenseTrackingSetUp()) {
            ExpenseTracking currentExpenseTracking =
                    ExpenseManager.getInstance().getCurrentActiveExpenseTracking();

            Date currentDay = new Date();
            Date payDay = new Date(
                    currentExpenseTracking.getDateOfReceivingIncome()
            );

            if (currentDay.after(payDay)) {
                new SweetAlertDialog(
                        activity,
                        SweetAlertDialog.WARNING_TYPE
                )
                        .setTitleText(activity.getString(R.string.title_warning))
                        .setContentText(activity.getString(R.string.receive_income_msg))
                        .setConfirmText(activity.getString(R.string.yes_btn))
                        .setCancelText(activity.getString(R.string.no_btn))
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                addIncomeToExpenseTracking();
                                addIncomeToDetails(activity);

                                UiNotifierInterface uiNotifierInterface = (MainActivity) activity;
                                uiNotifierInterface.notifyUi();

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        }
    }

    private static void addIncomeToExpenseTracking() {
        ExpenseTracking currentExpense =
                ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
        currentExpense.setCurrentAmount(
                currentExpense.getCurrentAmount() + currentExpense.getIncome()
        );
        if(currentExpense.getIncomeMethod() == ExpenseTrackingEnum.INCOME_METHOD_MONTHLY) {
            currentExpense.setDateOfReceivingIncome(
                    DateUtils.getDatePlusOneMonth(
                            new Date(
                                    currentExpense.getDateOfReceivingIncome()
                            )
                    ).getTime()
            );
        } else {
            currentExpense.setDateOfReceivingIncome(
                    DateUtils.getDatePlusOneYear(
                            new Date(
                                    currentExpense.getDateOfReceivingIncome()
                            )
                    ).getTime()
            );
        }
        currentExpense.save();
    }

    private static void addIncomeToDetails(Activity activity) {
        ExpenseTracking currentExpense =
                ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
        ExpenseTrackingDetails details = new ExpenseTrackingDetails();
        Category incomeCategory =
                Category.getCategoryByName(
                        activity.getString(R.string.income_tag)
                );
        Currency currentCurrency = Currency.getCurrency(
                SessionManager.getInstance().getCurrentUser().getCurrencyId()
        );
        details.setExpenseTrackingId(
                currentExpense.getId()
        );
        details.setCurrencyId(currentCurrency.getId());
        details.setSelectedCurrencyRate(currentCurrency.getCurrencyRate());
        details.setCurrentCurrencyRate(currentCurrency.getCurrencyRate());
        details.setExpenseCategoryId(
                incomeCategory.getId()
        );

        details.setExpensePrice(
                currentExpense.getIncome()
        );
        details.setRegisteredAt(
                System.currentTimeMillis()
        );
        if(currentExpense.getIncomeMethod() == ExpenseTrackingEnum.INCOME_METHOD_MONTHLY) {
            details.setDescription(
                    activity.getString(R.string.income_description_monthly)
            );
        } else {
            details.setDescription(
                    activity.getString(R.string.income_description_yearly)
            );
        }
        details.save();
    }
}