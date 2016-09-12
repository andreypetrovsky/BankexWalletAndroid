package com.elegion.android.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elegion.android.R;
import com.elegion.android.model.GroupInfo;
import com.elegion.android.presenter.MainPresenter;
import com.elegion.android.rx.RxError;
import com.elegion.android.util.ToolbarUtil;
import com.elegion.android.view.LoadingView;
import com.elegion.android.view.MainView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.arturvasilov.rxloader.LifecycleHandler;
import ru.arturvasilov.rxloader.LoaderLifecycleHandler;
import ru.arturvasilov.rxloader.RxSchedulers;
import ru.elegion.rxloadermanager.RxLoaderManager;
import rx.Observable;

/**
 * @author Nikita Bumakov
 */
public class MainActivity extends AppCompatActivity implements MainView, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tv_description)
    TextView mDescriptionText;

    @Bind(R.id.iv_logo)
    ImageView mLogoImageView;

    @Bind(R.id.empty_stub)
    View mEmptyStub;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private MainPresenter mPresenter;

    private final LoadingView mLoadingView = new LoadingView() {
        @Override
        public void showLoadingIndicator() {
            mRefreshLayout.setRefreshing(true);
        }

        @Override
        public void hideLoadingIndicator() {
            mRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        ButterKnife.bind(this);
        ToolbarUtil.setupToolbar(this);
        mRefreshLayout.setOnRefreshListener(this);
        mPresenter = new MainPresenter(this, this, mLoadingView, RxError.view(this), RxLoaderManager.get(this), getSupportLoaderManager());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.dispatchStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.dispatchStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.dispatchDestroy();
    }

    @OnClick(R.id.bt_refresh)
    public void onRefreshClick() {
        mPresenter.refresh();
    }

    @Override
    public void showInfo(@NonNull GroupInfo groupInfo) {
        setTitle(groupInfo.getName());
        setDescription(groupInfo.getDescription());
        setLogo(groupInfo.getPhoto());
    }

    private void setDescription(@Nullable String description) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(description);
        }
        mDescriptionText.setText(result);
    }

    private void setLogo(@Nullable String url) {
        Glide.with(this).load(url)
                .placeholder(new ColorDrawable(ContextCompat.getColor(this, R.color.material_gray_100)))
                .into(mLogoImageView);
    }

    @Override
    public void showEmptyStub() {
        mEmptyStub.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyStub() {
        mEmptyStub.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }
}
