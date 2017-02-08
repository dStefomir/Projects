package trac.portableexpensesdiary.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

import trac.portableexpensesdiary.expense.SessionManager;

@Table(database = ExpensesDatabase.class)
public class ExpenseTracking extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    private UUID id;

    @Column
    private UUID userId;

    @Column
    private float income;

    @Column
    private float currentAmount;

    @Column
    private int incomeMethod;

    @Column
    private long dateOfReceivingIncome;

    @Column
    private int trackingState;

    public ExpenseTracking() {
        super();

        this.id = UUID.randomUUID();
        this.userId = SessionManager.getInstance().getCurrentUser().getId();
        this.trackingState = ExpenseTrackingEnum.STARTED;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(float currentAmount) {
        this.currentAmount = currentAmount;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public int getIncomeMethod() {
        return incomeMethod;
    }

    public void setIncomeMethod(int incomeMethod) {
        this.incomeMethod = incomeMethod;
    }

    public long getDateOfReceivingIncome() {
        return dateOfReceivingIncome;
    }

    public void setDateOfReceivingIncome(long dateOfReceivingIncome) {
        this.dateOfReceivingIncome = dateOfReceivingIncome;
    }

    public int getTrackingState() {
        return trackingState;
    }

    public void setTrackingState(int trackingState) {
        this.trackingState = trackingState;
    }

    public static ExpenseTracking getCurrentExpenseTracking(UUID userId) {
        return SQLite.select()
                .from(ExpenseTracking.class)
                .where(ExpenseTracking_Table.userId.eq(userId))
                .querySingle();
    }
}
