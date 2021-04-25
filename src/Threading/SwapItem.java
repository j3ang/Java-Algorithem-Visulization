package Threading;

import javafx.scene.chart.XYChart;

public class SwapItem {

    private XYChart.Data<String, Integer> itemToSwap;
    private final int swapIndex;
    private int rawValue;


    public SwapItem(int rawValue, int swapIndex) {
        this.rawValue = rawValue;
        this.swapIndex = swapIndex;
    }



    public int getRawValue() {
        return rawValue;
    }

    public void setRawValue(int rawValue) {
        this.rawValue = rawValue;
    }

    public XYChart.Data<String, Integer> getItemToSwap() {
        return itemToSwap;
    }

    public int getSwapIndex() {
        return swapIndex;
    }
}
