package com.securegroup.githubapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.securegroup.githubapp.R;
import com.securegroup.githubapp.github.model.RepositoryIssues;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes care of the Repository Issues(ListView items)
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class RepositoryIssuesAdapter extends BaseAdapter {

    private Context context;

    private List<RepositoryIssues> issuesList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context context of the application
     */
    public RepositoryIssuesAdapter(Context context) {
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
                    R.layout.items_issues,
                    null
            );

            holder = new ViewHolder();

            holder.repositoryIssueTitleTxt =
                    (TextView) view.findViewById(R.id.repositoryIssuesTitleTxt);
            holder.repositoryIssueDateTxt =
                    (TextView) view.findViewById(R.id.repositoryIssueDateTxt);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final RepositoryIssues issue = issuesList.get(i);

        holder.repositoryIssueTitleTxt.setText(issue.getTitle());
        holder.repositoryIssueDateTxt.setText(issue.getDateOfCreation());

        view.setTag(
                R.layout.items_issues,
                issue
        );

        return view;
    }

    @Override
    public int getCount() {

        return issuesList.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < issuesList.size()) {

            return issuesList.get(i);
        }

        return null;
    }

    @Override
    public long getItemId(int i) {

        return 0;
    }

    /**
     * Takes care of loading the repository issues data to the adapter datastructure
     *
     * @param selectedRepositoryId id of the repository from GitHubRepository table (ORM)
     * @param issueState defines if the issue state is open, closed or all
     */
    public void loadData(long selectedRepositoryId, int issueState) {
        issuesList.clear();

        List<RepositoryIssues> repositoryIssues =
                RepositoryIssues.getRepositoryIssues(selectedRepositoryId);

        for(RepositoryIssues issue : repositoryIssues) {
            if(issueState == IssueState.all.value) {
                issuesList.add(issue);
            } else if(issue.getStatus() == issueState) {
                issuesList.add(issue);
            }
        }

        notifyDataSetChanged();
    }

    private static class ViewHolder {

        private TextView repositoryIssueTitleTxt;
        private TextView repositoryIssueDateTxt;
    }
}
