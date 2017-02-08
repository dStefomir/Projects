package trac.portableexpensesdiary.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

@Table(database = ExpensesDatabase.class)
public class SubCategory extends BaseModel {

    @PrimaryKey
    @Column
    private UUID id;

    @Column
    private UUID parentCategoryId;

    @Column
    private String subCategoryName;

    @Column
    private String subCategoryDescription;

    @Column
    private Blob subCategoryPicture;

    public SubCategory() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryDescription() {
        return subCategoryDescription;
    }

    public void setSubCategoryDescription(String subCategoryDescription) {
        this.subCategoryDescription = subCategoryDescription;
    }

    public Blob getSubCategoryPicture() {
        return subCategoryPicture;
    }

    public void setSubCategoryPicture(Blob subCategoryPicture) {
        this.subCategoryPicture = subCategoryPicture;
    }

    public static SubCategory getSubCategory(UUID id) {
        return SQLite
                .select()
                .from(SubCategory.class)
                .where(SubCategory_Table.id.eq(id))
                .querySingle();
    }

    public static SubCategory getSubCategoryByName(String subCategoryName) {
        return SQLite.select()
                .from(SubCategory.class)
                .where(SubCategory_Table.subCategoryName.eq(subCategoryName.trim()))
                .querySingle();
    }

    public static List<SubCategory> getAllSubCategoriesByParentId(UUID parentCategoryId) {
        return SQLite.select()
                .from(SubCategory.class)
                .where(SubCategory_Table.parentCategoryId.eq(parentCategoryId))
                .queryList();
    }

    public static List<SubCategory> getAllSubCategories() {
        return SQLite.select()
                .from(SubCategory.class)
                .queryList();
    }
}
