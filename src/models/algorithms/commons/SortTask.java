package models.algorithms.commons;

import application.Main;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import models.ActivityModel;
import models.Session;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SortTask extends Task<SwapItem> {

    protected XYChart.Series<String, Integer> chartData;
    protected Session session;
    protected final AtomicBoolean flag;
    protected Stopwatch stopwatch;
	protected String borderBottomBlack;

	public SortTask() {
        this.chartData = chartData;
        this.session = session;
        this.flag = new AtomicBoolean(false);
        this.stopwatch = stopwatch;
		this.borderBottomBlack = "-fx-border-color: transparent transparent Black transparent; -fx-border-width:8;";
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

    // Save to database
    public void saveActivity(){
        session.setDuration(stopwatch.elapsed(TimeUnit.SECONDS) + " sec");
        // Create new HistoryModel object
        String logJsonString = "{" +
                "\"user_id\":"+ Main.userModelLoggedIn.getUser_id()  +"," +
                "\"act_sort_task\":\""+  session.getConfig().getAlgorithmSelected()  +"\"," +
                "\"act_sort_size\":" + session.getGeneratedNumbers().length + "," +
                "\"act_sort_speed\":\"" + session.getConfig().getSpeedInterval() + " millisec\"" + "," +
                "\"act_sort_duration\":\"" + session.getDuration() +"\""
                + "}";

        // Gson helper
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        ActivityModel log = gson.fromJson(logJsonString, ActivityModel.class); // Activity object created form Json

        // Print to console
        logJsonString = gson.toJson(log);
        System.out.println(logJsonString);

        // Save to database
        System.out.println("Saving activity...");
        log.save();
    }
}
