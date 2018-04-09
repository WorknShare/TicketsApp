package fr.worknshare.tickets.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fr.worknshare.tickets.view.Paginator;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Super-class for models.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class Model<T> extends RecursiveTreeObject<T> {

	private SimpleIntegerProperty id;
	private Paginator paginator;
	
	public Model(int id) {
		this.id = new SimpleIntegerProperty(id);
	}

	public final SimpleIntegerProperty getId() {
		return id;
	}
	
	public final Paginator getPaginator() {
		return paginator;
	}

	public final void setPaginator(Paginator paginator) {
		this.paginator = paginator;
	}

}
