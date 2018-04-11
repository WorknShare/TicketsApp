package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fr.worknshare.tickets.model.Employee;
import fr.worknshare.tickets.model.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class TicketsController {

	@FXML private JFXTreeTableView<Ticket> table;

	private void initIdColumn() {
		JFXTreeTableColumn<Ticket, Integer> idColumn = new JFXTreeTableColumn<Ticket, Integer>("ID");
		
		idColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, Integer> param) -> {
			if (idColumn.validateValue(param)) {
				return param.getValue().getValue().getId().asObject();
			} else {
				return idColumn.getComputedValue(param);
			}
		});
		
		idColumn.setStyle("-fx-alignment: CENTER;");
		idColumn.setSortable(false);
		idColumn.setContextMenu(null);
		table.getColumns().add(idColumn);
	}
	
	private void initStatusColumn() {
		JFXTreeTableColumn<Ticket, Integer> statusColumn = new JFXTreeTableColumn<Ticket, Integer>("Statut");
		statusColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, Integer> param) -> {
			if (statusColumn.validateValue(param)) {
				return param.getValue().getValue().getStatus().asObject();
			} else {
				return statusColumn.getComputedValue(param);
			}
		});						
		statusColumn.setStyle("-fx-alignment: CENTER;");

		statusColumn.setCellFactory(column -> {
			return new TreeTableCell<Ticket, Integer>() {

				@Override
				protected void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						
						setText(String.valueOf(item));

						switch(item) {
						case 0:
							setText("En attente");
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.86, 0.207, 0.27, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 1:
							setText("En cours");
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(1.0, 0.756, 0.027, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 2:
							setText("Résolu");
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.157, 0.655, 0.27, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 3:
							setText("Non résolu");
							setTextFill(Color.WHITE);
							setBackground(new Background(new BackgroundFill(new Color(0.09, 0.635, 0.721, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						case 4:
							setText("Invalide");
							setTextFill(Color.BLACK);
							setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
							break;
						default:
							setTextFill(Color.BLACK);
							setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
						}
					}
				}
			};
		});

		statusColumn.setSortable(false);
		statusColumn.setContextMenu(null);
		table.getColumns().add(statusColumn);
	}
	
	private void initSerialColumn() {
		JFXTreeTableColumn<Ticket, String> serialColumn = new JFXTreeTableColumn<Ticket, String>("Numéro de série");
		serialColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			if (serialColumn.validateValue(param) && param.getValue().getValue().getEquipment() != null) {
				return param.getValue().getValue().getEquipment().getName();
			} else {
				return serialColumn.getComputedValue(param);
			}
		});
		serialColumn.setSortable(false);
		serialColumn.setContextMenu(null);
		table.getColumns().add(serialColumn);
	}
	
	private void initTypeColumn() {
		JFXTreeTableColumn<Ticket, String> typeColumn = new JFXTreeTableColumn<Ticket, String>("Type");
		typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			if (typeColumn.validateValue(param) && param.getValue().getValue().getEquipmentType() != null) {
				return param.getValue().getValue().getEquipmentType().getName();
			} else {
				return typeColumn.getComputedValue(param);
			}
		});
		typeColumn.setSortable(false);
		typeColumn.setContextMenu(null);
		table.getColumns().add(typeColumn);
	}
	
	private void initDescriptionColumn() {
		JFXTreeTableColumn<Ticket, String> descriptionColumn = new JFXTreeTableColumn<Ticket, String>("Description");
		descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			if (descriptionColumn.validateValue(param)) {
				return param.getValue().getValue().getDescription();
			} else {
				return descriptionColumn.getComputedValue(param);
			}
		});
		descriptionColumn.setSortable(false);
		descriptionColumn.setContextMenu(null);
		table.getColumns().add(descriptionColumn);
	}
	
	private void initEmployeeColumn() {
		JFXTreeTableColumn<Ticket, String> employeeColumn = new JFXTreeTableColumn<Ticket, String>("Employé affecté");
		employeeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			Employee employee = param.getValue().getValue().getEmployeeAssigned();
			if (employeeColumn.validateValue(param) && employee != null) {
				return employee.getFullName();
			} else {
				return employeeColumn.getComputedValue(param);
			}
		});
		employeeColumn.setSortable(false);
		employeeColumn.setContextMenu(null);
		table.getColumns().add(employeeColumn);
	}
	
	private void initEmployeeSrcColumn() {
		JFXTreeTableColumn<Ticket, String> employeeColumn = new JFXTreeTableColumn<Ticket, String>("Employé source");
		employeeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			Employee employee = param.getValue().getValue().getEmployeeSource();
			if (employeeColumn.validateValue(param) && employee != null) {
				return employee.getFullName();
			} else {
				return employeeColumn.getComputedValue(param);
			}
		});
		employeeColumn.setSortable(false);
		employeeColumn.setContextMenu(null);
		table.getColumns().add(employeeColumn);
	}
	
	private void initCreatedColumn() {
		JFXTreeTableColumn<Ticket, String> createColumn = new JFXTreeTableColumn<Ticket, String>("Date de création");
		createColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			if (createColumn.validateValue(param)) {
				return param.getValue().getValue().getCreatedAt();
			} else {
				return createColumn.getComputedValue(param);
			}
		});
		createColumn.setSortable(false);
		createColumn.setContextMenu(null);
		table.getColumns().add(createColumn);
	}
	
	private void initUpdatedColumn() {
		JFXTreeTableColumn<Ticket, String> updateColumn = new JFXTreeTableColumn<Ticket, String>("Date de mise à jour");
		updateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Ticket, String> param) -> {
			if (updateColumn.validateValue(param)) {
				return param.getValue().getValue().getUpdatedAt();
			} else {
				return updateColumn.getComputedValue(param);
			}
		});
		updateColumn.setSortable(false);
		updateColumn.setContextMenu(null);
		table.getColumns().add(updateColumn);
	}
 	
	private void initColumns() {
		ObservableList<Ticket> tickets = FXCollections.observableArrayList();
		for(int i = 0 ; i < 20 ; i++)
			tickets.add(new Ticket(i));

		final TreeItem<Ticket> root = new RecursiveTreeItem<Ticket>(tickets, RecursiveTreeObject::getChildren);
		table.setRoot(root);
		table.setShowRoot(false);
		
		initIdColumn();
		initStatusColumn();
		initSerialColumn();
		initTypeColumn();
		initDescriptionColumn();
		initEmployeeColumn();
		initEmployeeSrcColumn();
		initCreatedColumn();
		initUpdatedColumn();
	}
	
	@FXML
	private void initialize() {

		initColumns();

	}
	
}
