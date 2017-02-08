package com.securegroup.githubapp.users.repositories;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.securegroup.githubapp.R;
import com.securegroup.githubapp.adapters.UserRepositoriesAdapter;
import com.securegroup.githubapp.basecomponents.AntiMonkeyListView;
import com.securegroup.githubapp.basecomponents.BaseFragment;
import com.securegroup.githubapp.github.model.GitHubRepository;
import com.securegroup.githubapp.repository.RepositoryActivity;
import com.securegroup.githubapp.utils.Constants;

/**
 * Parent Fragment that all repository fragments should extend
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public abstract class RepositoriesFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    protected UserRepositoriesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_repositories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final GitHubRepository repository =
                (GitHubRepository) view.getTag(R.layout.items_repositories);
        final Intent intent =
                new Intent(getActivity(), RepositoryActivity.class);
        intent.putExtra(Constants.REPOSITORY_ID_TAG, repository.getId());
        intent.putExtra(Constants.VIEWED_USER_ID_TAG, repository.getViewedUserId());

        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void initViews(View rootView) {
        final AntiMonkeyListView ownedRepositoriesListView =
                (AntiMonkeyListView) rootView.findViewById(R.id.ownedRepositoriesListView);
        setAdapter(ownedRepositoriesListView);

        ownedRepositoriesListView.setOnItemClickListener(this);
    }

    /**
     * Abstract method that will allow the other fragment to make a custom set to the parent adapter
     */
    protected abstract void setAdapter(AntiMonkeyListView ownedRepositoriesListView);
}
