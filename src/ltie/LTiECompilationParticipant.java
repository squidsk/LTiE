package ltie;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.apache.commons.lang3.StringEscapeUtils;

@SuppressWarnings("restriction")
public class LTiECompilationParticipant extends org.eclipse.jdt.core.compiler.CompilationParticipant {
	
	private static final String[] MARKER_ATTRIBUTES = {IMarker.SEVERITY, IMarker.LINE_NUMBER, IMarker.MESSAGE};
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public LTiECompilationParticipant() {}
	
	public void buildStarting(BuildContext[] files, boolean isBatch) {}
	
	public void buildFinished(IJavaProject project) {
		try {
			StringBuilder sb = new StringBuilder();
			PreProcessing(project.getElementName());
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME);
			String fileName = prefs.get("LTiEFileName", "Empty String") + project.getElementName() + ".MTD";
			sb.append("<COMPILE_INSTANCE>");
			Calendar c = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss z");
			sb.append("<TIME UTC=\"" + c.getTimeInMillis() + "\">" + format.format(c.getTime()) + "</TIME>");
			IPackageFragment[] packages = project.getPackageFragments();
			sb.append("<PACKAGES>");
			for(IPackageFragment aPackage : packages){
				if(aPackage.getKind() == IPackageFragmentRoot.K_SOURCE){
					String packageName = aPackage.getElementName().isEmpty() ? "default" : aPackage.getElementName();
					Constants.writer.println("Package Fragment Name: " + packageName);
					sb.append("<PACKAGE>");
					sb.append("<NAME>"+ packageName +"</NAME>");
					sb.append("<FILES>");
					for(ICompilationUnit unit : aPackage.getCompilationUnits()){
						sb.append("<FILE>");
						sb.append("<NAME>"+unit.getElementName()+"</NAME>");
						printFileInternals(unit, sb);
						printFilesProblems(unit, sb);
						sb.append("<SOURCE>"+StringEscapeUtils.escapeHtml4(unit.getSource())+"</SOURCE>");
						sb.append("</FILE>");
					}
					ProcessUMLFiles(aPackage.getNonJavaResources(), sb);
					sb.append("</FILES>");
					sb.append("</PACKAGE>");
				}
			}
			sb.append("</PACKAGES>");
			Object[] nonJavaResources = project.getNonJavaResources();
			if(nonJavaResources !=null && nonJavaResources.length > 0){
				sb.append("<FILES>");
				ProcessUMLFiles(nonJavaResources, sb);
				sb.append("</FILES>");
			}
			sb.append("</COMPILE_INSTANCE>");
			PrintWriter out = new PrintWriter(new FileWriter(fileName, true));
			out.write(XMLFormatter.format(sb.toString()));
			out.close();
		} catch (Exception e){
			e.printStackTrace(Constants.writer);
		}
	}
	
	private void ProcessUMLFiles(Object[] packageFragments, StringBuilder sb) throws FileNotFoundException {
		for(Object obj : packageFragments){
			if(obj instanceof File){
				File file = (File)obj;
				if(file.getFileExtension().equals("uml")){
					sb.append("<FILE>");
					sb.append("<NAME>"+file.getName()+"</NAME>");
					sb.append("<SOURCE>");
					Scanner s = new Scanner(new FileInputStream(file.getRawLocation().toString()));
					s.useDelimiter("\\Z");
					sb.append(StringEscapeUtils.escapeHtml4(s.next()));
					s.close();
					sb.append("</SOURCE>");
					sb.append("</FILE>");
				}
			}
		}

	}
	
	private void PreProcessing(String projectName) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME);
		String fileName = prefs.get("LTiEFileName", null);
		java.io.File f = new java.io.File(fileName + projectName + ".MTD");
		if(!f.exists()){
			String id = "default";
			String UNCTime;
			Calendar calendar = Calendar.getInstance();
			if(fileName != null){
				int index = fileName.indexOf('_');
				int index2 = fileName.indexOf('_', index + 1);
				UNCTime = fileName.substring(index2 + 1, fileName.length()-1);
				id = fileName.substring(index + 1, index2);
				calendar.setTimeInMillis(Long.parseLong(UNCTime));
			}

			try {
				PrintStream p = new PrintStream(f);
				p.print(CreateStartOfSessionString(calendar, id, projectName));
				p.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private String CreateStartOfSessionString(Calendar calendar, String id, String asgn) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss z");
		String docTypeHeader = "<!DOCTYPE MILES PUBLIC \"-//AU//DTD LTiE//EN\" \"http://io.acad.athabascau.ca/~stevenka13/MILES.dtd\">";

		return XML_HEADER + "\r\n" + docTypeHeader + "\r\n" 
				+ "<MILES>\r\n\t<SESSION_INFO>\r\n\t\t<SESSION_START_TIME UTC=\""+ calendar.getTimeInMillis() +"\">\r\n\t\t\t" 
				+ format.format(calendar.getTime()) + "\r\n\t\t</SESSION_START_TIME>\r\n\t\t<STUDENT_ID>\r\n\t\t\t" 
				+ id + "\r\n\t\t</STUDENT_ID>" + "\r\n\t\t<ASSIGNMENT>\r\n\t\t\t"
				+ asgn + "\r\n\t\t</ASSIGNMENT>\r\n\t</SESSION_INFO>\r\n\t<SESSION_DATA>\r\n";
	}

	private void printFilesProblems(ICompilationUnit unit, StringBuilder sb)
			throws JavaModelException, CoreException {
		IResource javaSourceFile = unit.getUnderlyingResource();
		IMarker[] markers = javaSourceFile.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		if(markers != null && markers.length > 0){
			Document doc = new Document(unit.getSource());
			
			sb.append("<COMPILE_PROBLEMS>");
			for(IMarker marker : markers){
				printProblemXML(marker, doc, sb);
			}
			sb.append("</COMPILE_PROBLEMS>");
		}
	}
	
	private void printFileInternals(ICompilationUnit unit, StringBuilder sb) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		if(allTypes.length > 0){
			sb.append("<TYPES>");
			for(IType type : allTypes){
				sb.append("<TYPE NAME=\""+ type.getElementName() + "\">");
				printSuperclassAndInterfaces(type, sb);
				printMethods(type, sb);
				sb.append("</TYPE>");
			}
			sb.append("</TYPES>");
		}
	}

	private void printMethods(IType type, StringBuilder sb)  throws JavaModelException {
		IMethod[] methods = type.getMethods();
		if(methods.length > 0){
			sb.append("<METHODS>");
			for (IMethod method : methods) {
				sb.append("<METHOD NAME=\"" + method.getElementName()+"\">");
				ILocalVariable[] vars  = method.getParameters();
				sb.append("<PARAMETERS>");
				if(vars.length > 0){
					for(int i=0; i<vars.length; i+=1){
						System.out.println(vars[i].toString());
						System.out.println(vars[i].getSource());
						sb.append("<PARAMETER>" + vars[i].getSource() + "</PARAMETER>");
					}
				} else {
					sb.append("<PARAMETER>void</PARAMETER>");
				}
				sb.append("</PARAMETERS>");
				//method.gets
				sb.append("<SIGNATURE>" + method.getSignature()+"</SIGNATURE>");
				sb.append("<RETURNTYPE>" + method.getReturnType()+"</RETURNTYPE>");
				sb.append("</METHOD>");
			}
			sb.append("</METHODS>");
		}
	}

	private void printSuperclassAndInterfaces(IType type, StringBuilder sb)
			throws JavaModelException {
		String superclass = type.getSuperclassName();
		if(superclass != null) sb.append("<SUPERCLASS>"+superclass+"</SUPERCLASS>");
		String[] interfaces = type.getSuperInterfaceNames();
		if(interfaces.length > 0){
			sb.append("<INTERFACES>");
			for(String anInterface : interfaces){
				sb.append("<INTERFACE>"+ anInterface + "</INTERFACE>");
			}
			sb.append("</INTERFACES>");
		}
	}
	
	private void printProblemXML(IMarker marker, Document doc, StringBuilder sb) throws JavaModelException, CoreException {
		Object[] attribs = marker.getAttributes(MARKER_ATTRIBUTES);
		String problemType;
		int lineNumber = 0;
		int startOfLine = 0;
		int lineLength = 0;
		String lineOfCode = "";
		try {
			lineNumber = Integer.parseInt(attribs[1].toString()) - 1;
			startOfLine = doc.getLineOffset(lineNumber);
			lineLength = doc.getLineLength(lineNumber);
			lineOfCode = doc.get(startOfLine, lineLength);
		} catch (NumberFormatException e) {
			e.printStackTrace(Constants.writer);
		} catch (BadLocationException e) {
			e.printStackTrace(Constants.writer);
		}
		
		if(attribs[0].equals(IMarker.SEVERITY_ERROR)) problemType = "ERROR";
		else if (attribs[0].equals(IMarker.SEVERITY_WARNING)) problemType = "WARNING";
		else problemType = "INFORMATION";

		sb.append("<" + problemType + ">");
		sb.append("<LINE>" + attribs[1] + "</LINE>");
		sb.append("<MESSAGE>" + attribs[2] + "</MESSAGE>");
		sb.append("<CODELINE>" + lineOfCode.trim() + "</CODELINE>");
		sb.append("</" + problemType + ">");
	}
	
	public boolean isActive(IJavaProject project) {
		return true;
	}
	
}
