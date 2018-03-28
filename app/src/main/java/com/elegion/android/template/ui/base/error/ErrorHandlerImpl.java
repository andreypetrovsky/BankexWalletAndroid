package com.elegion.android.template.ui.base.error;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.elegion.android.template.data.model.ErrorBean;
import com.elegion.android.template.ui.base.view.ErrorView;
import com.elegion.android.template.ui.base.view.NoInternetStubView;
import com.elegion.android.template.util.GsonUtils;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Consumer;
import retrofit2.HttpException;
import timber.log.Timber;

public class ErrorHandlerImpl {
    protected static final List<Class<?>> NETWORK_EXCEPTIONS = Arrays.asList(
            UnknownHostException.class,
            SocketTimeoutException.class,
            ConnectException.class
    );

    private ErrorView mErrorView;
    private NoInternetStubView mNoInternetStubView;

    public static ErrorHandlerImpl create(@NonNull ErrorView errorView, @Nullable NoInternetStubView noInternetStubView) {
        return new ErrorHandlerImpl(errorView, noInternetStubView);
    }

    protected ErrorHandlerImpl(@NonNull ErrorView errorView, @Nullable NoInternetStubView noInternetStubView) {
        this(errorView);
        mNoInternetStubView = noInternetStubView;
    }

    protected ErrorHandlerImpl(@NonNull ErrorView errorView) {
        mErrorView = errorView;
    }

    @NonNull
    public final <T> FlowableTransformer<T, T> noInternetTransformer() {
        return observable -> observable
                .doOnSubscribe(disposable -> {
                    if (mNoInternetStubView != null) {
                        mNoInternetStubView.hideNoInternetStub();
                    }
                })
                .doOnError(error());
    }

    @NonNull
    public Consumer<Throwable> error() {
        return e -> {
            Timber.d(e, "from ErrorHandlerImpl.error");
            handleError(e);
        };
    }

    private void handleError(Throwable e) {
        if (e instanceof HttpException) {
            final HttpException httpException = (HttpException) e;
            try {
                final String errorBody = httpException.response().errorBody().string();
                final ErrorBean errorBean = GsonUtils.requestGson().fromJson(errorBody, ErrorBean.class);
                if (errorBean != null) {
                    handleProtocolError(errorBean, httpException);
                } else {
                    handleNonProtocolError(httpException);
                }
            } catch (IOException | IllegalStateException | JsonSyntaxException e1) {
                handleNonProtocolError(httpException);
            }
        } else if (NETWORK_EXCEPTIONS.contains(e.getClass())) {
            if (null == mNoInternetStubView) {
                handleNetworkError(e);
            }
        } else {
            handleUnexpectedError(e);
        }
    }

    protected void handleProtocolError(ErrorBean errorBean, HttpException httpException) {
        mErrorView.showErrorMessage(errorBean != null ? errorBean.getMessage() : httpException.message());
    }

    protected void handleNonProtocolError(HttpException httpException) {
        mErrorView.showErrorMessage(httpException.message());
    }

    protected void handleNetworkError(Throwable e) {
        mErrorView.showNetworkError();
    }

    protected void handleUnexpectedError(Throwable e) {
        mErrorView.showUnexpectedError();
    }
}
