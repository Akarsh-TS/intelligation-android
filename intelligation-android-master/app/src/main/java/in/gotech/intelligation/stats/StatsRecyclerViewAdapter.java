package in.gotech.intelligation.stats;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

import in.gotech.intelligation.R;

/**
 * Created by anirudh on 16/06/16.
 */
public class StatsRecyclerViewAdapter extends RecyclerView.Adapter<StatsRecyclerViewAdapter.StatsCardViewHolder> {
    private Context mContext;
    private ArrayList<SensorStatsData> mSensorTimeSeriesList;

    public StatsRecyclerViewAdapter(Context context, ArrayList<SensorStatsData> sensorStatDataList) {
        mSensorTimeSeriesList = sensorStatDataList;
        mContext = context;
    }
    @Override
    public StatsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_card, parent, false);
        StatsCardViewHolder vh = new StatsCardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StatsCardViewHolder holder, int position) {
        holder.mDataset.clear();
        holder.mDataset.addSeries(mSensorTimeSeriesList.get(position).sensorTimeSeries);
        holder.cropName.setText(mSensorTimeSeriesList.get(position).cropName);
    }

    @Override
    public int getItemCount() {
        return mSensorTimeSeriesList.size();
    }

    public class StatsCardViewHolder extends RecyclerView.ViewHolder {
        private TextView cropName;
        private XYMultipleSeriesDataset mDataset;
        private XYMultipleSeriesRenderer mRenderer;
        private GraphicalView mChartView;
        private TimeSeries time_series;
        private LinearLayout mStatsCardLinearLayout;
        public StatsCardViewHolder(ViewGroup statsCardView) {
            super(statsCardView);
            mStatsCardLinearLayout = (LinearLayout) statsCardView.findViewById(R.id.linear_layout_stats_card);
            cropName = (TextView) statsCardView.findViewById(R.id.text_view_crop_name);
            // create dataset and renderer
            mDataset = new XYMultipleSeriesDataset();
            mRenderer = new XYMultipleSeriesRenderer();
            mRenderer.setAxisTitleTextSize(16);
            mRenderer.setChartTitleTextSize(20);
            mRenderer.setLabelsTextSize(25);
            mRenderer.setPointSize(2f);
            mRenderer.setApplyBackgroundColor(true);
            mRenderer.setBackgroundColor(Color.WHITE);
            mRenderer.setMarginsColor(Color.WHITE);
            mRenderer.setShowLegend(false);
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(Color.BLACK);
            r.setPointStyle(PointStyle.CIRCLE);
            r.setFillPoints(true);
            mRenderer.addSeriesRenderer(r);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(20);
            mRenderer.setPanEnabled(true);
            mRenderer.setZoomEnabled(true, true);
            mRenderer.setZoomButtonsVisible(true);
            mRenderer.setMargins(new int[] {30, 50, 70, 30});
            mRenderer.setYLabelsPadding(30f);
            time_series = new TimeSeries("test");

            mDataset.addSeries(time_series);

            mChartView = ChartFactory.getTimeChartView(mContext, mDataset, mRenderer,
                    "H:mm:ss");
            mChartView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                }
            });

            mChartView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650));
            mChartView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
            mStatsCardLinearLayout.addView(mChartView);
        }
    }
}
