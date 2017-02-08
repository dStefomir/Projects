package com.securegroup.githubapp.menu;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.securegroup.githubapp.R;
import com.securegroup.githubapp.github.Session;
import com.securegroup.githubapp.users.UserActivity;
import com.securegroup.githubapp.utils.Constants;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Takes care of the Options Menu Dialog
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class MenuDialog extends DialogFragment implements View.OnClickListener {

    private long userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPreconditions();
        initViews(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.myProfileCardView) {
            final Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra(Constants.USER_ID_TAG, userId);

            this.dismiss();
            getActivity().startActivity(intent);
            getActivity().finish();
        } else {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.warning_title))
                    .setContentText(getString(R.string.confirm_log_out_msg))
                    .setConfirmText(getString(R.string.ok_btn))
                    .setCancelText(getString(R.string.no_btn))
                    .setConfirmClickListener(
                            new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    final Session session = Session.getInstance();
                                    session.setUserId(-1L);
                                    session.setUserName(null);

                                    MenuDialog.this.dismiss();
                                    getActivity().finish();

                                    sDialog.dismissWithAnimation();
                                }
                            }
                    )
                    .setCancelClickListener(
                            new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            }
                    ).show();
        }
    }

    private void setPreconditions() {
        Bundle args = getArguments();

        userId = args.getLong(Constants.USER_ID_TAG);
    }

    private void initViews(View view) {
        final CardView myProfileCardView =
                (CardView) view.findViewById(R.id.myProfileCardView);
        final CardView logOutCardview =
                (CardView) view.findViewById(R.id.logOutCardView);

        myProfileCardView.setOnClickListener(this);
        logOutCardview.setOnClickListener(this);
    }

    /**
     * Creates a new instance of the dialog while passing some arguments to it
     *
     * @param userId logged user id extracted from GitHubUser Table
     *
     * @return MenuDialog instance
     */
    public static MenuDialog newInstance(long userId) {
        final MenuDialog dialog = new MenuDialog();

        final Bundle bundle = new Bundle();
        bundle.putLong(Constants.USER_ID_TAG, userId);

        dialog.setArguments(bundle);

        return dialog;
    }
}
