package application;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * This class is responsible for importing and exporting images.
 * @author Hangjian Yuan
 *
 */
public class SpriteFileHandler {
	private PixelGridModel pgm;
	
	public SpriteFileHandler(PixelGridModel model) {
		pgm = model;
	}
	
	/**
	 * export the image on canvas to a designated destination.
	 * @param filepath The file path of the destination.
	 */
	public void export(String filepath) {
		int modelWidth = pgm.getWidth();
		int modelHeight = pgm.getHeight();
		WritableImage image = new WritableImage(modelWidth, modelHeight);
		PixelWriter writer = image.getPixelWriter();
		
		for (int row = 0; row < modelHeight; row++) {
			for (int col = 0; col < modelWidth; col++) {
				Color color = pgm.getColor(row, col);
				writer.setColor(col, row, color);
			}
		}
		
		File savedImage = new File(filepath);
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
		try {
			ImageIO.write(renderedImage, "png", savedImage);
		} catch (IOException e) {
			System.out.println("Fail to export the image.");
			e.printStackTrace();
		}
	}
	/**
	 * import the selected image.
	 * @param imageFile The selected image.
	 * @return A boolean value representing whether the image file is valid.
	 */
	public boolean open(File imageFile) {
		Image image = null;
		try {
			BufferedImage bufferedImage = ImageIO.read(imageFile);
            image = SwingFXUtils.toFXImage(bufferedImage, null);
		} catch (Exception e) {
			System.out.println("Fail to import the image.");
			e.printStackTrace();
		}
		
		PixelReader pixelReader = image.getPixelReader();
		int imageWidth = (int)image.getWidth();
		int imageHeight = (int)image.getHeight();
		
		if (imageWidth > 256 || imageHeight > 256) {
			return false;
		}
		
		pgm.resize(imageHeight, imageWidth);
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {
				Color color = pixelReader.getColor(j, i);
				pgm.setColor(i, j, color);
			}
		}
		
		return true;
	}
}
