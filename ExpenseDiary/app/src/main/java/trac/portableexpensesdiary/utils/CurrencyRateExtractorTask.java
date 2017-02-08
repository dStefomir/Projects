package trac.portableexpensesdiary.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.model.Currency;

public class CurrencyRateExtractorTask extends AsyncTask<Void, Void, String> {

    private final String CURRENCIES_RATE_SERVICE_ADDRESS = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private final String ENCODING = "UTF-8";
    private Context context;

    public CurrencyRateExtractorTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(CURRENCIES_RATE_SERVICE_ADDRESS);
        String serviceResponse = null;

        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            serviceResponse = getAsciiContentFromEntity(entity);
        } catch (Exception e) {
            this.cancel(true);
        }

        return serviceResponse;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        new SweetAlertDialog(
                context,
                SweetAlertDialog.ERROR_TYPE
        )
                .setTitleText(context.getString(R.string.error_title))
                .setContentText(context.getString(R.string.currencies_download_err))
                .setConfirmText(context.getString(R.string.yes_btn))
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        }
                )
                .show();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        Map<String, String> currencies =
                new CurrencyExtractorUtils().getExtractedCurrencies(response);
        List<Currency> allCurrencies = Currency.getAllCurrencies();

        for (Currency currency : allCurrencies) {
            currency.setCurrencyRate(currencies.get(currency.getCurrencyName()));
            currency.save();
            System.out.println(currency.getCurrencyName());
        }
    }

    private String getAsciiContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream inputStream = entity.getContent();
        StringWriter writer = new StringWriter();

        IOUtils.copy(
                inputStream,
                writer,
                ENCODING
        );

        return writer.toString();
    }

    private class CurrencyExtractorUtils {

        private final String ROOT_ELEMENT_TAG = "Cube";
        private final String CURRENCY_NAME_TAG = "currency";
        private final String CURRENCY_RATE_TAG = "rate";
        private final String TIME_ATTRIBUTE_TAG = "time";

        private Document getDocument(String response) {
            Document document = null;

            try {
                DocumentBuilderFactory documentBuilderFactory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder =
                        documentBuilderFactory.newDocumentBuilder();
                InputSource inputSource =
                        new InputSource(new StringReader(response));
                document = documentBuilder.parse(inputSource);

            } catch (Exception e) {
                CurrencyRateExtractorTask.this.cancel(true);
            }

            return document;
        }

        private Map<String, String> getExtractedCurrencies(String response) {
            Map<String, String> currencies = new HashMap<>();
            currencies.put("EUR", "1");

            Document document = getDocument(
                    response.replaceAll("\\n", "")
                            .replaceAll("\\t", "")
            );
            document.normalizeDocument();

            NodeList documentAsList =
                    document.getElementsByTagName(ROOT_ELEMENT_TAG);
            Node rootDocumentTag = null;

            for (int i = 0; i < documentAsList.getLength(); i++) {
                Node currentDocumentTag = documentAsList.item(i);

                if (currentDocumentTag.hasAttributes()) {
                    if (currentDocumentTag.getAttributes().getNamedItem(TIME_ATTRIBUTE_TAG) != null) {
                        rootDocumentTag = currentDocumentTag;

                        break;
                    }
                }
            }

            if (rootDocumentTag != null) {
                NodeList list =
                        rootDocumentTag.getChildNodes();
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        currencies.put(
                                getNodeValue(CURRENCY_NAME_TAG, node),
                                getNodeValue(CURRENCY_RATE_TAG, node)
                        );
                    }
                }
            }

            return currencies;
        }

        private String getNodeValue(
                String tag,
                Node rootElement
        ) {

            return rootElement
                    .getAttributes()
                    .getNamedItem(tag)
                    .getNodeValue();
        }
    }
}
