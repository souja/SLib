package org.xutils.http.loader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.souja.lib.utils.MTool;

import org.xutils.cache.DiskCacheEntity;
import org.xutils.common.util.IOUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;

import java.io.InputStream;

/**
 * Author: wyouflf
 * Time: 2014/06/16
 */
class JSONObjectLoader extends Loader<JsonObject> {

    private String charset = "UTF-8";
    private String resultStr = null;

    @Override
    public Loader<JsonObject> newInstance() {
        return new JSONObjectLoader();
    }

    @Override
    public void setParams(final RequestParams params) {
        if (params != null) {
            String charset = params.getCharset();
            if (!MTool.isEmpty(charset)) {
                this.charset = charset;
            }
        }
    }

    @Override
    public JsonObject load(final InputStream in) throws Throwable {
        resultStr = IOUtil.readStr(in, charset);
        return new Gson().fromJson(resultStr, JsonObject.class);
    }

    @Override
    public JsonObject load(final UriRequest request) throws Throwable {
        request.sendRequest();
        return this.load(request.getInputStream());
    }

    @Override
    public JsonObject loadFromCache(final DiskCacheEntity cacheEntity) throws Throwable {
        if (cacheEntity != null) {
            String text = cacheEntity.getTextContent();
            if (!MTool.isEmpty(text)) {
                return new Gson().fromJson(text, JsonObject.class);
            }
        }

        return null;
    }

    @Override
    public void save2Cache(UriRequest request) {
        saveStringCache(request, resultStr);
    }
}
