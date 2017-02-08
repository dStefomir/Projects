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
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.ImageUtils;

public class SubCategoryAdapter extends BaseAdapter {

    private List<SubCategory> subCategoryList = new ArrayList<>();
    private Context context;

    public SubCategoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(
                    R.layout.sub_category_item,
                    null
            );

            holder = new ViewHolder();

            holder.subCategoryImage =
                    (CircleImageView) view.findViewById(R.id.subCategoryPicture);
            holder.subCategoryTitle =
                    (TextView) view.findViewById(R.id.subCategoryTitle);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final SubCategory subCategoryItem = subCategoryList.get(position);

        view.setTag(
                R.layout.sub_category_item,
                subCategoryItem
        );

        AsyncThumbnailImageLoaderTask subCategoryImageThread =
                new AsyncThumbnailImageLoaderTask(holder.subCategoryImage);
        subCategoryImageThread.execute(
                subCategoryItem.getSubCategoryPicture()
        );

        view.setTag(
                R.id.subCategoryPicture,
                subCategoryImageThread
        );

        holder.subCategoryTitle.setText(
                subCategoryItem.getSubCategoryName()
        );

        return view;
    }

    @Override
    public int getCount() {
        return subCategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < subCategoryList.size()) {

            return subCategoryList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setSubCategoryList(List<SubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    private static class ViewHolder {

        private CircleImageView subCategoryImage;
        private TextView subCategoryTitle;
    }
}
