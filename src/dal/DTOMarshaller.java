package dal;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class DTOMarshaller<T> {
	
	public String writeToString(T object) {		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.marshal(object, writer);
			return writer.toString();
			
		} catch (JAXBException exception) {
			System.err.print("JAXB Exception : " + exception.getMessage());
			System.err.print(exception.getStackTrace());
			return null;
		}
	}
	
	public T readFromString(String string, Class<T> clazz) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			T result = clazz.cast(unmarshaller.unmarshal(new StringReader(string)));
			return result;
		} catch (JAXBException exception) {
			System.err.print("JAXB Exception : " + exception.getMessage());
			System.err.print(exception.getStackTrace());
			return null;
		}
	}

	public boolean writeToFile(String path, T object) {

		Writer writer = null;

		try {

			writer = new FileWriter(path);

			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(object, writer);
			return true;

		} catch (JAXBException exception) {
			System.err.print("JAXB Exception : " + exception.getMessage());
			System.err.print(exception.getStackTrace());
			return false;

		} catch (IOException exception) {
			System.err.print("IO Exception : " + exception.getMessage());
			System.err.print(exception.getStackTrace());
			return false;

		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
				return false;
			}
		}

	}
	
	public T readFromFile(String path, Class<T> clazz) {
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			T result = clazz.cast(unmarshaller.unmarshal(new FileReader(path)));
			return result;
		} catch (JAXBException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		}		
	}
}
