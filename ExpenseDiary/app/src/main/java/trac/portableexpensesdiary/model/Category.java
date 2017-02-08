package trac.portableexpensesdiary.model;

import android.graphics.drawable.Drawable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

import trac.portableexpensesdiary.PortableExpensesDiaryApp;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.utils.CategoryLanguageParser;
import trac.portableexpensesdiary.utils.ImageUtils;

@Table(database = ExpensesDatabase.class)
public class Category extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    private UUID id;

    @Column
    private UUID userId;

    @Column
    private String categoryName;

    @Column
    private String categoryDescription;

    @Column
    private Blob categoryPicture;

    @Column
    private boolean isDeletable;

    @Column
    private int numberOfSubCategories;

    public Category() {
        this.id = UUID.randomUUID();
        this.userId = SessionManager.getInstance().getCurrentUser().getId();
        this.isDeletable = true;
        this.numberOfSubCategories = 0;
    }

    public Category(String categoryName, Drawable categoryPicture, UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.categoryName = categoryName;
        this.categoryPicture = new Blob(
                ImageUtils.defaultDrawableToBlob(
                        categoryPicture
                )
        );
        this.isDeletable = false;
        this.numberOfSubCategories = 0;
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

    public String getCategoryName() {
        return CategoryLanguageParser.getParsedCategoryName(PortableExpensesDiaryApp.getContext(), categoryName);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public Blob getCategoryPicture() {
        return categoryPicture;
    }

    public void setCategoryPicture(Blob categoryPicture) {
        this.categoryPicture = categoryPicture;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public int getNumberOfSubCategories() {
        return numberOfSubCategories;
    }

    public void setNumberOfSubCategories(int numberOfSubCategories) {
        this.numberOfSubCategories = numberOfSubCategories;
    }

    public static Category getCategoryById(UUID id) {
        return SQLite
                .select()
                .from(Category.class)
                .where(Category_Table.id.eq(id))
                .querySingle();
    }

    public static Category getCategoryByName(String categoryName) {
        return SQLite
                .select()
                .from(Category.class)
                .where(Category_Table.categoryName.eq(categoryName.trim()))
                .querySingle();
    }

    public static List<Category> getAllCategoriesForUser() {
        return SQLite.select()
                .from(Category.class)
                .where(Category_Table.userId.eq(SessionManager.getInstance().getCurrentUser().getId()))
                .queryList();
    }

    public static long getNumberOfRecords() {
        return SQLite.select(Method.count())
                .from(Category.class)
                .where(Category_Table.userId.eq(SessionManager.getInstance().getCurrentUser().getId()))
                .count() - 1;
    }
}