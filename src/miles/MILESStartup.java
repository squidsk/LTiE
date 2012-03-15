package miles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class MILESStartup implements IStartup {

	final IWorkbench workbench = PlatformUI.getWorkbench();

	@Override
	public void earlyStartup() {
	    workbench.getDisplay().asyncExec(new Runnable() {
	      public void run() {
	    	  LoadXSLFile();
	    	  XSLTransform();
	    	  IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
	    	  if (window != null) {
	    		  MDialog dlg = new MDialog(window.getShell(), "MILES Input Dialog");
	    		  dlg.open();
	    	  }
	       }
	     });		
	}
	
	private void LoadXSLFile(){
			try {
				URL url = FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.xsl"));
				Scanner xslFile = new Scanner(url.openStream());
				File dtdFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.dtd")).toURI());

				xslFile.useDelimiter("\\Z");
				IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
				prefs.put("XSL", xslFile.next());
				prefs.put("DTD", dtdFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	}
	
	public void XSLTransform(){
		TransformerFactory tFactory= TransformerFactory.newInstance();
		try {	
			File xslFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.xsl")).toURI());
			File xmlFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.MTD")).toURI());
			File htmlFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.html")).toURI());
			Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));
			transformer.transform(new StreamSource(xmlFile), new StreamResult(new FileOutputStream(htmlFile)));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

}
