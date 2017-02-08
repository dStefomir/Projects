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
public class RepositoryIssues extends BaseModel {

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private long repositoryId;

    @Column
    private String title;

    @Column
    private String dateOfCreation;

    @Column
    private int status;

    public RepositoryIssues() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static RepositoryIssues getRepositoryIssues(String issueTitle, long repositoryId) {

        return SQLite
                .select()
                .from(RepositoryIssues.class)
                .where(RepositoryIssues_Table.title.eq(issueTitle)
                ).and(RepositoryIssues_Table.repositoryId.eq(repositoryId)).querySingle();
    }

    public static List<RepositoryIssues> getRepositoryIssues(long repositoryId) {

        return SQLite
                .select()
                .from(RepositoryIssues.class)
                .where(RepositoryIssues_Table.repositoryId.eq(repositoryId)
                ).queryList();
    }
}
