package fr.worknshare.tickets.view;

import com.jfoenix.controls.JFXComboBox;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Abstract utility class allowing to create status combo boxes quickly.
 * @author Jérémy LAMBERT
 *
 */
public abstract class StatusComboBoxMaker {

	/**
	 * Setup a combo box to be a status combo box. Adds the items and sets the coloring.
	 * @param box - the combo box to prepare
	 */
	public static final void make(JFXComboBox<StatusItem> box) {
		make(box, false);
	}

	public static ListCell<StatusItem> createCell() {
		return new ListCell<StatusItem>() {
			@Override
			protected void updateItem(StatusItem item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
	                setText(null);
	                setGraphic(null);
	            } else {
	                setText(item.getName());
	                setGraphic(null);
	            }
			}        
		};
	}

	/**
	 * Setup a combo box to be a status combo box. Adds the items and sets the coloring.
	 * @param box - the combo box to prepare
	 * @param allOption - when true, adds an "All" option with value -1
	 */
	public static final void make(JFXComboBox<StatusItem> box, boolean allOption) {
		ObservableList<StatusItem> items = FXCollections.observableArrayList();
		if(allOption)
			items.add(new StatusItem(-1, "Tous"));
		items.addAll(new StatusItem(0, "En attente"), 
				new StatusItem(1, "En cours"),
				new StatusItem(2, "Résolu"),
				new StatusItem(3, "Non résolu"),
				new StatusItem(4, "Invalide"));
		box.setItems(items);

		box.setCellFactory(lv -> createCell());

		box.buttonCellProperty().bind(Bindings.createObjectBinding(() -> {

			StackPane arrowButton = (StackPane) box.lookup(".arrow-button");

			return new ListCell<StatusItem>() {

				@Override
				protected void updateItem(StatusItem item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setBackground(Background.EMPTY);
						setText("");
					} else {

						setText(item.getName());
						switch(item.getStatus()) {
						case 0:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.86, 0.207, 0.27, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 1:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(1.0, 0.756, 0.027, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 2:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.157, 0.655, 0.27, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 3:
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.09, 0.635, 0.721, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 4:
							setTextFill(Color.BLACK);
							setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						default:
							setTextFill(Color.BLACK);
							setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
						}
					}

					if(arrowButton != null)
						arrowButton.setBackground(getBackground());
				}

			};
		}, box.valueProperty()));
	}

}
