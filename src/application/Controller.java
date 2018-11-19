package application;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.MenuItem;

/**
 * The controller class of the sprite editor application. 
 * @author Hangjian Yuan
 *
 */
public class Controller {

	PixelGridModel pgm;
	
	SpriteCanvasHandler canvasHandler;
	SpriteFileHandler fileHandler;
	
	@FXML ColorPicker colorPicker;
	@FXML Canvas canvas;
	@FXML ScrollPane scrollPane;
	@FXML ToggleGroup defaultSizes;
	@FXML RadioMenuItem resize_16;
	@FXML RadioMenuItem resize_32;
	@FXML RadioMenuItem resize_64;
	@FXML Slider zoomValue;

	@FXML MenuItem brighter;
	@FXML MenuItem darker;
	@FXML MenuItem saturate;
	@FXML MenuItem desaturate;
	@FXML MenuItem grayscale;
	@FXML MenuItem invert;

	/**
	 * initialize all the components: PixelGridModel, SpriteCanvasHandler, SpriteFileHandler.
	 * Set the value for resize menuItems and color effect menuItems.
	 */
	public void initialize() {
		pgm = new PixelGridModel(colorPicker);
		canvasHandler = new SpriteCanvasHandler(pgm, canvas, zoomValue);
		fileHandler = new SpriteFileHandler(pgm);
		
		zoomValue.valueProperty().addListener(event -> canvasHandler.initCanvas());
		
		resize_16.setUserData(new Integer(16));
		resize_32.setUserData(new Integer(32));
		resize_64.setUserData(new Integer(64));
		
		brighter.setUserData(new String("brighter"));
		darker.setUserData(new String("darker"));
		saturate.setUserData(new String("saturate"));
		desaturate.setUserData(new String("desaturate"));
		grayscale.setUserData(new String("grayscale"));
		invert.setUserData(new String("invert"));
	}

	/**
	 * clear colors of all the pixels in canvas. Set them to transparent.
	 */
	@FXML public void clearColor() {
		pgm.clear();
		canvasHandler.drawPixels();
	}

	/**
	 * exit the application with a confirmation dialog.
	 */
	@FXML public void exitEditor() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Do you want to exit the editor?");
		
		Optional<ButtonType> option = alert.showAndWait();
		if (option.get() == ButtonType.OK) {
			System.exit(0);
		}
		else if (option.get() == ButtonType.CANCEL) {
			return;
		}
	}

	/**
	 * show the brief introduction of this application.
	 */
	@FXML public void onAboutClicked() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About This Editor");
		alert.setHeaderText("Simple Sprite Editor");
		alert.setContentText("Copyright © 2017 Hangjian Yuan. All rights reserved.");
		alert.show();
	}

	/**
	 * show a simple dialog informing users some information.
	 * this function is served to other public functions.
	 * @param message The message printed on the screen.
	 */
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.show();
	}
	
	/**
	 * export the image on canvas to a selected destination.
	 */
	@FXML public void exportFile() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a destination");
		File directory = directoryChooser.showDialog(null);
		if (directory == null) {
			return;
		}
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		String filepath = directory.getAbsolutePath() + "/" + timeStamp + ".png";
		
		fileHandler.export(filepath);
		
		String message = "Export the image to " + directory.getName() + " successfully!";
		showAlert(message);
	}
	
	/**
	 * import the selected image into the application's canvas.
	 */
	@FXML public void importFile() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
		fileChooser.setTitle("Select an image");
		fileChooser.getExtensionFilters().add(filter);
		File image = fileChooser.showOpenDialog(null);
		if (image == null) {
			return;
		}

		if (fileHandler.open(image)) {
			canvasHandler.initCanvas();
			defaultSizes.getSelectedToggle().setSelected(false);
			
			String message = "Import " + image.getName() + " successfully!";
			showAlert(message);
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Image Size Error");
			alert.setContentText("Sorry! Currently this editor doesn't support images larger than 256 * 256.");
			alert.show();
		}
	}

	/**
	 * undo the last change on canvas.
	 * you can change the undo limit in PixelGridModel class.
	 */
	@FXML public void undo() {
		pgm.undo();
		canvasHandler.drawPixels();
	}

	/**
	 * show a dialog to let users enter the row number and the column number.
	 */
	@FXML public void customizeGrid() {
		Dialog<Pair<Integer, Integer>> dialog = new Dialog<Pair<Integer, Integer>>();
		dialog.setTitle("New Canvas");
		dialog.setHeaderText(null);
		
		ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
		
		GridPane gp = new GridPane();
		Label widthLabel = new Label("Width: ");
		Label heightLabel = new Label("Height: ");		
		TextField widthField = new TextField();
		TextField heightField = new TextField();
		gp.add(widthLabel, 0, 0);
		gp.add(widthField, 1, 0);
		gp.add(heightLabel, 0, 1);
		gp.add(heightField, 1, 1);
		dialog.getDialogPane().setContent(gp);
	
		dialog.setResultConverter(clickedButton -> {
		    if (clickedButton == okButton) {
		    	    int width = Integer.parseInt(widthField.getText());
		      	int height = Integer.parseInt(heightField.getText());
		     	return new Pair<>(width, height);
		    }
			return null;
		});
		
		Optional<Pair<Integer, Integer>> result = dialog.showAndWait();
		
		int newWidth = 0;
		int newHeight = 0;
		if (result.isPresent()) {
			newWidth = result.get().getKey();
			newHeight = result.get().getValue();
		}
		
		if (newWidth <= 0 || newHeight <= 0) {
			return;
		}
		
		pgm.resize(newHeight, newWidth);
		canvasHandler.initCanvas();
		defaultSizes.getSelectedToggle().setSelected(false);
	}

	/**
	 * this function serves to "Size" menu's items.
	 * @param event the click event on "Size" menubar's items.
	 */
	@FXML public void resizeGrid(ActionEvent event) {
		RadioMenuItem tmp = (RadioMenuItem)(event.getSource());
		int size = ((Integer)(tmp.getUserData())).intValue();
		pgm.resize(size, size);
		canvasHandler.initCanvas();
	}

	/**
	 * this function serves to "Effect" menu's items. 
	 * @param event The clicked event, used to get the source menu item.
	 */
	@FXML public void setColorEffect(ActionEvent event) {
		MenuItem tmp = (MenuItem)(event.getSource());
		String effectType = (String)(tmp.getUserData());
		pgm.setColorEffect(effectType);
		canvasHandler.drawPixels();
	}
}
