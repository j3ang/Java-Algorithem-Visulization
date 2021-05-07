package models.algorithms;

import com.google.common.base.Stopwatch;
import javafx.scene.chart.XYChart;
import models.Session;
import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

import java.util.concurrent.TimeUnit;

public class InsertionSort extends SortTask {

	// Constructors
	public InsertionSort(){ super();}
    public InsertionSort(XYChart.Series<String, Integer> chartData, Session session) { super(); }

    @Override
    protected void doSorting() throws InterruptedException {

		int n = chartData.getData().size();
		stopwatch = Stopwatch.createStarted();// Print to console
		for (int i = 1; i < n; ++i ) {


			int keyValue = getYvalueAt(i);
			String keyStyle = getStyleAt(i);
			int j = i - 1;

			while ( j >= 0 && getYvalueAt(j) > keyValue ){
				setStyleAt(j, getStyleAt(j).replace("Black", "Transparent"));

				Thread.sleep(session.getConfig().getSpeedInterval());
				SwapItem swapItem = new SwapItem(
				chartData.getData().get(j).getYValue(), j+1,
				chartData.getData().get(j).getNode().getStyle()
				);
				updateValue(swapItem);


				// Delay
				System.out.println("Sleeing for " + session.getConfig().getSpeedInterval() );

				j = j - 1;  // move to left

				waitOnFlag();
			}


			setValueAt(j+1, keyValue);
			setStyleAt(j+1, keyStyle);

		}

		// Update message to view
		updateMessage( "Time Elapsed: " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds" );
		saveActivity();// Save to database
		session.getConfig().setSorted(true); // trigger main view for when switch the algorithms
    }


	@Override
	public Runnable getSwapCode(SwapItem swapItem) {
		return ()->{
			try{
				if (swapItem != null){
					int swapIndex =  swapItem.getSwapIndex();

					// swap
					setValueAt(swapIndex, swapItem.getRawValue());
					setStyleAt(swapIndex, swapItem.getStyle());
				}
				flag.set(true);
			} catch (Exception exp){
				exp.printStackTrace();
			}
		};
	}

}
