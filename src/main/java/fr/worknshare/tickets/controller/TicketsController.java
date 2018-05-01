package fr.worknshare.tickets.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;

import fr.worknshare.tickets.SpreadSheet;
import fr.worknshare.tickets.model.Ticket;
import fr.worknshare.tickets.repository.FailCallback;
import fr.worknshare.tickets.repository.PaginatedRequestCallback;
import fr.worknshare.tickets.repository.PaginatedResponse;
import fr.worknshare.tickets.repository.TicketRepository;
import fr.worknshare.tickets.view.Paginator;
import fr.worknshare.tickets.view.StatusComboBoxMaker;
import fr.worknshare.tickets.view.StatusItem;
import fr.worknshare.tickets.view.TicketTableMaker;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TicketsController extends Controller implements Authorizable {

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
	@FXML private JFXButton exportButton;

	private Stage dialog;

	private int page;
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
				searchbar.setDisable(false);
				statusFilter.setDisable(false);
				exportButton.setDisable(false);
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
				statusFilter.setDisable(false);
				exportButton.setDisable(false);

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

	private void initSearchSubmitOnEnter() {
		searchbar.setOnKeyPressed((event) -> {
			if (event.getCode().equals(KeyCode.ENTER))
				submitSearch();
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

		initSearchSubmitOnEnter();

		StatusComboBoxMaker.make(statusFilter, true);
		statusFilter.getSelectionModel().select(0);

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
		statusFilter.setDisable(true);
		exportButton.setDisable(true);
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

	public void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	public TicketRepository getTicketRepository() {
		return ticketRepository;
	}

	public void setTicketShowController(TicketShowController controller) {
		this.ticketShowController = controller;
	}

	@Override
	public void updateAuthorizations(int role) {
		exportButton.setVisible(role == 1);
	}

	@FXML
	private void exportClicked() {
		FileChooser fileChooser = new FileChooser();

		//Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers excel (*.xlsx)", "*.xlsx");
		fileChooser.getExtensionFilters().add(extFilter);

		File selectedFile = fileChooser.showSaveDialog(exportButton.getScene().getWindow());

		if (selectedFile != null) {
			Logger.getGlobal().info("Exporting ticket database to file " + selectedFile.getAbsolutePath());
			exportTickets(selectedFile);
		}
	}

	private void exportTickets(File file) {
		ticketRepository.getAll(new PaginatedRequestCallback<Ticket>() {

			@Override
			public void run() {
				new Thread(() -> {
					
					ArrayList<Ticket> items = getPaginatedResponse().getItems();
					String[] header = {
									"ID", "Statut", "Numéro de série",
									"Type", "Description", "Employé affecté",
									"Employé source", "Date de création", "Date de mise à jour"
									};

					Object data[][] = new Object[items.size()][9];
					int i = 0;
					for(Ticket ticket : items)
						data[i++] = ticket.toArray();
					
					SpreadSheet spreadsheet = new SpreadSheet(file.getAbsolutePath());
					
					spreadsheet.setTitle("Tickets");
					spreadsheet.setHeader(header);
					spreadsheet.setData(data);
					
					if(spreadsheet.save()) {
						//Success
						Platform.runLater(() -> {
							dialog.close();
							getSnackbar().enqueue(new SnackbarEvent("Base des tickets exportée avec succès !", "success"));
						});
					} else {
						//Failure
						Platform.runLater(() -> {
							dialog.close();
							getSnackbar().enqueue(new SnackbarEvent("Une erreur est survenue lors de l'export de la base des tickets.", "error"));
						});
					}
					
				}).start();
			}
		}, new FailCallback() {

			@Override
			public void run() {
				dialog.close();
				getSnackbar().enqueue(new SnackbarEvent(getFullMessage(), "error"));
				Logger.getGlobal().log(Level.SEVERE, "Get all tickets request failed :\n\t" + getFullMessage());
			}
		});
		showLoadingDialog();
	}

	private void showLoadingDialog() {
		if(dialog == null) {
			try {
				dialog = new Stage();
				dialog.initOwner(exportButton.getScene().getWindow());
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.setResizable(false);

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoadingDialog.fxml"));
				VBox rootLayout = (VBox) loader.load();
				Scene scene = new Scene(rootLayout);

				dialog.setTitle("Chargement...");
				dialog.getIcons().add(new Image(getClass().getResource("/view/logo16.png").toExternalForm()));
				dialog.getIcons().add(new Image(getClass().getResource("/view/logo32.png").toExternalForm()));
				dialog.getIcons().add(new Image(getClass().getResource("/view/logo64.png").toExternalForm()));

				//Prevent dialog from closing
				dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						event.consume();
					}
				});

				dialog.setScene(scene);
			} catch( Exception e ) {
				Logger.getGlobal().log(Level.SEVERE, "Error while loading the loading dialog interface.", e);
			}
		}
		dialog.show();
	}
}
