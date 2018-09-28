package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private Canvas canvas;
    private GraphicsContext gc;
    private PixelWriter pixelWriter;

    private  double[] relativeMousePos = {0, 0};

    private double realMin = -2.5;
    private double realMax = 1.5;

    private double imaginaryMin = -2;
    private double imaginaryMax = 2;

    private double maxValue2 = 1000;
    private int maxIterations = 100;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 800);

        primaryStage.setTitle("Mandelbrot Menge");
        primaryStage.setResizable(false);

        canvas = new Canvas(scene.getWidth(), scene.getHeight());
        gc = canvas.getGraphicsContext2D();

        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                relativeMousePos[0] = event.getX() / canvas.getWidth();
                relativeMousePos[1] = event.getY() / canvas.getHeight();
            }
        });

        root.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(final ScrollEvent event) {
                final double change = 1 - event.getDeltaY() * 0.001;

                if (event.getDeltaY() < 0) {               // Zoom out
                    realMin *= change;
                    realMax *= change;
                    imaginaryMin *= change;
                    imaginaryMax *= change;
                } else {                        // Zoom in
                    realMin *= change;
                    realMax *= change;
                    imaginaryMin *= change;
                    imaginaryMax *= change;
                }
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                apfel(realMin, realMax, imaginaryMin, imaginaryMax, maxValue2, maxIterations);
            }
        });

        root.getChildren().add(canvas);
        pixelWriter = gc.getPixelWriter();

        primaryStage.setScene(scene);
        primaryStage.show();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        apfel(realMin, realMax, imaginaryMin, imaginaryMax, maxValue2, maxIterations);
    }

    private void apfel(final double realMin, final double realMax, final double imaginaryMin, final double imaginaryMax, final double maxValue2, final int maxIterations) {
        for (int y = 0; y < canvas.getHeight(); y++) {
            double cIm = imaginaryMin + (imaginaryMax - imaginaryMin) * y / canvas.getHeight();

            for (int x = 0; x < canvas.getWidth(); x++) {
                double cRe = realMin + (realMax - realMin) * x / canvas.getWidth();
                int iterations = julia(cRe, cIm, cRe, cIm, maxValue2, maxIterations);
                Color color = chooseColor(iterations, maxIterations);
                pixelWriter.setColor(x, y, color);
            }
        }
    }

    private int julia(double x, double y, final double xAdd, final double yAdd, final double maxBetrag2, final int maxIterations) {
        double xSquared = x * x;
        double ySquared = y * y;
        double xTimesY = x * y;
        double xSquaredPlusYSquared = xSquared + ySquared;

        int remainingIterations;
        for (remainingIterations = maxIterations; xSquaredPlusYSquared <= maxBetrag2 && remainingIterations > 0; remainingIterations--) {
            x = xSquared - ySquared + xAdd;
            y = xTimesY * 2 + yAdd;
            xSquared = x*x;
            ySquared = y*y;
            xTimesY = x*y;
            xSquaredPlusYSquared = xSquared + ySquared;
        }

        return maxIterations - remainingIterations;
    }

    private Color chooseColor(final int iterations, final int maxIterations) {
        final double color = (double)iterations / maxIterations * 255;
        return Color.hsb(color, 1, 1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
