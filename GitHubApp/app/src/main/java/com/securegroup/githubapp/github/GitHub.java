package com.securegroup.githubapp.github;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.sdk.services.search.IssuesSearchClient;
import com.alorma.github.sdk.services.search.UsersSearchClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.UnauthorizedException;
import com.alorma.github.sdk.services.user.UserFollowersClient;
import com.alorma.github.sdk.services.user.UserFollowingClient;
import com.alorma.gitskarios.core.Pair;
import com.securegroup.githubapp.github.model.GitHubRepository;
import com.securegroup.githubapp.github.model.GitHubUser;
import com.securegroup.githubapp.github.model.GitHubViewedUsers;
import com.securegroup.githubapp.github.model.RepositoryIssues;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Holds all service calls to the GitHub API
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */
public class GitHub {

    private OnAttemptLogin onAttemptLogin;
    private OnUserSearchFinished onUserSearchFinished;
    private OnFail onFail;

    private final List<GitHubViewedUsers> foundUsersId = new ArrayList<>();

    /**
     * Constructor
     *
     * @param onAttemptLogin interface that returns the call to the login activity
     * @param onFail interface that returns call back to the activity that implements it when
     *               something happens with the information download
     */
    public GitHub(OnAttemptLogin onAttemptLogin, OnFail onFail) {
        this.onAttemptLogin = onAttemptLogin;
        this.onFail = onFail;
    }

    /**
     * Constructor
     *
     * @param onUserSearchFinished interface that returns the call to the search user activity
     * @param onFail interface that returns call back to the activity that implements it when
     *               something happens with the information download
     */
    public GitHub(OnUserSearchFinished onUserSearchFinished, OnFail onFail) {
        this.onUserSearchFinished = onUserSearchFinished;
        this.onFail = onFail;
    }

    /**
     * Service call to validate the user credentials and returns the user data
     *
     * @param userName name of the user
     * @param userPassword password of the user
     *
     */
    public void attemptLogIn(final String userName, final String userPassword) {
        new GetAuthUserClient(userName, userPassword).observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<User>() {
                               @Override
                               public void call(User user) {
                                   GitHubUser loggedUser = persistGitHubUser(userName, userPassword, user);
                                   getLoggedUserRepositories(loggedUser.getId(), loggedUser.getName());
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (throwable instanceof UnauthorizedException) {
                                    onAttemptLogin.onLoginFailed(null);
                                } else {
                                    onAttemptLogin.onLoginFailed(throwable.getMessage());
                                }
                            }
                        }
                );
    }

    private void getLoggedUserRepositories(final long loggedUserId, final String loggedUserName) {
        final GithubReposClient client = new UserReposClient(loggedUserName, null);
        client.observable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<List<Repo>, Integer>>() {
                               @Override
                               public void call(Pair<List<Repo>, Integer> listIntegerPair) {
                                   for (Repo repo : listIntegerPair.first) {
                                       final GitHubRepository repository =
                                               persistUserRepositories(loggedUserId, repo, null);
                                       getRepositoryIssues(loggedUserName, repository);
                                   }

                                   if (onAttemptLogin != null) {
                                       onAttemptLogin.onLoginSuccess();
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof UnauthorizedException) {
                                       if (onFail != null) {
                                           onFail.onFail(null);
                                       }
                                   } else {
                                       if (onFail != null) {
                                           onFail.onFail(throwable.getMessage());
                                       }
                                   }
                               }
                           }
                );
    }

    /**
     * Service call that returns a content of the searched users
     *
     * @param userName name of the user
     * @param loggedUserId id of the logged user (extracted from GitHubUser Table)
     *
     */
    public void getSearchedUsers(final long loggedUserId, final String userName) {
        final String query = String.format("%s%s", "user:", userName);
        final UsersSearchClient client = new UsersSearchClient(query);
        client.observable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<List<User>, Integer>>() {
                               @Override
                               public void call(Pair<List<User>, Integer> listIntegerPair) {
                                   foundUsersId.clear();
                                   for (User user : listIntegerPair.first) {
                                       final GitHubViewedUsers viewedUser = persistGitHubViewedUserData(loggedUserId, user);
                                       getSearchedUserRepositories(loggedUserId, viewedUser.getName());
                                   }

                                   if (onUserSearchFinished != null) {
                                       onUserSearchFinished.onUserSearchFinished(foundUsersId);
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof UnauthorizedException) {
                                       if (onFail != null) {
                                           onFail.onFail(null);
                                       }
                                   } else {
                                       if (onFail != null) {
                                           onFail.onFail(throwable.getMessage());
                                       }
                                   }
                               }
                           }
                );
    }

    private void getSearchedUserRepositories(final long loggedUserId, final String userName) {
        final GithubReposClient client = new UserReposClient(userName, null);
        client.observable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<List<Repo>, Integer>>() {
                               @Override
                               public void call(Pair<List<Repo>, Integer> listIntegerPair) {
                                   for (Repo repo : listIntegerPair.first) {
                                       final GitHubViewedUsers viewedUser = persistGitHubViewedUserData(loggedUserId, repo.owner);
                                       final GitHubRepository repository = persistUserRepositories(loggedUserId, repo, viewedUser);
                                       getRepositoryIssues(userName, repository);
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof UnauthorizedException) {
                                       if (onFail != null) {
                                           onFail.onFail(null);
                                       }
                                   } else {
                                       if (onFail != null) {
                                           onFail.onFail(throwable.getMessage());
                                       }
                                   }
                               }
                           }
                );
    }

    private void getRepositoryIssues(final String userName, final GitHubRepository repository) {
        final String query = String.format(
                "%s%s%s%s",
                "user:", userName, " ", "repo:" + repository.getRepositoryName()
        );

        IssuesSearchClient asd = new IssuesSearchClient(query);
        asd.observable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<List<Issue>, Integer>>() {
                               @Override
                               public void call(Pair<List<Issue>, Integer> listIntegerPair) {
                                   for (Issue issue : listIntegerPair.first) {

                                       persistRepositoryIssues(repository, issue);
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof UnauthorizedException) {
                                       if (onFail != null) {
                                           onFail.onFail(null);
                                       }
                                   } else {
                                       if (onFail != null) {
                                           onFail.onFail(throwable.getMessage());
                                       }
                                   }
                               }
                           }
                );
    }

    /**
     * Service call that returns all user followers
     *
     * @param userName name of the user
     * @param loggedUserId id of the logged user (Extracted from the GitHubUser Table)
     *
     */
    public void getUserFollowers(final long loggedUserId, final String userName) {
        UserFollowersClient userFollowersClient = new UserFollowersClient(userName);
        userFollowersClient.observable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<List<User>, Integer>>() {
                               @Override
                               public void call(Pair<List<User>, Integer> listIntegerPair) {
                                   foundUsersId.clear();

                                   for (User user : listIntegerPair.first) {

                                       persistGitHubViewedUserData(loggedUserId, user);

                                       getSearchedUserRepositories(loggedUserId, userName);
                                   }

                                   if (onUserSearchFinished != null) {
                                       onUserSearchFinished.onUserSearchFinished(foundUsersId);
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof UnauthorizedException) {
                                       if (onFail != null) {
                                           onFail.onFail(null);
                                       }
                                   } else {
                                       if (onFail != null) {
                                           onFail.onFail(throwable.getMessage());
                                       }
                                   }
                               }
                           }
                );
    }

    /**
     * Service call that returns all user followings
     *
     * @param userName name of the user
     * @param loggedUserId id of the logged user (Extracted from the GitHubUser Table)
     *
     */
    public void getUserFollowings(final long loggedUserId, final String userName) {
        UserFollowingClient userFollowingClient = new UserFollowingClient(userName);
        userFollowingClient.observable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<List<User>, Integer>>() {
                               @Override
                               public void call(Pair<List<User>, Integer> listIntegerPair) {
                                   foundUsersId.clear();
                                   for (User user : listIntegerPair.first) {

                                       persistGitHubViewedUserData(loggedUserId, user);

                                       getSearchedUserRepositories(loggedUserId, userName);
                                   }

                                   if (onUserSearchFinished != null) {
                                       onUserSearchFinished.onUserSearchFinished(foundUsersId);
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof UnauthorizedException) {
                                       if (onFail != null) {
                                           onFail.onFail(null);
                                       }
                                   } else {
                                       if (onFail != null) {
                                           onFail.onFail(throwable.getMessage());
                                       }
                                   }
                               }
                           }
                );
    }

    private GitHubViewedUsers persistGitHubViewedUserData(long loggedUserId, User user) {
        GitHubViewedUsers viewedUsers =
                GitHubViewedUsers.getGitHubViewedUser(loggedUserId, user.login);
        if (viewedUsers == null) {
            viewedUsers = new GitHubViewedUsers();
        }
        viewedUsers.setUserId(loggedUserId);
        viewedUsers.setName(user.login);
        viewedUsers.setAvatarUrl(user.avatar_url);
        viewedUsers.setFollowers(user.followers);
        viewedUsers.setFollowing(user.following);

        viewedUsers.save();
        foundUsersId.add(viewedUsers);

        return viewedUsers;
    }

    private void persistRepositoryIssues(GitHubRepository repository, Issue issue) {
        RepositoryIssues issues =
                RepositoryIssues.getRepositoryIssues(issue.title, repository.getId());
        if (issues == null) {
            issues = new RepositoryIssues();
        }

        issues.setRepositoryId(repository.getId());
        issues.setTitle(issue.title);
        issues.setDateOfCreation(issue.created_at);
        issues.setStatus(issue.state.value);

        issues.save();
    }

    private GitHubRepository persistUserRepositories(final long loggedUserId, final Repo repo,  final GitHubViewedUsers viewedUsers) {
        GitHubRepository repository =
                GitHubRepository
                        .getGitHubRepository(loggedUserId, viewedUsers != null ? viewedUsers.getId() : -1L, repo.full_name);
        if (repository == null) {
            repository = new GitHubRepository();
        }

        repository.setUserId(loggedUserId);
        repository.setViewedUserId(viewedUsers != null ? viewedUsers.getId() : -1L);
        repository.setRepositoryName(repo.full_name);
        repository.setUserUrl(repo.owner.html_url);
        repository.setBranches(repo.branches != null ? repo.branches.size() : 0);
        int commitsCount = 0;
        if (repo.branches != null) {

            for (Branch branch : repo.branches) {
                commitsCount = commitsCount + branch.commit.stats.total;
            }
        }
        repository.setCommits(commitsCount);
        repository.setContributions(repo.subscribers_count);
        repository.setForks(repo.forks_count);
        repository.setReleases(repo.network_count);
        repository.setStars(repo.stargazers_count);
        repository.setUsedLanguages(repo.language);
        repository.setStared(false);

        repository.save();

        return repository;
    }

    private GitHubUser persistGitHubUser(final String userName, String userPassword, User user) {
        GitHubUser loggedUser =
                GitHubUser.getGitHubUser(userName);

        if (loggedUser == null) {
            loggedUser = new GitHubUser();
        }

        loggedUser.setName(user.login);
        loggedUser.setAvatarUrl(user.avatar_url);
        loggedUser.setPassword(userPassword);
        loggedUser.setUserHtmlUrl(user.html_url);
        loggedUser.setFollowers(user.followers);
        loggedUser.setFollowing(user.following);

        loggedUser.save();

        final Session session = Session.getInstance();
        session.setUserName(loggedUser.getName());
        session.setUserId(loggedUser.getId());

        return loggedUser;
    }
}
