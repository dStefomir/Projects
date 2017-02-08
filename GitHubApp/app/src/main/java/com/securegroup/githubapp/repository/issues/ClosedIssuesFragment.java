package com.securegroup.githubapp.repository.issues;

import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.securegroup.githubapp.basecomponents.AntiMonkeyListView;
import com.securegroup.githubapp.utils.Constants;

/**
 * Takes care of the All Issues Screen
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class ClosedIssuesFragment extends IssuesFragment {

    @Override
    protected void setAdapter(AntiMonkeyListView ownedRepositoriesListView) {
        Bundle args = getArguments();
        final long selectedRepositoryId = args.getLong(Constants.REPOSITORY_ID_TAG);

        ownedRepositoriesListView.setAdapter(adapter);

        adapter.loadData(selectedRepositoryId, IssueState.closed.value);
    }

    /**
     * Returns a new instance of the fragment while passing some arguments to it
     *
     * @param repositoryId id of the selected repository extracted from GitHubRepository Table
     * @return ClosedIssueFragment instance
     */
    public static ClosedIssuesFragment newInstance(long repositoryId) {
        ClosedIssuesFragment fragment = new ClosedIssuesFragment();

        Bundle bundle = new Bundle();

        bundle.putLong(Constants.REPOSITORY_ID_TAG, repositoryId);

        fragment.setArguments(bundle);

        return fragment;
    }
}
