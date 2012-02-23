package miles;

//import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.util.Calendar;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("deprecation")
public class MDialog extends Dialog {
	
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final String DOC_TYPE_HEADER = "<!DOCTYPE MILES SYSTEM \"MILES.dtd\">";

	private String title;
	private String id;
	private String asgn;
	private String time;
	//private String projLoc;
	
	private Text idText;
	private Text asignText;
	//private Text projLocText;
	

	public MDialog(Shell parentShell, String dialogTitle) {
		super(parentShell);
		title = dialogTitle;
		time = String.valueOf(Calendar.getInstance().getTimeInMillis());
	}
	
	/*
	 * (non-JavaDoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID){
			id = idText.getText();
			asgn = asignText.getText();
			//projLoc = projLocText.getText();
		}
	}

	/*
	 * (non-JavaDoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}
	
	/*
	 * (non-JavaDoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(final Composite parent) {
		// create OK and Cancel buttons by default
		Button okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		//do this here because setting the text will enable the OK button
		okButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//System.out.println("OK Button Clicked");
				if(validInput()){
					String fileName = "MILES_"+ id + "_" + asgn + "_" + time + ".MTD";
					
					writeSessionInfoToFile(fileName);
					savePluginSettings(fileName);
					Runtime.getRuntime().addShutdownHook(new Thread() {
						public void run() {
							if (PlatformUI.isWorkbenchRunning()) {
								PlatformUI.getWorkbench().close();
							}
							try {
								IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
								String fileName = prefs.get("FileName", "Empty String");
								FileWriter writer = new FileWriter(fileName, true);
								PrintWriter out = new PrintWriter(writer);
								out.println("\t</SESSION_DATA>");
								out.println("</MILES>");
								out.close();
								writer.close();
							} catch (IOException e) {
								
							}						
						}
					});

					parent.getShell().dispose();
				} else {
					showMessage();
				}
			}

			private boolean validInput() {
				return id != "" && asgn != "";
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("No sure when this is called");				
			}
			
		});
		
		cancelButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Cancel Button Clicked");
				parent.getShell().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		
		});
		idText.setFocus();
		if (id != null) {
			idText.setText(id);
			idText.selectAll();
		}
		if(asgn != null) asignText.setText(asgn);
		//if(projLoc != null) projLocText.setText(projLoc);
	}

	protected void writeSessionInfoToFile(String fileName) {
		IProject myProject = ResourcesPlugin.getWorkspace().getRoot().getProject("MILESData");
		try {
			if(!myProject.exists()) myProject.create(null);
			if(!myProject.isOpen()) myProject.open(null);

			IFile sessionFile =  myProject.getFile(fileName);
			String startOfSession = CreateStartOfSessionString();
			StringBufferInputStream s = new StringBufferInputStream(startOfSession);
			sessionFile.create(s, false, null);			
		} catch (CoreException e){
			
		}
	}	

	private String CreateStartOfSessionString() {
		return XML_HEADER + "\r\n" + DOC_TYPE_HEADER + "\r\n" 
				+ "<MILES>\r\n\t<SESSION_INFO>\r\n\t\t<SESSION_ID>\r\n\t\t\t" 
				+ time + "\r\n\t\t</SESSION_ID>\r\n\t\t<STUDENT_ID>\r\n\t\t\t" 
				+ id + "\r\n\t\t</STUDENT_ID>" + "\r\n\t\t<ASSIGNMENT>\r\n\t\t\t"
				+ asgn + "\r\n\t\t</ASSIGNMENT>\r\n\t</SESSION_INFO>\r\n\t<SESSION_DATA>\r\n";
	}

	private void showMessage() {
		MessageDialog.openError(super.getShell(), "Error!", "Values for the student ID number and the assignment!");
	}
	
	/*
	 * (non-JavaDoc) Method declared on Dialog.	
	 */
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		
		composite.setLayout(new GridLayout(2, false));
		
		new Label(composite, SWT.NONE).setText("ID: ");
		idText = new Text(composite, getInputTextStyle());
		idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(composite, SWT.NONE).setText("Assignment: ");
		asignText = new Text(composite, getInputTextStyle());
		asignText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
//		new Label(composite, SWT.NONE).setText("Project Location: ");
//		projLocText = new Text(composite, getInputTextStyle());
//		projLocText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Returns the style bits that should be used for the input text field.
	 * Defaults to a single line entry. Subclasses may override.
	 * 
	 * @return the integer style bits that should be used when creating the
	 *		 input text
	 * 
	 * @since 3.4
	 */
	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
	
	private void savePluginSettings(String fileName) {
		IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getProject("MILESData").getFile(fileName);	
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
		prefs.put("FileName", myFile.getRawLocation().toString());
	}
	
//	private void loadPluginSettings() {
//		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("MILES");
//		  // you might want to call prefs.sync() if you're worried about others changing your settings
//		  String someStr = prefs.get("ID", "");
//		  
//		}
}
