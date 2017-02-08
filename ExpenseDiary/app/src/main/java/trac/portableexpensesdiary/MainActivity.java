package trac.portableexpensesdiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import trac.portableexpensesdiary.adapters.PieChartLegendAdapter;
import trac.portableexpensesdiary.adapters.ReviewExpensesAdapter;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.basecomponents.StaticAntiMonkeyListView;
import trac.portableexpensesdiary.contextdialog.ContextDialog;
import trac.portableexpensesdiary.contextdialog.ContextFunctionalityEnum;
import trac.portableexpensesdiary.expense.ExpenseManager;
import trac.portableexpensesdiary.expense.ExpenseRegisterDialog;
import trac.portableexpensesdiary.expense.ReviewExpensesActivity;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.expense.StartExpenseTrackingActivity;
import trac.portableexpensesdiary.interfaces.DateFilterInterface;
import trac.portableexpensesdiary.interfaces.UiNotifierInterface;
import trac.portableexpensesdiary.manualexpenseregister.ManualExpenseRegisterActivity;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.ExpenseTrackingEnum;
import trac.portableexpensesdiary.options.OptionsDialog;
import trac.portableexpensesdiary.smartexpenseregister.SmartExpenseRegisterActivity;
import trac.portableexpensesdiary.utils.CategoryLanguageParser;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.CurrencyRateExtractorTask;
import trac.portableexpensesdiary.utils.CurrencyUtils;
import trac.portableexpensesdiary.utils.DateUtils;
import trac.portableexpensesdiary.utils.DisplayUtils;
import trac.portableexpensesdiary.utils.ImageUtils;
import trac.portableexpensesdiary.utils.ReceiveIncomeUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class MainActivity extends BaseActivity
        implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        DateFilterInterface,
        UiNotifierInterface,
        OnChartValueSelectedListener {

    private ExpenseTracking currentExpenseTracking;

    private LinearLayout incomeDateLayout;
    private LinearLayout daysTillIncomeLayout;
    private LinearLayout monthlyIncomeLayout;
    private TextView dayOfReceivingIncomeTxt;
    private TextView daysTillIncomeTxt;
    private TextView incomeTxt;
    private TextView expenseTrackingNotSetUpTxt;

    private CardView expenseBalanceLayout;
    private TextView balanceTxt;

    private TextView categoryCountTxtView;

    private PieChart pieChart;
    private PieChartLegendAdapter pieChartLegendAdapter;
    private CardView selectedPieChartItemLayout;
    private CircleImageView selectedPieChartItemCategoryImage;
    private TextView selectedPieChartItemCategoryTxt;
    private TextView selectedPieChartItemAmountTxt;
    private CardView pieChartLegendLayout;

    private CardView lastRecordsLayout;
    private ReviewExpensesAdapter expensesAdapter;

    private Date fromDate;
    private Date toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        preconditionChecker();

        init();
    }

    @Override
    protected void onResume() {
        notifyUi();

        super.onResume();
    }

    @Override
    protected void onPause() {
        selectedPieChartItemLayout.setVisibility(View.GONE);

        super.onPause();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (level == TRIM_MEMORY_COMPLETE) {
            onDestroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.options_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.options) {
            OptionsDialog optionsDialog = OptionsDialog.newInstance();
            optionsDialog.setCancelable(true);
            optionsDialog.show(
                    getFragmentManager(),
                    Constants.OPTIONS_MENU_DIALOG_TAG
            );
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.expenseTrackingImageView:

                if (ExpenseManager.getInstance().isExpenseTrackingSetUp()
                        && currentExpenseTracking.getTrackingState() == ExpenseTrackingEnum.STARTED) {
                    ContextDialog dialog = ContextDialog.newInstance(
                            getString(R.string.context_dialog_edit_tracking),
                            getString(R.string.context_dialog_stop_tracking),
                            getString(R.string.context_dialog_export_csv),
                            ContextFunctionalityEnum.EXPENSE_TRACKING,
                            getSupportFragmentManager()
                    );
                    dialog.setCancelable(true);
                    dialog.show(
                            getFragmentManager(),
                            Constants.CONTEXT_DIALOG_TAG
                    );
                } else if (ExpenseManager.getInstance().isExpenseTrackingSetUp()
                        && currentExpenseTracking.getTrackingState() == ExpenseTrackingEnum.STOPPED) {
                    ContextDialog dialog = ContextDialog.newInstance(
                            getString(R.string.context_dialog_start_tracking),
                            null,
                            getString(R.string.context_dialog_export_csv),
                            ContextFunctionalityEnum.EXPENSE_TRACKING,
                            getSupportFragmentManager()
                    );
                    dialog.setCancelable(true);
                    dialog.show(
                            getFragmentManager(),
                            Constants.CONTEXT_DIALOG_TAG
                    );
                } else {
                    ContextDialog dialog = ContextDialog.newInstance(
                            getString(R.string.context_dialog_start_tracking),
                            null,
                            null,
                            ContextFunctionalityEnum.EXPENSE_TRACKING,
                            getSupportFragmentManager()
                    );
                    dialog.setCancelable(true);
                    dialog.show(
                            getFragmentManager(),
                            Constants.CONTEXT_DIALOG_TAG
                    );
                }

                break;

            case R.id.balanceImageView:

                ContextDialog dialog = ContextDialog.newInstance(
                        getString(R.string.context_dialog_edit_balance),
                        null,
                        null,
                        ContextFunctionalityEnum.BALANCE,
                        getSupportFragmentManager()
                );
                dialog.setCancelable(true);
                dialog.show(
                        getFragmentManager(),
                        Constants.CONTEXT_DIALOG_TAG
                );

                break;

            case R.id.categoriesImageView:

                dialog = ContextDialog.newInstance(
                        getString(R.string.context_dialog_new_category),
                        getString(R.string.context_dialog_review_categories),
                        null,
                        ContextFunctionalityEnum.CATEGORY,
                        getSupportFragmentManager()
                );
                dialog.setCancelable(true);
                dialog.show(
                        getFragmentManager(),
                        Constants.CONTEXT_DIALOG_TAG
                );

                break;

            case R.id.statisticsImageView:
                ContextDialog contextDialog = ContextDialog.newInstance(
                        getString(R.string.context_dialog_date_filter),
                        null,
                        null,
                        ContextFunctionalityEnum.STATISTICS,
                        getSupportFragmentManager()
                );

                contextDialog.setCancelable(true);
                contextDialog.show(
                        getFragmentManager(),
                        Constants.CONTEXT_DIALOG_TAG
                );

                break;

            case R.id.lastRecordsImageView:

                dialog = ContextDialog.newInstance(
                        getString(R.string.context_dialog_review_expenses),
                        null,
                        null,
                        ContextFunctionalityEnum.LAST_RECORDS,
                        getSupportFragmentManager()
                );
                dialog.setCancelable(true);
                dialog.show(
                        getFragmentManager(),
                        Constants.CONTEXT_DIALOG_TAG
                );

                break;

            case R.id.addIncomeBtn:
                if (isExpenseTrackingSetUp()) {
                    Category incomeCategory = Category.getCategoryByName(Constants.DEFAULT_CATEGORY_INCOME);
                    ExpenseRegisterDialog expenseRegisterDialog = ExpenseRegisterDialog.newInstance(
                            getSupportFragmentManager(),
                            incomeCategory.getId().toString(),
                            null,
                            null,
                            null
                    );
                    expenseRegisterDialog.setCancelable(false);
                    expenseRegisterDialog.show(
                            getFragmentManager(),
                            Constants.EXPENSE_REGISTER_DIALOG_TAG
                    );
                }

                break;

            case R.id.smartExpenseRegBtn:
                if (isExpenseTrackingSetUp()) {
                    DisplayUtils.startNewActivity(
                            this,
                            new Intent(
                                    this,
                                    SmartExpenseRegisterActivity.class
                            ),
                            false);
                }

                break;

            case R.id.manualExpenseRegBtn:
                if (isExpenseTrackingSetUp()) {
                    DisplayUtils.startNewActivity(
                            this,
                            new Intent(
                                    this,
                                    ManualExpenseRegisterActivity.class
                            ),
                            false);
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.lastRecordsListView) {
            releaseResources();
            DisplayUtils.startNewActivity(
                    this,
                    new Intent(
                            this,
                            ReviewExpensesActivity.class
                    ),
                    false
            );
        }
    }

    @Override
    public void setFilterDates(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        if (pieChart != null) {
            setPieChartData(pieChart);
        }
        setLastRecordsLayoutValues();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        selectedPieChartItemLayout.setVisibility(View.VISIBLE);

        PieEntry pieEntry = (PieEntry) e;

        selectedPieChartItemCategoryImage.setImageBitmap(
                ImageUtils.blobToBitmap(
                        Category.getCategoryByName(
                                CategoryLanguageParser.getTranslatedCategoryName(
                                        this,
                                        pieEntry.getLabel()
                                )
                        ).getCategoryPicture().getBlob()
                )
        );
        selectedPieChartItemCategoryTxt.setText(
                pieEntry.getLabel()
        );
        selectedPieChartItemAmountTxt.setText(
                RoundUtils.floatToStringWithCurrency(
                        pieEntry.getValue()
                )
        );
    }

    @Override
    public void onNothingSelected() {
        selectedPieChartItemLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyUi() {
        new NotifyUiLoaderTask().execute();
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    private void init() {
        setUpPreconditions();
        initExpenseTrackingLayout();
        initBalanceLayout();
        initCategoryLayout();
        initStatisticsLayout();
        initLastRecordsLayout();
        initFloatingActionButton();
    }

    private void initExpenseTrackingLayout() {
        incomeDateLayout =
                (LinearLayout) findViewById(R.id.incomeDateLayout);
        daysTillIncomeLayout =
                (LinearLayout) findViewById(R.id.daysTillIncomeLayout);
        monthlyIncomeLayout =
                (LinearLayout) findViewById(R.id.monthlyIncomeLayout);
        ImageView expenseTrackingImageView =
                (ImageView) findViewById(R.id.expenseTrackingImageView);
        dayOfReceivingIncomeTxt =
                (TextView) findViewById(R.id.receivingIncomeDateTxt);
        daysTillIncomeTxt =
                (TextView) findViewById(R.id.daysTillIncomeTxt);
        incomeTxt =
                (TextView) findViewById(R.id.incomeTxt);
        expenseTrackingNotSetUpTxt =
                (TextView) findViewById(R.id.expenseTrackingNotSetUpTxt);

        expenseTrackingImageView.setOnClickListener(this);
        registerForContextMenu(expenseTrackingImageView);
    }

    private void setExpenseTrackingLayoutValues() {
        if (currentExpenseTracking == null ||
                currentExpenseTracking.getTrackingState() == ExpenseTrackingEnum.STOPPED) {
            incomeDateLayout.setVisibility(View.GONE);
            daysTillIncomeLayout.setVisibility(View.GONE);
            monthlyIncomeLayout.setVisibility(View.GONE);
            expenseTrackingNotSetUpTxt.setVisibility(View.VISIBLE);
        } else {
            incomeDateLayout.setVisibility(View.VISIBLE);
            daysTillIncomeLayout.setVisibility(View.VISIBLE);
            monthlyIncomeLayout.setVisibility(View.VISIBLE);
            expenseTrackingNotSetUpTxt.setVisibility(View.GONE);

            String dayOfReceivingIncomeMessage = String.valueOf(
                    DateUtils.getDayOfMonth(
                            currentExpenseTracking.getDateOfReceivingIncome()
                    )
            ).concat(
                    getString(R.string.day_of_receiving_income_sufix)
            );

            if (currentExpenseTracking.getIncomeMethod() == ExpenseTrackingEnum.INCOME_METHOD_YEARLY) {

                dayOfReceivingIncomeMessage = String.valueOf(
                        DateUtils.dateToStringWithoutYear(
                                currentExpenseTracking.getDateOfReceivingIncome()
                        )
                ).concat(
                        getString(R.string.year_of_receiving_income_suffix)
                );
            }

            dayOfReceivingIncomeTxt.setText(dayOfReceivingIncomeMessage);

            daysTillIncomeTxt.setText(
                    String.format(
                            "%s%s",
                            getDayOfReceivingIncome(),
                            Integer.parseInt(getDayOfReceivingIncome()) <= 1 ? " ".concat(getString(R.string.day_sufix)) : " ".concat(getString(R.string.days_sufix))
                    )
            );
            incomeTxt.setText(
                    RoundUtils.floatToStringWithCurrency(
                            currentExpenseTracking.getIncome()
                    )
            );
        }
    }

    private void initBalanceLayout() {
        expenseBalanceLayout =
                (CardView) findViewById(R.id.balance_layout);
        balanceTxt =
                (TextView) findViewById(R.id.balanceTxt);
        ImageView balanceImageView =
                (ImageView) findViewById(R.id.balanceImageView);

        balanceImageView.setOnClickListener(this);
    }

    private void setBalanceLayoutValues() {
        if (currentExpenseTracking != null) {
            expenseBalanceLayout.setVisibility(View.VISIBLE);
            balanceTxt.setText(
                    RoundUtils.floatToStringWithCurrency(
                            currentExpenseTracking.getCurrentAmount()
                    )
            );
        } else {
            expenseBalanceLayout.setVisibility(View.GONE);
        }
    }

    private void initCategoryLayout() {
        ImageView categoryImageView =
                (ImageView) findViewById(R.id.categoriesImageView);
        categoryCountTxtView =
                (TextView) findViewById(R.id.categoryCountTxtView);

        categoryImageView.setOnClickListener(this);
    }

    private void setCategoryLayoutValues() {
        long registeredCategories = Category.getNumberOfRecords();

        categoryCountTxtView.setText(
                String.format(
                        "%s%s%s",
                        String.valueOf(registeredCategories),
                        " ",
                        getString(R.string.registered_categories_suffix)
                )
        );
    }

    private void initStatisticsLayout() {
        ImageView statisticsImageView =
                (ImageView) findViewById(R.id.statisticsImageView);
        pieChart =
                (PieChart) findViewById(R.id.pieChart);
        selectedPieChartItemLayout =
                (CardView) findViewById(R.id.selectedPieChartItemLayout);
        selectedPieChartItemCategoryImage =
                (CircleImageView) findViewById(R.id.selectedPieChartItemCategoryImage);
        selectedPieChartItemCategoryTxt =
                (TextView) findViewById(R.id.selectedPieItemCategory);
        selectedPieChartItemAmountTxt =
                (TextView) findViewById(R.id.selectedPieItemAmount);
        pieChartLegendLayout =
                (CardView) findViewById(R.id.pieChartLegendLayout);
        StaticAntiMonkeyListView pieChartLegendListView =
                (StaticAntiMonkeyListView) findViewById(R.id.pieChartLegendListView);

        pieChartLegendAdapter = new PieChartLegendAdapter(this);
        pieChartLegendAdapter.setPieChart(pieChart);
        pieChartLegendListView.setAdapter(pieChartLegendAdapter);

        setPieChart(pieChart);

        pieChart.setOnChartValueSelectedListener(this);
        statisticsImageView.setOnClickListener(this);
    }

    private void setStatisticsLayoutValues() {
        setPieChartData(pieChart);
    }

    private void setPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(false);
        pieChart.setDescription("");
        pieChart.animateX(1000);
        pieChart.setDrawSliceText(false);
    }

    private void setPieChartData(final PieChart pieChart) {
        if (currentExpenseTracking != null) {
            List<ExpenseTrackingDetails> expenseTrackingDetailsList =
                    ExpenseTrackingDetails.getExpenseTrackingDetailsFiltered(
                            fromDate.getTime(),
                            toDate.getTime(),
                            null
                    );

            List<PieEntry> yValues = new ArrayList<>();
            List<Integer> colorList = initColors();
            Iterator<ExpenseTrackingDetails> iterator =
                    expenseTrackingDetailsList.iterator();

            float totalDetailPrice = 0;

            while (iterator.hasNext()) {
                ExpenseTrackingDetails details = iterator.next();
                Category category = Category.getCategoryById(
                        details.getExpenseCategoryId()
                );

                if (category.getCategoryName().equals(Constants.DEFAULT_CATEGORY_INCOME)) {
                    iterator.remove();
                }
            }

            if (expenseTrackingDetailsList.isEmpty()) {
                pieChartLegendLayout.setVisibility(View.GONE);
            } else {
                pieChartLegendLayout.setVisibility(View.VISIBLE);
            }


            List<PieChartDataHolder> pieChartDataHolderList = new ArrayList<>();
            Currency currentCurrency = Currency.getCurrency(
                    SessionManager.getInstance().getCurrentUser().getCurrencyId()
            );

            for (int i = 0; i < expenseTrackingDetailsList.size(); i++) {
                Currency selectedCurrency = Currency.getCurrency(
                        expenseTrackingDetailsList.get(i).getCurrencyId()
                );
                UUID id = expenseTrackingDetailsList.get(i).getExpenseCategoryId();
                String currentCurrencyRate;

                if(!currentCurrency.getCurrencyName().equals(selectedCurrency.getCurrencyName()) &&
                        !currentCurrency.getCurrencyRate().equals(selectedCurrency.getCurrencyRate())) {
                    currentCurrencyRate = currentCurrency.getCurrencyRate();
                } else {
                    currentCurrencyRate = selectedCurrency.getCurrencyRate();
                }

                float price = ExpenseManager.getInstance().convertExpensePrice(
                        expenseTrackingDetailsList.get(i).getSelectedCurrencyRate(),
                        currentCurrencyRate,
                        expenseTrackingDetailsList.get(i).getExpensePrice()
                );

                totalDetailPrice += price;

                boolean shouldAddToStructure = true;

                for (int j = 0; j < pieChartDataHolderList.size(); j++) {
                    if (pieChartDataHolderList.get(j).getCategoryId().equals(id)) {
                        pieChartDataHolderList.get(j).addExpensePrice(price);
                        shouldAddToStructure = false;

                        break;
                    }
                }

                if (shouldAddToStructure) {
                    pieChartDataHolderList.add(
                            new PieChartDataHolder(
                                    id,
                                    price
                            )
                    );
                }
            }

            pieChartLegendAdapter.setPieChartDataHolderList(pieChartDataHolderList);

            for (PieChartDataHolder pieChartDataHolderDetail : pieChartDataHolderList) {
                yValues.add(
                        new PieEntry(
                                pieChartDataHolderDetail.getExpenseDetailPrice(),
                                Category.getCategoryById(
                                        pieChartDataHolderDetail.getCategoryId()
                                ).getCategoryName()
                        )
                );
            }

            setCenterPieChartText(
                    totalDetailPrice,
                    pieChart
            );

            PieDataSet dataSet = new PieDataSet(yValues, "");
            dataSet.setSliceSpace(3);
            dataSet.setSelectionShift(5);

            dataSet.setColors(colorList);

            PieData pieData = new PieData();
            pieData.setDataSet(dataSet);
            pieData.setValueFormatter(new PercentFormatter());
            pieData.setValueTextSize(11f);
            pieData.setValueTextColor(Color.WHITE);

            pieChart.setData(pieData);
            pieChart.highlightValue(null);
            pieChart.invalidate();
        } else {
            pieChartLegendLayout.setVisibility(View.GONE);
        }

        pieChartLegendAdapter.notifyDataSetChanged();
    }

    private List<Integer> initColors() {
        List<Integer> colorList = new ArrayList<>();

        for (int colorId : ColorTemplate.PASTEL_COLORS) {
            colorList.add(colorId);
        }

        for (int colorId : ColorTemplate.COLORFUL_COLORS) {
            colorList.add(colorId);
        }

        for (int colorId : ColorTemplate.JOYFUL_COLORS) {
            colorList.add(colorId);
        }

        for (int colorId : ColorTemplate.LIBERTY_COLORS) {
            colorList.add(colorId);
        }

        for (int colorId : ColorTemplate.VORDIPLOM_COLORS) {
            colorList.add(colorId);
        }

        for (int colorId : ColorTemplate.MATERIAL_COLORS) {
            colorList.add(colorId);
        }

        return colorList;
    }

    private void setCenterPieChartText(float totalAmount, PieChart pieChart) {
        pieChart.setCenterText(
                getString(R.string.statistics_spent_amount).concat(
                        RoundUtils.floatToStringWithCurrency(totalAmount)
                )
        );
        pieChart.setCenterTextColor(
                getResources().getColor(R.color.button_text_color)
        );
        pieChart.setDrawCenterText(true);
    }

    private void initLastRecordsLayout() {
        lastRecordsLayout =
                (CardView) findViewById(R.id.lastRecordsLayout);
        ImageView lastRecordsImageView =
                (ImageView) findViewById(R.id.lastRecordsImageView);
        StaticAntiMonkeyListView lastRecordsListView =
                (StaticAntiMonkeyListView) findViewById(R.id.lastRecordsListView);
        expensesAdapter = new ReviewExpensesAdapter(this);

        lastRecordsListView.setAdapter(expensesAdapter);

        lastRecordsImageView.setOnClickListener(this);
        lastRecordsListView.setOnItemClickListener(this);
    }

    private void setLastRecordsLayoutValues() {
        expensesAdapter.setNumberOfRecords(2);
        expensesAdapter.setFromDate(fromDate.getTime());
        expensesAdapter.setToDate(toDate.getTime());

        expensesAdapter.notifyDataSetChanged();

        if (!expensesAdapter.isEmpty()) {
            lastRecordsLayout.setVisibility(View.VISIBLE);
        } else {
            lastRecordsLayout.setVisibility(View.GONE);
        }
    }

    private void initFloatingActionButton() {
        FloatingActionMenu menuBtn =
                (FloatingActionMenu) findViewById(R.id.menuFabBtn);
        menuBtn.setMenuButtonColorNormal(
                getResources().getColor(R.color.colorPrimary)
        );

        FloatingActionButton addIncomeBtn =
                (FloatingActionButton) findViewById(R.id.addIncomeBtn);
        FloatingActionButton smartExpenseRegBtn =
                (FloatingActionButton) findViewById(R.id.smartExpenseRegBtn);
        FloatingActionButton manualExpenseRegBtn =
                (FloatingActionButton) findViewById(R.id.manualExpenseRegBtn);

        addIncomeBtn.setColorNormal(
                getResources().getColor(R.color.colorPrimary)
        );
        smartExpenseRegBtn.setColorNormal(
                getResources().getColor(R.color.colorPrimary)
        );
        manualExpenseRegBtn.setColorNormal(
                getResources().getColor(R.color.colorPrimary)
        );

        addIncomeBtn.setOnClickListener(this);
        smartExpenseRegBtn.setOnClickListener(this);
        manualExpenseRegBtn.setOnClickListener(this);
    }

    private boolean isExpenseTrackingSetUp() {
        boolean isExpenseTrackingRegistered = true;

        if (!ExpenseManager.getInstance().isExpenseTrackingStarted()) {
            isExpenseTrackingRegistered = false;

            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.title_warning))
                    .setContentText(getString(R.string.expense_tracking_not_started))
                    .setConfirmText(getString(R.string.yes_btn))
                    .setCancelText(getString(R.string.cancel_btn))
                    .showCancelButton(true)
                    .setCancelClickListener(
                            new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            }
                    )
                    .setConfirmClickListener(
                            new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    DisplayUtils.startNewActivity(
                                            MainActivity.this,
                                            new Intent(
                                                    MainActivity.this,
                                                    StartExpenseTrackingActivity.class
                                            ),
                                            false
                                    );

                                    sDialog.dismissWithAnimation();
                                }
                            })
                    .show();
        }

        return isExpenseTrackingRegistered;
    }

    private void setUpPreconditions() {
        currentExpenseTracking =
                ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        fromDate = calendar.getTime();
        toDate = new Date();
    }

    private void preconditionChecker() {
        new CurrencyUtils(this).checkCurrencyForInit();
        ReceiveIncomeUtils.checkActiveExpenseTracking(this);
        new CurrencyRateExtractorTask(this).execute();
    }

    private String getDayOfReceivingIncome() {
        if (currentExpenseTracking != null) {

            return DateUtils.subtractDates(
                    new Date(
                            currentExpenseTracking.getDateOfReceivingIncome()
                    ),
                    new Date()
            );
        }

        return "";
    }

    public void releaseResources() {
        pieChartLegendAdapter.clearData();
        expensesAdapter.clearData();
    }

    private void confirmExit() {
        new SweetAlertDialog(
                this,
                SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.title_warning))
                .setContentText(getString(R.string.confirm_exit_app))
                .setConfirmText(getString(R.string.yes_btn))
                .setCancelText(getString(R.string.no_btn))
                .showCancelButton(true)
                .setCancelClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }
                )
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                finish();
                            }
                        })
                .show();
    }

    public class PieChartDataHolder {

        private UUID categoryId;
        private float expenseDetailPrice;

        PieChartDataHolder(UUID categoryId, float expenseDetailPrice) {
            this.categoryId = categoryId;
            this.expenseDetailPrice = expenseDetailPrice;
        }

        public UUID getCategoryId() {
            return categoryId;
        }

        public float getExpenseDetailPrice() {
            return expenseDetailPrice;
        }


        void addExpensePrice(float expenseDetailPrice) {
            this.expenseDetailPrice =
                    expenseDetailPrice + this.expenseDetailPrice;
        }
    }

    private class NotifyUiLoaderTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(
                    MainActivity.this,
                    R.style.ProgressDialogTheme
            );
            progressDialog.setMessage(
                    MainActivity.this.getString(R.string.progress_dialog_msg)
            );
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            MainActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            setUpPreconditions();
                            setExpenseTrackingLayoutValues();
                            setBalanceLayoutValues();
                            setCategoryLayoutValues();
                            setStatisticsLayoutValues();
                            setLastRecordsLayoutValues();

                        }
                    }
            );

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
        }
    }
}