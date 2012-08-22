package impossible.dal;
import impossible.model.Graph;

public interface InputGraphStreamer {
	boolean hasNext();
	Graph getNext();
}
