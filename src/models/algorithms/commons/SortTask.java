package models.algorithms.commons;

import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import models.ConfigModel;
import models.Session;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SortTask extends Task<SwapItem> {

    protected final XYChart.Series<String, Integer> chartData;
    protected final AtomicBoolean flag;
    protected long comparisonCount = 0;
    protected Session session;

    public SortTask(XYChart.Series<String, Integer> chartData, Session session) {
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

    public String getName(){
        String Regex = "(\\.)(\\w+)(\\.)(\\w+)";
        Pattern pattern = Pattern.compile(Regex);
        Matcher matcher = pattern.matcher(getClass().getName());
        if ( matcher.find() ){
            String name = matcher.group(4);
            System.out.println(name);
            return name;
        }
        return "";
    }

    public void setFlag(boolean value) {
        flag.set(value);
    }

}
