package trac.portableexpensesdiary.model;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import trac.portableexpensesdiary.expense.ExpenseManager;

@Table(database = ExpensesDatabase.class)
public class ExpenseTrackingDetails extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    private UUID id;

    @Column
    private UUID expenseTrackingId;

    @Column
    private UUID expenseCategoryId;

    @Nullable
    @Column
    private UUID expenseSubCategoryId;

    @Column
    private UUID currencyId;

    @Column
    private String selectedCurrencyRate;

    @Column
    private String currentCurrencyRate;

    @Column
    private float expensePrice;

    @Column
    private String description;

    @Column
    private long registeredAt;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    public ExpenseTrackingDetails() {
        super();

        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getExpenseTrackingId() {
        return expenseTrackingId;
    }

    public void setExpenseTrackingId(UUID expenseTrackingId) {
        this.expenseTrackingId = expenseTrackingId;
    }

    public UUID getExpenseCategoryId() {
        return expenseCategoryId;
    }

    public void setExpenseCategoryId(UUID expenseCategoryId) {
        this.expenseCategoryId = expenseCategoryId;
    }

    @Nullable
    public UUID getExpenseSubCategoryId() {
        return expenseSubCategoryId;
    }

    public void setExpenseSubCategoryId(UUID expenseSubCategoryId) {
        this.expenseSubCategoryId = expenseSubCategoryId;
    }

    public UUID getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }

    public String getSelectedCurrencyRate() {
        return selectedCurrencyRate;
    }

    public void setSelectedCurrencyRate(String selectedCurrencyRate) {
        this.selectedCurrencyRate = selectedCurrencyRate;
    }

    public String getCurrentCurrencyRate() {
        return currentCurrencyRate;
    }

    public void setCurrentCurrencyRate(String currentCurrencyRate) {
        this.currentCurrencyRate = currentCurrencyRate;
    }

    public float getExpensePrice() {
        return expensePrice;
    }

    public void setExpensePrice(float expensePrice) {
        this.expensePrice = expensePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(long registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static List<ExpenseTrackingDetails> getExpenseTrackingDetailByRootCategory(Category category, long fromDate, long toDate) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.expenseCategoryId.eq(category.getId()))
                .and(ExpenseTrackingDetails_Table.registeredAt.between(fromDate).and(toDate))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .queryList();
    }

    public static List<ExpenseTrackingDetails> getExpenseTrackingDetailByRootCategory(Category category) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.expenseCategoryId.eq(category.getId()))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .queryList();
    }

    public static List<ExpenseTrackingDetails> getExpenseTrackingDetailBySubCategory(SubCategory subCategory) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.expenseSubCategoryId.eq(subCategory.getId()))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .queryList();
    }

    public static List<ExpenseTrackingDetails> getExpenseTrackingDetailsFiltered(
            long fromDate,
            long toDate,
            Integer expenseCounter) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        if(expenseCounter == null) {

            return SQLite.select()
                    .from(ExpenseTrackingDetails.class)
                    .where(ExpenseTrackingDetails_Table.registeredAt.between(fromDate).and(toDate))
                    .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                    .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                    .queryList();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.registeredAt.between(fromDate).and(toDate))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .limit(expenseCounter)
                .queryList();
    }

    public static List<ExpenseTrackingDetails> getExpenseTracingDetailByCategory(UUID categoryId) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.expenseCategoryId.eq(categoryId))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .queryList();
    }

    public static List<ExpenseTrackingDetails> getExpenseTrackingDetailBySubCategory(UUID subCategoryId) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.expenseSubCategoryId.eq(subCategoryId))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .queryList();
    }

    public static List<ExpenseTrackingDetails> getLastTwoExpenseTrackingDetails(long fromDate, long toDate) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return new ArrayList<>();
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.registeredAt.between(fromDate).and(toDate))
                .and(ExpenseTrackingDetails_Table.expenseTrackingId.eq(ExpenseManager.getInstance().getCurrentActiveExpenseTracking().getId()))
                .orderBy(ExpenseTrackingDetails_Table.registeredAt, false)
                .limit(2)
                .queryList();
    }

    public static ExpenseTrackingDetails getExpenseDetail(UUID id) {
        if (ExpenseManager.getInstance().getCurrentActiveExpenseTracking() == null) {

            return null;
        }

        return SQLite.select()
                .from(ExpenseTrackingDetails.class)
                .where(ExpenseTrackingDetails_Table.id.eq(id))
                .querySingle();
    }
}