package trac.portableexpensesdiary.smartexpenseregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.microblink.activity.SegmentScanActivity;
import com.microblink.ocr.ScanConfiguration;
import com.microblink.recognizers.blinkocr.parser.generic.AmountParserSettings;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.CategoryAdapter;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyGridView;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.category.SubCategoryChooserDialog;
import trac.portableexpensesdiary.expense.ExpenseRegisterDialog;
import trac.portableexpensesdiary.interfaces.NotificationInterface;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;

public class SmartExpenseRegisterActivity extends BaseActivity
        implements AdapterView.OnItemClickListener, NotificationInterface, AbsListView.RecyclerListener {

    private Category selectedCategory;
    private SubCategory selectedSubCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual_register);
        setTitle(getString(R.string.smart_expense_register_title));
        initViews();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == 5) {
            if (resultCode == SegmentScanActivity.RESULT_OK &&
                    data != null) {

                Bundle extras = data.getExtras();
                Bundle results =
                        extras.getBundle(SegmentScanActivity.EXTRAS_SCAN_RESULTS);

                if (results != null) {
                    String price =
                            results.getString(Constants.OCR_AMOUNT_FUNCTIONALITY);

                    if (price != null && price.contains(",")) {
                        price = price.replaceAll(",", ".");
                    }

                    ExpenseRegisterDialog dialog = ExpenseRegisterDialog.newInstance(
                            getSupportFragmentManager(),
                            selectedCategory.getId().toString(),
                            selectedSubCategory != null ? selectedSubCategory.getId().toString() : null,
                            price,
                            null
                    );
                    dialog.setCancelable(false);
                    dialog.show(
                            getFragmentManager(),
                            Constants.MANUAL_EXPENSE_REGISTER_DIALOG_TAG
                    );
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory =
                (Category) view.getTag(R.layout.manual_register_item);

        if (selectedCategory.getNumberOfSubCategories() == 0) {
            Intent intent = new Intent(
                    this,
                    SegmentScanActivity.class
            );

            ScanConfiguration[] confArray = new ScanConfiguration[]{
                    new ScanConfiguration(getString(R.string.ocr_scanning_title),
                            getString(R.string.ocr_tip_title), Constants.OCR_AMOUNT_FUNCTIONALITY,
                            new AmountParserSettings())
            };

            intent.putExtra(
                    SegmentScanActivity.EXTRAS_LICENSE_KEY,
                    Constants.OCR_LICENCE_KEY
            );
            intent.putExtra(
                    SegmentScanActivity.EXTRAS_SCAN_CONFIGURATION,
                    confArray
            );

            startActivityForResult(intent, Constants.OCR_CAPTURE);
        } else {
            SubCategoryChooserDialog dialog = SubCategoryChooserDialog.newInstance(
                    getSupportFragmentManager(),
                    selectedCategory.getId().toString(),
                    Constants.SMART_FUNCTION
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

    @Override
    public void notifyField(Object object) {
        selectedSubCategory = (SubCategory) object;
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