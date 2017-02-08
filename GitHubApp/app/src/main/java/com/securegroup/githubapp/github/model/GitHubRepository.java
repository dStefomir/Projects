package com.securegroup.githubapp.github.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.securegroup.githubapp.github.Session;

import java.util.List;

/**
 * Created by user on 05-Feb-17.
 */

@Table(database = GitHubAppDatabase.class)
public class GitHubRepository extends BaseModel {

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String userUrl;
    //idto na potrebitelq koito se e lognal
    @Column
    private long userId;
    //trqbva da e -1 ako e moeto erpo ako ne e znachi sam e repo na nqkoi drug
    @Column
    private long viewedUserId;

    @Column
    private String repositoryName;

    @Column
    private int commits;

    @Column
    private int branches;

    @Column
    private int releases;

    @Column
    private int stars;

    @Column
    private int forks;

    @Column
    private int contributions;

    @Column
    private String usedLanguages;

    @Column
    private boolean isStared;

    public GitHubRepository() {
        this.userId = -1L;
        this.viewedUserId = -1L;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getViewedUserId() {
        return viewedUserId;
    }

    public void setViewedUserId(long viewedUserId) {
        this.viewedUserId = viewedUserId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public int getCommits() {
        return commits;
    }

    public void setCommits(int commits) {
        this.commits = commits;
    }

    public int getBranches() {
        return branches;
    }

    public void setBranches(int branches) {
        this.branches = branches;
    }

    public int getReleases() {
        return releases;
    }

    public void setReleases(int releases) {
        this.releases = releases;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }

    public String getUsedLanguages() {
        return usedLanguages;
    }

    public void setUsedLanguages(String usedLanguages) {
        this.usedLanguages = usedLanguages;
    }

    public boolean isStared() {
        return isStared;
    }

    public void setStared(boolean stared) {
        isStared = stared;
    }

    public static GitHubRepository getGitHubRepository(long id) {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.id.eq(id)
                ).and(GitHubRepository_Table.userId.eq(Session.getInstance().getUserId())).and(GitHubRepository_Table.viewedUserId.eq(-1)).querySingle();
    }

    public static GitHubRepository getGitHubRepository(long id, long viewedUserId) {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.id.eq(id)
                ).and(GitHubRepository_Table.userId.eq(Session.getInstance().getUserId())).and(GitHubRepository_Table.viewedUserId.eq(viewedUserId)).querySingle();
    }

    public static GitHubRepository getGitHubRepository(long userId, long viewedUserId, String repositoryName) {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.userId.eq(userId)
                ).and(GitHubRepository_Table.viewedUserId.eq(viewedUserId))
                .and(GitHubRepository_Table.repositoryName.eq(repositoryName)).querySingle();
    }

    public static List<GitHubRepository> getGitHubRepositories(long viewedUserId) {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.isStared.eq(true)
                ).and(GitHubRepository_Table.viewedUserId.eq(viewedUserId)).queryList();
    }

    public static List<GitHubRepository> getGitHubRepositories() {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.isStared.eq(true)
                ).and(GitHubRepository_Table.userId.eq(Session.getInstance().getUserId())).queryList();
    }

    public static List<GitHubRepository> getGitHubRepositoriesById(long userId) {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.userId.eq(userId)
                ).and(GitHubRepository_Table.viewedUserId.eq(-1)).and(GitHubRepository_Table.isStared.eq(false)).queryList();
    }

    public static List<GitHubRepository> getGitHubRepositoriesByIds(long userId, long viewedUserId) {

        return SQLite
                .select()
                .from(GitHubRepository.class)
                .where(GitHubRepository_Table.userId.eq(userId)
                ).and(GitHubRepository_Table.viewedUserId.eq(viewedUserId)).and(GitHubRepository_Table.isStared.eq(false)).queryList();
    }
}
