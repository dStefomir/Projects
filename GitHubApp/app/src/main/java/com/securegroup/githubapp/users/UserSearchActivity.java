package com.securegroup.githubapp.users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;

import com.securegroup.githubapp.R;
import com.securegroup.githubapp.adapters.SearchedUsersAdapter;
import com.securegroup.githubapp.basecomponents.AntiMonkeyListView;
import com.securegroup.githubapp.basecomponents.BaseActivity;
import com.securegroup.githubapp.github.GitHub;
import com.securegroup.githubapp.github.OnFail;
import com.securegroup.githubapp.github.OnUserSearchFinished;
import com.securegroup.githubapp.github.Session;
import com.securegroup.githubapp.github.model.GitHubViewedUsers;
import com.securegroup.githubapp.menu.MenuDialog;
import com.securegroup.githubapp.utils.Constants;
import com.securegroup.githubapp.utils.NetworkUtils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Takes care of the User Search Screen
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class UserSearchActivity extends BaseActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener, OnUserSearchFinished, SearchView.OnQueryTextListener, OnFail {

    private SearchedUsersAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_search);

        setUsersActionBar(getString(R.string.search_title));
        initViews();
        setPreconditions();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.menuIcon) {
            final Session session = Session.getInstance();
            final MenuDialog dialog = MenuDialog.newInstance(session.getUserId());
            dialog.setCancelable(true);

            dialog.show(getFragmentManager(), Constants.MENU_DIALOG_TAG);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final GitHubViewedUsers viewedUsers =
                (GitHubViewedUsers) view.getTag(R.layout.items_searched_users);
        final Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(Constants.VIEWED_USER_ID_TAG, viewedUsers.getId());
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserSearchFinished(List<GitHubViewedUsers> foundUsersList) {
        progressDialog.dismiss();
        adapter.loadData(foundUsersList);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadSearchUserContent(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onFail(String error) {
        progressDialog.dismiss();
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

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.warning_title))
                .setContentText(getString(R.string.confirm_exit_msg))
                .setConfirmText(getString(R.string.ok_btn))
                .setCancelText(getString(R.string.no_btn))
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                final Session session = Session.getInstance();
                                session.setUserId(-1L);
                                session.setUserName(null);

                                finish();

                                sDialog.dismissWithAnimation();
                            }
                        }
                )
                .setCancelClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        }
                ).show();
    }

    private void setPreconditions() {
        final String searchContent = getIntent().getStringExtra(Constants.SEARCH_RESULT_TAG);
        final int getFollowersKey = getIntent().getIntExtra(Constants.EXTRACT_FOLLOWERS_TAG, -1);
        final int getFollowingKey = getIntent().getIntExtra(Constants.EXTRACT_FOLLOWING_TAG, -1);

        loadUserGitHubDoings(getFollowersKey, getFollowingKey);
        loadSearchUserContent(searchContent);
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialog_msg));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final AntiMonkeyListView searchUsersListView =
                (AntiMonkeyListView) findViewById(R.id.searchedUsersListView);

        setAdapter(searchUsersListView);

        searchUsersListView.setOnItemClickListener(this);
        searchView.setOnQueryTextListener(this);
        menuImageView.setOnClickListener(this);
    }

    private void setAdapter(AntiMonkeyListView searchedUsersListView) {
        adapter = new SearchedUsersAdapter(this);
        searchedUsersListView.setAdapter(adapter);
    }

    private void loadSearchUserContent(String searchContent) {
        if (searchContent != null && !searchContent.equals("")) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                final GitHub gitHub = new GitHub(this, this);
                gitHub.getSearchedUsers(Session.getInstance().getUserId(), searchContent);
            } else {
                List<GitHubViewedUsers> viewedUsersList =
                        GitHubViewedUsers.getAllSearchedUsers(Session.getInstance().getUserId(), searchContent);
                onUserSearchFinished(viewedUsersList);
            }
        }
    }

    private void loadUserGitHubDoings(int getFollowersKey, int getFollowingKey) {
        final GitHub gitHub = new GitHub(this, this);

        if (getFollowersKey != -1 && NetworkUtils.isNetworkAvailable(this)) {
            gitHub.getUserFollowers(Session.getInstance().getUserId(), Session.getInstance().getUserName());
        } else if (getFollowingKey != -1 && NetworkUtils.isNetworkAvailable(this)) {
            gitHub.getUserFollowings(Session.getInstance().getUserId(), Session.getInstance().getUserName());
        } else {
            List<GitHubViewedUsers> viewedUsersList =
                    GitHubViewedUsers.getAllViewedUsers(Session.getInstance().getUserId());
            onUserSearchFinished(viewedUsersList);
        }
    }
}