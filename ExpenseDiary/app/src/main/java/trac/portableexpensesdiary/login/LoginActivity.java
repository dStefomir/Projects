package trac.portableexpensesdiary.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import cn.pedant.SweetAlert.SweetAlertDialog;

import trac.portableexpensesdiary.MainActivity;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.model.User;

import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DisplayUtils;
import trac.portableexpensesdiary.utils.SharedPreferencesUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText userNameTxt;
    private EditText userPasswordTxt;
    private TextInputLayout userNameInputLayout;
    private TextInputLayout userPasswordInputLayout;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login_title));

        initViews();
    }

    @Override
    protected void onResume() {
        BaseActivity.setIsAuthenticationScreenActive(true);

        super.onResume();
    }

    @Override
    protected void onPause() {
        BaseActivity.setIsAuthenticationScreenActive(false);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.log_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reg_acc) {
            DisplayUtils.startNewActivity(
                    this,
                    new Intent(
                            this,
                            SignUpActivity.class
                    ),
                    false
            );
        } else {
            String userName =
                    userNameTxt.getText().toString();

            if (userName.equals("")) {
                new SweetAlertDialog(
                        this,
                        SweetAlertDialog.ERROR_TYPE
                )
                        .setTitleText(getString(R.string.error_title))
                        .setContentText(getString(R.string.user_name_not_entered))
                        .setConfirmText(getString(R.string.ok_btn))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            } else {

                final User selectedUser =
                        User.getUserByName(userName);
                if (selectedUser == null) {
                    new SweetAlertDialog(
                            this,
                            SweetAlertDialog.ERROR_TYPE
                    )
                            .setTitleText(getString(R.string.error_title))
                            .setContentText(getString(R.string.no_such_user_err))
                            .setConfirmText(getString(R.string.ok_btn))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {

                    Intent intent = new Intent(
                            this,
                            AccountRetrievementActivity.class
                    );
                    intent.putExtra(
                            Constants.USER_ID_TAG,
                            selectedUser.getId().toString()
                    );

                    DisplayUtils.startNewActivity(
                            this,
                            intent,
                            false
                    );
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logInBtn) {
            if (areFieldsValidated()) {
                SharedPreferencesUtils.setBooleanData(
                        this,
                        rememberMeCheckBox.isChecked(),
                        Constants.REMEMBER_ME
                );

                try {
                    SessionManager.getInstance()
                            .attemptLogin(
                                    userNameTxt.getText().toString().trim(),
                                    userPasswordTxt.getText().toString().trim()
                            );

                    DisplayUtils.startNewActivity(
                            this,
                            new Intent(
                                    this,
                                    MainActivity.class
                            ),
                            true
                    );
                } catch (AuthenticationException e) {
                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        } else {
            finish();
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
        if (rememberMeCheckBox.isChecked()) {
            rememberMeCheckBox.setChecked(false);

            SharedPreferencesUtils.setBooleanData(
                    this,
                    rememberMeCheckBox.isChecked(),
                    Constants.REMEMBER_ME
            );
        }
    }

    private void initViews() {
        userNameTxt =
                (EditText) findViewById(R.id.userName);
        userPasswordTxt =
                (EditText) findViewById(R.id.userPassword);
        userNameInputLayout =
                (TextInputLayout) findViewById(R.id.userNameTxtLayout);
        userPasswordInputLayout =
                (TextInputLayout) findViewById(R.id.userPasswordTxtLayout);
        rememberMeCheckBox =
                (CheckBox) findViewById(R.id.remember_me_check_box);
        AntiMonkeyButton logInBtn =
                (AntiMonkeyButton) findViewById(R.id.logInBtn);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) findViewById(R.id.cancelBtn);

        logInBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        boolean isRememberMeChecked =
                SharedPreferencesUtils.getBooleanData(this, Constants.REMEMBER_ME);

        rememberMeCheckBox.setChecked(isRememberMeChecked);

        if (rememberMeCheckBox.isChecked()) {
            User lastLoggedInUser =
                    User.getLastLoggedInUser();
            if (lastLoggedInUser != null) {
                userNameTxt.setText(
                        lastLoggedInUser.getUserName()
                );
                userPasswordTxt.setText(
                        lastLoggedInUser.getUserPassword()
                );
            }
        }

        userNameTxt.addTextChangedListener(this);
        userPasswordTxt.addTextChangedListener(this);
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;
        userNameInputLayout.setError(null);
        userPasswordInputLayout.setError(null);

        if (userNameTxt.getText().toString().trim().equals("")) {
            userNameInputLayout.setError(getString(R.string.empty_field_err));
            areValidated = false;
        }

        if (userPasswordTxt.getText().toString().trim().equals("")) {
            userPasswordInputLayout.setError(getString(R.string.empty_field_err));
            areValidated = false;
        }

        return areValidated;
    }
}
