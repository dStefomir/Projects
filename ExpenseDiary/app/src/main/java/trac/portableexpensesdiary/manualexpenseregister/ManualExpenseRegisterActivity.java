package trac.portableexpensesdiary.manualexpenseregister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.CategoryAdapter;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyGridView;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.category.SubCategoryChooserDialog;
import trac.portableexpensesdiary.expense.ExpenseRegisterDialog;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;

public class ManualExpenseRegisterActivity extends BaseActivity
        implements AdapterView.OnItemClickListener, AbsListView.RecyclerListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual_register);
        setTitle(getString(R.string.manual_expense_register_title));

        initViews();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category =
                (Category) view.getTag(R.layout.manual_register_item);

        if (category.getNumberOfSubCategories() == 0) {
            ExpenseRegisterDialog dialog = ExpenseRegisterDialog.newInstance(
                    getSupportFragmentManager(),
                    category.getId().toString(),
                    null,
                    null,
                    null
            );
            dialog.setCancelable(false);
            dialog.show(
                    getFragmentManager(),
                    Constants.EXPENSE_REGISTER_DIALOG_TAG
            );
        } else {
            SubCategoryChooserDialog dialog = SubCategoryChooserDialog.newInstance(
                    getSupportFragmentManager(),
                    category.getId().toString(),
                    Constants.MANUAL_FUNCTION
            );
            dialog.setCancelable(true);
            dialog.show(
                    getFragmentManager(),
                    Constants.SUB_CATEGORY_CHOOSER_DIALOG
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
        AntiMonkeyGridView gridView =
                (AntiMonkeyGridView) findViewById(R.id.gridView);
        CategoryAdapter adapter = new CategoryAdapter(
                this,
                Constants.CATEGORY_IMAGE_SIZE_BIG
        );

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setRecyclerListener(this);
    }
}
