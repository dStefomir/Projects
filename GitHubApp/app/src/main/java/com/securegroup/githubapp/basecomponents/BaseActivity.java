package com.securegroup.githubapp.basecomponents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.securegroup.githubapp.R;

/**
 * Parent activity component which all activities should reuse
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class BaseActivity extends AppCompatActivity {

    protected SearchView searchView;
    protected ImageView stareRepoImageView;
    protected ImageView repoBackImageView;
    protected ImageView menuImageView;
    protected ImageView userAvatarImageView;
    protected TextView currentUserNameTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    /**
     * Sets the custom User activity action bar
     *
     * @param userName name of the selected user
     */
    protected void setUsersActionBar(String userName) {
        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            final LayoutInflater mInflater = LayoutInflater.from(this);

            final View view =
                    mInflater.inflate(R.layout.action_bar_users, null);

            userAvatarImageView =
                    (ImageView) view.findViewById(R.id.userAvatarImageView);
            currentUserNameTxt =
                    (TextView) view.findViewById(R.id.currentRepoNameTxt);
            searchView =
                    (SearchView) view.findViewById(R.id.userSearchView);
            menuImageView =
                    (ImageView) view.findViewById(R.id.menuIcon);

            currentUserNameTxt.setText(userName);

            actionBar.setCustomView(view);
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }

    /**
     * Sets the custom Repository activity action bar
     *
     * @param repositoryName name of the selected repository
     */
    protected void setRepositoriesActionBar(String repositoryName) {
        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            final LayoutInflater mInflater = LayoutInflater.from(this);

            final View view =
                    mInflater.inflate(R.layout.action_bar_repos, null);

            final TextView currentRepoNameTxt =
                    (TextView) view.findViewById(R.id.currentRepoNameTxt);
            stareRepoImageView =
                    (ImageView) view.findViewById(R.id.stareRepoIcon);
            repoBackImageView =
                    (ImageView) view.findViewById(R.id.navigateBackIcon);
            menuImageView =
                    (ImageView) view.findViewById(R.id.menuIcon);

            currentRepoNameTxt.setText(repositoryName);

            actionBar.setCustomView(view);
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }
}
