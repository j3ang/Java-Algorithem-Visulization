package models.algorithms;

import Threading.SortTask;
import Threading.SwapItem;
import javafx.scene.chart.XYChart;

import java.util.Arrays;

public class BubbleSort extends SortTask {

    // this will setup the rectangles Array
    public BubbleSort(XYChart.Series<String, Integer> chartData) {
        super(chartData);
    }


    @Override
    protected void doSorting() {
        int n = chartData.getData().size();
        boolean swapped;

        for ( int i = 0; i < n - 1; i++ ){
            // move the number to be compared through the array length
            // as long as the index is less than the length after deducted first number index
            for (int j = 0; j < n - i - 1; j++){
                waitOnFlag();
                if (  getValueAt(j) >  getValueAt(j+1)  ){
                    updateValue(new SwapItem(chartData.getData().get(j).getYValue(), j+1));    // talk to event listener in main controller

                }
            }

        }
    }

    @Override
    public Runnable getSwapCode(SwapItem swapItem) {

        return ()->{ // returns runnable
            try {

                if(swapItem!=null){
                    // temp
                    int i = swapItem.getSwapIndex(); //  value at left
                   setValueAt(i-1, getValueAt(i));
                   setValueAt(i, swapItem.getRawValue());

                }

                flag.set(true);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        };

    }

}
