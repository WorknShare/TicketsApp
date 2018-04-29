package fr.worknshare.tickets.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.EquipmentType;
import fr.worknshare.tickets.repository.EquipmentRepository;
import fr.worknshare.tickets.repository.EquipmentTypeRepository;
import fr.worknshare.tickets.repository.FailCallback;
import fr.worknshare.tickets.repository.PaginatedRequestCallback;
import fr.worknshare.tickets.repository.PaginatedResponse;
import fr.worknshare.tickets.view.Paginator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;

public class EquipmentController extends Controller implements RequestController{
	
	private EquipmentRepository equipmentRepository;
	private EquipmentTypeRepository equipmentTypeRepository;
	
	private ObservableList<Equipment> equipmentList;	
	private ObservableList<EquipmentType> equipmentTypeList;

	@FXML private JFXTreeTableView<Equipment> tableEquipment;
	@FXML private Label paginationLabel;
	@FXML private JFXButton nextButton;
	@FXML private JFXButton previousButton;
	@FXML private JFXSpinner loader;
	@FXML private JFXTextField searchbar;
	private EquipmentType allSite;
	@FXML private JFXComboBox<EquipmentType> equipmentTypeSelected;
	
	private int page;
	private FailCallback failCallback;
	private PaginatedRequestCallback<Equipment> callback;
	
	private HttpClient httpClient;
	private HttpContext httpContext;
	
	private void initEquipmentTypeCellFactory() {
		equipmentTypeSelected.setCellFactory((lv) -> {
			return new ListCell<EquipmentType>() {
				@Override
				protected void updateItem(EquipmentType equipmentType, boolean empty) {
					super.updateItem(equipmentType, empty);

					if (empty || equipmentType == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(equipmentType.toString());
						setGraphic(null);
					}
				}
			};
		});
	}
	
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
	
	public void setEquipmentRepository(EquipmentRepository equipmentRepository) {
		this.equipmentRepository = equipmentRepository;
	}
	
	public EquipmentRepository getEquipmentRepository() {
		return this.equipmentRepository;
	}
	
	public EquipmentTypeRepository getEquipmentTypeRepository() {
		return equipmentTypeRepository;
	}

	public void setEquipmentTypeRepository(EquipmentTypeRepository equipmentTypeRepository) {
		this.equipmentTypeRepository = equipmentTypeRepository;
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
		tableEquipment.getColumns().add(serialColumn);
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
		tableEquipment.getColumns().add(typeColumn);
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
		tableEquipment.getColumns().add(siteColumn);
	}
	
	private void initColumns() {

		final TreeItem<Equipment> root = new RecursiveTreeItem<Equipment>(equipmentList, RecursiveTreeObject::getChildren);
		tableEquipment.setRoot(root);
		tableEquipment.setShowRoot(false);

		initNameColumn();
		initTypeColumn();
		initSiteColumn();
	}
	
	@FXML
	private void initialize() {
		initFailCallback();
		initCallback();
		initEquipmentTypeCellFactory();
		initSearchSubmitOnEnter();
		allSite = new EquipmentType(-1);
		allSite.setName("Tous");
		equipmentTypeList = FXCollections.observableArrayList();
		equipmentTypeSelected.setItems(equipmentTypeList);
		equipmentList = FXCollections.observableArrayList();
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
	
	public void setPage(int page) {
		this.page = page;
	}
	
	private void initFailCallback() {
		failCallback = new FailCallback() {

			@Override
			public void run() {
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
				paginationLabel.setText("Page 1/1");
				tableEquipment.setDisable(false);
				previousButton.setDisable(true);
				nextButton.setDisable(true);
				loader.setVisible(false);
				searchbar.setDisable(false);
				equipmentTypeSelected.setDisable(false);
			}

		};
	}

	private void initCallback() {
		callback = new PaginatedRequestCallback<Equipment>() {

			@Override
			public void run() {
				PaginatedResponse<Equipment> response = getPaginatedResponse();
				Paginator paginator = response.getPaginator();

				
				equipmentList.addAll(response.getItems());

				paginationLabel.setText("Page " + paginator.getCurrentPage() + "/" + paginator.getMaxPage());
				paginationLabel.getStyleClass().remove("text-muted");
				previousButton.setDisable(paginator.getCurrentPage() == 1);
				nextButton.setDisable(paginator.getCurrentPage() == paginator.getMaxPage());
				
				searchbar.setDisable(false);
				tableEquipment.setDisable(false);
				loader.setVisible(false);
				equipmentTypeSelected.setDisable(false);

			}
		};
	}
	
	
	private void initSearchSubmitOnEnter() {
		searchbar.setOnKeyPressed((event) -> {
			if (event.getCode().equals(KeyCode.ENTER))
				submitSearch();
		});
	}
	
	private void submitSearch() {
		if(searchbar.getText() != null) {
			String text = searchbar.getText().trim();
			if(!text.isEmpty())
				search(text);
			else {
				setPage(1);
				refresh();
			}
		} else {
			setPage(1);
			refresh();
		}
	}
	
	
	public void search(String search) {
		tableEquipment.setDisable(true);
		previousButton.setDisable(true);
		nextButton.setDisable(true);
		paginationLabel.getStyleClass().add("text-muted");
		loader.setVisible(true);
		searchbar.setDisable(true);
		equipmentTypeSelected.setDisable(true);
		equipmentList.clear();
		page = 1;
		
		int idtype = -1;
		EquipmentType type = equipmentTypeSelected.getSelectionModel().getSelectedItem();
		if(type != null) 
			idtype = type.getId().get();
		
		if(idtype == -1)
			equipmentRepository.where(search, callback, failCallback);
		else
			equipmentRepository.where(search, idtype, callback, failCallback);
	}
	
	
	public void refresh() {

		tableEquipment.setDisable(true);
		previousButton.setDisable(true);
		nextButton.setDisable(true);
		paginationLabel.getStyleClass().add("text-muted");
		loader.setVisible(true);
		equipmentList.clear(); //Empty the list
		int idtype = -1;
		
		EquipmentType type = equipmentTypeSelected.getSelectionModel().getSelectedItem();
		if(type != null) 
			idtype = type.getId().get();
		 
		if(idtype == -1)
			equipmentRepository.paginate(page, callback, failCallback);
		else
			equipmentRepository.paginate(page, idtype, callback, failCallback);
	}
	
	
	
	public void updateSite() {
		equipmentTypeList.clear();

		equipmentTypeRepository.getEquipmentType(new PaginatedRequestCallback<EquipmentType>() {

			@Override
			public void run() {
				equipmentTypeList.add(allSite);
				equipmentTypeList.addAll(getPaginatedResponse().getItems());
			}
		},
				new FailCallback() {

			@Override
			public void run() {
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
			}
		});
	}
	
	
	@FXML
	private void equipmentTypeChanged() {
		if(!equipmentTypeSelected.isDisabled()) {
			setPage(1);
			submitSearch();
		}
			
	}

}
