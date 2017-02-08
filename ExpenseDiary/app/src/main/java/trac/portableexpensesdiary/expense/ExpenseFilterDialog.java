package trac.portableexpensesdiary.expense;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.CategoryAdapter;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyGridView;
import trac.portableexpensesdiary.interfaces.NotificationInterface;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.utils.Constants;

public class ExpenseFilterDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    public ExpenseFilterDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_filter_by_category,
                container
        );

        setDialogLayout();
        initViews(view);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category selectedCategory =
                (Category) view.getTag(R.layout.manual_register_item);

        NotificationInterface notificationInterface =
                (NotificationInterface) getActivity();
        notificationInterface.notifyField(selectedCategory);

        dismiss();
    }

    private void setDialogLayout() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void initViews(View view) {
        AntiMonkeyGridView categoryGridView =
                (AntiMonkeyGridView) view.findViewById(R.id.categoryGridView);

        categoryGridView.setOnItemClickListener(this);

        setAdapter(categoryGridView);
    }

    private void setAdapter(AntiMonkeyGridView gridView) {
        CategoryAdapter adapter = new CategoryAdapter(
                getActivity(),
                Constants.CATEGORY_IMAGE_SIZE_SMALL
        );
        gridView.setAdapter(adapter);
    }

    public static ExpenseFilterDialog newInstance() {

        return new ExpenseFilterDialog();
    }
}
