package models.algorithms.commons;

import javafx.scene.chart.XYChart;

public class SwapItem {

    private XYChart.Data<String, Integer> itemToSwap;
    private final int swapIndex;
    private int rawValue;
    private final String style;


    public SwapItem(int rawValue, int swapIndex, String style) {
        this.rawValue = rawValue;
        this.swapIndex = swapIndex;
        this.style = style;
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

    public String getStyle() {
        return style;
    }
}
