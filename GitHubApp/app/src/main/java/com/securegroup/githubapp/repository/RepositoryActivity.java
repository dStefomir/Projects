package com.securegroup.githubapp.repository;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.securegroup.githubapp.R;
import com.securegroup.githubapp.adapters.ViewPagerAdapter;
import com.securegroup.githubapp.basecomponents.BaseActivity;
import com.securegroup.githubapp.github.Session;
import com.securegroup.githubapp.github.model.GitHubRepository;
import com.securegroup.githubapp.github.model.GitHubUser;
import com.securegroup.githubapp.github.model.GitHubViewedUsers;
import com.securegroup.githubapp.menu.MenuDialog;
import com.securegroup.githubapp.repository.issues.AllIssuesFragment;
import com.securegroup.githubapp.repository.issues.ClosedIssuesFragment;
import com.securegroup.githubapp.repository.issues.OpenIssuesFragment;
import com.securegroup.githubapp.users.UserActivity;
import com.securegroup.githubapp.utils.Constants;
import com.securegroup.githubapp.utils.ImageUtils;

/**
 * Takes care of the Repository Screen
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class RepositoryActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ownerImageView;
    private TextView ownerNameTxt;
    private TextView commitsNumberTxt;
    private TextView branchesNumberTxt;
    private TextView releasesNumberTxt;
    private TextView starsNumberTxt;
    private TextView forkNumberTxt;
    private TextView contributionsNumberTxt;
    private TextView usedLanguagesTxt;
    private ViewPager viewPager;

    private ImageLoader imageLoader;

    private GitHubUser user;
    private GitHubViewedUsers viewedUser;
    private GitHubRepository selectedRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_repository);

        setUpPreconditions();
        setRepositoriesActionBar(selectedRepository.getRepositoryName());
        initViews();
        setAdapter();
        setViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ownerLayout:
                navigateToUserActivity();

                break;

            case R.id.stareRepoIcon:
                if (selectedRepository != null) {
                    if (selectedRepository.isStared()) {
                        selectedRepository.setStared(false);
                        selectedRepository.save();
                        stareRepoImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_white_36dp));
                        Toast.makeText(this, getString(R.string.repo_not_stared_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        selectedRepository.setStared(true);
                        selectedRepository.save();
                        stareRepoImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_white_36dp));
                        Toast.makeText(this, getString(R.string.repo_stared_msg), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.navigateBackIcon:
                navigateToUserActivity();

                break;

            case R.id.menuIcon:
                final Session session = Session.getInstance();
                final MenuDialog dialog = MenuDialog.newInstance(session.getUserId());
                dialog.setCancelable(true);

                dialog.show(getFragmentManager(), Constants.MENU_DIALOG_TAG);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        navigateToUserActivity();
    }

    private void initViews() {
        final LinearLayout ownerLayout =
                (LinearLayout) findViewById(R.id.ownerLayout);
        final TabLayout tabLayout =
                (TabLayout) findViewById(R.id.tabsLayout);
        ownerImageView =
                (ImageView) findViewById(R.id.ownerImage);
        ownerNameTxt =
                (TextView) findViewById(R.id.ownerNameTxt);
        commitsNumberTxt =
                (TextView) findViewById(R.id.commitsTxt);
        branchesNumberTxt =
                (TextView) findViewById(R.id.branchesTxt);
        releasesNumberTxt =
                (TextView) findViewById(R.id.releasesTxt);
        starsNumberTxt =
                (TextView) findViewById(R.id.starTxt);
        forkNumberTxt =
                (TextView) findViewById(R.id.forkTxt);
        contributionsNumberTxt =
                (TextView) findViewById(R.id.contributionTxt);
        usedLanguagesTxt =
                (TextView) findViewById(R.id.languagesTxt);
        viewPager =
                (ViewPager) findViewById(R.id.viewPager);

        if (selectedRepository.isStared()) {
            stareRepoImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_white_36dp));
        } else {
            stareRepoImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_white_36dp));
        }

        tabLayout.setupWithViewPager(viewPager);
        imageLoader = ImageLoader.getInstance();

        repoBackImageView.setOnClickListener(this);
        menuImageView.setOnClickListener(this);
        stareRepoImageView.setOnClickListener(this);
        ownerLayout.setOnClickListener(this);
    }

    private void setUpPreconditions() {
        final long gitHubRepositoryId = getIntent().getLongExtra(Constants.REPOSITORY_ID_TAG, -1L);
        final long gitHubViewedUserId = getIntent().getLongExtra(Constants.VIEWED_USER_ID_TAG, 1L);

        viewedUser = GitHubViewedUsers.getGitHubViewedUser(gitHubViewedUserId);
        if (viewedUser != null) {
            selectedRepository =
                    GitHubRepository.getGitHubRepository(gitHubRepositoryId, viewedUser.getId());
        } else {
            selectedRepository = GitHubRepository.getGitHubRepository(gitHubRepositoryId);
            user = GitHubUser.getGitHubUser(selectedRepository.getUserId());
        }
    }

    private void setViews() {
        if (user == null) {
            user = new GitHubUser();
            user.setUserHtmlUrl(selectedRepository.getUserUrl());
        }

        if (selectedRepository != null) {
            setOwnerImageView();
            setOwnerNameTxt();
            setCommitsNumberTxt();
            setBranchesNumberTxt();
            setReleasesNumberTxt();
            setStarsNumberTxt();
            setForkNumberTxt();
            setContributionsNumberTxt();
            setUsedLanguagesTxt();
        }
    }

    private void setAdapter() {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(OpenIssuesFragment.newInstance(selectedRepository.getId()), getString(R.string.fragment_open_name));
        adapter.addFragment(ClosedIssuesFragment.newInstance(selectedRepository.getId()), getString(R.string.fragment_closed_name));
        adapter.addFragment(AllIssuesFragment.newInstance(selectedRepository.getId()), getString(R.string.fragment_all_name));

        viewPager.setAdapter(adapter);
    }

    private void setOwnerImageView() {
        if (viewedUser != null) {
            imageLoader.displayImage(viewedUser.getAvatarUrl(), ownerImageView, ImageUtils.getImageLoaderOptions());
        } else {
            imageLoader.displayImage(user.getAvatarUrl(), ownerImageView, ImageUtils.getImageLoaderOptions());
        }
    }

    private void setOwnerNameTxt() {
        if (viewedUser != null) {
            ownerNameTxt.setText(viewedUser.getName());
        } else {
            ownerNameTxt.setText(user.getName());
        }
    }

    private void setCommitsNumberTxt() {
        commitsNumberTxt.setText(String.valueOf(selectedRepository.getCommits()));
    }

    private void setBranchesNumberTxt() {
        branchesNumberTxt.setText(String.valueOf(selectedRepository.getBranches()));
    }

    private void setReleasesNumberTxt() {
        releasesNumberTxt.setText(String.valueOf(selectedRepository.getReleases()));
    }

    private void setStarsNumberTxt() {
        starsNumberTxt.setText(String.valueOf(selectedRepository.getStars()));
    }

    private void setForkNumberTxt() {
        forkNumberTxt.setText(String.valueOf(selectedRepository.getForks()));
    }

    private void setContributionsNumberTxt() {
        contributionsNumberTxt.setText(String.valueOf(selectedRepository.getContributions()));
    }

    private void setUsedLanguagesTxt() {
        usedLanguagesTxt.setText(String.format("%s%s", getString(R.string.languages_title), selectedRepository.getUsedLanguages()));
    }

    private void navigateToUserActivity() {
        final String ownerName = ownerNameTxt.getText().toString().trim();
        final GitHubUser user = GitHubUser.getGitHubUser(ownerName);
        final Intent intent = new Intent(this, UserActivity.class);

        if (user != null) {
            intent.putExtra(Constants.USER_ID_TAG, user.getId());
        } else {
            final GitHubViewedUsers viewedUsers = GitHubViewedUsers.getGitHubViewedUser(ownerName);
            intent.putExtra(Constants.VIEWED_USER_ID_TAG, viewedUsers.getId());
        }

        startActivity(intent);
        finish();
    }
}
