package com.securegroup.githubapp.repository.issues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.securegroup.githubapp.R;
import com.securegroup.githubapp.adapters.RepositoryIssuesAdapter;
import com.securegroup.githubapp.basecomponents.AntiMonkeyListView;
import com.securegroup.githubapp.basecomponents.BaseFragment;

/**
 * Parent Fragment that all issue fragments should extend
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public abstract class IssuesFragment extends BaseFragment {

    protected RepositoryIssuesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issues, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

    }

    private void initViews(View rootView) {
        final AntiMonkeyListView ownedRepositoriesListView =
                (AntiMonkeyListView) rootView.findViewById(R.id.issuesListView);
        initAdapter();
        setAdapter(ownedRepositoriesListView);
    }

    private void initAdapter() {
        adapter = new RepositoryIssuesAdapter(getContext());
    }

    /**
     * Abstract method that will allow the other fragment to make a custom set to the parent adapter
     */
    protected abstract void setAdapter(AntiMonkeyListView ownedRepositoriesListView);
}
