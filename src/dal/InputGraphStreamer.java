package dal;
import dto.GraphDTO;

public interface InputGraphStreamer {
	boolean hasNext();
	GraphDTO getNext();
}
