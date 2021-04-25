package Threading;

import javafx.concurrent.Task;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SortTask  extends Task<SwapItem> {

    protected final ArrayList<Rectangle> rectanglesArray;
    protected final AtomicBoolean flag;

    public SortTask(ArrayList<Rectangle> rectanglesArray) {
        this.rectanglesArray = rectanglesArray;
        this.flag = new AtomicBoolean(false);
    }

    protected abstract void doSorting();

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

    protected int getValueAt(int index){
        Rectangle rect = rectanglesArray.get(index);
        return Double.valueOf(rect.getHeight()).intValue();
    }
    protected void setValueAt(int index, Integer value){
        Rectangle rect = rectanglesArray.get(index);
        rect.setHeight(value);
    }

    protected void waitOnFlag() {
        flag.set(false);
        long val = System.currentTimeMillis();
        while (!flag.get()) {
            if ((System.currentTimeMillis() - val) > 500) {
                System.out.println("Locked");
                flag.set(true);
            }
        }
    }

    public void setFlag(boolean value) {
        flag.set(value);
    }


}
