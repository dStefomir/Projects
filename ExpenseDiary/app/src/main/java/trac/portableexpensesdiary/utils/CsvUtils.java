package trac.portableexpensesdiary.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import trac.portableexpensesdiary.expense.ExpenseManager;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;

public class CsvUtils {

    private static String getCsvFileName() {
        return String.format(
                "ExpenseReport-%s.csv",
                DateUtils.dateToString(
                        new Date(
                                System.currentTimeMillis()
                        )
                ).replace(':', '-').replace(" ", "-")
        );
    }

    private static String getCsvHeader() {

        return String.format(
                "%s;%s;%s;%s;%s;",
                "",
                "",
                "Selected ExpenseTracking Tracking",
                "",
                ""
        );
    }

    private static String getCsvExpenseColumns() {

        return String.format(
                "%s;%s;%s;",
                "Date of receiving income",
                "Income",
                "Current Amount"
        );
    }

    private static String getCsvExpenseRows(ExpenseTracking expenseTracking) {

        return String.format(
                "%s;%s;%s;",
                DateUtils.dateToString(
                        expenseTracking.getDateOfReceivingIncome()
                ),
                RoundUtils.floatToStringWithCurrency(
                        expenseTracking.getIncome()
                ),
                RoundUtils.floatToStringWithCurrency(
                        expenseTracking.getCurrentAmount()
                )
        );
    }

    private static String getCsvExpenseDetailsHeader() {

        return String.format(
                "%s;%s;%s;%s;",
                "",
                "",
                "ExpenseTracking Tracking Details",
                ""
        );
    }

    private static String getCsvExpenseDetailsColumns() {

        return String.format(
                "%s;%s;%s;%s;",
                "Registered At",
                "Category",
                "ExpenseTracking Price",
                "ExpenseTracking Note"
        );
    }

    private static String getCsvExpenseDetailsRows(
            ExpenseTrackingDetails expenseTrackingDetails,
            Currency currency) {

        return String.format("%s;%s;%s;%s;",
                DateUtils.dateToString(
                        expenseTrackingDetails.getRegisteredAt()
                ),
                Category.getCategoryById(
                        expenseTrackingDetails.getExpenseCategoryId()
                ).getCategoryName(),
                expenseTrackingDetails.getExpensePrice() + " " + currency.getCurrencyName(),
                expenseTrackingDetails.getDescription()
        );
    }

    public static boolean exportCsvFile(long fromDate, long toDate) {
        boolean isAppFolderCreated = true;
        boolean isCsvFileCreated = true;
        boolean isCsvFileExported = false;

        List<ExpenseTrackingDetails> expenseTrackingDetailsList =
                ExpenseTrackingDetails.getExpenseTrackingDetailsFiltered(fromDate, toDate, null);

        if (expenseTrackingDetailsList.isEmpty()) {

            return isCsvFileExported;
        }

        try {
            File appRootFolder =
                    new File(Constants.APPLICATION_FOLDER_PATH + File.separator);

            if (!appRootFolder.exists()) {
                isAppFolderCreated = appRootFolder.mkdirs();
            }

            File csvFile = new File(
                    Constants.APPLICATION_FOLDER_PATH,
                    getCsvFileName()
            );

            if (isAppFolderCreated && !csvFile.exists()) {
                try {
                    isCsvFileCreated = csvFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();

                    isCsvFileCreated = false;
                }
            }

            FileOutputStream fileOutputStream =
                    new FileOutputStream(csvFile, true);
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(fileOutputStream);

            outputStreamWriter.write(getCsvHeader());
            outputStreamWriter.write("\n");
            outputStreamWriter.write(getCsvExpenseColumns());
            outputStreamWriter.write("\n");
            outputStreamWriter.write(
                    getCsvExpenseRows(
                            ExpenseManager.getInstance().getCurrentActiveExpenseTracking()
                    )
            );
            outputStreamWriter.write("\n");
            outputStreamWriter.write("\n");
            outputStreamWriter.write(getCsvExpenseDetailsHeader());
            outputStreamWriter.write("\n");
            outputStreamWriter.write(getCsvExpenseDetailsColumns());
            outputStreamWriter.write("\n");

            if (isCsvFileCreated) {
                for (ExpenseTrackingDetails expenseTrackingDetails : expenseTrackingDetailsList) {
                    outputStreamWriter.write(
                            getCsvExpenseDetailsRows(
                                    expenseTrackingDetails,
                                    Currency.getCurrency(
                                            SessionManager.getInstance().getCurrentUser().getCurrencyId()
                                    )
                            )
                    );
                    outputStreamWriter.write("\n");
                }

                outputStreamWriter.close();
                fileOutputStream.close();

                isCsvFileExported = true;
            } else {
                isCsvFileExported = false;
            }
        } catch (Exception ex) {

            isCsvFileExported = false;
        }

        return isCsvFileExported;
    }
}