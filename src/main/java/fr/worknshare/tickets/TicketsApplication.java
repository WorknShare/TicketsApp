package fr.worknshare.tickets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main class. Sets up error handling and loads config
 * @author Jérémy LAMBERT
 * @see Config
 *
 */
public class TicketsApplication extends Application {

	private static Rollbar rollbar;

	@Override
	public void start(Stage primaryStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
			StackPane rootLayout = (StackPane) loader.load();
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("/view/application.css").toExternalForm());

			primaryStage.setTitle("Work'n Share Tickets");
			primaryStage.setMinHeight(635);
			primaryStage.setMinWidth(1050);

			setupIcons(primaryStage);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch( Exception e ) {
			Logger.getGlobal().log(Level.SEVERE, "Error while loading the graphical interface.", e);
			Platform.exit();
		}

	}

	private void setupIcons(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/logo16.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/logo32.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/logo64.png").toExternalForm()));
	}
	
	private static void setup() {
		setupLogging();
		setupErrorHandling();
		loadConfig();
	}

	private static void loadConfig() {
		Config.getInstance(); //Creates the Config instance and loads it
	}

	private static void setupLogging() {
		Logger logger = Logger.getGlobal();
		LogFormatter formatter = new LogFormatter();

		//Print INFO logs to System.out
		StreamHandler handlerInfo = new StreamHandler(System.out, formatter) {

			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);
				flush(); //Flush needed to print the message even if the buffer is not full
			}

		};
		handlerInfo.setFilter((LogRecord record) -> { return record.getLevel().equals(Level.INFO); }); //Only show INFO logs
		handlerInfo.setLevel(Level.INFO);

		logger.setUseParentHandlers(false);
		logger.addHandler(handlerInfo);
		
		setupRollbar();
		
		//Print other logs to System.err and report them to Rollbar
		RollbarConsoleHandler handler = new RollbarConsoleHandler(rollbar);
		handler.setFormatter(formatter);

		logger.addHandler(handler);

	}

	private static void setupErrorHandling() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		});
	}

	private static void setupRollbar() {
		String token = Config.getInstance().get("RollbarToken");
		if(token != null) {
			Logger.getGlobal().info("Setting up rollbar");
			com.rollbar.notifier.config.Config config = ConfigBuilder.withAccessToken(token)
					.environment(Config.getInstance().get("Environment"))
					.build();
			rollbar = Rollbar.init(config);
		}
	}

	public static Rollbar getRollbar() {
		return rollbar;
	}

	public static void main(String[] args) {
		setup();
		try {
			launch(args);
		} catch(Throwable e) {
			Logger.getGlobal().log(Level.SEVERE, "Error while starting application.", e);
		}
		Logger.getGlobal().info("Application exit");
	}
}
