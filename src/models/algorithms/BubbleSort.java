package models.algorithms;

import javafx.scene.chart.XYChart;
import models.Session;
import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

public class BubbleSort extends SortTask {

    // this will setup the rectangles Array
    public BubbleSort(XYChart.Series<String, Integer> chartData, Session session) {
        super();
    }

    String borderBottomBlack = "-fx-border-color: transparent transparent Black transparent; -fx-border-width:8;";

    public BubbleSort() {
        super();
    }

    @Override
    protected void doSorting() {
        int n = chartData.getData().size();

        try {
            for (int i = 0; i < n - 1; i++) {
                // as long as the index is less than the length after deducted first number index
                for (int j = 0; j < n - i - 1; j++) {
                    setStyleAt(j, getStyleAt(j) + borderBottomBlack);

                    Thread.sleep(session.getConfig().getSpeedInterval());


                    if (getYvalueAt(j) > getYvalueAt(j + 1)) {
                        System.out.println("sleeping: " + session.getConfig().getSpeedInterval());
                        // talk to event listener in main controller
                        updateValue(new SwapItem(
                                        chartData.getData().get(j).getYValue(), j + 1,
                                        chartData.getData().get(j).getNode().getStyle()
                                )
                        );

                        waitOnFlag();
                    }

                    setStyleAt(j, getStyleAt(j).replaceAll("Black", "transparent"));

                }

            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Runnable getSwapCode(SwapItem swapItem) {
        return () -> {
            try {
                if (swapItem != null) {
                    int left = swapItem.getSwapIndex();
                    int right = left - 1;

                    // Swap
                    setValueAt(right, getYvalueAt(left));
                    setStyleAt(right, getStyleAt(left));

                    setValueAt(left, swapItem.getRawValue());
                    setStyleAt(left, swapItem.getStyle());

                }
                flag.set(true);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        };

    }

}
