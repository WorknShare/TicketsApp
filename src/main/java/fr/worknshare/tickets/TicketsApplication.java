package fr.worknshare.tickets;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
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
import javafx.scene.layout.BorderPane;
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
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch( Exception e ) {
			Logger.getGlobal().log(Level.SEVERE, "Error while loading the graphical interface.", e);
			Platform.exit();
		}

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
		DateFormat df = new SimpleDateFormat("[HH:mm:ss.SSS]");
		Logger logger = Logger.getGlobal();

		Formatter formatter = new Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(128);

				//Date and level
				builder.append(df.format(new Date(record.getMillis())));
				builder.append("[").append(record.getLevel()).append("] ");

				//Message
				builder.append(formatMessage(record));
				builder.append("\n");

				//Print stacktrace if an error occurred
				if(record.getThrown() != null) {
					StringWriter errors = new StringWriter();
					record.getThrown().printStackTrace(new PrintWriter(errors));
					builder.append(errors.toString());

					//Optional error dialog
				}
				return builder.toString();
			}

		};

		//Print INFO logs to System.out
		StreamHandler handlerInfo = new StreamHandler(System.out, formatter) {

			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);
				flush();
			}

		};
		handlerInfo.setFilter((LogRecord record) -> { return record.getLevel().equals(Level.INFO); }); //Only show INFO logs
		handlerInfo.setLevel(Level.INFO);

		//Print other logs to System.err and report them to Rollbar
		ConsoleHandler handler = new ConsoleHandler() {

			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);

				if(rollbar != null)
					switch(record.getLevel().getName()) {
					case "SEVERE":
						rollbar.error(record.getThrown());
						break;
					case "WARNING":
						rollbar.warning(record.getThrown());
						break;
				}
				flush();
			}

		};
		handler.setFilter((LogRecord record) -> { return !record.getLevel().equals(Level.INFO); }); //All but INFO logs
		handler.setFormatter(formatter);

		logger.setUseParentHandlers(false);
		logger.addHandler(handlerInfo);
		logger.addHandler(handler);

	}

	private static void setupErrorHandling() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		});
		setupRollbar();
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
