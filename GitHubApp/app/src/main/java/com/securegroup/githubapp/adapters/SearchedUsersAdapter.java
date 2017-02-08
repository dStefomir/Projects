package com.securegroup.githubapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.securegroup.githubapp.R;
import com.securegroup.githubapp.github.model.GitHubViewedUsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes care of the Searched User(ListView items)
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class SearchedUsersAdapter extends BaseAdapter {

    private Context context;

    private List<GitHubViewedUsers> viewedUsersList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context context of the application
     */
    public SearchedUsersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(
                    R.layout.items_searched_users,
                    null
            );

            holder = new ViewHolder();

            holder.searchedUsersTxt =
                    (TextView) view.findViewById(R.id.searchedUsersTxt);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GitHubViewedUsers viewedUsers = viewedUsersList.get(i);

        holder.searchedUsersTxt.setText(viewedUsers.getName());

        view.setTag(
                R.layout.items_searched_users,
                viewedUsers
        );

        return view;
    }

    @Override
    public int getCount() {

        return viewedUsersList.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < viewedUsersList.size()) {

            return viewedUsersList.get(i);
        }

        return null;
    }

    @Override
    public long getItemId(int i) {

        return 0;
    }

    /**
     * Takes care of loading the searched users data to the adapter datastructure
     *
     * @param foundUserIdList holds extracted from the database GitHubViewedUser objects
     */
    public void loadData(List<GitHubViewedUsers> foundUserIdList) {
        viewedUsersList.clear();

        for (GitHubViewedUsers viewedUser : foundUserIdList) {
            viewedUsersList.add(viewedUser);
        }

        notifyDataSetChanged();
    }

    private static class ViewHolder {

        private TextView searchedUsersTxt;
    }
}
