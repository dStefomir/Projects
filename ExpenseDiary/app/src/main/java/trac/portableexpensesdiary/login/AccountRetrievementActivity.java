package trac.portableexpensesdiary.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.model.User;
import trac.portableexpensesdiary.utils.Constants;

public class AccountRetrievementActivity extends BaseActivity implements View.OnClickListener {

    private EditText userForgetAnswerTxt;
    private TextInputLayout userForgetAnswerTextInput;

    private User foundUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_retrievement);

        setTitle(getString(R.string.acc_retrievement_title));
        initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.validateBtn) {
            if (areFieldsValidated()) {
                if (foundUser.getForgetAnswer()
                        .equals(userForgetAnswerTxt.getText().toString().trim())) {
                    String userName = String.format(
                            "%s%s%s",
                            getString(R.string.user_name),
                            " ",
                            foundUser.getUserName()
                    );
                    String userPassword = String.format(
                            "%s%s%s",
                            getString(R.string.user_password),
                            " ",
                            foundUser.getUserPassword()
                    );

                    String formattedCredentials =
                            userName.concat("\n").concat(userPassword);
                    new SweetAlertDialog(
                            this,
                            SweetAlertDialog.SUCCESS_TYPE
                    )
                            .setTitleText(getString(R.string.title_success))
                            .setContentText(formattedCredentials)
                            .setConfirmText(getString(R.string.ok_btn))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(
                            this,
                            SweetAlertDialog.ERROR_TYPE
                    )
                            .setTitleText(getString(R.string.error_title))
                            .setContentText(getString(R.string.user_retrievement_err))
                            .setConfirmText(getString(R.string.ok_btn))
                            .show();
                }
            }
        }
    }

    private void initViews() {
        EditText userForgetQuestionTxt =
                (EditText) findViewById(R.id.forgetQuestion);
        userForgetAnswerTxt =
                (EditText) findViewById(R.id.forgetAnswer);
        userForgetAnswerTextInput =
                (TextInputLayout) findViewById(R.id.forgetAnswerTxtLayout);
        AntiMonkeyButton validateBtn =
                (AntiMonkeyButton) findViewById(R.id.validateBtn);

        String userId =
                getIntent().getStringExtra(Constants.USER_ID_TAG);
        foundUser = User.getUserById(UUID.fromString(userId));

        userForgetQuestionTxt.setText(
                foundUser.getForgetQuestion()
        );

        validateBtn.setOnClickListener(this);
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;
        userForgetAnswerTextInput.setError(null);

        if (userForgetAnswerTxt.getText().toString().trim().equals("")) {
            userForgetAnswerTextInput.setError(getString(R.string.empty_field_err));
            userForgetAnswerTextInput.requestFocus();
            areValidated = false;
        }

        return areValidated;
    }
}