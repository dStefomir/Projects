package trac.portableexpensesdiary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.expense.ExpenseManager;
import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.Currency;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DateUtils;
import trac.portableexpensesdiary.utils.RoundUtils;

public class ReviewExpensesAdapter extends BaseAdapter {

    private Context context;

    private List<ExpenseTrackingDetails> expenseTrackingDetailsList = new ArrayList<>();
    private Map<UUID, Category> categories = new HashMap<>();
    private Map<UUID, SubCategory> subCategories = new HashMap<>();

    private Category selectedFilterCategory;
    private Currency selectedCurrency;

    private Integer numberOfRecords;

    private List<UUID> groupedDateExpenseDetails = new ArrayList<>();

    private long fromDate;
    private long toDate;

    public ReviewExpensesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ExpenseTrackingDetails expenseTrackingDetails =
                expenseTrackingDetailsList.get(position);
        final Currency expenseDetailCurrency =
                Currency.getCurrency(expenseTrackingDetails.getCurrencyId());
        final ViewHolder holder;

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(
                    R.layout.review_expenses_item,
                    null
            );
            holder = new ViewHolder();

            holder.dateOfExpenseRegistering =
                    (TextView) view.findViewById(R.id.dateOfExpenseRegistering);
            holder.categoryName =
                    (TextView) view.findViewById(R.id.categoryName);
            holder.subCategoryName =
                    (TextView) view.findViewById(R.id.subCategoryName);
            holder.image =
                    (CircleImageView) view.findViewById(R.id.expenseImage);
            holder.categoriesDelimiter =
                    view.findViewById(R.id.categoriesDelimiter);
            holder.subCategoryImage =
                    (CircleImageView) view.findViewById(R.id.subCategoryImage);
            holder.expenseAmount =
                    (TextView) view.findViewById(R.id.expenseAmount);
            holder.madeOn =
                    (TextView) view.findViewById(R.id.madeOn);
            holder.expenseNoteLayout =
                    (LinearLayout) view.findViewById(R.id.expenseNoteLayout);
            holder.expenseNote =
                    (TextView) view.findViewById(R.id.expenseNote);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!groupedDateExpenseDetails.isEmpty() && groupedDateExpenseDetails.contains(expenseTrackingDetails.getId())) {
            holder.dateOfExpenseRegistering.setVisibility(View.VISIBLE);
            holder.dateOfExpenseRegistering.setText(
                    DateUtils.groupDateToString(
                            expenseTrackingDetails.getRegisteredAt()
                    )
            );
        } else {
            holder.dateOfExpenseRegistering.setVisibility(View.GONE);
        }

        holder.categoryName.setText(
                categories.get(
                        expenseTrackingDetails.getExpenseCategoryId()
                ).getCategoryName()
        );

        AsyncThumbnailImageLoaderTask categoryImageThread =
                new AsyncThumbnailImageLoaderTask(holder.image);
        categoryImageThread.execute(
                categories.get(
                        expenseTrackingDetails.getExpenseCategoryId()
                ).getCategoryPicture()
        );

        view.setTag(
                R.id.expenseImage,
                categoryImageThread
        );

        if (subCategories.get(expenseTrackingDetails.getExpenseSubCategoryId()) != null) {
            holder.categoriesDelimiter.setVisibility(View.VISIBLE);
            holder.subCategoryName.setVisibility(View.VISIBLE);
            holder.subCategoryImage.setVisibility(View.VISIBLE);
            holder.subCategoryName.setText(
                    subCategories.get(
                            expenseTrackingDetails.getExpenseSubCategoryId()
                    ).getSubCategoryName()
            );

            AsyncThumbnailImageLoaderTask subCategoryImageThread =
                    new AsyncThumbnailImageLoaderTask(holder.subCategoryImage);
            subCategoryImageThread.execute(
                    subCategories.get(
                            expenseTrackingDetails.getExpenseSubCategoryId()
                    ).getSubCategoryPicture()
            );

            view.setTag(
                    R.id.subCategoryImage,
                    subCategoryImageThread
            );
        } else {
            holder.categoriesDelimiter.setVisibility(View.GONE);
            holder.subCategoryName.setVisibility(View.GONE);
            holder.subCategoryImage.setVisibility(View.GONE);
        }

        if (categories.get(expenseTrackingDetails.getExpenseCategoryId()).getCategoryName()
                .equals(Constants.DEFAULT_CATEGORY_INCOME)) {
            holder.expenseAmount.setTextColor(
                    context.getResources().getColor(R.color.success_stroke_color)
            );
        } else {
            holder.expenseAmount.setTextColor(
                    context.getResources().getColor(R.color.colorAccent)
            );
        }

        String totalForeignPrice = null;

        if (!selectedCurrency.getCurrencyName().equals(expenseDetailCurrency.getCurrencyName())) {
            String currentCurrencyRate = expenseTrackingDetails.getCurrentCurrencyRate();

            if(!selectedCurrency.getCurrencyRate().equals(expenseDetailCurrency.getCurrencyRate())) {
                currentCurrencyRate = selectedCurrency.getCurrencyRate();
            }

            totalForeignPrice = String.format(
                    "%s%s%s%s%s",
                    " (",
                    RoundUtils.floatToString(
                            ExpenseManager.getInstance().convertExpensePrice(
                                    expenseTrackingDetails.getSelectedCurrencyRate(),
                                    currentCurrencyRate,
                                    expenseTrackingDetails.getExpensePrice()
                            )
                    ),
                    " ",
                    selectedCurrency.getCurrencyName(),
                    ")"
            );
        }

        String expensePrice =
                RoundUtils.floatToString(
                        expenseTrackingDetails.getExpensePrice()
                );
        holder.expenseAmount.setText(
                expensePrice.concat(" ").concat(
                        expenseDetailCurrency.getCurrencyName()
                ).concat(
                        totalForeignPrice != null ? totalForeignPrice : ""
                )
        );
        holder.madeOn.setText(
                DateUtils.timeToString(
                        expenseTrackingDetails.getRegisteredAt()
                )
        );
        String expenseDescription = expenseTrackingDetails.getDescription();

        if (expenseDescription.trim().isEmpty()) {
            holder.expenseNoteLayout.setVisibility(View.GONE);
        } else {
            holder.expenseNoteLayout.setVisibility(View.VISIBLE);
        }

        holder.expenseNote.setText(
                expenseTrackingDetails.getDescription()
        );

        view.setTag(
                R.layout.review_expenses_item,
                expenseTrackingDetails
        );

        return view;
    }

    @Override
    public int getCount() {
        if (numberOfRecords != null) {
            if (numberOfRecords > expenseTrackingDetailsList.size()) {

                return expenseTrackingDetailsList.size();
            } else {

                return numberOfRecords;
            }
        }

        return expenseTrackingDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < expenseTrackingDetailsList.size()) {

            return expenseTrackingDetailsList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        loadData();

        super.notifyDataSetChanged();
    }

    private void loadData() {
        groupedDateExpenseDetails.clear();
        Long dateOfExpenseRegistering = null;
        selectedCurrency = Currency.getCurrency(
                SessionManager.getInstance().getCurrentUser().getCurrencyId()
        );

        if (getSelectedFilterCategory() == null) {
            expenseTrackingDetailsList = ExpenseTrackingDetails.getExpenseTrackingDetailsFiltered(
                    getFromDate(),
                    getToDate(),
                    numberOfRecords
            );
        } else {
            expenseTrackingDetailsList = ExpenseTrackingDetails.getExpenseTrackingDetailByRootCategory(
                    getSelectedFilterCategory(),
                    getFromDate(),
                    getToDate()
            );
        }

        for (ExpenseTrackingDetails details : expenseTrackingDetailsList) {
            if (dateOfExpenseRegistering == null) {
                dateOfExpenseRegistering = 0L;
            }

            Date expenseRegisteredAt = new Date(dateOfExpenseRegistering);
            Date detailsRegisteredAt = new Date(details.getRegisteredAt());

            if (!org.apache.commons.lang3.time.DateUtils.isSameDay(expenseRegisteredAt, detailsRegisteredAt)) {
                groupedDateExpenseDetails.add(details.getId());
                dateOfExpenseRegistering = details.getRegisteredAt();
            }

            if (categories.get(details.getExpenseCategoryId()) == null) {
                categories.put(
                        details.getExpenseCategoryId(),
                        Category.getCategoryById(
                                details.getExpenseCategoryId()
                        )
                );
            }

            if (details.getExpenseSubCategoryId() != null) {
                if (subCategories.get(details.getExpenseSubCategoryId()) == null) {
                    subCategories.put(
                            details.getExpenseSubCategoryId(),
                            SubCategory.getSubCategory(
                                    details.getExpenseSubCategoryId()
                            )
                    );
                }
            }
        }
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public Category getSelectedFilterCategory() {
        return selectedFilterCategory;
    }

    public void setNumberOfRecords(Integer numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public void setSelectedFilterCategory(Category selectedFilterCategory) {
        this.selectedFilterCategory = selectedFilterCategory;
    }

    public void clearData() {
        this.expenseTrackingDetailsList.clear();
    }

    private static class ViewHolder {

        private TextView dateOfExpenseRegistering;
        private TextView categoryName;
        private TextView subCategoryName;
        private CircleImageView image;
        private View categoriesDelimiter;
        private CircleImageView subCategoryImage;
        private TextView expenseAmount;
        private TextView madeOn;
        private LinearLayout expenseNoteLayout;
        private TextView expenseNote;
    }
}