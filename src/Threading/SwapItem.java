package Threading;

import javafx.scene.shape.Rectangle;

public class SwapItem {
    private Rectangle rectToSwap;
    private int originalPos;
    private int secondPos;
    private int value;


    // Set original index, and have the painted rectangle array
    public SwapItem(Rectangle rectToSwap, int originalPos) {
        this.rectToSwap = rectToSwap;
        this.originalPos = originalPos;
    }

    public Rectangle getRectToSwap() {
        return rectToSwap;
    }

    public SwapItem setRectToSwap(Rectangle rectToSwap) {
        Rectangle thisRectToSwap = rectToSwap;
        this.rectToSwap = thisRectToSwap;
        return this;
    }

    public int getOriginalPos() {
        return originalPos;
    }

    public SwapItem setOriginalPos(int originalPos) {
        this.originalPos = originalPos;
        return this;
    }

    public int getSecondPos() {
        return secondPos;
    }

    public SwapItem setSecondPos(int secondPos) {
        this.secondPos = secondPos;
        return this;
    }

    public int getValue() {
        return value;
    }

    public SwapItem setValue(int value) {
        this.value = value;
        return this;
    }
}
