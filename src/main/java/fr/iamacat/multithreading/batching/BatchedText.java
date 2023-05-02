package fr.iamacat.multithreading.batching;

public class BatchedText {

    public final int x;
    public final int y;
    public final String text;
    public final int color;
    public int backgroundColor; // add this line

    public BatchedText(String text, int x, int y, int color, int backgroundColor) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.backgroundColor = backgroundColor; // initialize the field
    }
}
