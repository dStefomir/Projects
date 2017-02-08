package com.securegroup.githubapp.github;

import com.securegroup.githubapp.github.model.GitHubViewedUsers;

import java.util.List;

/**
 * Interface that notifies the UI that the searching of user is finished
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public interface OnUserSearchFinished {

    void onUserSearchFinished(List<GitHubViewedUsers> foundUsersList);
}
