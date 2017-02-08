package com.securegroup.githubapp.github.model;

import com.alorma.github.sdk.bean.dto.response.User;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by user on 05-Feb-17.
 */

@Table(database = GitHubAppDatabase.class)
public class GitHubUser extends BaseModel {

    @Column(name = "id")
    @PrimaryKey (autoincrement = true)
    private long id;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String avatarUrl;

    @Column
    private int followers;

    @Column
    private int following;

    @Column
    private String userHtmlUrl;

    public GitHubUser() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public static GitHubUser getGitHubUser(long id) {

        return SQLite
                .select()
                .from(GitHubUser.class)
                .where(GitHubUser_Table.id.eq(id)
                ).querySingle();
    }

    public static GitHubUser getGitHubUser(String userName) {

        return SQLite
                .select()
                .from(GitHubUser.class)
                .where(GitHubUser_Table.name.eq(userName)
                ).querySingle();
    }
}
