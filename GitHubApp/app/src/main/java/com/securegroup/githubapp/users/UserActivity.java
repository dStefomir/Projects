package com.securegroup.githubapp.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.securegroup.githubapp.R;
import com.securegroup.githubapp.adapters.ViewPagerAdapter;
import com.securegroup.githubapp.basecomponents.BaseActivity;
import com.securegroup.githubapp.github.Session;
import com.securegroup.githubapp.github.model.GitHubUser;
import com.securegroup.githubapp.github.model.GitHubViewedUsers;
import com.securegroup.githubapp.menu.MenuDialog;
import com.securegroup.githubapp.users.repositories.OwnedRepositoriesFragment;
import com.securegroup.githubapp.users.repositories.StaredRepositoriesFragment;
import com.securegroup.githubapp.utils.Constants;
import com.securegroup.githubapp.utils.ImageUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Takes care of the User Screen
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */
public class UserActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private ImageView userAvatarImage;
    private TextView userNameTxt;
    private TextView followersTxt;
    private TextView followingTxt;
    private ViewPager viewPager;

    private GitHubUser loggedUser;
    private GitHubViewedUsers viewedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        setPreconditions();
        setUsersActionBar(loggedUser != null ? loggedUser.getName() : viewedUser.getName());
        initViews();
        setViews();
        setAdapter();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.followersCardView) {
            final Intent intent = new Intent(this, UserSearchActivity.class);
            intent.putExtra(Constants.EXTRACT_FOLLOWERS_TAG, Constants.GET_FOLLOWERS_TAG);
            startActivity(intent);

            finish();

        } else if (v.getId() == R.id.menuIcon) {
            final Session session = Session.getInstance();
            final MenuDialog dialog = MenuDialog.newInstance(session.getUserId());
            dialog.setCancelable(true);

            dialog.show(getFragmentManager(), Constants.MENU_DIALOG_TAG);
        } else {
            final Intent intent = new Intent(this, UserSearchActivity.class);
            intent.putExtra(Constants.EXTRACT_FOLLOWING_TAG, Constants.GET_FOLLOWING_TAG);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final Intent intent = new Intent(this, UserSearchActivity.class);
        intent.putExtra(Constants.SEARCH_RESULT_TAG, query);
        startActivity(intent);

        finish();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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

    private void initViews() {
        final TabLayout tabLayout =
                (TabLayout) findViewById(R.id.tabsLayout);
        final CardView followersCardView =
                (CardView) findViewById(R.id.followersCardView);
        final CardView followingCardView =
                (CardView) findViewById(R.id.followingCardView);
        userAvatarImage =
                (ImageView) findViewById(R.id.userAvatarImage);
        userNameTxt =
                (TextView) findViewById(R.id.userNameTxt);
        followersTxt =
                (TextView) findViewById(R.id.followersTxt);
        followingTxt =
                (TextView) findViewById(R.id.followingTxt);
        viewPager =
                (ViewPager) findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);
        followersCardView.setOnClickListener(this);
        followingCardView.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        menuImageView.setOnClickListener(this);
    }

    private void setPreconditions() {
        final long userId = getIntent().getLongExtra(Constants.USER_ID_TAG, -1L);
        loggedUser = GitHubUser.getGitHubUser(userId);

        if (loggedUser == null) {
            final long viewedUserId = getIntent().getLongExtra(Constants.VIEWED_USER_ID_TAG, -1L);
            viewedUser = GitHubViewedUsers.getGitHubViewedUser(viewedUserId);

            if(viewedUser == null) {
                loggedUser = GitHubUser.getGitHubUser(Session.getInstance().getUserName());
            }
        }
    }

    private void setAdapter() {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        long userId = -1L;
        long viewedUserId = -1L;

        if(loggedUser != null) {
            userId = loggedUser.getId();
        } else if(viewedUser != null) {
            viewedUserId = viewedUser.getId();
        }

        adapter.addFragment(OwnedRepositoriesFragment.newInstance(userId, viewedUserId), getString(R.string.owned_fragment_name));
        adapter.addFragment(StaredRepositoriesFragment.newInstance(userId, viewedUserId), getString(R.string.stared_fragment_name));

        viewPager.setAdapter(adapter);
    }

    private void setViews() {
        final ImageLoader imageLoader = ImageLoader.getInstance();

        if (loggedUser != null) {
            imageLoader.displayImage(loggedUser.getAvatarUrl(), userAvatarImageView, ImageUtils.getImageLoaderOptions());
            imageLoader.displayImage(loggedUser.getAvatarUrl(), userAvatarImage, ImageUtils.getImageLoaderOptions());
            userNameTxt.setText(loggedUser.getName());
            followersTxt.setText(
                    String.format("%s%s", getString(R.string.user_followers_title), String.valueOf(loggedUser.getFollowers()))
            );
            followingTxt.setText(
                    String.format("%s%s", getString(R.string.user_following_title), String.valueOf(loggedUser.getFollowing()))
            );
        } else if (viewedUser != null) {
            imageLoader.displayImage(viewedUser.getAvatarUrl(), userAvatarImageView, ImageUtils.getImageLoaderOptions());
            imageLoader.displayImage(viewedUser.getAvatarUrl(), userAvatarImage, ImageUtils.getImageLoaderOptions());
            userNameTxt.setText(viewedUser.getName());
            followersTxt.setText(
                    String.format("%s%s", getString(R.string.user_followers_title), String.valueOf(viewedUser.getFollowers()))
            );
            followingTxt.setText(
                    String.format("%s%s", getString(R.string.user_following_title), String.valueOf(viewedUser.getFollowing()))
            );
        }
    }
}
