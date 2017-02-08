package trac.portableexpensesdiary.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

@Table(database = ExpensesDatabase.class)
public class User extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    private UUID id;

    @Column
    private UUID currencyId;

    @Column
    private String userName;

    @Column
    private String userPassword;

    @Column
    private String forgetQuestion;

    @Column
    private String forgetAnswer;

    @Column(defaultValue = "0")
    private long lastLoginDate;

    public User() {
        super();

        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getForgetQuestion() {
        return forgetQuestion;
    }

    public void setForgetQuestion(String forgetQuestion) {
        this.forgetQuestion = forgetQuestion;
    }

    public String getForgetAnswer() {
        return forgetAnswer;
    }

    public void setForgetAnswer(String forgetAnswer) {
        this.forgetAnswer = forgetAnswer;
    }

    public long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public static User getUserByCredentials(String userName) {
        return SQLite
                .select()
                .from(User.class)
                .where(User_Table.userName.eq(userName))
                .querySingle();
    }

    public static User getLastLoggedInUser() {
        return SQLite
                .select()
                .from(User.class)
                .orderBy(User_Table.lastLoginDate, false)
                .querySingle();
    }

    public static User getUserByName(String userName) {
        return SQLite
                .select()
                .from(User.class)
                .where(User_Table.userName.eq(userName))
                .querySingle();
    }

    public static User getUserById(UUID id) {
        return SQLite
                .select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }
}