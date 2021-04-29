package models.algorithms;

import models.algorithms.commons.SortTask;
import models.algorithms.commons.SwapItem;

public class SelectionSort extends SortTask {
    public SelectionSort() {
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
