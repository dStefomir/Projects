package trac.portableexpensesdiary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.model.Currency;

public class CurrencyRegisterAdapter extends BaseAdapter {

    private List<Currency> currencyList = new ArrayList<>();
    private List<Currency> filteredCurrencyList = new ArrayList<>();
    private Context context;

    public CurrencyRegisterAdapter(Context context) {
        this.context = context;
        loadData();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(
                    R.layout.currency_register_item,
                    null
            );

            holder = new ViewHolder();

            holder.currencyImage =
                    (ImageView) view.findViewById(R.id.currencyImage);
            holder.currencyName =
                    (TextView) view.findViewById(R.id.currencyName);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Currency currencyItem = currencyList.get(position);

        view.setTag(
                R.layout.currency_register_item,
                currencyItem
        );

        holder.currencyImage.setImageResource(
                currencyItem.getCurrencyPictureId()
        );
        holder.currencyName.setText(
                currencyItem.getCurrencyName()
        );

        return view;
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < currencyList.size()) {

            return currencyList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        loadData();

        super.notifyDataSetChanged();
    }

    private void loadData() {
        List<Currency> currencies = Currency.getAllCurrencies();

        if(!filteredCurrencyList.isEmpty()) {
            this.currencyList = filteredCurrencyList;
        } else if (!currencies.isEmpty()) {
            this.currencyList = currencies;
        } else {
            this.currencyList = getData();
        }
    }

    private List<Currency> getData() {
        List<Currency> currencyList = Currency.getAllCurrencies();

        if (currencyList.isEmpty()) {
            currencyList.add(
                    getCurrency(
                            R.drawable.europeanunion,
                            "EUR"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.australia,
                            "AUD"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.brazil,
                            "BRL"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.bulgaria,
                            "BGN"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.canada,
                            "CAD"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.china,
                            "CNY"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.cookislands,
                            "NZD"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.croatia,
                            "HRK"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.czechrepublic,
                            "CZK"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.denmark,
                            "DKK"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.hungary,
                            "HUF"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.india,
                            "INR"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.indonezia,
                            "IDR"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.israel,
                            "ILS")
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.japan,
                            "JPY"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.malaysia,
                            "MYR"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.mexico,
                            "MXN"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.norway,
                            "NOK"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.philippines,
                            "PHP"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.poland,
                            "PLN"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.russianfederation,
                            "RUB"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.singapore,
                            "SGD"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.southafrica,
                            "ZAR"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.sweden,
                            "SEK"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.switzerland,
                            "CHF"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.thailand,
                            "THB"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.turkey,
                            "TRY"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.unitedkingdom,
                            "GBP"
                    )
            );
            currencyList.add(
                    getCurrency(
                            R.drawable.unitedstatesofamerica,
                            "USD"
                    )
            );
        }

        return currencyList;
    }

    private Currency getCurrency(
            int pictureId,
            String name
    ) {
        Currency currency = new Currency();

        currency.setCurrencyPictureId(pictureId);
        currency.setCurrencyName(name);
        currency.save();

        return currency;
    }

    public void setFilteredCurrencyList(List<Currency> filteredCurrencyList) {
        this.filteredCurrencyList = filteredCurrencyList;
    }

    private static class ViewHolder {

        private ImageView currencyImage;
        private TextView currencyName;
    }
}
