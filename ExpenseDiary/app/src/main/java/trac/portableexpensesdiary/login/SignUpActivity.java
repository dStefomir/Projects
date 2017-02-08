package trac.portableexpensesdiary.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.User;
import trac.portableexpensesdiary.utils.Constants;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameTxt;
    private EditText userPasswordTxt;
    private EditText confirmPasswordTxt;
    private EditText userForgetQuestionTxt;
    private EditText userForgetAnswerTxt;
    private TextInputLayout userNameInputLayout;
    private TextInputLayout userPasswordInputLayout;
    private TextInputLayout userConfirmPasswordInputLayout;
    private TextInputLayout userForgetQuestionInputLayout;
    private TextInputLayout userForgetAnswerInputLayout;

    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        setTitle(getString(R.string.sign_up_title));

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
    public void onClick(View v) {
        if (v.getId() == R.id.signBtn) {
            if (areFieldsValidated()) {

                String userName =
                        userNameTxt.getText().toString();
                String userPassword =
                        userPasswordTxt.getText().toString();
                String forgetQuestion =
                        userForgetQuestionTxt.getText().toString();
                String forgetAnswer =
                        userForgetAnswerTxt.getText().toString();

                if(currentUser == null) {
                    currentUser = new User();
                }
                currentUser.setUserName(userName.trim());
                currentUser.setUserPassword(userPassword.trim());
                currentUser.setForgetQuestion(forgetQuestion.trim());
                currentUser.setForgetAnswer(forgetAnswer.trim());

                currentUser.save();

                initDefaultCategories(currentUser.getId());

                String toastMessage = getString(R.string.reg_successful_hint);

                if(currentUser != null) {
                    toastMessage = getString(R.string.user_edit_successful_hint);
                }

                Toast.makeText(
                        this,
                        toastMessage,
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }
        } else {
            finish();
        }
    }

    private void initViews() {
        userNameTxt = (EditText) findViewById(R.id.userName);
        userPasswordTxt = (EditText) findViewById(R.id.userPassword);
        confirmPasswordTxt = (EditText) findViewById(R.id.confirmUserPassword);
        userForgetQuestionTxt = (EditText) findViewById(R.id.userForgetQuestion);
        userForgetAnswerTxt = (EditText) findViewById(R.id.userForgetAnswer);
        userNameInputLayout = (TextInputLayout) findViewById(R.id.userNameTxtLayout);
        userPasswordInputLayout = (TextInputLayout) findViewById(R.id.userPasswordTxtLayout);
        userConfirmPasswordInputLayout =
                (TextInputLayout) findViewById(R.id.userPasswordConfirmTxtLayout);
        userForgetQuestionInputLayout =
                (TextInputLayout) findViewById(R.id.userForgetQuestionTxtLayout);
        userForgetAnswerInputLayout = (TextInputLayout) findViewById(R.id.userForgetAnswerTxtLayout);
        AntiMonkeyButton signInBtn = (AntiMonkeyButton) findViewById(R.id.signBtn);
        AntiMonkeyButton cancelBtn = (AntiMonkeyButton) findViewById(R.id.cancelBtn);

        loadData();

        signInBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void loadData() {
        String userId =
                getIntent().getStringExtra(Constants.USER_ID_TAG);
        if(userId != null) {
            currentUser = User.getUserById(UUID.fromString(userId));

            if (currentUser != null) {
                userNameTxt.setText(currentUser.getUserName());
                userPasswordTxt.setText(currentUser.getUserPassword());
                confirmPasswordTxt.setText(currentUser.getUserPassword());
                userForgetQuestionTxt.setText(currentUser.getForgetQuestion());
                userForgetAnswerTxt.setText(currentUser.getForgetAnswer());
            }
        }
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;
        userNameInputLayout.setError(null);
        userPasswordInputLayout.setError(null);
        userConfirmPasswordInputLayout.setError(null);
        userForgetQuestionInputLayout.setError(null);
        userForgetAnswerInputLayout.setError(null);

        String userName =
                userNameTxt.getText().toString();

        if (userNameTxt.getText().toString().trim().equals("")) {
            userNameInputLayout.setError(getString(R.string.empty_field_err));
            userNameInputLayout.requestFocus();
            areValidated = false;
        }

        if (userPasswordTxt.getText().toString().trim().equals("")) {
            userPasswordInputLayout.setError(getString(R.string.empty_field_err));
            userPasswordInputLayout.requestFocus();
            areValidated = false;
        }

        if (confirmPasswordTxt.getText().toString().trim().equals("")) {
            userConfirmPasswordInputLayout.setError(getString(R.string.empty_field_err));
            userConfirmPasswordInputLayout.requestFocus();
            areValidated = false;
        }

        if (userForgetQuestionTxt.getText().toString().trim().equals("")) {
            userForgetQuestionInputLayout.setError(getString(R.string.empty_field_err));
            userForgetQuestionInputLayout.requestFocus();
            areValidated = false;
        }

        if (userForgetAnswerTxt.getText().toString().trim().equals("")) {
            userForgetAnswerInputLayout.setError(getString(R.string.empty_field_err));
            userForgetAnswerInputLayout.requestFocus();
            areValidated = false;
        }

        if (!userPasswordTxt.getText()
                .toString()
                .trim()
                .toLowerCase()
                .equals(confirmPasswordTxt.getText().toString().trim().toLowerCase())) {
            userConfirmPasswordInputLayout.setError(getString(R.string.confirm_password_not_same));
            userConfirmPasswordInputLayout.requestFocus();
            areValidated = false;
        }

        User user = User.getUserByCredentials(userName);
        if (user != null && currentUser == null || (user != null &&!currentUser.getUserName().equals(user.getUserName()))) {

            userNameInputLayout.setError(getString(R.string.user_existing_err));
            userNameInputLayout.requestFocus();
            areValidated = false;
        }

        return areValidated;
    }

    private void initDefaultCategories(UUID userId) {
        new Category(
                Constants.DEFAULT_CATEGORY_INCOME,
                getResources().getDrawable(R.drawable.ic_attach_money_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_FOOD,
                getResources().getDrawable(R.drawable.ic_restaurant_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_ALCOHOL,
                getResources().getDrawable(R.drawable.ic_local_bar_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_PUBLIC_TRANSPORT,
                getResources().getDrawable(R.drawable.ic_directions_bus_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_FUEL,
                getResources().getDrawable(R.drawable.ic_local_gas_station_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_CREDIT,
                getResources().getDrawable(R.drawable.ic_account_balance_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_BILLS,
                getResources().getDrawable(R.drawable.ic_flash_auto_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_MEDICAL_DRUGS,
                getResources().getDrawable(R.drawable.ic_local_hospital_white_36dp),
                userId
        ).save();
        new Category(
                Constants.DEFAULT_CATEGORY_SMOKES,
                getResources().getDrawable(R.drawable.ic_smoking_rooms_white_36dp),
                userId
        ).save();
    }
}