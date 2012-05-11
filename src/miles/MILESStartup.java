package miles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class MILESStartup implements IStartup {

	final IWorkbench workbench = PlatformUI.getWorkbench();
	private static final String XSL_FILE = "http://io.acad.athabascau.ca/~stevenka13/MILES.xsl";

	@Override
	public void earlyStartup() {
	    workbench.getDisplay().asyncExec(new Runnable() {
	      public void run() {
	    	  //LoadXSLFile();
	    	  //XSLTransform();
	    	  startLTiE();
	    	  IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
	    	  if (window != null) {
	    		  MDialog dlg = new MDialog(window.getShell(), "MILES Input Dialog");
	    		  dlg.open();
	    	  }
	       }
	     });		
	}
	
	private void startLTiE() {	
		createProject();
		addShutDownHook();
	}

	private void addShutDownHook() {
		final String dirName = ResourcesPlugin.getWorkspace().getRoot().getProject("MILESData").getLocation().toString();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (PlatformUI.isWorkbenchRunning()) {
					PlatformUI.getWorkbench().close();
				}
				try {
					IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
					String fileName = prefs.get("LTiEFileName", null);
					File[] files = Filter.finder(dirName, fileName.substring(fileName.lastIndexOf('/') + 1));
					for(File file : files){
						writeClosingXML(file.getAbsolutePath());
					}
				} catch (IOException e) {
					
				}						
			}

		});
	}

	private void writeClosingXML(String fileName) throws IOException {
		FileWriter writer = new FileWriter(fileName, true);
		PrintWriter out = new PrintWriter(writer);
		out.println("\t</SESSION_DATA>");
		out.println("</MILES>");
		out.close();
		writer.close();
		XSLTransform(fileName);
	}
	
	private void createProject() {
		IProject myProject = ResourcesPlugin.getWorkspace().getRoot().getProject("MILESData");
		try {
			if(!myProject.exists()) myProject.create(null);
		} catch (CoreException e){
			
		}
	}	

	private void XSLTransform(String filename){
		TransformerFactory tFactory= TransformerFactory.newInstance();
		PrintWriter p = null; 
		try {
			p = new PrintWriter(new FileWriter("c:\\log.txt"));
			String htmlFilename = filename.substring(0, filename.lastIndexOf('.') + 1) + "html";
			
			Transformer transformer = tFactory.newTransformer(new StreamSource(XSL_FILE));
			transformer.transform(new StreamSource(new FileInputStream(filename)), new StreamResult(new FileOutputStream(htmlFilename)));
		} catch (TransformerConfigurationException e) {	
			e.printStackTrace(p);
		} catch (FileNotFoundException e) {
			e.printStackTrace(p);	
		} catch (TransformerException e) {
			e.printStackTrace(p);
		} catch (IOException e) {
			e.printStackTrace(p);
		} finally {
			if(p != null) p.close();
		}
	}
//	private void LoadXSLFile(){
//			try {
//				URL url = FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.xsl"));
//				Scanner xslFile = new Scanner(url.openStream());
//				File dtdFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.dtd")).toURI());
//
//				xslFile.useDelimiter("\\Z");
//				IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
//				prefs.put("XSL", xslFile.next());
//				prefs.put("DTD", dtdFile.getAbsolutePath());
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//	}
//	
//	public void XSLTransform(){
//		TransformerFactory tFactory= TransformerFactory.newInstance();
//		try {
//			Bundle bundle = Platform.getBundle("MILES");
//			String[] bundlePaths = bundle.getLocation().split("@");
//			System.out.println(bundlePaths[0]);
//			File xslFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.xsl")).toURI());
//			File xmlFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.MTD")).toURI());
//			File htmlFile = new File(FileLocator.toFileURL(Platform.getBundle("MILES").getEntry("/MILES.html")).toURI());
//			Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));
//			transformer.transform(new StreamSource(xmlFile), new StreamResult(new FileOutputStream(htmlFile)));
//		} catch (TransformerConfigurationException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//		
//	}

}
