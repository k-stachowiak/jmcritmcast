package impossible.dal;
import impossible.dto.GraphDTO;

public interface InputGraphStreamer {
	boolean hasNext();
	GraphDTO getNext();
}
