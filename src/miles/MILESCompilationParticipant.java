package miles;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class MILESCompilationParticipant extends org.eclipse.jdt.core.compiler.CompilationParticipant {
	
	private static final String[] MARKER_ATTRIBUTES = {IMarker.SEVERITY, IMarker.LINE_NUMBER, IMarker.MESSAGE};

	public MILESCompilationParticipant() {}
	
	public void buildStarting(BuildContext[] files, boolean isBatch){
	}
	
	public void buildFinished(IJavaProject project) {
		try {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
			String fileName = prefs.get("FileName", "Empty String");
			PrintWriter out = new PrintWriter(new FileWriter(fileName, true));
			out.println("\t\t<COMPILE_INSTANCE>");
			Calendar c = Calendar.getInstance();
			out.println("\t\t\t<TIME>\r\n\t\t\t\t" + c.getTimeInMillis() + "\r\n\t\t\t</TIME>");
			IPackageFragment[] packages = project.getPackageFragments();
			for(IPackageFragment aPackage : packages){
				if(aPackage.getKind() == IPackageFragmentRoot.K_SOURCE){
					out.println("\t\t\t<FILES>");
					for(ICompilationUnit unit : aPackage.getCompilationUnits()){
						out.println("\t\t\t\t<FILE>");
						out.println("\t\t\t\t\t<NAME>"+unit.getElementName()+"</NAME>");
						printFileInternals(unit, out);
						printFilesProblems(unit, out);
						out.println("\t\t\t\t\t<SOURCE>"+unit.getSource()+"\r\n\t\t\t\t\t</SOURCE>");
						out.println("\t\t\t\t</FILE>");
					}
					out.println("\t\t\t</FILES>");
				}
			}
			out.println("\t\t</COMPILE_INSTANCE>");
			out.close();
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Threw an error.");
		}
		
		// do nothing by default
	}

	private void printFilesProblems(ICompilationUnit unit, PrintWriter out)
			throws JavaModelException, CoreException {
		IResource javaSourceFile = unit.getUnderlyingResource();
		IMarker[] markers = javaSourceFile.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		if(markers != null && markers.length > 0){
			out.println("\t\t\t\t\t<COMPILE_PROBLEMS>");
			for(IMarker marker : markers){
				Object[] attribs = marker.getAttributes(MARKER_ATTRIBUTES);
				out.println("\t\t\t\t\t\t" + ProblemXML(attribs[0], attribs[1], attribs[2]));
			}
			out.println("\t\t\t\t\t</COMPILE_PROBLEMS>");
		}
	}
	
	private void printFileInternals(ICompilationUnit unit, PrintWriter out) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		if(allTypes.length > 0){
			out.println("\t\t\t\t\t<TYPES>");
			for(IType type : allTypes){
				out.println("\t\t\t\t\t\t<TYPE NAME=\""+ type.getElementName() + "\">");
				printSuperclassAndInterfaces(type, out);
				printMethods(type, out);
				out.println("\t\t\t\t\t\t</TYPE>");
			}
			out.println("\t\t\t\t\t</TYPES>");
		}
	}

	private void printMethods(IType type, PrintWriter out)  throws JavaModelException {
		IMethod[] methods = type.getMethods();
		if(methods.length > 0){
			out.println("\t\t\t\t\t\t\t<METHODS>");
			for (IMethod method : methods) {
				out.println("\t\t\t\t\t\t\t\t<METHOD NAME=\"" + method.getElementName()+"\">");
				out.println("\t\t\t\t\t\t\t\t\t<SIGNATURE>" + method.getSignature()+"</SIGNATURE>");
				out.println("\t\t\t\t\t\t\t\t\t<RETURNTYPE>" + method.getReturnType()+"</RETURNTYPE>");
				out.println("\t\t\t\t\t\t\t\t</METHOD>");
			}
			out.println("\t\t\t\t\t\t\t</METHODS>");
		}
	}

	private void printSuperclassAndInterfaces(IType type, PrintWriter out)
			throws JavaModelException {
		String superclass = type.getSuperclassName();
		if(superclass != null) out.println("\t\t\t\t\t\t\t<SUPERCLASS>"+superclass+"</SUPERCLASS>");
		String[] interfaces = type.getSuperInterfaceNames();
		if(interfaces.length > 0){
			out.println("\t\t\t\t\t\t\t<INTERFACES>");
			for(String anInterface : interfaces){
				out.println("\t\t\t\t\t\t\t\t<INTERFACE>"+ anInterface + "</INTERFACE>");
			}
			out.println("\t\t\t\t\t\t\t</INTERFACES>");
		}
	}
	
	private String ProblemXML(Object severity, Object lineNumber, Object message){
		String internal = "LINE=\"" + lineNumber + "\">" + message;
		if(severity.equals(IMarker.SEVERITY_ERROR)) return "<ERROR " + internal + "</ERROR>";
		if(severity.equals(IMarker.SEVERITY_WARNING)) return "<WARNING " + internal + "</WARNING>";
		return "<INFORMATION " + internal + "</INFORMATION>";
	}
	
	public boolean isActive(IJavaProject project) {
		return true;
	}
}
