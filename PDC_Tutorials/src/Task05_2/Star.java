package Task05_2;

public class Star implements Runnable {

    private Figure fig;
    private int max;

    public Star(Figure fig) {
        this.fig = fig;
        this.max = 9;
    }

    @Override
    public void run() {
        for (int i = 1; i <= max; i++) {
            fig.printStar(i);
        }
    }
}
