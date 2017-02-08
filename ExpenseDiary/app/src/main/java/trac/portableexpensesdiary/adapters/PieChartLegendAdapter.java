package trac.portableexpensesdiary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import java.util.ArrayList;
import java.util.List;

import trac.portableexpensesdiary.MainActivity;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.utils.RoundUtils;

public class PieChartLegendAdapter extends BaseAdapter {

    private Context context;

    private PieChart pieChart;
    private List<LegendHolder> legendHolderList = new ArrayList<>();
    private List<MainActivity.PieChartDataHolder> pieChartDataHolderList = new ArrayList<>();

    public PieChartLegendAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder viewHolder;

        View view = convertView;

        LegendHolder legendHolder = legendHolderList.get(position);

        if (view == null) {
            view = inflater.inflate(
                    R.layout.pie_chart_legend_item,
                    null
            );

            viewHolder = new ViewHolder();
            viewHolder.labelColorImageView =
                    view.findViewById(R.id.colorImageView);
            viewHolder.labelTxt =
                    (TextView) view.findViewById(R.id.labelTxt);
            viewHolder.pieChartValueTxt =
                    (TextView) view.findViewById(R.id.pieValueTxt);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.labelColorImageView.setBackgroundColor(legendHolder.getColor());
        viewHolder.labelTxt.setText(legendHolder.getLabel());

        for (MainActivity.PieChartDataHolder holder : pieChartDataHolderList) {

            if (Category.getCategoryById(holder.getCategoryId()).getCategoryName()
                    .equals(legendHolder.getLabel())) {

                viewHolder.pieChartValueTxt.setText(
                        RoundUtils.floatToStringWithCurrency(
                                holder.getExpenseDetailPrice()
                        )
                );
            }
        }

        view.setTag(R.layout.pie_chart_legend_item, legendHolder);

        return view;
    }

    @Override
    public int getCount() {
        return legendHolderList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < legendHolderList.size()) {

            return legendHolderList.get(position);
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
        legendHolderList.clear();

        if (pieChart != null) {
            Legend pieChartLegend = pieChart.getLegend();
            pieChartLegend.setEnabled(false);
            int[] pieChartColorsHolder = pieChartLegend.getColors();
            String[] pieChartLabelsHolder = pieChartLegend.getLabels();
            if (pieChartColorsHolder != null) {
                for (int i = 0; i < pieChartLabelsHolder.length; i++) {
                    if (!pieChartLabelsHolder[i].equals("")) {
                        legendHolderList.add(
                                new LegendHolder(
                                        pieChartLabelsHolder[i],
                                        pieChartColorsHolder[i]
                                )
                        );
                    }
                }
            }
        }
    }

    public void setPieChartDataHolderList(List<MainActivity.PieChartDataHolder> pieChartDataHolderList) {
        this.pieChartDataHolderList = pieChartDataHolderList;
    }

    public void setPieChart(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    public void clearData() {
        this.legendHolderList.clear();
        this.pieChartDataHolderList.clear();
    }

    private class LegendHolder {

        private String label;
        private int color;

        LegendHolder(String label, int color) {
            this.label = label;
            this.color = color;
        }

        private String getLabel() {
            return label;
        }

        private int getColor() {
            return color;
        }
    }

    private static class ViewHolder {

        private View labelColorImageView;
        private TextView labelTxt;
        private TextView pieChartValueTxt;

    }
}
