package com.securegroup.githubapp.users.repositories;

import android.os.Bundle;

import com.securegroup.githubapp.adapters.UserRepositoriesAdapter;
import com.securegroup.githubapp.basecomponents.AntiMonkeyListView;
import com.securegroup.githubapp.utils.Constants;

/**
 * Takes care of the Owned Repository Screen
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class OwnedRepositoriesFragment extends RepositoriesFragment {

    @Override
    protected void setAdapter(AntiMonkeyListView ownedRepositoriesListView) {
        final Bundle args = getArguments();
        final long userId = args.getLong(Constants.USER_ID_TAG);
        final long viewedUserId = args.getLong(Constants.VIEWED_USER_ID_TAG);

        adapter = new UserRepositoriesAdapter(getContext());
        ownedRepositoriesListView.setAdapter(adapter);

        if(userId != -1L) {
            adapter.loadLoggedUserData(userId, false);
        } else {
            adapter.loadViewedUserData(viewedUserId, false);
        }
    }

    /**
     * Returns a new instance of the fragment while passing some arguments to it
     *
     * @param userId id of the logged User extracted from GitHubUser Table
     * @param viewedUserId id of the viewed user extracted from GitHubViewedUser Table
     *
     * @return OwnedRepositoriesFragment instance
     */
    public static OwnedRepositoriesFragment newInstance(long userId, long viewedUserId) {
        final OwnedRepositoriesFragment fragment = new OwnedRepositoriesFragment();

        final Bundle bundle = new Bundle();
        bundle.putLong(Constants.USER_ID_TAG, userId);
        bundle.putLong(Constants.VIEWED_USER_ID_TAG, viewedUserId);

        fragment.setArguments(bundle);

        return fragment;
    }
}
