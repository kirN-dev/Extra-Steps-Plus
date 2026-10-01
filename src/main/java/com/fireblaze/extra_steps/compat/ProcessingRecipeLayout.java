package com.fireblaze.extra_steps.compat;

/** Category dimensions and positions relative to a centered processing arrow. */
public final class ProcessingRecipeLayout {
    private static final int COLUMNS = 3;
    private static final int ITEM_SIZE = 16;
    private static final int SLOT_STEP = 18;
    private static final int ROW_STEP = 20;
    private static final int ARROW_WIDTH = 24;
    private static final int GAP = 4;
    private final int width;
    private final int height;

    public ProcessingRecipeLayout(int inputs, int outputs, boolean hasTool) {
        int count = Math.max(1, Math.max(inputs, outputs));
        int columns = Math.min(COLUMNS, count);
        int halfWidth = ARROW_WIDTH / 2 + GAP + ITEM_SIZE + (columns - 1) * SLOT_STEP + 4;
        width = Math.max(120, halfWidth * 2);
        int rows = (count + COLUMNS - 1) / COLUMNS;
        int itemsBottom = arrowY() + (rows - 1) * ROW_STEP + ITEM_SIZE + 4;
        height = Math.max(hasTool ? 64 : 54, itemsBottom);
    }

    public int width() { return width; }
    public int height() { return height; }
    public int arrowX() { return (width - ARROW_WIDTH) / 2; }
    public int arrowY() { return 24; }
    public int equipmentX() { return (width - ITEM_SIZE) / 2; }
    public int equipmentY() { return 2; }
    public int toolY() { return 44; }

    public int inputX(int index, int inputCount) {
        int rowStart = index / COLUMNS * COLUMNS;
        int rowCount = Math.min(COLUMNS, inputCount - rowStart);
        return arrowX() - GAP - ITEM_SIZE - (rowCount - 1 - index % COLUMNS) * SLOT_STEP;
    }
    public int outputX(int index) { return arrowX() + ARROW_WIDTH + GAP + index % COLUMNS * SLOT_STEP; }
    public int slotY(int index) { return arrowY() + index / COLUMNS * ROW_STEP; }
}
