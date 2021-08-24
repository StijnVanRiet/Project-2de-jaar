package domain;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class CommentManager extends DCLoggedInUser {

	private final Ticket t;
	private ObservableList<String> commentsList;
	
	
	public CommentManager(Ticket t) {
		this.t = t;
		commentsList = FXCollections.observableArrayList(t.getComments());
	}
	
	public ObservableList<String> getCommentsList() {
		return FXCollections.unmodifiableObservableList(commentsList);
	}

	public boolean noComments() {
		return commentsList.isEmpty();
	}

	public void addObserver(ListChangeListener<String> listener) {
		commentsList.addListener(listener);
	}

}
