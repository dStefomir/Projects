package trac.portableexpensesdiary.currency;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.MainActivity;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.CurrencyRegisterAdapter;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.expense.ExpenseManager;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.interfaces.UiNotifierInterface;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTracking;
import trac.portableexpensesdiary.model.User;
import trac.portableexpensesdiary.utils.CurrencyUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class CurrencyRegisterDialog extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private CurrencyRegisterAdapter adapter;

    private Currency selectedCurrency;
    private String searchViewContent;

    public CurrencyRegisterDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_currency_register,
                container
        );

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent,
            View view,
            int position,
            long id) {
        selectedCurrency =
                (Currency) view.getTag(R.layout.currency_register_item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.selectBtn) {
            setCurrency();
        } else {
            User currentUser = SessionManager.getInstance().getCurrentUser();

            if(currentUser.getCurrencyId() == null) {
                getActivity().finish();
            }

            CurrencyUtils.setIsShown(false);
            this.dismiss();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals("")) {
            searchViewContent = "";
        } else {
            searchViewContent = newText.toLowerCase();
        }

        notifyAdapterToUpdate();

        return true;
    }

    private void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        SearchView searchView =
                (SearchView) view.findViewById(R.id.searchView);
        setSearchViewTextAttribute(searchView);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) view.findViewById(R.id.cancelBtn);
        AntiMonkeyButton selectBtn =
                (AntiMonkeyButton) view.findViewById(R.id.selectBtn);
        ListView listView =
                (ListView) view.findViewById(R.id.listView);
        adapter =
                new CurrencyRegisterAdapter(getActivity());
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(this);
        listView.setOnItemClickListener(this);
        cancelBtn.setOnClickListener(this);
        selectBtn.setOnClickListener(this);
    }

    private void setSearchViewTextAttribute(SearchView searchView) {
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.button_text_color));
        searchEditText.setHintTextColor(getResources().getColor(R.color.button_text_color));
    }

    public static CurrencyRegisterDialog newInstance() {
        CurrencyRegisterDialog manualExpenseRegisterDialog =
                new CurrencyRegisterDialog();

        Bundle args = new Bundle();
        manualExpenseRegisterDialog.setArguments(args);

        return manualExpenseRegisterDialog;
    }

    private void setCurrency() {
        if (selectedCurrency != null) {
            User currentUser =
                    SessionManager.getInstance().getCurrentUser();

            if (ExpenseManager.getInstance().isExpenseTrackingSetUp()) {
                Currency currentCurrency =
                        Currency.getCurrency(currentUser.getCurrencyId());
                ExpenseTracking currentExpenseTracking =
                        ExpenseManager.getInstance().getCurrentActiveExpenseTracking();
                currentExpenseTracking.setIncome(
                        RoundUtils.round(
                                currentExpenseTracking.getIncome() /
                                        RoundUtils.stringToFloat(
                                                currentCurrency.getCurrencyRate()) *
                                        RoundUtils.stringToFloat(selectedCurrency.getCurrencyRate()
                                        ),
                                2
                        )
                );
                currentExpenseTracking.setCurrentAmount(
                        RoundUtils.round(
                                currentExpenseTracking.getCurrentAmount() /
                                        RoundUtils.stringToFloat(currentCurrency.getCurrencyRate()) *
                                        RoundUtils.stringToFloat(selectedCurrency.getCurrencyRate()
                                        ),
                                2
                        )
                );
                currentExpenseTracking.save();
            }

            currentUser.setCurrencyId(selectedCurrency.getId());
            currentUser.save();

            Toast.makeText(
                    getActivity(),
                    getString(R.string.currency_saved),
                    Toast.LENGTH_SHORT
            ).show();

            CurrencyUtils.setIsShown(false);

            UiNotifierInterface uiNotifierInterface = ((MainActivity) getActivity());
            uiNotifierInterface.notifyUi();

            dismiss();
        } else {
            new SweetAlertDialog(
                    getActivity(),
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(getString(R.string.error_title))
                    .setContentText(getString(R.string.currency_selection_err))
                    .show();
        }
    }

    private void notifyAdapterToUpdate() {
        List<Currency> filteredCurrencies =
                Currency.getFilteredCurrencies(searchViewContent);
        adapter.setFilteredCurrencyList(filteredCurrencies);
        adapter.notifyDataSetChanged();

    }
}