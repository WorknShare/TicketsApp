package fr.worknshare.tickets.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.repository.EquipmentRepository;
import fr.worknshare.tickets.repository.FailCallback;
import fr.worknshare.tickets.repository.PaginatedRequestCallback;
import fr.worknshare.tickets.repository.PaginatedResponse;
import fr.worknshare.tickets.repository.TicketRepository;
import fr.worknshare.tickets.view.Paginator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

public class EquipmentController extends Controller implements RequestController{
	
	private EquipmentRepository equipmentRepository;
	private ObservableList<Equipment> ticketList;	

	@FXML private JFXTreeTableView<Equipment> table;
	@FXML private Label paginationLabel;
	@FXML private JFXButton nextButton;
	@FXML private JFXButton previousButton;
	@FXML private JFXSpinner loader;
	
	private int page;
	private HttpClient httpClient;
	private HttpContext httpContext;
	
	@Override
	public void setHttpClient(HttpClient client) {
		this.httpClient = client;
		equipmentRepository.setHttpClient(httpClient);
		
	}

	@Override
	public void setHttpContext(HttpContext context) {
		this.httpContext = context;
		equipmentRepository.setHttpContext(httpContext);
		
	}
	
	private void initNameColumn() {
		JFXTreeTableColumn<Equipment, String> serialColumn = new JFXTreeTableColumn<Equipment, String>("Equipement");
		serialColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Equipment, String> param) -> {
			if (serialColumn.validateValue(param)) {
				return param.getValue().getValue().getName();
			} else {
				return serialColumn.getComputedValue(param);
			}
		});
		serialColumn.setSortable(false);
		serialColumn.setContextMenu(null);
		table.getColumns().add(serialColumn);
	}
	
	private void initTypeColumn() {
		JFXTreeTableColumn<Equipment, String> typeColumn = new JFXTreeTableColumn<Equipment, String>("Type");
		typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Equipment, String> param) -> {
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
	
	private void initSiteColumn() {
		JFXTreeTableColumn<Equipment, String> siteColumn = new JFXTreeTableColumn<Equipment, String>("Site");
		siteColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Equipment, String> param) -> {
			if (siteColumn.validateValue(param) && param.getValue().getValue().getSite() != null) {
				return param.getValue().getValue().getSite().getName();
			} else {
				return siteColumn.getComputedValue(param);
			}
		});
		siteColumn.setSortable(false);
		siteColumn.setContextMenu(null);
		table.getColumns().add(siteColumn);
	}
	
	private void initColumns() {

		final TreeItem<Equipment> root = new RecursiveTreeItem<Equipment>(ticketList, RecursiveTreeObject::getChildren);
		table.setRoot(root);
		table.setShowRoot(false);

		initNameColumn();
		initTypeColumn();
		initSiteColumn();
	}
	
	@FXML
	private void initialize() {

		equipmentRepository = new EquipmentRepository();
		ticketList = FXCollections.observableArrayList();
		page = 1;
		initColumns();

	}
	
	@FXML
	public void nextClicked(ActionEvent e) {
		page++;
		refresh();
	}

	@FXML
	public void previousClicked(ActionEvent e) {
		if(page > 1)
			page--;
		refresh();
	}
	
	public void refresh() {
		table.setDisable(true);
		previousButton.setDisable(true);
		nextButton.setDisable(true);
		paginationLabel.getStyleClass().add("text-muted");
		loader.setVisible(true);
		
		equipmentRepository.paginate(page, new PaginatedRequestCallback<Equipment>() {
			
			@Override
			public void run() {
				PaginatedResponse<Equipment> response = getPaginatedResponse();
				Paginator paginator = response.getPaginator();

				ticketList.clear(); //Empty the list
				ticketList.addAll(response.getItems());

				paginationLabel.setText("Page " + paginator.getCurrentPage() + "/" + paginator.getMaxPage());
				paginationLabel.getStyleClass().remove("text-muted");
				previousButton.setDisable(paginator.getCurrentPage() == 1);
				nextButton.setDisable(paginator.getCurrentPage() == paginator.getMaxPage());
				
				table.setDisable(false);
				loader.setVisible(false);
			}
		}, new FailCallback() {

			@Override
			public void run() {
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
				paginationLabel.setText("Page 1/1");
				table.setDisable(false);
				previousButton.setDisable(true);
				nextButton.setDisable(true);
				table.setDisable(false);
				loader.setVisible(false);
			}

		});
	}
	
	

}
