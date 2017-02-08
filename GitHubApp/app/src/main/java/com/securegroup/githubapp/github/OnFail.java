package com.securegroup.githubapp.github;

/**
 * Interface that notifies the UI that something went wrong with the service call
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public interface OnFail {

    void onFail(String error);
}
