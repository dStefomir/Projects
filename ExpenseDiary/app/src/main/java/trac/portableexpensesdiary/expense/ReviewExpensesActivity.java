package trac.portableexpensesdiary.expense;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.ReviewExpensesAdapter;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.contextdialog.ContextDialog;
import trac.portableexpensesdiary.contextdialog.ContextFunctionalityEnum;
import trac.portableexpensesdiary.interfaces.DateFilterInterface;
import trac.portableexpensesdiary.interfaces.NotificationInterface;
import trac.portableexpensesdiary.maps.ExpenseLocationActivity;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DateUtils;
import trac.portableexpensesdiary.utils.DisplayUtils;
import trac.portableexpensesdiary.utils.PermissionUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class ReviewExpensesActivity extends BaseActivity
        implements NotificationInterface, DateFilterInterface, View.OnClickListener, AbsListView.RecyclerListener {

    private TextView currentAmountTxtView;
    private TextView filterRangeTxtView;
    private ListView expenseListView;
    private ReviewExpensesAdapter adapter;
    private LinearLayout categoryFilterLayout;
    private TextView categoryFilterNameTxtView;
    private ImageView removeDateFilterImg;
    private Date fromDate;
    private Date toDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_expenses);
        setTitle(
                getString(
                        R.string.context_dialog_review_expenses
                )
        );
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.review_expenses_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.category_filter) {
            ContextDialog contextDialog = ContextDialog.newInstance(
                    getString(R.string.date_filter_title),
                    getString(R.string.category_filter_title),
                    null,
                    ContextFunctionalityEnum.REVIEW_EXPENSES,
                    getSupportFragmentManager()
            );
            contextDialog.setCancelable(true);
            contextDialog.show(
                    getFragmentManager(),
                    Constants.CONTEXT_DIALOG_TAG
            );
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listView) {
            final AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            final ExpenseTrackingDetails expenseTrackingDetails =
                    (ExpenseTrackingDetails) expenseListView.getItemAtPosition(adapterContextMenuInfo.position);

            menu.add(
                    Menu.NONE,
                    0,
                    0,
                    getString(R.string.expense_edit)
            );
            menu.add(
                    Menu.NONE,
                    1,
                    0,
                    getString(R.string.expense_delete)
            );
            if (expenseTrackingDetails.getLatitude() != null &&
                    expenseTrackingDetails.getLongitude() != null) {

                menu.add(Menu.NONE,
                        2,
                        0,
                        getString(R.string.expense_location)
                );
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final ExpenseTrackingDetails expenseTrackingDetails =
                (ExpenseTrackingDetails) expenseListView.getItemAtPosition(adapterContextMenuInfo.position);

        if (item.getItemId() == 0) {
            UUID subCategoryId = expenseTrackingDetails.getExpenseSubCategoryId();
            String subCategory = null;

            if (subCategoryId != null) {
                subCategory = subCategoryId.toString();
            }
            ExpenseRegisterDialog dialog = ExpenseRegisterDialog.newInstance(
                    getSupportFragmentManager(),
                    expenseTrackingDetails.getExpenseCategoryId().toString(),
                    subCategory,
                    null,
                    expenseTrackingDetails.getId().toString()
            );
            dialog.setCancelable(false);
            dialog.show(
                    getFragmentManager(),
                    Constants.EXPENSE_REGISTER_DIALOG_TAG
            );
        } else if (item.getItemId() == 1) {
            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.WARNING_TYPE
            )
                    .setTitleText(getString(R.string.warning_title))
                    .setContentText("Are you sure that you want to delete this expense?")
                    .setConfirmText(getString(R.string.yes_btn))
                    .setCancelText(getString(R.string.no_btn))
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            final ExpenseTracking currentExpenseTracking =
                                    ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
                            final Category expenseDetailCategory =
                                    Category.getCategoryById(expenseTrackingDetails.getExpenseCategoryId());

                            if(expenseDetailCategory.getCategoryName().equals(getString(R.string.income_tag))) {
                                currentExpenseTracking.setCurrentAmount(
                                        RoundUtils.round(
                                                currentExpenseTracking.getCurrentAmount() - expenseTrackingDetails.getExpensePrice(),
                                                2
                                        )
                                );
                            } else {
                                currentExpenseTracking.setCurrentAmount(
                                        RoundUtils.round(
                                                currentExpenseTracking.getCurrentAmount() + expenseTrackingDetails.getExpensePrice(),
                                                2
                                        )
                                );
                            }
                            currentExpenseTracking.save();
                            expenseTrackingDetails.delete();

                            adapter.notifyDataSetChanged();
                            currentAmountTxtView.setText(
                                    RoundUtils.floatToStringWithCurrency(
                                            currentExpenseTracking.getCurrentAmount()
                                    )
                            );

                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            if (PermissionUtils.isGpsPermissionGranted(this)) {
                Intent intent = new Intent(this, ExpenseLocationActivity.class);
                intent.putExtra(
                        Constants.EXPENSE_TRACKING_DETAIL_TAG,
                        expenseTrackingDetails.getId().toString()
                );
                DisplayUtils.startNewActivity(this, intent, false);
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.removeDateFilterImg) {
            removeDateFilterImg.setVisibility(View.GONE);
            setPreconditions();
            setFilterRangeTxtView();
            adapter.setFromDate(fromDate.getTime());
            adapter.setToDate(toDate.getTime());
            adapter.notifyDataSetChanged();

        } else {
            categoryFilterLayout.setVisibility(View.GONE);
            adapter.setSelectedFilterCategory(null);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        AsyncThumbnailImageLoaderTask categoryImageThread =
                (AsyncThumbnailImageLoaderTask)view.getTag(R.id.expenseImage);
        AsyncThumbnailImageLoaderTask subCategoryImageThread =
                (AsyncThumbnailImageLoaderTask)view.getTag(R.id.subCategoryImage);

        if(categoryImageThread != null) {
            categoryImageThread.cancel(true);
        }

        if(subCategoryImageThread != null) {
            subCategoryImageThread.cancel(true);
        }
    }

    @Override
    public void notifyField(Object object) {
        Category selectedCategory = (Category) object;

        if (selectedCategory != null) {
            categoryFilterNameTxtView.setText(selectedCategory.getCategoryName());
            categoryFilterLayout.setVisibility(View.VISIBLE);
        }

        adapter.setFromDate(
                this.fromDate.getTime()
        );
        adapter.setToDate(
                this.toDate.getTime()
        );
        adapter.setSelectedFilterCategory(selectedCategory);
        adapter.notifyDataSetChanged();
    }

    private void initViews() {
        setPreconditions();

        expenseListView =
                (ListView) findViewById(R.id.listView);
        TextView startAmountTxtView =
                (TextView) findViewById(R.id.startingAmount);
        currentAmountTxtView =
                (TextView) findViewById(R.id.currentAmount);
        filterRangeTxtView =
                (TextView) findViewById(R.id.filterRange);
        categoryFilterLayout =
                (LinearLayout) findViewById(R.id.categoryFilterLayout);
        categoryFilterNameTxtView =
                (TextView) findViewById(R.id.categoryFilter);
        ImageView removeCategoryFilterImg =
                (ImageView) findViewById(R.id.removeCategoryFilterImg);
        removeDateFilterImg =
                (ImageView) findViewById(R.id.removeDateFilterImg);

        setIncomeInfo(
                startAmountTxtView,
                currentAmountTxtView
        );
        setFilterRangeTxtView();
        registerForContextMenu(expenseListView);
        setAdapter(expenseListView);

        expenseListView.setRecyclerListener(this);
        removeCategoryFilterImg.setOnClickListener(this);
        removeDateFilterImg.setOnClickListener(this);
    }

    @Override
    public void setFilterDates(Date fromDate, Date toDate) {
        removeDateFilterImg.setVisibility(View.VISIBLE);
        this.fromDate = fromDate;
        this.toDate = toDate;

        adapter.setFromDate(fromDate.getTime());
        adapter.setToDate(toDate.getTime());
        adapter.notifyDataSetChanged();
        setFilterRangeTxtView();
    }

    private void setPreconditions() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        fromDate = calendar.getTime();
        toDate = new Date();
    }

    private void setFilterRangeTxtView() {
        String filterRange = String.format(
                "%s%s%s",
                DateUtils.subtractDates(
                        fromDate,
                        toDate
                ),
                " ",
                getString(R.string.days_sufix)
        );
        filterRangeTxtView.setText(filterRange);
    }

    private void setAdapter(ListView listView) {
        adapter = new ReviewExpensesAdapter(this);

        adapter.setFromDate(fromDate.getTime());
        adapter.setToDate(toDate.getTime());

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setIncomeInfo(
            TextView startAmountTxtView,
            TextView currentAmountTxtView
    ) {
        ExpenseTracking currentExpenseTracking = ExpenseTracking.getCurrentExpenseTracking(
                SessionManager.getInstance().getCurrentUser().getId()
        );

        startAmountTxtView.setText(
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