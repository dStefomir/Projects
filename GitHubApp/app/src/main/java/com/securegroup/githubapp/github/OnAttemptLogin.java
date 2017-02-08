package com.securegroup.githubapp.github;

/**
 * Interface that returns events if the user is logged or not
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public interface OnAttemptLogin {

    void onLoginSuccess();

    void onLoginFailed(String errorMessage);
}
