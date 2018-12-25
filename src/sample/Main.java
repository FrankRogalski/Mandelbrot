package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private Canvas canvas;
    private GraphicsContext gc;
    private PixelWriter pixelWriter;

    private double[] relativeMousePos = {0, 0};

    private double realMidPoint = 0;
    private double imaginaryMidPoint = 0;
    private double difference = 2.5;

    private static final long MAX_VALUE = 1000;
    private double maxIterations = 100;

    private static final double SIDE_SCROLLING = 6;
    private static final double ZOOM_LEVEL = 0.5;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 800);

        primaryStage.setTitle("Mandelbrot Menge");
        primaryStage.setResizable(false);

        canvas = new Canvas(scene.getWidth(), scene.getHeight());
        gc = canvas.getGraphicsContext2D();

        root.setOnMouseMoved(mouseEvent -> {
            relativeMousePos[0] = mouseEvent.getX();
            relativeMousePos[1] = mouseEvent.getY();
        });

        root.setOnScroll(this::scrolled);

        root.getChildren().add(canvas);
        pixelWriter = gc.getPixelWriter();

        primaryStage.setScene(scene);
        primaryStage.show();

        standardMandelBrot();
    }

    private void scrolled(final ScrollEvent scrollEvent) {
        final double realChange = map(
                relativeMousePos[0],
                0, canvas.getWidth(),
                -difference / SIDE_SCROLLING, difference / SIDE_SCROLLING);

        final double imaginaryChange = map(
                relativeMousePos[1],
                0, canvas.getHeight(),
                -difference / SIDE_SCROLLING, difference / SIDE_SCROLLING);

        difference *= 1 - scrollEvent.getDeltaY() * 0.001;

        if (scrollEvent.getDeltaY() > 0) {
            realMidPoint += realChange;
            imaginaryMidPoint += imaginaryChange;
            maxIterations += ZOOM_LEVEL;
        } else {
            realMidPoint -= realChange;
            imaginaryMidPoint -= imaginaryChange;
            maxIterations -= ZOOM_LEVEL;
        }

        standardMandelBrot();
    }

    private void standardMandelBrot() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        madelbrot(realMidPoint - difference,
                realMidPoint + difference,
                imaginaryMidPoint - difference,
                imaginaryMidPoint + difference,
                MAX_VALUE,
                Math.round(maxIterations));
    }

    private void madelbrot(final double realMin, final double realMax, final double imaginaryMin, final double imaginaryMax, final long maxValue, final long maxIterations) {
        for (int y = 0; y < canvas.getHeight(); y++) {
            double cIm = imaginaryMin + (imaginaryMax - imaginaryMin) * y / canvas.getHeight();

            for (int x = 0; x < canvas.getWidth(); x++) {
                double cRe = realMin + (realMax - realMin) * x / canvas.getWidth();
                long iterations = julia(cRe, cIm, cRe, cIm, maxValue, maxIterations);
                Color color = chooseColor(iterations, maxIterations);
                pixelWriter.setColor(x, y, color);
            }
        }
    }

    private long julia(double x, double y, final double xAdd, final double yAdd, final long maxValue, final long maxIterations) {
        double xSquared = x * x;
        double ySquared = y * y;
        double xTimesY = x * y;
        double xSquaredPlusYSquared = xSquared + ySquared;

        long remainingIterations;
        for (remainingIterations = maxIterations; xSquaredPlusYSquared <= maxValue && remainingIterations > 0; remainingIterations--) {
            x = xSquared - ySquared + xAdd;
            y = xTimesY * 2 + yAdd;
            xSquared = x * x;
            ySquared = y * y;
            xTimesY = x * y;
            xSquaredPlusYSquared = xSquared + ySquared;
        }

        return maxIterations - remainingIterations;
    }

    private Color chooseColor(final long iterations, final long maxIterations) {
        final double color = (double) iterations / maxIterations * 255;
        return Color.hsb(color, 1, 1);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static double map(double value, double min, double max, double nMin, double nMax) {
        return ((value - min) / (max - min)) * (nMax - nMin) + nMin;
    }
}
