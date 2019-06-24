package org.xutils.common;

import java.lang.reflect.Type;

/**
 * Created by wyouflf on 15/6/5.
 * 通用回调接口
 */
public interface Callback {

    interface CommonCallback<ResultType> extends Callback {
        void onSuccess(ResultType result);

        void onError(Throwable ex, boolean isOnCallback);

        void onCancelled(CancelledException cex);

        void onFinished();
    }

    interface TypedCallback<ResultType> extends CommonCallback<ResultType> {
        Type getLoadType();
    }

    interface CacheCallback<ResultType> extends CommonCallback<ResultType> {
        boolean onCache(ResultType result);
    }

    interface ProxyCacheCallback<ResultType> extends CacheCallback<ResultType> {
        boolean onlyCache();
    }

    interface PrepareCallback<PrepareType, ResultType> extends CommonCallback<ResultType> {
        ResultType prepare(PrepareType rawData);
    }

    interface ProgressCallback<ResultType> extends CommonCallback<ResultType> {
        void onWaiting();

        void onStarted();

        void onLoading(long total, long current, boolean isDownloading);
    }

    interface GroupCallback<ItemType> extends Callback {
        void onSuccess(ItemType item);

        void onError(ItemType item, Throwable ex, boolean isOnCallback);

        void onCancelled(ItemType item, CancelledException cex);

        void onFinished(ItemType item);

        void onAllFinished();
    }

//    interface Callable<ResultType> {
//        void call(ResultType result);
//    }

    interface Cancelable {
        void cancel();

        boolean isCancelled();
    }

    class CancelledException extends RuntimeException {
        public CancelledException(String detailMessage) {
            super(detailMessage);
        }
    }
}
