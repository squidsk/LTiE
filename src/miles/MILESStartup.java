package miles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class MILESStartup implements IStartup {

	final IWorkbench workbench = PlatformUI.getWorkbench();

	@Override
	public void earlyStartup() {
		// TODO Auto-generated method stub
		//MILESDialog dialog = new MILESDialog();
		//dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		//dialog.setVisible(true);
		//System.out.println(PlatformUI.getWorkbench().getWorkbenchWindowCount());
		//MessageDialog.openConfirm(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(), "Test", "Test Message");
	    workbench.getDisplay().asyncExec(new Runnable() {
	      public void run() {
	    	 XSLTransform();
	         IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
	         if (window != null) {
	        	 //MessageDialog.openConfirm(window.getShell(), "Test", "Test Message");
	        	 MDialog dlg = new MDialog(window.getShell(), "MILES Input Dialog");
	        	 dlg.open();
	         }
	       }
	     });		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
