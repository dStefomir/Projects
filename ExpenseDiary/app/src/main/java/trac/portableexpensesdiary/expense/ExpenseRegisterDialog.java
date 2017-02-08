package trac.portableexpensesdiary.expense;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import trac.portableexpensesdiary.MainActivity;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.interfaces.NotificationInterface;
import trac.portableexpensesdiary.interfaces.UiNotifierInterface;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DateUtils;
import trac.portableexpensesdiary.utils.ImageUtils;
import trac.portableexpensesdiary.utils.LocationUtils;
import trac.portableexpensesdiary.utils.PermissionUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class ExpenseRegisterDialog extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {

    private LinearLayout subCategoryLayout;
    private EditText expenseAmountTxt;
    private Spinner expenseAmountCurrencySpinner;
    private EditText expenseDescription;
    private AntiMonkeyButton dateOfRegisteringBtn;
    private AntiMonkeyButton finishBtn;
    private CircleImageView rootCategoryImage;
    private CircleImageView subCategoryImage;

    private static FragmentManager supportFragmentManager;
    private SlideDateTimeListener dateOfRegisteringListener;
    private Date dateOfRegistering;

    private ExpenseTrackingDetails selectedExpenseTrackingDetails;

    private Category category;
    private SubCategory subCategory;
    private Currency selectedCurrency;

    private String expenseAmount;

    private Double latitude;
    private Double longitude;

    public ExpenseRegisterDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_expense_register,
                container
        );
        onViewCreated(view, savedInstanceState);

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        String categoryId =
                args.getString(Constants.CATEGORY_ID_TAG);
        String subCategoryId =
                args.getString(Constants.SUB_CATEGORY_ID_TAG);
        expenseAmount =
                args.getString(Constants.EXPENSE_AMOUNT_TAG);
        String expenseDetailId =
                args.getString(Constants.EXPENSE_DETAIL_TAG);

        if (expenseDetailId != null) {
            selectedExpenseTrackingDetails = ExpenseTrackingDetails.getExpenseDetail(
                    UUID.fromString(expenseDetailId)
            );
        }

        category = Category.getCategoryById(
                UUID.fromString(categoryId)
        );

        if (subCategoryId != null) {
            subCategory = SubCategory.getSubCategory(
                    UUID.fromString(
                            subCategoryId
                    )
            );
        }
    }

    @Override
    public void onClick(View v) {
        Date currentDate;

        switch (v.getId()) {

            case R.id.dateOfRegisteringBtn:
                currentDate = dateOfRegistering;

                new SlideDateTimePicker
                        .Builder(supportFragmentManager)
                        .setListener(dateOfRegisteringListener)
                        .setInitialDate(currentDate)
                        .setIs24HourTime(true)
                        .build()
                        .show();

                break;

            case R.id.locationBtn:
                setLocation();

                break;

            case R.id.btnConfirm:
                saveData();

                break;

            case R.id.btnCancel:
                if (getActivity().getClass().getSimpleName()
                        .equals(Constants.SMART_REGISTER_ACTIVITY)) {

                    NotificationInterface notify =
                            (NotificationInterface) getActivity();
                    notify.notifyField(null);
                }

                dismiss();

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.expenseAmountCurrencyTxt) {
            selectedCurrency =
                    Currency.getCurrencyByName(
                            expenseAmountCurrencySpinner.getItemAtPosition(position).toString()
                    );
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent.getId() == R.id.expenseAmountCurrencyTxt && selectedCurrency == null) {
            selectedCurrency = Currency.getCurrency(
                    SessionManager.getInstance().getCurrentUser().getCurrencyId()
            );
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().length() > 0) {
            enableButton(finishBtn);
        } else {
            disableButton(finishBtn);
        }
    }

    private void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        dateOfRegisteringBtn =
                (AntiMonkeyButton) view.findViewById(R.id.dateOfRegisteringBtn);
        subCategoryLayout =
                (LinearLayout) view.findViewById(R.id.subCategoryLayout);
        expenseAmountTxt =
                (EditText) view.findViewById(R.id.expenseAmount);
        expenseAmountCurrencySpinner =
                (Spinner) view.findViewById(R.id.expenseAmountCurrencyTxt);
        expenseDescription =
                (EditText) view.findViewById(R.id.expenseDescription);
        CircleImageView locationBtn =
                (CircleImageView) view.findViewById(R.id.locationBtn);
        rootCategoryImage =
                (CircleImageView) view.findViewById(R.id.rootCategoryImage);
        subCategoryImage =
                (CircleImageView) view.findViewById(R.id.subCategoryImage);
        finishBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnConfirm);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) view.findViewById(R.id.btnCancel);

        dateOfRegistering = new Date();

        setDateOfRegisteringBtn();
        initDateTimePickerListener();

        expenseAmountTxt.addTextChangedListener(this);
        dateOfRegisteringBtn.setOnClickListener(this);
        expenseAmountCurrencySpinner.setOnItemSelectedListener(this);
        finishBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        locationBtn.setOnClickListener(this);

        disableButton(finishBtn);

        loadData();
    }

    private void setSpinner() {
        List<Currency> allCurrencies = Currency.getAllCurrencies();
        List<String> currencyNames = new ArrayList<>();
        for (Currency currency : allCurrencies) {
            currencyNames.add(currency.getCurrencyName());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.spinner_item,
                currencyNames
        );
        spinnerArrayAdapter.setDropDownViewResource(
                R.layout.spinner_drop_down_item
        );

        expenseAmountCurrencySpinner.setAdapter(spinnerArrayAdapter);
        expenseAmountCurrencySpinner.setSelection(
                spinnerArrayAdapter.getPosition(
                        Currency.getCurrency(
                                SessionManager
                                        .getInstance()
                                        .getCurrentUser()
                                        .getCurrencyId()
                        ).getCurrencyName()
                )
        );
    }

    private void setLocation() {
        if (PermissionUtils.isGpsPermissionGranted(getActivity())) {
            final ProgressDialog progressDialog = new ProgressDialog(
                    getActivity(),
                    R.style.ProgressDialogTheme
            );
            progressDialog.setMessage(
                    getActivity().getString(R.string.progress_dialog_msg)
            );
            progressDialog.setCancelable(false);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();

                    getActivity().runOnUiThread(
                            new Runnable() {

                                @Override
                                public void run() {
                                    progressDialog.show();
                                }
                            }
                    );
                    LocationUtils.LocationResult locationResult =
                            new LocationUtils.LocationResult() {

                                @Override
                                public void gotLocation(final Location location) {
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }

                                    getActivity().runOnUiThread(
                                            new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (location == null) {
                                                        Toast.makeText(
                                                                getActivity(),
                                                                "Application was not able to acquire your location!",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    } else {
                                                        Toast.makeText(
                                                                getActivity(),
                                                                "Location acquired!",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }

                                                    progressDialog.dismiss();
                                                }
                                            }
                                    );
                                }
                            };


                    LocationUtils locationUtils = new LocationUtils();
                    locationUtils.getLocation(
                            getActivity(),
                            locationResult
                    );
                    Looper.loop();
                }
            };
            new Thread(runnable).start();
        }
    }

    private void setDateOfRegisteringBtn() {
        dateOfRegisteringBtn.setText(
                DateUtils.dateToString(
                        dateOfRegistering
                )
        );
    }

    private void disableButton(FancyButton button) {
        button.setEnabled(false);
        button.setBorderColor(
                getActivity().getResources().getColor(R.color.gray_btn_bg_color)
        );
        button.setTextColor(
                getActivity().getResources().getColor(R.color.gray_btn_bg_color)
        );
    }

    private void enableButton(FancyButton button) {
        button.setEnabled(true);
        button.setBorderColor(
                getActivity().getResources().getColor(R.color.button_text_color)
        );
        button.setTextColor(
                getActivity().getResources().getColor(R.color.button_text_color)
        );
    }

    private void initDateTimePickerListener() {
        dateOfRegisteringListener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                dateOfRegistering = date;
                setDateOfRegisteringBtn();
            }
        };
    }

    private void loadData() {
        rootCategoryImage.setImageDrawable(
                ImageUtils.blobToDrawable(
                        getResources(),
                        category.getCategoryPicture().getBlob()
                )
        );

        if (subCategory != null) {
            subCategoryLayout.setVisibility(View.VISIBLE);
            subCategoryImage.setImageDrawable(
                    ImageUtils.blobToDrawable(
                            getResources(),
                            subCategory.getSubCategoryPicture().getBlob()
                    )
            );
        }

        if (expenseAmount != null) {
            expenseAmountTxt.setText(expenseAmount);
        } else if (selectedExpenseTrackingDetails != null) {
            expenseAmountTxt.setText(
                    RoundUtils.floatToString(
                            selectedExpenseTrackingDetails.getExpensePrice()
                    )
            );

            expenseDescription.setText(
                    selectedExpenseTrackingDetails.getDescription()
            );
        }

        setSpinner();
    }

    private void saveData() {
        final ExpenseTracking currentExpenseTracking =
                ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
        final Currency currentCurrency = Currency.getCurrency(
                SessionManager.getInstance().getCurrentUser().getCurrencyId()
        );
        float expensePrice = ExpenseManager.getInstance().convertExpensePrice(
                selectedCurrency.getCurrencyRate(),
                null,
                RoundUtils.stringToFloat(
                        expenseAmountTxt.getText().toString()
                )
        );

        ExpenseTrackingDetails details = selectedExpenseTrackingDetails;

        if (details == null) {
            details = new ExpenseTrackingDetails();
        } else {
            //TODO : proveri dali vsichko e nared s tova tuka che e pod vapros!
            //TODO : baga e che sled kato editnesh pokupka cenata se setva cqlata a ne se dobavq s razlikata mezdu novata i starata
            float oldExpensePrice = details.getExpensePrice();

            if(expensePrice > oldExpensePrice) {
                expensePrice = ExpenseManager.getInstance().convertExpensePrice(
                        selectedCurrency.getCurrencyRate(),
                        null,
                        RoundUtils.round(
                                expensePrice - oldExpensePrice,
                                2
                        )
                );
            } else if(expensePrice < oldExpensePrice) {
                expensePrice = ExpenseManager.getInstance().convertExpensePrice(
                        selectedCurrency.getCurrencyRate(),
                        null,
                        RoundUtils.round(
                                oldExpensePrice - expensePrice,
                                2
                        )
                );
            }
        }

        details.setExpenseTrackingId(
                currentExpenseTracking.getId()
        );
        details.setExpenseCategoryId(category.getId());

        if (subCategory != null) {
            details.setExpenseSubCategoryId(subCategory.getId());
        }
        details.setCurrencyId(selectedCurrency.getId());
        details.setSelectedCurrencyRate(selectedCurrency.getCurrencyRate());
        details.setCurrentCurrencyRate(currentCurrency.getCurrencyRate());
        details.setExpensePrice(
                RoundUtils.stringToFloat(
                        expenseAmountTxt.getText().toString()
                )
        );
        details.setRegisteredAt(dateOfRegistering.getTime());
        details.setDescription(expenseDescription.getText().toString());
        details.setLatitude(latitude);
        details.setLongitude(longitude);
        details.save();

        if (category.getCategoryName().equals(Constants.DEFAULT_CATEGORY_INCOME)) {
            currentExpenseTracking.setCurrentAmount(
                    RoundUtils.round(
                            currentExpenseTracking.getCurrentAmount() + expensePrice,
                            2
                    )
            );
        } else {
            currentExpenseTracking.setCurrentAmount(
                    RoundUtils.round(
                            currentExpenseTracking.getCurrentAmount() - expensePrice,
                            2
                    )
            );
        }
        currentExpenseTracking.save();

        if (getActivity().getClass().getSimpleName()
                .equals(Constants.SMART_REGISTER_ACTIVITY)) {
            NotificationInterface notify =
                    (NotificationInterface) getActivity();
            notify.notifyField(null);

        } else if (getActivity().getClass().getSimpleName()
                .equals("MainActivity")) {
            UiNotifierInterface uiNotifierInterface =
                    (MainActivity) getActivity();
            uiNotifierInterface.notifyUi();
        }

        if (selectedExpenseTrackingDetails == null) {
            Toast.makeText(
                    getActivity(),
                    getString(R.string.manual_reg_expense_reg_successfully),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    getActivity(),
                    "Expense edited successfully",
                    Toast.LENGTH_SHORT
            ).show();
        }

        dismiss();
    }

    public static ExpenseRegisterDialog newInstance(
            FragmentManager fragmentManager,
            String categoryId,
            String subCategoryId,
            String expenseAmount,
            String expenseDetailId) {

        ExpenseRegisterDialog expenseRegisterDialog =
                new ExpenseRegisterDialog();

        Bundle args = new Bundle();

        supportFragmentManager = fragmentManager;

        args.putString(
                Constants.CATEGORY_ID_TAG,
                categoryId
        );
        args.putString(
                Constants.SUB_CATEGORY_ID_TAG,
                subCategoryId
        );
        args.putString(
                Constants.EXPENSE_AMOUNT_TAG,
                expenseAmount
        );
        args.putString(
                Constants.EXPENSE_DETAIL_TAG,
                expenseDetailId
        );

        expenseRegisterDialog.setArguments(args);

        return expenseRegisterDialog;
    }
}