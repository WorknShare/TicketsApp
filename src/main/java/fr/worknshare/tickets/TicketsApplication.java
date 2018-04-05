package fr.worknshare.tickets;
	
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main class. Sets up error handling and loads config
 * @author Jérémy LAMBERT
 * @see Config
 *
 */
public class TicketsApplication extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		setup();
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load scene", e);
		}
	}
	
	private void setup() {
		setupLogging();
		loadConfig();
		setupErrorHandling();
	}
	
	private void loadConfig() {
		Config.getInstance(); //Creates the Config instance and loads it
	}
	
	private void setupLogging() {
		DateFormat df = new SimpleDateFormat("[HH:mm:ss.SSS]");
		Logger logger = Logger.getGlobal();
		//logger.setFilter((LogRecord record) -> { return !record.getLevel().equals(Level.INFO); });
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(df.format(new Date(record.getMillis())));
				builder.append("[").append(record.getLevel()).append("] ");
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}

			public String getHead(Handler h) {
				return super.getHead(h);
			}

			public String getTail(Handler h) {
				return super.getTail(h);
			}
		});

		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
	}
	
	private void setupErrorHandling() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				//Show error dialog
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
