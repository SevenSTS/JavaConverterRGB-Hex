package fr.seven;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ColorPickerApp extends Application {
    private static final int SQUARE_SIZE = 300;
    private static final int CURSOR_RADIUS = 6;

    private final Canvas colorCanvas = new Canvas(SQUARE_SIZE, SQUARE_SIZE);
    private final Circle cursor = new Circle(CURSOR_RADIUS, Color.TRANSPARENT);
    private final Label rgbLabel = new Label();

    @Override
    public void start(Stage stage) {
        cursor.setStroke(Color.WHITE);
        cursor.setStrokeWidth(2);

        drawColorSquare();

        StackPane squarePane = new StackPane(colorCanvas, cursor);
        squarePane.setPadding(new Insets(12));
        StackPane.setAlignment(cursor, Pos.TOP_LEFT);

        squarePane.setOnMousePressed(this::handleMouseSelection);
        squarePane.setOnMouseDragged(this::handleMouseSelection);

        Button showHexButton = new Button("Afficher Hex");
        showHexButton.setOnAction(e -> {
            Color c = getColorAt(cursor.getTranslateX(), cursor.getTranslateY());
            String hex = String.format("#%02X%02X%02X",
                    (int) Math.round(c.getRed() * 255),
                    (int) Math.round(c.getGreen() * 255),
                    (int) Math.round(c.getBlue() * 255));

            Alert alert = new Alert(Alert.AlertType.NONE, "Code Hex: " + hex, ButtonType.OK);
            alert.setTitle("Code Couleur");
            alert.setHeaderText(null);
            alert.show();
        });

        HBox bottomBar = new HBox(12, new Label("RGB:"), rgbLabel, showHexButton);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(12));

        BorderPane root = new BorderPane(squarePane);
        root.setBottom(bottomBar);

        updateSelection(SQUARE_SIZE * 0.75, SQUARE_SIZE * 0.25);

        stage.setTitle("Choix de couleur (carré)");
        stage.setScene(new Scene(root));
        stage.show();
    }


    private void handleMouseSelection(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            updateSelection(event.getX(), event.getY());
        }
    }


    private void updateSelection(double x, double y) {
        double clampedX = clamp(x - CURSOR_RADIUS, 0, SQUARE_SIZE - 1);
        double clampedY = clamp(y - CURSOR_RADIUS, 0, SQUARE_SIZE - 1);

        cursor.setTranslateX(clampedX);
        cursor.setTranslateY(clampedY);

        Color c = getColorAt(clampedX, clampedY);
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        rgbLabel.setText(String.format("%d, %d, %d", r, g, b));
    }


    private void drawColorSquare() {
        GraphicsContext gc = colorCanvas.getGraphicsContext2D();
        for (int y = 0; y < SQUARE_SIZE; y++) {
            for (int x = 0; x < SQUARE_SIZE; x++) {
                double hue = (x / (double) (SQUARE_SIZE - 1)) * 360.0;
                double saturation = y / (double) (SQUARE_SIZE - 1);
                gc.getPixelWriter().setColor(x, y, Color.hsb(hue, saturation, 1.0));
            }
        }
    }


    private Color getColorAt(double x, double y) {
        double hue = (clamp(x, 0, SQUARE_SIZE - 1) / (double) (SQUARE_SIZE - 1)) * 360.0;
        double saturation = clamp(y, 0, SQUARE_SIZE - 1) / (double) (SQUARE_SIZE - 1);
        return Color.hsb(hue, saturation, 1.0);
    }


    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
