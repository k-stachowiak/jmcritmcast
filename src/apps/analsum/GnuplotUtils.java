package apps.analsum;

import java.io.File;

public class GnuplotUtils {

	public static final String DIR_NAME = "./gnuplot";

	public static void createDirIfNotExists() {
		File dir = new File(DIR_NAME);
		if (!dir.exists()) {
			try {
				dir.mkdir();
			} catch (SecurityException ex) {
				ex.printStackTrace();
			}
		}
	}
}
