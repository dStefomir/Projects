package com.securegroup.githubapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.securegroup.githubapp.R;
import com.securegroup.githubapp.github.Session;
import com.securegroup.githubapp.github.model.GitHubRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes care of the User Repositories (ListView items)
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */


public class UserRepositoriesAdapter extends BaseAdapter {

    private Context context;

    private List<GitHubRepository> repoList = new ArrayList<>();

    public UserRepositoriesAdapter(Context context) {
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
                    R.layout.items_repositories,
                    null
            );

            holder = new ViewHolder();

            holder.repositoryNameTxt =
                    (TextView) view.findViewById(R.id.repositoryNameTxt);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GitHubRepository repository = repoList.get(i);

        holder.repositoryNameTxt.setText(repository.getRepositoryName());

        view.setTag(
                R.layout.items_repositories,
                repository
        );

        return view;
    }

    @Override
    public int getCount() {

        return repoList.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < repoList.size()) {

            return repoList.get(i);
        }

        return null;
    }

    @Override
    public long getItemId(int i) {

        return 0;
    }

    /**
     * Takes care of loading the user repositories data to the adapter datastructure
     *
     * @param userId logged user id extracted from GitHubUser table
     * @param isStared notifies if it should load stared repositories
     */
    public void loadLoggedUserData(long userId, boolean isStared) {
        repoList.clear();

        List<GitHubRepository> repositoryList;
        if (isStared) {
            repositoryList = GitHubRepository.getGitHubRepositories();
        } else {
            repositoryList = GitHubRepository.getGitHubRepositoriesById(userId);
        }

        for (GitHubRepository repository : repositoryList) {
            repoList.add(repository);
        }

        notifyDataSetChanged();
    }


    /**
     * Takes care of loading the user repositories data to the adapter datastructure
     *
     * @param viewedUserId viewedUser id extracted from GitHubViewedUser table
     * @param isStared notifies if it should load stared repositories
     */
    public void loadViewedUserData(long viewedUserId, boolean isStared) {
        repoList.clear();

        List<GitHubRepository> repositoryList;
        if (isStared) {
            repositoryList = GitHubRepository.getGitHubRepositories(viewedUserId);
        } else {
            repositoryList = GitHubRepository.getGitHubRepositoriesByIds(Session.getInstance().getUserId(), viewedUserId);
        }

        for (GitHubRepository repository : repositoryList) {
            repoList.add(repository);
        }

    }

    private static class ViewHolder {

        private TextView repositoryNameTxt;
    }
}
