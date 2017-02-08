package trac.portableexpensesdiary.category;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.microblink.activity.SegmentScanActivity;
import com.microblink.ocr.ScanConfiguration;
import com.microblink.recognizers.blinkocr.parser.generic.AmountParserSettings;

import java.util.List;
import java.util.UUID;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.SubCategoryAdapter;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyGridView;
import trac.portableexpensesdiary.expense.ExpenseRegisterDialog;
import trac.portableexpensesdiary.interfaces.NotificationInterface;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;

public class SubCategoryChooserDialog extends DialogFragment
        implements AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.RecyclerListener {

    private Category category;

    private static FragmentManager supportFragmentManager;

    private int dialogFunctionality;

    public SubCategoryChooserDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_sub_category_chooser,
                container
        );
        onViewCreated(
                view,
                savedInstanceState
        );

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        String categoryId =
                args.getString(Constants.CATEGORY_ID_TAG);
        dialogFunctionality =
                args.getInt(Constants.SUB_CATEGORY_CHOOSER_FUNCTIONALITY);
        category = Category.getCategoryById(
                UUID.fromString(categoryId)
        );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SubCategory selectedSubCategory =
                (SubCategory) view.getTag(R.layout.sub_category_item);

        if (dialogFunctionality == Constants.MANUAL_FUNCTION) {
            ExpenseRegisterDialog dialog = ExpenseRegisterDialog.newInstance(
                    supportFragmentManager,
                    category.getId().toString(),
                    selectedSubCategory.getId().toString(),
                    null,
                    null
            );

            dialog.setCancelable(false);
            dialog.show(
                    getFragmentManager(),
                    Constants.EXPENSE_REGISTER_DIALOG_TAG
            );
        } else {
            NotificationInterface notify =
                    (NotificationInterface) getActivity();
            notify.notifyField(selectedSubCategory);

            Intent intent = new Intent(
                    getActivity(),
                    SegmentScanActivity.class
            );
            intent.putExtra(
                    SegmentScanActivity.EXTRAS_LICENSE_KEY,
                    Constants.OCR_LICENCE_KEY
            );

            ScanConfiguration[] confArray = new ScanConfiguration[]{
                    new ScanConfiguration(
                            getString(R.string.ocr_scanning_title),
                            getString(R.string.ocr_tip_title),
                            Constants.OCR_AMOUNT_FUNCTIONALITY,
                            new AmountParserSettings()
                    )
            };
            intent.putExtra(
                    SegmentScanActivity.EXTRAS_SCAN_CONFIGURATION,
                    confArray
            );

            getActivity().startActivityForResult(
                    intent,
                    Constants.OCR_CAPTURE
            );
        }
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {

            dismiss();
        }
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        AsyncThumbnailImageLoaderTask categoryImageThread =
                (AsyncThumbnailImageLoaderTask) view.getTag(R.id.subCategoryPicture);

        if(categoryImageThread != null) {
            categoryImageThread.cancel(true);
        }
    }

    private void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        AntiMonkeyGridView subCategoriesGridView =
                (AntiMonkeyGridView) view.findViewById(R.id.subCategoryGridView);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnCancel);

        List<SubCategory> subCategoryList =
                SubCategory.getAllSubCategoriesByParentId(category.getId());
        SubCategoryAdapter adapter = new SubCategoryAdapter(getActivity());
        adapter.setSubCategoryList(subCategoryList);
        subCategoriesGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        subCategoriesGridView.setRecyclerListener(this);
        cancelBtn.setOnClickListener(this);
        subCategoriesGridView.setOnItemClickListener(this);
    }

    public static SubCategoryChooserDialog newInstance(
            FragmentManager fragmentManager,
            String categoryId,
            int functionality
    ) {
        SubCategoryChooserDialog subCategoryChooserDialog =
                new SubCategoryChooserDialog();

        Bundle args = new Bundle();

        supportFragmentManager = fragmentManager;

        args.putString(
                Constants.CATEGORY_ID_TAG,
                categoryId
        );
        args.putInt(
                Constants.SUB_CATEGORY_CHOOSER_FUNCTIONALITY,
                functionality
        );

        subCategoryChooserDialog.setArguments(args);

        return subCategoryChooserDialog;
    }
}
