package trac.portableexpensesdiary.contextdialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.MainActivity;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.category.CategoryActivity;
import trac.portableexpensesdiary.category.CategoryManagement;
import trac.portableexpensesdiary.expense.ExpenseFilterDialog;
import trac.portableexpensesdiary.expense.ExpenseManager;
import trac.portableexpensesdiary.expense.ReviewExpensesActivity;
import trac.portableexpensesdiary.expense.StartExpenseTrackingActivity;
import trac.portableexpensesdiary.export.ExportCsvDialog;
import trac.portableexpensesdiary.filterdialog.FilterDialog;
import trac.portableexpensesdiary.interfaces.UiNotifierInterface;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DisplayUtils;

public class ContextDialog extends DialogFragment implements View.OnClickListener {

    private String firstArgument;
    private String secondArgument;
    private String thirdArgument;
    private int functionTag;

    private static FragmentManager supportFragmentManager;

    public ContextDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_context,
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
    public void onClick(View v) {
        if (v.getId() == R.id.firstArgumentLayout) {
            switch (functionTag) {

                case ContextFunctionalityEnum.EXPENSE_TRACKING:
                    ((MainActivity) getActivity()).releaseResources();
                    DisplayUtils.startNewActivity(
                            getActivity(),
                            new Intent(
                                    getActivity(),
                                    StartExpenseTrackingActivity.class
                            ),
                            false
                    );

                    this.dismiss();

                    break;

                case ContextFunctionalityEnum.BALANCE:
                    ((MainActivity) getActivity()).releaseResources();
                    Intent intent = new Intent(
                            getActivity(),
                            StartExpenseTrackingActivity.class
                    );
                    intent.putExtra(
                            Constants.EDIT_BALANCE_TAG,
                            Constants.EDIT_BALANCE_TAG
                    );
                    DisplayUtils.startNewActivity(
                            getActivity(),
                            intent,
                            false
                    );

                    this.dismiss();

                    break;

                case ContextFunctionalityEnum.CATEGORY:
                    ((MainActivity) getActivity()).releaseResources();
                    DisplayUtils.startNewActivity(
                            getActivity(),
                            new Intent(
                                    getActivity(),
                                    CategoryActivity.class
                            ),
                            false
                    );

                    this.dismiss();

                    break;

                case ContextFunctionalityEnum.STATISTICS:
                    FilterDialog filterDialog =
                            FilterDialog.newInstance(supportFragmentManager);
                    filterDialog.setCancelable(false);

                    filterDialog.show(
                            getFragmentManager(),
                            Constants.FILTER_DIALOG_TAG
                    );

                    this.dismiss();

                    break;

                case ContextFunctionalityEnum.LAST_RECORDS:
                    ((MainActivity) getActivity()).releaseResources();
                    DisplayUtils.startNewActivity(
                            getActivity(),
                            new Intent(
                                    getActivity(),
                                    ReviewExpensesActivity.class
                            ),
                            false
                    );

                    this.dismiss();

                    break;

                case ContextFunctionalityEnum.REVIEW_EXPENSES:
                    filterDialog =
                            FilterDialog.newInstance(supportFragmentManager);
                    filterDialog.setCancelable(false);

                    filterDialog.show(
                            getFragmentManager(),
                            Constants.FILTER_DIALOG_TAG
                    );

                    this.dismiss();

                    break;
            }
        } else if (v.getId() == R.id.secondArgumentLayout) {
            switch (functionTag) {

                case ContextFunctionalityEnum.EXPENSE_TRACKING:
                    new SweetAlertDialog(
                            getActivity(),
                            SweetAlertDialog.WARNING_TYPE
                    )
                            .setTitleText(getString(R.string.title_warning))
                            .setContentText(getString(R.string.stop_current_expense_tracking_msg))
                            .setConfirmText(getString(R.string.yes_btn))
                            .setCancelText(getString(R.string.no_btn))
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    ExpenseManager.getInstance().stopExpenseTracking();

                                    sDialog.dismissWithAnimation();
                                    UiNotifierInterface uiNotifierInterface =
                                            (MainActivity) getActivity();
                                    uiNotifierInterface.notifyUi();

                                    ContextDialog.this.dismiss();
                                }
                            })
                            .show();
                    break;

                case ContextFunctionalityEnum.CATEGORY:
                    ((MainActivity) getActivity()).releaseResources();
                    DisplayUtils.startNewActivity(
                            getActivity(),
                            new Intent(
                                    getActivity(),
                                    CategoryManagement.class
                            ),
                            false
                    );
                    this.dismiss();

                    break;

                case ContextFunctionalityEnum.REVIEW_EXPENSES:
                    ExpenseFilterDialog dialog =
                            ExpenseFilterDialog.newInstance();
                    dialog.setCancelable(true);
                    dialog.show(
                            getFragmentManager(),
                            Constants.EXPENSE_FILTER_DIALOG_TAG
                    );

                    this.dismiss();

                    break;
            }
        } else {
            switch (functionTag) {

                case ContextFunctionalityEnum.EXPENSE_TRACKING:
                    ExportCsvDialog dialog =
                            ExportCsvDialog.newInstance(supportFragmentManager);
                    dialog.setCancelable(false);
                    dialog.show(
                            getFragmentManager(),
                            Constants.EXPORT_CSV_DIALOG_TAG
                    );

                    this.dismiss();

                    break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        firstArgument =
                args.getString(Constants.CONTEXT_DIALOG_FIRST_ARG_TAG);
        secondArgument =
                args.getString(Constants.CONTEXT_DIALOG_SECOND_ARG_TAG);
        thirdArgument =
                args.getString(Constants.CONTEXT_DIALOG_THIRD_ARG_TAG);
        functionTag =
                args.getInt(Constants.CONTEXT_DIALOG_FUNCTION_TAG);
    }

    private void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        CardView firstArgumentLayout =
                (CardView) view.findViewById(R.id.firstArgumentLayout);
        CardView secondArgumentLayout =
                (CardView) view.findViewById(R.id.secondArgumentLayout);
        CardView thirdArgumentLayout = (CardView) view.findViewById(R.id.thirdArgumentLayout);

        TextView firstArgumentTxt =
                (TextView) view.findViewById(R.id.firstArgument);
        TextView secondArgumentTxt =
                (TextView) view.findViewById(R.id.secondArgument);
        TextView thirdArgumentTxt =
                (TextView) view.findViewById(R.id.thirdArgument);

        firstArgumentTxt.setText(firstArgument);
        if (secondArgument != null) {
            secondArgumentLayout.setVisibility(View.VISIBLE);
            secondArgumentTxt.setText(secondArgument);
        }

        if (thirdArgument != null) {
            thirdArgumentLayout.setVisibility(View.VISIBLE);
            thirdArgumentTxt.setText(thirdArgument);
        }

        firstArgumentLayout.setOnClickListener(this);
        secondArgumentLayout.setOnClickListener(this);
        thirdArgumentLayout.setOnClickListener(this);
    }

    public static ContextDialog newInstance(
            String firstArg,
            String secondArg,
            String thirdArgument,
            int functionTag,
            FragmentManager fragmentManager) {
        ContextDialog expenseFilterDialog = new ContextDialog();

        Bundle args = new Bundle();

        args.putString(
                Constants.CONTEXT_DIALOG_FIRST_ARG_TAG,
                firstArg
        );
        args.putString(
                Constants.CONTEXT_DIALOG_SECOND_ARG_TAG,
                secondArg
        );
        args.putString(
                Constants.CONTEXT_DIALOG_THIRD_ARG_TAG,
                thirdArgument
        );
        args.putInt(
                Constants.CONTEXT_DIALOG_FUNCTION_TAG,
                functionTag
        );

        supportFragmentManager = fragmentManager;

        expenseFilterDialog.setArguments(args);

        return expenseFilterDialog;
    }
}