package models.algorithms;

import javafx.scene.chart.XYChart;
import models.Session;
import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

public class InsertionSort extends SortTask {
    public InsertionSort(XYChart.Series<String, Integer> chartData, Session session) {
        super();
    }

    @Override
    public Runnable getSwapCode(SwapItem swapItem) {
        return null;
    }

    @Override
    protected void doSorting() throws InterruptedException {

    }
}
