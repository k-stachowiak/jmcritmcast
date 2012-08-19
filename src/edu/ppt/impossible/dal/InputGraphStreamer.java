package edu.ppt.impossible.dal;
import edu.ppt.impossible.model.Graph;

public interface InputGraphStreamer {
	boolean hasNext();
	Graph getNext();
}
