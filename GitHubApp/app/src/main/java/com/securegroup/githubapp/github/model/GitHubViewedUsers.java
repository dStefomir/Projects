package com.securegroup.githubapp.github.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by user on 05-Feb-17.
 */

@Table(database = GitHubAppDatabase.class)
public class GitHubViewedUsers extends BaseModel {

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private long userId;

    @Column
    private String name;

    @Column
    private String avatarUrl;

    @Column
    private int followers;

    @Column
    private int following;

    @Column
    private String userHtmlUrl;

    public GitHubViewedUsers() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public String getUserHtmlUrl() {
        return userHtmlUrl;
    }

    public void setUserHtmlUrl(String userHtmlUrl) {
        this.userHtmlUrl = userHtmlUrl;
    }

    public static GitHubViewedUsers getGitHubViewedUser(String userName) {
        return SQLite
                .select()
                .from(GitHubViewedUsers.class)
                .where(GitHubViewedUsers_Table.name.eq(userName)
                ).querySingle();
    }

    public static GitHubViewedUsers getGitHubViewedUser(long id) {
        return SQLite
                .select()
                .from(GitHubViewedUsers.class)
                .where(GitHubViewedUsers_Table.id.eq(id)
                ).querySingle();
    }

    public static GitHubViewedUsers getGitHubViewedUser(long userId, String userName) {
        return SQLite
                .select()
                .from(GitHubViewedUsers.class)
                .where(GitHubViewedUsers_Table.userId.eq(userId)
                ).and(GitHubViewedUsers_Table.name.eq(userName)).querySingle();
    }

    public static List<GitHubViewedUsers> getAllSearchedUsers(long userId, String userName) {
        return SQLite
                .select()
                .from(GitHubViewedUsers.class)
                .where(GitHubViewedUsers_Table.userId.eq(userId)
                ).and(GitHubViewedUsers_Table.name.like("%" + userName + "%")).queryList();
    }

    public static List<GitHubViewedUsers> getAllViewedUsers(long userId) {
        return SQLite
                .select()
                .from(GitHubViewedUsers.class)
                .where(GitHubViewedUsers_Table.userId.eq(userId)
                ).queryList();
    }
}
