package trac.portableexpensesdiary.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.CategoryAdapter;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.statistics.AnalyzeExpenseDetailDialog;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DisplayUtils;

public class CategoryManagement extends BaseActivity implements AdapterView.OnItemClickListener, AbsListView.RecyclerListener {

    private CategoryAdapter adapter;
    private GridView categoryGridView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_management);
        setTitle(
                getString(
                        R.string.review_expense_categories_title
                )
        );
        initViews();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.categoryGridView) {
            menu.add(
                    Menu.NONE,
                    0,
                    0,
                    getString(R.string.category_delete)
            );
            menu.add(
                    Menu.NONE,
                    1,
                    0,
                    getString(R.string.analyze_expenses_title)
            );
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Category category = (Category) categoryGridView.getItemAtPosition(
                adapterContextMenuInfo.position
        );

        if (item.getItemId() == 0) {
            if (isCategoryDeletable(category)) {

                new SweetAlertDialog(
                        this,
                        SweetAlertDialog.WARNING_TYPE
                )
                        .setTitleText(getString(R.string.warning_title))
                        .setContentText(getString(R.string.category_sure_deletion))
                        .setConfirmText(getString(R.string.yes_btn))
                        .setCancelText(getString(R.string.no_btn))
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                deleteCategory(category);

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        } else {
            AnalyzeExpenseDetailDialog dialog = AnalyzeExpenseDetailDialog.newInstance(
                    Constants.ROOT_CATEGORY,
                    category.getId().toString()
            );
            dialog.setCancelable(false);
            dialog.show(
                    getFragmentManager(),
                    Constants.ANALYZE_EXPENSE_DETAIL_DIALOG_TAG
            );
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.categoryGridView) {
            Category category = (Category) view.getTag(R.layout.manual_register_item);
            String categoryId = category.getId().toString();

            Intent intent = new Intent(
                    this,
                    CategoryActivity.class
            );
            intent.putExtra(
                    Constants.CATEGORY_ID_TAG,
                    categoryId
            );

            DisplayUtils.startNewActivity(
                    this,
                    intent,
                    true
            );
        }
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        AsyncThumbnailImageLoaderTask categoryImageThread =
                (AsyncThumbnailImageLoaderTask) view.getTag(R.id.imgManualRegisterIcon);

        if(categoryImageThread != null) {
            categoryImageThread.cancel(true);
        }
    }

    private void initViews() {
        categoryGridView =
                (GridView) findViewById(R.id.categoryGridView);
        setAdapter(categoryGridView);

        registerForContextMenu(categoryGridView);
        categoryGridView.setOnItemClickListener(this);
        categoryGridView.setRecyclerListener(this);
    }

    private void setAdapter(GridView gridView) {
        adapter = new CategoryAdapter(this, Constants.CATEGORY_IMAGE_SIZE_BIG);
        gridView.setAdapter(adapter);
    }

    private boolean isCategoryDeletable(Category category) {
        if (!category.isDeletable()) {
            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.error_title))
                    .setContentText(getString(R.string.category_not_deletable))
                    .setConfirmText(getString(R.string.ok_btn))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();

            return false;
        }

        return true;
    }

    private void deleteCategory(Category category) {
        List<ExpenseTrackingDetails> expenseTrackingDetailsList =
                ExpenseTrackingDetails.getExpenseTrackingDetailByRootCategory(category);

        if (!expenseTrackingDetailsList.isEmpty()) {
            for (ExpenseTrackingDetails details : expenseTrackingDetailsList) {
                details.delete();
            }
        }

        List<SubCategory> subCategoryList =
                SubCategory.getAllSubCategoriesByParentId(category.getId());

        for (SubCategory subCategory : subCategoryList) {
            expenseTrackingDetailsList =
                    ExpenseTrackingDetails.getExpenseTrackingDetailBySubCategory(subCategory);
            for (ExpenseTrackingDetails details : expenseTrackingDetailsList) {
                details.delete();
            }

            subCategory.delete();
        }

        category.delete();

        adapter.notifyDataSetChanged();
    }
}
