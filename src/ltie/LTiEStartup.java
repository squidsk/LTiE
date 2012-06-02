package ltie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class LTiEStartup implements IStartup {

	final IWorkbench workbench = PlatformUI.getWorkbench();
	private static final String XSL_FILE= "http://io.acad.athabascau.ca/~stevenka13/MILES.xsl";
	
	static {
		//XSL_FILE = locateFile("MILES.xsl").toString();
	}

//	private static URI locateFile(String fullPath) {
//		try {
//			URL url = FileLocator.find(Platform.getBundle(Constants.BUNDLE_NAME), new Path(fullPath), null);
//			if(url != null)
//				return FileLocator.resolve(url).toURI();
//		} catch (Exception e) {
//			e.printStackTrace(Constants.writer);
//		}
//		return null;
//	}


	@Override
	public void earlyStartup() {
		System.out.println(XSL_FILE);
	    workbench.getDisplay().asyncExec(new Runnable() {
	      public void run() {
	    	  startLTiE();
	    	  IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
	    	  if (window != null) {
	    		  LTiEDialog dlg = new LTiEDialog(window.getShell(), "LTiE Input Dialog");
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
					IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME);
					String fileName = prefs.get("LTiEFileName", null);
					File[] files = Filter.finder(dirName, fileName.substring(fileName.lastIndexOf('/') + 1));
					for(File file : files){
						writeClosingXML(file.getAbsolutePath());
					}
				} catch (IOException e) {
					e.printStackTrace(Constants.writer);
				}						
			}

		});
		Constants.writer.close();
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
			e.printStackTrace(Constants.writer);
		}
	}	

	private void XSLTransform(String filename){
		TransformerFactory tFactory= TransformerFactory.newInstance();
		try {
			String htmlFilename = filename.substring(0, filename.lastIndexOf('.') + 1) + "html";
			Transformer transformer = tFactory.newTransformer(new StreamSource(XSL_FILE));
			transformer.transform(new StreamSource(new FileInputStream(filename)), new StreamResult(new FileOutputStream(htmlFilename)));
		} catch (TransformerConfigurationException e) {	
			e.printStackTrace(Constants.writer);
		} catch (FileNotFoundException e) {
			e.printStackTrace(Constants.writer);	
		} catch (TransformerException e) {
			e.printStackTrace(Constants.writer);
		}
	}
}
