package miles;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MILESDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1732506552110430870L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JDialog thisDialog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			MILESDialog dialog = new MILESDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public MILESDialog() { this((Frame)null); }
	
	public MILESDialog(Frame frame) {
		super(frame);
		thisDialog = this;
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("MILES");
		setBounds(100, 100, 302, 187);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("right:default"),
				ColumnSpec.decode("center:10dlu"),
				ColumnSpec.decode("left:default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblId = new JLabel("ID:");
		lblId.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblId, "1, 2");


		textField = new JTextField();
		contentPanel.add(textField, "3, 2, fill, default");
		textField.setColumns(10);


		JLabel lblProject = new JLabel("Project:");
		contentPanel.add(lblProject, "1, 3, 1, 4");


		textField_1 = new JTextField();
		contentPanel.add(textField_1, "3, 5, fill, default");
		textField_1.setColumns(10);


		JLabel label = new JLabel("MDT Path:");
		contentPanel.add(label, "1, 8");


		textField_2 = new JTextField();
		contentPanel.add(textField_2, "3, 8, fill, default");
		textField_2.setColumns(10);


		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);


		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisDialog.setVisible(false);
				thisDialog.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);


	}

}
