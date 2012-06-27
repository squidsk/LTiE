package ltie;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

final class Constants {
	static final String BUNDLE_NAME = "LTiE";
	static final String OUTPUT_FOLDER = "LTiEData";
	
	static final PrintWriter writer;
	
	static {
		PrintWriter temp = null;
		try {
			temp = new PrintWriter(new FileWriter("c:\\log.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer = temp;
		}
	}
}
