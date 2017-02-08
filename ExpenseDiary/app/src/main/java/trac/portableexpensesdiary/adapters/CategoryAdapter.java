package trac.portableexpensesdiary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.ImageUtils;

public class CategoryAdapter extends BaseAdapter {

    private List<Category> categoryList = new ArrayList<>();

    private Context context;

    private int imageSize;

    public CategoryAdapter(Context context, int imageSize) {
        this.context = context;
        this.imageSize = imageSize;

        loadData();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;

        View view = convertView;

        if (view== null) {
            view = inflater.inflate(
                    R.layout.manual_register_item,
                    null
            );

            holder = new ViewHolder();

            holder.image =
                    (CircleImageView) view.findViewById(R.id.imgManualRegisterIcon);
            final float scale = context.getResources().getDisplayMetrics().density;
            holder.image.getLayoutParams().height = (int)(imageSize * scale);
            holder.image.getLayoutParams().width = (int)(imageSize * scale);
            holder.menuTitle =
                    (TextView) view.findViewById(R.id.txtManualRegisterTitle);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Category categoryItem = categoryList.get(position);

        view.setTag(
                R.layout.manual_register_item,
                categoryItem
        );

        AsyncThumbnailImageLoaderTask categoryImageThread =
                new AsyncThumbnailImageLoaderTask(holder.image);
        categoryImageThread.execute(
                categoryItem.getCategoryPicture()
        );

        view.setTag(
                R.id.imgManualRegisterIcon,
                categoryImageThread
        );

        holder.menuTitle.setText(
                categoryItem.getCategoryName()
        );

        return view;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < categoryList.size()) {

            return categoryList.get(position);
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
        this.categoryList =
                Category.getAllCategoriesForUser();

        final int INCOME_CATEGORY = 0;
        this.categoryList.remove(INCOME_CATEGORY);
    }

    private static class ViewHolder {

        private CircleImageView image;
        private TextView menuTitle;
    }
}