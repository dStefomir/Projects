package com.securegroup.githubapp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.securegroup.githubapp.github.OnFail;
import com.securegroup.githubapp.github.Session;
import com.securegroup.githubapp.github.model.GitHubUser;
import com.securegroup.githubapp.users.UserActivity;
import com.securegroup.githubapp.R;
import com.securegroup.githubapp.basecomponents.AntiMonkeyButton;
import com.securegroup.githubapp.basecomponents.BaseActivity;
import com.securegroup.githubapp.github.GitHub;
import com.securegroup.githubapp.github.OnAttemptLogin;
import com.securegroup.githubapp.utils.NetworkUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Takes care of the Login Screen
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, OnAttemptLogin, OnFail {

    private EditText userNameTxt;
    private EditText userPasswordTxt;
    private TextInputLayout userNameInputLayout;
    private TextInputLayout userPasswordInputLayout;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logInBtn) {
            if (areFieldsValidated()) {
                final String userName =
                        userNameTxt.getText().toString().trim();
                final String userPassword =
                        userPasswordTxt.getText().toString().trim();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.progress_dialog_msg));
                progressDialog.setCancelable(false);
                progressDialog.show();

                if (NetworkUtils.isNetworkAvailable(this)) {
                    new GitHub(this, this).attemptLogIn(userName, userPassword);
                } else {
                    GitHubUser loggedUser = GitHubUser.getGitHubUser(userName);
                    if (loggedUser != null) {
                        Session session = Session.getInstance();
                        session.setUserId(loggedUser.getId());
                        session.setUserName(loggedUser.getName());

                        onLoginSuccess();
                    } else {
                        onLoginFailed(null);
                    }
                }
            }
        }
    }

    @Override
    public void onLoginSuccess() {
        progressDialog.cancel();
        Toast.makeText(
                this,
                getString(R.string.login_successful_msg),
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(
                this,
                UserActivity.class
        );
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        progressDialog.cancel();
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.error_title))
                .setContentText(errorMessage != null ? errorMessage : getString(R.string.wrong_credentials_msg))
                .setConfirmText(getString(R.string.ok_btn))
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        }
                ).show();
    }

    @Override
    public void onFail(String error) {
        progressDialog.cancel();
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.error_title))
                .setContentText(error != null ? error : getString(R.string.on_fail_msg))
                .setConfirmText(getString(R.string.ok_btn))
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        }
                ).show();
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
        AntiMonkeyButton logInBtn =
                (AntiMonkeyButton) findViewById(R.id.logInBtn);
        userNameTxt.setText("Stefomir");
        userPasswordTxt.setText("153sbd759");

        logInBtn.setOnClickListener(this);
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
