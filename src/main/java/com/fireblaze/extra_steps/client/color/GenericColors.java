package com.fireblaze.extra_steps.client.color;

public enum GenericColors {

    WHITE(0xFFFFFF),
    RED(0xB02E26),
    BLUE(0x3C44AA),
    GREEN(0x4B6311),
    YELLOW(0xFFE000),
    BLACK(0x0F0F0F),
    ORANGE(0xF9801D),
    PURPLE(0x8932B8),
    PINK(0xF38BAA),
    CYAN(0x169C9C),
    LIGHT_BLUE(0x3AB3DA),
    LIME(0x80C71F),
    MAGENTA(0xC74EBD),
    BROWN(0x5C3A21),
    GRAY(0x474F52),
    LIGHT_GRAY(0x9D9D97);

    private final int color;

    GenericColors(int color) {
        this.color = color;
    }

    public int getGenericColor() {
        return color;
    }
}