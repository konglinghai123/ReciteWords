package com.kong.recitewords;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.DialogWrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by sky on 16/5/18.
 */
public class RequestRunnable implements Runnable {
    private static final String HOST = "fanyi.youdao.com";
    private static final String PATH = "/openapi.do";
    private static final String PARAM_KEY_FROM = "keyfrom";
    private static final String PARAM_KEY = "key";
    private static final String PARAM_TYPE = "type";
    private static final String TYPE = "data";
    private static final String PARAM_DOC_TYPE = "doctype";
    private static final String DOC_TYPE = "json";
    private static final String PARAM_CALL_BACK = "callback";
    private static final String CALL_BACK = "show";
    private static final String PARAM_VERSION = "version";
    private static final String VERSION = "1.1";
    private static final String PARAM_QUERY = "q";
    //replace your own key, see http://fanyi.youdao.com/openapi?path=data-mode
    private static final String KEY_FROM = "Skykai521";
    private static final String KEY = "977124034";
    private Editor mEditor;
    private String mQuery;
    private final String basePath;

    public RequestRunnable(Editor editor, String query, String basePath) {
        this.mEditor = editor;
        this.mQuery = query;
        this.basePath = basePath;
    }

    public void run() {
        try {
            URI uri = createTranslationURI(mQuery);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000).build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity resEntity = response.getEntity();
                String json = EntityUtils.toString(resEntity, "UTF-8");
                Gson gson = new Gson();
                Translation translation = gson.fromJson(json, Translation.class);
                //show result
                showPopupBalloon(translation.toString(), translation);

            } else {
                showPopupBalloon(response.getStatusLine().getReasonPhrase(), new Translation());
            }
        } catch (IOException e) {
            showPopupBalloon(e.getMessage(), new Translation());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void showPopupBalloon(final String result, final Translation translation) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                DialogWrapper dialogWrapper = new HtmlDialog(mEditor, result, new HtmlDialog.SaveAction() {
                    @Override
                    public void save() {
                        Logger.info(translation.toString());
                        if (!MarkDownProcessing.isSave(basePath, translation.getQuery())) {
                            MarkDownProcessing.saveWords(translation.getQuery(), translation.toString(), basePath);
                        }
                    }
                });
                dialogWrapper.show();
            }
        });

    }


    private URI createTranslationURI(String query) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPath(PATH)
                .addParameter(PARAM_KEY_FROM, KEY_FROM)
                .addParameter(PARAM_KEY, KEY)
                .addParameter(PARAM_TYPE, TYPE)
                .addParameter(PARAM_VERSION, VERSION)
                .addParameter(PARAM_DOC_TYPE, DOC_TYPE)
                .addParameter(PARAM_CALL_BACK, CALL_BACK)
                .addParameter(PARAM_QUERY, query);
        return builder.build();
    }
}
