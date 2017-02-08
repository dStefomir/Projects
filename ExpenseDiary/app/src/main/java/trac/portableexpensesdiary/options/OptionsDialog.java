package trac.portableexpensesdiary.options;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.currency.CurrencyRegisterDialog;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.login.SignUpActivity;
import trac.portableexpensesdiary.model.User;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DisplayUtils;

public class OptionsDialog extends DialogFragment
        implements View.OnClickListener {

    public OptionsDialog() {}

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_options,
                container
        );

        onViewCreated(view, savedInstanceState);

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onViewCreated(
            View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.editUserBtn) {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            String currentUserId = currentUser.getId().toString();

            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            intent.putExtra(Constants.USER_ID_TAG, currentUserId);

            DisplayUtils.startNewActivity(getActivity(), intent, false);
        } else if(v.getId() == R.id.changeCurrencyBtn) {
            CurrencyRegisterDialog currencyRegisterDialog =
                    CurrencyRegisterDialog.newInstance();
            currencyRegisterDialog.setCancelable(true);

            currencyRegisterDialog.show(
                    getFragmentManager(),
                    Constants.CURRENCY_REGISTER_DIALOG_TAG
            );
        }
    }

    protected void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        AntiMonkeyButton editUserBtn =
                (AntiMonkeyButton) view.findViewById(R.id.editUserBtn);
        AntiMonkeyButton changeCurrencyBtn =
                (AntiMonkeyButton) view.findViewById(R.id.changeCurrencyBtn);

        editUserBtn.setOnClickListener(this);
        changeCurrencyBtn.setOnClickListener(this);
    }

    public static OptionsDialog newInstance() {
        return new OptionsDialog();
    }
}
