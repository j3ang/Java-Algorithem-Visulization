package models.algorithms.commons;

import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import models.Session;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SortTask extends Task<SwapItem> {

    protected XYChart.Series<String, Integer> chartData;
    protected Session session;
    protected final AtomicBoolean flag;


    public SortTask() {
        this.chartData = chartData;
        this.session = session;
        this.flag = new AtomicBoolean(false);
    }

    @Override
    protected SwapItem call() throws Exception {
        try {
            doSorting();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }

    public abstract Runnable getSwapCode(SwapItem swapItem);

    protected int getYvalueAt(int index) {
        return chartData.getData().get(index).getYValue();
    }
    protected void setValueAt(int index, int value) {
        chartData.getData().get(index).setYValue(value);
    }

    protected String getStyleAt(int index){
        return chartData.getData().get(index).getNode().getStyle();
    }
    protected void setStyleAt(int index, String style){
        chartData.getData().get(index).getNode().setStyle(style);
    }


    protected abstract void doSorting() throws InterruptedException;

    protected void waitOnFlag() throws InterruptedException {
        flag.set(false);
        long val = System.currentTimeMillis();
        while (!flag.get()) {
            if ((System.currentTimeMillis() - val) > 500) {
                flag.set(true);
            }
        }
    }


    public void setFlag(boolean value) {
        flag.set(value);
    }

    public SortTask setChartData(XYChart.Series<String, Integer> chartData) {
        this.chartData = chartData;
        return this;
    }

    public XYChart.Series<String, Integer> getChartData() {
        return chartData;
    }

    public Session getSession() {
        return session;
    }

    public SortTask setSession(Session session) {
        this.session = session;
        return this;
    }
}
