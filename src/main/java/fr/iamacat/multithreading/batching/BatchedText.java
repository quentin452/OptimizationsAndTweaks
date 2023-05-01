package fr.iamacat.multithreading.batching;

public class BatchedText {

    public final int x;
    public final int y;
    public final String text;
    public final int color;

    public BatchedText(int x, int y, String text, int color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }
}
