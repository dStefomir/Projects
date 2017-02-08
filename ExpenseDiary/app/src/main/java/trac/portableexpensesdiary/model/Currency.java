package trac.portableexpensesdiary.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

@Table(database = ExpensesDatabase.class)
public class Currency extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    private UUID id;

    @Column
    private int currencyPictureId;

    @Column
    private String currencyName;

    @Column
    private String currencyRate;

    public Currency() {
        this.id = UUID.randomUUID();
        this.currencyRate = "1.00";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getCurrencyPictureId() {
        return currencyPictureId;
    }

    public void setCurrencyPictureId(int currencyPictureId) {
        this.currencyPictureId = currencyPictureId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(String currencyRate) {
        this.currencyRate = currencyRate;
    }

    public static List<Currency> getAllCurrencies() {
        return SQLite
                .select()
                .from(Currency.class)
                .orderBy(Currency_Table.currencyName, true)
                .queryList();
    }

    public static Currency getCurrency(UUID currencyId) {
        return SQLite.select()
                .from(Currency.class)
                .where(Currency_Table.id.eq(currencyId))
                .querySingle();
    }

    public static Currency getCurrencyByName(String currencyName) {
        return SQLite.select()
                .from(Currency.class)
                .where(Currency_Table.currencyName.eq(currencyName))
                .querySingle();
    }

    public static List<Currency> getFilteredCurrencies(String filter) {
        return SQLite
                .select()
                .from(Currency.class)
                .where(
                        Currency_Table.currencyName.like(
                                "%" + filter.trim() + "%"
                        )
                ).queryList();
    }
}
