package dal;

import java.util.Scanner;

import dto.GraphDTO;

public interface GraphReader {
	GraphDTO readGraph(Scanner scanner);
}
