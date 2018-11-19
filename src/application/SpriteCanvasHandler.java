package application;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;

/**
 * This class in responsible for manage the canvas in the application.
 * @author Hangjian Yuan
 *
 */
public class SpriteCanvasHandler {
	SimpleDoubleProperty zoomProperty;
	
	private PixelGridModel pgm;
	private Canvas canvas;
	private boolean mouseIsPressed = false;
	
	/**
	 * the constructor of this class
	 * @param pgm The pixel grid model of this application.
	 * @param canvas The canvas of this application.
	 * @param zoomValue The slider in GUI, need to get its value when draw pixels on canvas.
	 */
	public SpriteCanvasHandler(PixelGridModel pgm, Canvas canvas, Slider zoomValue) {
		this.pgm = pgm;
		this.canvas = canvas;
		zoomProperty = new SimpleDoubleProperty();
		zoomProperty.bind(zoomValue.valueProperty());
		initCanvas();
	}
	
	/**
	 * initialize the canvas with designated width and height, and redraw all the pixels.
	 */
	public void initCanvas() {
		double width = pgm.getWidth();
		double height = pgm.getHeight();
		canvas.setWidth(width * zoomProperty.getValue());
		canvas.setHeight(height * zoomProperty.getValue());
		drawPixels();
		
		canvas.setOnMousePressed(mouse -> {
			mouseIsPressed = true;
			pgm.recordChange();
			updateColorOnClickedPixel(mouse.getX(), mouse.getY());
		});
		canvas.setOnMouseDragged(mouse -> {
			if (mouseIsPressed) {
				updateColorOnClickedPixel(mouse.getX(), mouse.getY());
			}
		});
		canvas.setOnMouseReleased(mouse -> mouseIsPressed = false);
	}
	
	/**
	 * update the color of the designated pixel
	 * @param x The x coordinate of the pixel
	 * @param y The y coordinate of the pixel
	 */
	private void updateColorOnClickedPixel(double x, double y) {
		int col = (int) (x / zoomProperty.getValue());
		int row = (int) (y / zoomProperty.getValue());
		pgm.updateColor(row, col);
		drawPixels();
	}
	
	/**
	 * draw all the pixels on the canvas.
	 */
	public void drawPixels() {
		int width = pgm.getWidth();
		int height = pgm.getHeight();
		double zoomValue = zoomProperty.getValue();
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				graphics.setFill(pgm.getColor(i, j));
				graphics.fillRect(j * zoomValue, i * zoomValue, zoomValue, zoomValue);
				graphics.strokeRect(j * zoomValue, i * zoomValue, zoomValue, zoomValue);
			}
		}
		
		graphics.strokeRect(0, 0, width * zoomValue, height * zoomValue);
	}
}
