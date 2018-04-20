package fr.worknshare.tickets;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.rollbar.notifier.Rollbar;

/**
 * Custom console handler used to automatically report errors and warnings to Rollbar error tracker.<br>Ignores INFO logs.
 * @author Jérémy LAMBERT
 * 
 * @see Rollbar
 *
 */
public class RollbarConsoleHandler extends ConsoleHandler {

	private Rollbar rollbar;
	
	public RollbarConsoleHandler(Rollbar rollbar) {
		this.rollbar = rollbar;
		setFilter((LogRecord record) -> { return !record.getLevel().equals(Level.INFO); }); //All but INFO logs
	}
	
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
	
}
