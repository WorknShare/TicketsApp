package fr.worknshare.tickets.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import fr.worknshare.tickets.model.Equipment;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.repository.FailCallback;
import fr.worknshare.tickets.repository.PaginatedRequestCallback;
import fr.worknshare.tickets.repository.PaginatedResponse;
import fr.worknshare.tickets.repository.TicketRepository;
import fr.worknshare.tickets.view.Paginator;
import fr.worknshare.tickets.view.TicketTableMaker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class EquipmentShowController extends Controller implements Backable, Authorizable {

	@FXML private VBox pane;
	@FXML private Label serialLabel;
	@FXML private Label typeLabel;
	@FXML private Label siteLabel;
	@FXML private JFXTreeTableView<Ticket> table;
	@FXML private JFXButton createTicket;
	@FXML private JFXButton back;
	@FXML private Label paginationLabel;
	@FXML private JFXButton nextButton;
	@FXML private JFXButton previousButton;
	@FXML private JFXSpinner loader;

	private int page;
	private Equipment equipment;

	private ObservableList<Ticket> ticketList;
	private Pane backPanel;
	private TicketShowController ticketShowController;
	private TicketCreateController ticketCreateController;
	private TicketRepository ticketRepository;

	private FailCallback failCallback;
	private PaginatedRequestCallback<Ticket> callback;

	private void initFailCallback() {
		failCallback = new FailCallback() {

			@Override
			public void run() {
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
				paginationLabel.setText("Page 1/1");
				paginationLabel.getStyleClass().remove("text-muted");
				table.setDisable(false);
				previousButton.setDisable(true);
				nextButton.setDisable(true);
				loader.setVisible(false);
				back.setDisable(false);
				createTicket.setDisable(false);
			}

		};
	}

	private void initCallback() {
		callback = new PaginatedRequestCallback<Ticket>() {

			@Override
			public void run() {
				PaginatedResponse<Ticket> response = getPaginatedResponse();
				Paginator paginator = response.getPaginator();

				ticketList.addAll(response.getItems());

				paginationLabel.setText("Page " + paginator.getCurrentPage() + "/" + paginator.getMaxPage());
				paginationLabel.getStyleClass().remove("text-muted");
				previousButton.setDisable(paginator.getCurrentPage() == 1);
				nextButton.setDisable(paginator.getCurrentPage() == paginator.getMaxPage());

				table.setDisable(false);
				loader.setVisible(false);
				back.setDisable(false);
				createTicket.setDisable(false);

			}
		};
	}
	
	public void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}
	
	@FXML
	private void initialize() {
		ticketList = FXCollections.observableArrayList();
		TicketTableMaker.make(table, ticketList);
		
		initCallback();
		initFailCallback();
		initDoubleClickListener();
	}

	private void initDoubleClickListener() {
		table.setRowFactory( tv -> {
			TreeTableRow<Ticket> row = new TreeTableRow<Ticket>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					Ticket ticket = row.getItem();
					ticketShowController.setBackPanel(pane);
					ticketShowController.showTicket(ticket);
				}
			});
			return row;
		});
	}

	public void showEquipment(Equipment equipment) {
		this.equipment = equipment;
		serialLabel.setText(equipment.getName().get());
		typeLabel.setText(equipment.getEquipmentType().getName().get());
		siteLabel.setText(equipment.getSite() != null ? equipment.getSite().getName().get() : "Aucun");
		setPage(1);
		refresh();
		pane.toFront();
	}

	@Override
	public void updateAuthorizations(int role) {
		createTicket.setVisible(role < 4 && role > 0);
	}

	@Override
	public void setBackPanel(Pane pane) {
		this.backPanel = pane;
	}

	@FXML
	public void backClicked() {
		if(backPanel != null)
			backPanel.toFront();
	}
	
	@FXML
	private void nextClicked() {
		page++;
		refresh();
	}

	@FXML
	private void previousClicked() {
		if(page > 1)
			page--;
		refresh();
	}
	
	private void prepareRequest() {
		table.setDisable(true);
		previousButton.setDisable(true);
		nextButton.setDisable(true);
		paginationLabel.getStyleClass().add("text-muted");
		loader.setVisible(true);
		createTicket.setDisable(true);
		back.setDisable(true);
		ticketList.clear();
	}
	
	public void refresh() {
		prepareRequest();
		ticketRepository.paginate(page, "equipment/" + equipment.getId().get(), callback, failCallback);
	}
	
	public void setPage(int page) {
		this.page = page;
	}

	@FXML
	public void createClicked() {
		ticketCreateController.setBackPanel(pane);
		ticketCreateController.setSelectedEquipment(equipment);
		ticketCreateController.show();
	}

	public void setTicketShowController(TicketShowController ticketShowController) {
		this.ticketShowController = ticketShowController;
	}

	public void setTicketCreateController(TicketCreateController ticketCreateController) {
		this.ticketCreateController = ticketCreateController;
	}

}
