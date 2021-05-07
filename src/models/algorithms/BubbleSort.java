package models.algorithms;

import application.Main;
import com.google.common.base.Stopwatch;
import controllers.MainController;
import javafx.scene.chart.XYChart;
import models.Session;
import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

import java.util.concurrent.TimeUnit;

public class BubbleSort extends SortTask {

	// Constructors
    // this will setup the rectangles Array
	public BubbleSort() {
		super();
	}
    public BubbleSort(XYChart.Series<String, Integer> chartData, Session session) {
        super();
    }

    @Override
    protected void doSorting() {
        int n = chartData.getData().size();
        try {
            stopwatch = Stopwatch.createStarted();// Print to console
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

            // Stop stopwatch
            stopwatch.stop();
            updateMessage( "Time Elapsed: " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds" );
            saveActivity();
			session.getConfig().setSorted(true); // trigger main view for when switch the algorithms

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
