package edu.put.et.stik.mm.dal;
import edu.put.et.stik.mm.dto.GraphDTO;

public interface InputGraphStreamer {
	boolean hasNext();
	GraphDTO getNext();
}
