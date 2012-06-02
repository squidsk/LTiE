package ltie;

import java.util.Calendar;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
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

public class LTiEDialog extends Dialog {
	

	private String title;
	private String id;
	private String time;
	
	private Text idText;
	

	public LTiEDialog(Shell parentShell, String dialogTitle) {
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
			setReturnCode(OK);
		} else {
			setReturnCode(CANCEL);
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
				if(validInput()){
					String fileName = "LTiE_" + id + "_" + time + "_";
					
					savePluginSettings(fileName, "LTiEFileName");
					parent.getShell().dispose();
				} else {
					showMessage();
				}
			}

			private boolean validInput() {
				return id != "";
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("No sure when this is called");				
			}
			
		});
		
		cancelButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.getShell().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		
		});
		idText.setFocus();
		if (id != null) {
			idText.setText(id);
			idText.selectAll();
		}
	}
		
	private void showMessage() {
		MessageDialog.openError(super.getShell(), "Error!", "Enter a user ID!");
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
	
	private void savePluginSettings(String fileName, String settingName) {
		IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getProject("MILESData").getFile(fileName);	
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME);
		prefs.put(settingName, myFile.getRawLocation().toString());
	}
	
//	private void loadPluginSettings() {
//		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME);
//		  // you might want to call prefs.sync() if you're worried about others changing your settings
//		  String someStr = prefs.get("ID", "");
//		  
//		}
}
