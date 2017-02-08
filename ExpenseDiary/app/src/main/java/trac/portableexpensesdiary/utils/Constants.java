package trac.portableexpensesdiary.utils;

import android.os.Environment;

public class Constants {

    public static final String DATABASE_NAME = "ExpensesDataBase";
    public static final int DATABASE_VERSION = 1;
    public final static String MANUAL_EXPENSE_REGISTER_DIALOG_TAG = "expenseRegisterDialog";
    public final static String CATEGORY_ID_TAG = "categoryId";
    public final static int CAMERA_CAPTURE = 1;
    public final static int SELECT_IMAGE = 2;
    public final static int REQUEST_CAMERA = 0;
    public final static String REMEMBER_ME = "rememberMe";
    public final static int BTN_DELAY_IN_MILLISECONDS = 250;
    public static final String APPLICATION_FOLDER_PATH =
            Environment.getExternalStorageDirectory().toString() + "/PortableExpensesDiary/";
    public static final String USER_ID_TAG = "userId";
    public static final int OCR_CAPTURE = 5;
    public static final String OCR_LICENCE_KEY =
            "G4NSSEKL-6XFG3XWO-6LC2W2KM-V5YVMGBW-EN5TICIR-GOWODRPD-U67MGKQO-CTKNNR7T";
    public static final String OCR_AMOUNT_FUNCTIONALITY = "Amount";
    public static final String CURRENCY_REGISTER_DIALOG_TAG = "currencyRegisterDialog";
    public static final String EXPORT_CSV_DIALOG_TAG = "exportCsvDialog";
    public static final String SUB_CATEGORY_ID_TAG = "subCategoryId";
    public static final String SUB_CATEGORY_CHOOSER_DIALOG = "subCategoryChooserDialog";
    public static final String EXPENSE_AMOUNT_TAG = "expenseAmount";
    public static final String EXPENSE_REGISTER_DIALOG_TAG = "expenseRegisterDialog";
    public static final String SUB_CATEGORY_CHOOSER_FUNCTIONALITY = "subCategoryChooserFunctionality";
    public static final int SMART_FUNCTION = 1;
    public static final int MANUAL_FUNCTION = 0;
    public static final String SMART_REGISTER_ACTIVITY = "SmartExpenseRegisterActivity";
    public static final String EXPENSE_FILTER_DIALOG_TAG = "expenseFilterDialog";
    public static final String ANALYZE_EXPENSE_DETAIL_DIALOG_TAG = "analyzeDialog";
    public static final String CONTEXT_DIALOG_FIRST_ARG_TAG = "firstArg";
    public static final String CONTEXT_DIALOG_SECOND_ARG_TAG = "secondArg";
    public static final String CONTEXT_DIALOG_THIRD_ARG_TAG = "thirdArg";
    public static final String CONTEXT_DIALOG_FUNCTION_TAG = "functionTag";
    public static final String CONTEXT_DIALOG_TAG = "contextDialog";
    public static final String FILTER_DIALOG_TAG = "filterDialog";
    public static final String EDIT_BALANCE_TAG = "editBalance";
    public static final int CATEGORY_IMAGE_SIZE_BIG = 70;
    public static final int CATEGORY_IMAGE_SIZE_SMALL = 55;
    public static final String EXPENSE_TRACKING_DETAIL_TAG = "expenseTrackingDetail";
    public static final String CATEGORY_TAG = "category";
    public static final String ROOT_CATEGORY = "rootCategory";
    public static final String SUB_CATEGORY = "subCategory";
    public static final String EXPENSE_DETAIL_TAG = "expenseDetail";
    public static final int COMPRESSED_IMAGE_WIDTH = 200;
    public static final int COMPRESSED_IMAGE_HEIGHT = 200;
    public static final String OPTIONS_MENU_DIALOG_TAG = "optionsDialog";
    public static final String DEFAULT_CATEGORY_INCOME = "Income";
    public static final String DEFAULT_CATEGORY_FOOD = "Food/Drinks";
    public static final String DEFAULT_CATEGORY_ALCOHOL = "Alcohol";
    public static final String DEFAULT_CATEGORY_PUBLIC_TRANSPORT = "Public Transport";
    public static final String DEFAULT_CATEGORY_FUEL = "Fuel";
    public static final String DEFAULT_CATEGORY_CREDIT = "Credit";
    public static final String DEFAULT_CATEGORY_BILLS = "Bills";
    public static final String DEFAULT_CATEGORY_MEDICAL_DRUGS = "Medical Drugs";
    public static final String DEFAULT_CATEGORY_SMOKES = "Smokes";
}