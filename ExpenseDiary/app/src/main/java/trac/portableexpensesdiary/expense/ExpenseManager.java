package trac.portableexpensesdiary.expense;

import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingEnum;
import trac.portableexpensesdiary.utils.RoundUtils;

public class ExpenseManager {

    private static ExpenseManager instance = new ExpenseManager();

    private ExpenseManager() {}

    public static ExpenseManager getInstance() {
        return instance;
    }

    void setExpenseTracking(
            float totalIncome,
            float currentAmount,
            int incomeMethod,
            long dateOfReceivingIncome) {
        ExpenseTracking expenseTracking =
                getCurrentActiveExpenseTracking();

        if (expenseTracking == null) {
            expenseTracking = new ExpenseTracking();
        }

        expenseTracking.setIncome(totalIncome);
        expenseTracking.setIncomeMethod(incomeMethod);
        expenseTracking.setDateOfReceivingIncome(dateOfReceivingIncome);
        expenseTracking.setCurrentAmount(currentAmount);
        expenseTracking.setTrackingState(ExpenseTrackingEnum.STARTED);
        expenseTracking.save();
    }

    public void stopExpenseTracking() {
        ExpenseTracking currentExpenseTracking =
                getCurrentActiveExpenseTracking();
        currentExpenseTracking.setTrackingState(ExpenseTrackingEnum.STOPPED);
        currentExpenseTracking.save();
    }

    void addIncome(float additionalIncome) {
        ExpenseTracking currentExpenseTracking =
                getCurrentActiveExpenseTracking();

        if (currentExpenseTracking != null) {
            currentExpenseTracking.setCurrentAmount(additionalIncome);
            currentExpenseTracking.save();
        }
    }

    public float convertExpensePrice(
            String selectedCurrencyRate,
            String currentCurrencyRate,
            float expenseAmount
    ) {
        if(currentCurrencyRate == null) {
            currentCurrencyRate = Currency.getCurrency(
                    SessionManager.getInstance().getCurrentUser().getCurrencyId()
            ).getCurrencyRate();
        }

        float convertedValue =
                expenseAmount /
                        Float.parseFloat(selectedCurrencyRate) *
                        Float.parseFloat(currentCurrencyRate);

        return RoundUtils.round(convertedValue, 2);
    }

    public ExpenseTracking getCurrentActiveExpenseTracking() {

        return ExpenseTracking.getCurrentExpenseTracking(
                SessionManager.getInstance().getCurrentUser().getId()
        );
    }

    public boolean isExpenseTrackingSetUp() {
        boolean isStarted = false;

        ExpenseTracking currentExpenseTracking =
                getCurrentActiveExpenseTracking();

        if (currentExpenseTracking != null) {
            isStarted = true;
        }

        return isStarted;
    }

    public boolean isExpenseTrackingStarted() {
        boolean isStarted = true;

        if (isExpenseTrackingSetUp()) {
            ExpenseTracking currentExpenseTracking =
                    getCurrentActiveExpenseTracking();

            if (currentExpenseTracking.getTrackingState() == ExpenseTrackingEnum.STOPPED) {
                isStarted = false;
            }
        } else {
            isStarted = false;
        }

        return isStarted;
    }
}