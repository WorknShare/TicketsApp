package fr.worknshare.tickets.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;

import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.repository.FailCallback;
import fr.worknshare.tickets.repository.PaginatedRequestCallback;
import fr.worknshare.tickets.repository.PaginatedResponse;
import fr.worknshare.tickets.repository.TicketRepository;
import fr.worknshare.tickets.view.Paginator;
import fr.worknshare.tickets.view.StatusComboBoxMaker;
import fr.worknshare.tickets.view.StatusItem;
import fr.worknshare.tickets.view.TicketTableMaker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.KeyCode;

public class TicketsController extends Controller implements RequestController {

	private TicketRepository ticketRepository;
	private TicketShowController ticketShowController;

	private ObservableList<Ticket> ticketList;	

	@FXML private JFXTreeTableView<Ticket> table;
	@FXML private Label paginationLabel;
	@FXML private JFXButton nextButton;
	@FXML private JFXButton previousButton;
	@FXML private JFXSpinner loader;
	@FXML private JFXTextField searchbar;
	@FXML private JFXComboBox<StatusItem> statusFilter;

	private int page;
	private HttpClient httpClient;
	private HttpContext httpContext;
	private FailCallback failCallback;
	private PaginatedRequestCallback<Ticket> callback;

	private void initFailCallback() {
		failCallback = new FailCallback() {

			@Override
			public void run() {
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
				paginationLabel.setText("Page 1/1");
				table.setDisable(false);
				previousButton.setDisable(true);
				nextButton.setDisable(true);
				table.setDisable(false);
				loader.setVisible(false);
				searchbar.setDisable(false);
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
				searchbar.setDisable(false);

			}
		};
	}

	private void initDoubleClickListener() {
		table.setRowFactory( tv -> {
			TreeTableRow<Ticket> row = new TreeTableRow<Ticket>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					Ticket ticket = row.getItem();
					ticketShowController.showTicket(ticket);
				}
			});
			return row;
		});
	}

	@FXML
	private void initialize() {

		ticketList = FXCollections.observableArrayList();
		page = 1;
		TicketTableMaker.make(table, ticketList);
		initDoubleClickListener();
		initFailCallback();
		initCallback();

		StatusComboBoxMaker.make(statusFilter, true);
		statusFilter.getSelectionModel().select(0);

		//Submit on enter
		searchbar.setOnKeyPressed((event) -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				submitSearch();
			}
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

	@FXML
	private void statusFilterChanged() {
		if(!statusFilter.isDisabled())
			submitSearch();
	}

	private void prepareRequest() {
		table.setDisable(true);
		previousButton.setDisable(true);
		nextButton.setDisable(true);
		paginationLabel.getStyleClass().add("text-muted");
		loader.setVisible(true);
		searchbar.setDisable(true);
		ticketList.clear();
	}

	public void refresh() {
		prepareRequest();

		int status = statusFilter.getSelectionModel().getSelectedItem().getStatus();
		if(status == -1)
			ticketRepository.paginate(page, callback, failCallback);
		else
			ticketRepository.paginate(page, status, callback, failCallback);
	}

	public void search(String search) {
		prepareRequest();
		page = 1;

		int status = statusFilter.getSelectionModel().getSelectedItem().getStatus();
		if(status == -1)
			ticketRepository.where(search, callback, failCallback);
		else
			ticketRepository.where(search, status, callback, failCallback);
	}

	public void setPage(int page) {
		this.page = page;
		this.searchbar.setText(null);
	}

	public void resetFilter() {
		statusFilter.setDisable(true);
		statusFilter.getSelectionModel().select(0);
		statusFilter.setDisable(false);
	}

	@Override
	public void setHttpClient(HttpClient client) {
		this.httpClient = client;
		ticketRepository.setHttpClient(httpClient);
	}

	@Override
	public void setHttpContext(HttpContext context) {
		this.httpContext = context;
		ticketRepository.setHttpContext(httpContext);
	}

	public void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	public TicketRepository getTicketRepository() {
		return ticketRepository;
	}

	public void setTicketShowController(TicketShowController controller) {
		this.ticketShowController = controller;
	}
}
