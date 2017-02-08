package trac.portableexpensesdiary.statistics;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.ImageUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class AnalyzeExpenseDetailDialog extends DialogFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView expensesAmountTxt;

    private List<ExpenseTrackingDetails> detailsList;
    private Category selectedCategory;
    private SubCategory selectedSubCategory;
    private String categoryTag;

    public AnalyzeExpenseDetailDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_analyze_expense_detail,
                container
        );

        onViewCreated(view, savedInstanceState);

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {
            dismiss();
        }
    }

    @Override
    public void onCheckedChanged(
            CompoundButton buttonView,
            boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.oneMonthBtn:
                if (isChecked) {
                    setExpensesAmountTxt(1);
                }

                break;

            case R.id.threeMonthBtn:
                if (isChecked) {
                    setExpensesAmountTxt(3);
                }

                break;

            case R.id.sixMonthBtn:
                if (isChecked) {
                    setExpensesAmountTxt(6);
                }

                break;

            case R.id.nineMonthBtn:
                if (isChecked) {
                    setExpensesAmountTxt(9);
                }

                break;

            case R.id.twelveMonthBtn:
                if (isChecked) {
                    setExpensesAmountTxt(12);
                }

                break;
        }
    }

    @Override
    public void onViewCreated(
            View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState);

        Bundle args = getArguments();

        categoryTag =
                args.getString(Constants.CATEGORY_TAG);

        String categoryId =
                args.getString(Constants.CATEGORY_ID_TAG);

        if (categoryTag.equals(Constants.ROOT_CATEGORY)) {
            selectedCategory = Category.getCategoryById(
                    UUID.fromString(categoryId)
            );

            detailsList = ExpenseTrackingDetails.getExpenseTracingDetailByCategory(
                    UUID.fromString(categoryId)
            );
        } else {
            selectedSubCategory = SubCategory.getSubCategory(
                    UUID.fromString(categoryId)
            );

            detailsList = ExpenseTrackingDetails.getExpenseTrackingDetailBySubCategory(
                    UUID.fromString(categoryId)
            );
        }
    }

    private void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnCancel);
        TextView categoryNameTxt =
                (TextView) view.findViewById(R.id.categoryNameTitle);
        ImageView categoryImage =
                (ImageView) view.findViewById(R.id.categoryPicture);
        RadioButton oneMonthBtn =
                (RadioButton) view.findViewById(R.id.oneMonthBtn);
        oneMonthBtn.setChecked(true);
        RadioButton threeMonthsBtn =
                (RadioButton) view.findViewById(R.id.threeMonthBtn);
        RadioButton sixMonthsBtn =
                (RadioButton) view.findViewById(R.id.sixMonthBtn);
        RadioButton nineMonthsBtn =
                (RadioButton) view.findViewById(R.id.nineMonthBtn);
        RadioButton twelveMonthsBtn =
                (RadioButton) view.findViewById(R.id.twelveMonthBtn);
        expensesAmountTxt =
                (TextView) view.findViewById(R.id.expensesAmount);
        setExpensesAmountTxt(1);

        if (categoryTag.equals(Constants.ROOT_CATEGORY)) {
            categoryNameTxt.setText(
                    selectedCategory.getCategoryName()
            );
            categoryImage.setImageDrawable(
                    ImageUtils.blobToDrawable(
                            getResources(),
                            selectedCategory.getCategoryPicture().getBlob()
                    )
            );
        } else {
            categoryNameTxt.setText(
                    selectedSubCategory.getSubCategoryName()
            );
            categoryImage.setImageDrawable(
                    ImageUtils.blobToDrawable(
                            getResources(),
                            selectedSubCategory.getSubCategoryPicture().getBlob()
                    )
            );
        }

        cancelBtn.setOnClickListener(this);
        oneMonthBtn.setOnCheckedChangeListener(this);
        threeMonthsBtn.setOnCheckedChangeListener(this);
        sixMonthsBtn.setOnCheckedChangeListener(this);
        nineMonthsBtn.setOnCheckedChangeListener(this);
        twelveMonthsBtn.setOnCheckedChangeListener(this);
    }

    private float getCalculatedAmount(int monthRange) {
        float calculatedExpenseAmount = 0f;

        for (ExpenseTrackingDetails details : detailsList) {
            calculatedExpenseAmount += details.getExpensePrice();
        }

        return RoundUtils.round(
                calculatedExpenseAmount / monthRange,
                2
        );
    }

    private void setExpensesAmountTxt(int monthRange) {
        float calculatedExpenseAmount =
                getCalculatedAmount(monthRange);

        expensesAmountTxt.setText(
                RoundUtils.floatToStringWithCurrency(
                        calculatedExpenseAmount
                )
        );
    }

    public static AnalyzeExpenseDetailDialog newInstance(String categoryTag, String categoryId) {
        AnalyzeExpenseDetailDialog analyzeExpenseDetailDialog =
                new AnalyzeExpenseDetailDialog();

        Bundle args = new Bundle();

        args.putString(
                Constants.CATEGORY_TAG,
                categoryTag
        );

        args.putString(
                Constants.CATEGORY_ID_TAG,
                categoryId
        );

        analyzeExpenseDetailDialog.setArguments(args);

        return analyzeExpenseDetailDialog;
    }
}