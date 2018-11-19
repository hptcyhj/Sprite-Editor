package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 * This class is responsible for the model of the pixel grid.
 * @author Hangjian Yuan
 *
 */
public class PixelGridModel {
	private Color[][] colorGrid;
	private ColorPicker colorPicker;
	private List<Color[][]> undoList = new ArrayList<Color[][]>();
	final int UNDO_LIMIT = 100;

	public PixelGridModel(ColorPicker colorPicker) {
		resize(16, 16);
		this.colorPicker = colorPicker;
	}
	
	/**
	 * resize the width and height of the grid.
	 * @param rowNum The height of the grid
	 * @param colNum The width of the grid
	 */
	public void resize(int rowNum, int colNum) {
		colorGrid = new Color[rowNum][colNum];
		clear();
	}
	
	/**
	 * update the color of the designated pixel to the value of ColorPicker.
	 * @param row The row number of that pixel
	 * @param col The column number of that pixel
	 */
	public void updateColor(int row, int col) {
		colorGrid[row][col] = colorPicker.getValue();
	}
	
	/**
	 * set the color of the designated pixel to the given color.
	 * this function is used by importing images functionality.
	 * @param row The row number of that pixel
	 * @param col The column number of that pixel
	 * @param color The new color for that pixel
	 */
	public void setColor(int row, int col, Color color) {
		colorGrid[row][col] = color;
	}
	
	/**
	 * set the color effect for all the pixels.
	 * @param effectType The type of color effect
	 */
	public void setColorEffect(String effectType) {
		for (int row = 0; row < colorGrid.length; row++) {
			for (int col = 0; col < colorGrid[0].length; col++) {
				Color tmp = colorGrid[row][col];
				switch (effectType) {
				case "brighter":
					colorGrid[row][col] = tmp.brighter();
					break;
				case "darker":
					colorGrid[row][col] = tmp.darker();
					break;
				case "saturate":
					colorGrid[row][col] = tmp.saturate();
					break;
				case "desaturate":
					colorGrid[row][col] = tmp.desaturate();
					break;
				case "grayscale":
					colorGrid[row][col] = tmp.grayscale();
					break;
				case "invert":
					colorGrid[row][col] = tmp.invert();
					break;
				}
			}
		}
	}
	
	/**
	 * get the color of a designated pixel.
	 * @param row The row number of that pixel
	 * @param col The column number of pixel
	 * @return The color of that pixel
	 */
	public Color getColor(int row, int col) {
		return colorGrid[row][col];
	}
	
	/**
	 * get the height of the pixel grid.
	 * @return The height of this grid
	 */
	public int getHeight() {
		return colorGrid.length;
	}
	
	/**
	 * get the width of the pixel grid.
	 * @return The width of this grid
	 */
	public int getWidth() {
		return colorGrid[0].length;
	}
	
	/**
	 * clear all the pixels' color.
	 */
	public void clear() {
		for (int row = 0; row < colorGrid.length; row++) {
			for (int col = 0; col < colorGrid[0].length; col++) {
				colorGrid[row][col] = Color.TRANSPARENT;
			}
		}
		undoList.clear();
	}
	
	/**
	 * undo the recent change on this grid model.
	 */
	public void undo() {
		int index = undoList.size() - 1;
		if (index < 0) {
			return;
		}
		colorGrid = undoList.get(index);
		undoList.remove(index);
	}
	
	/**
	 * record the current grid model.
	 * provide chances to undo later changes.
	 */
	public void recordChange() {
		if (undoList.size() > UNDO_LIMIT) {
			for (int i = 0; i < 5; i++) {
				undoList.remove(0);
			}
		}
		
		int height = getHeight();
		int width = getWidth();
		Color[][] currentGrid = new Color[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				currentGrid[i][j] = colorGrid[i][j];
			}
		}
		
		undoList.add(currentGrid);
	}
}
