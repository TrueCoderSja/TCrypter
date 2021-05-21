package ml.truecoder.vlc_lock;

import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.event.ActionEvent;

public class EncryptScreen extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPasswordField password1;
	private JPasswordField password2;
	private File[] selectedFiles;
	private JCheckBox chkBox;
	private JButton encryptBtn;
	private JButton copyEncryptBtn;
	private static ProgressScreen progressScreen;

	/**
	 * Create the panel.
	 */
	public EncryptScreen() {		
		setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));
		setLayout(new GridLayout(8, 1));
		
		ActionListener btnClickAction=new BtnClickAction();
		
		JLabel lbl1 = new JLabel("Choose Media File:");
		add(lbl1);
		
		JButton chooseBtn = new JButton("Choose");
		chooseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(true);
				if(fileChooser.showOpenDialog(Window.window)==JFileChooser.APPROVE_OPTION) {
					selectedFiles=fileChooser.getSelectedFiles();
				}
			}
		});
		add(chooseBtn);
		
		JLabel lblNewLabel_1 = new JLabel("Enter Password:");
		add(lblNewLabel_1);
		
		password1 = new JPasswordField();
		add(password1);
		
		JLabel lblNewLabel_2 = new JLabel("Confirm Password:");
		add(lblNewLabel_2);
		
		password2 = new JPasswordField();
		add(password2);
		
		chkBox = new JCheckBox("Notify for wrong password when decrypting");
		chkBox.setSelected(true);
		add(chkBox);
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		encryptBtn = new JButton("Encrypt");
		encryptBtn.addActionListener(btnClickAction);
		panel.add(encryptBtn);
		
		copyEncryptBtn = new JButton("Copy And Encrypt");
		copyEncryptBtn.addActionListener(btnClickAction);
		panel.add(copyEncryptBtn);
	}

	public static ProgressScreen getProgressScreenObject() {
		return progressScreen;
	}
	
	
	
	class BtnClickAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String password=new String(password1.getPassword());
			String passwordConfirm=new String(password2.getPassword());
			if(selectedFiles==null) {
				JOptionPane.showMessageDialog(Window.window, "Please select a file");
			}
			else if(password.length()<8) {
				JOptionPane.showMessageDialog(Window.window, "Password must be atleast 8 digits long");
			}
			else if(!password.equals(passwordConfirm)) {
				JOptionPane.showMessageDialog(Window.window, "Password do not match!");
			}
			else {
				progressScreen=new ProgressScreen(selectedFiles.length, ProgressScreen.ENCRYPT_MODE);
				Thread encryptQueuer=new Thread() {
					@Override
					public void run() {
						try {
							File targetLocation = null;
							if(e.getSource()==encryptBtn)
							{}
							else {
								JFileChooser dirChooser=new JFileChooser();					
								
								if(selectedFiles.length>1) {
									dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
									dirChooser.setDialogTitle("Save To:");
									if(dirChooser.showDialog(Window.window, "Save Here")==JFileChooser.APPROVE_OPTION) {
										targetLocation=dirChooser.getSelectedFile();
									}
								}
								else {
									if(dirChooser.showSaveDialog(Window.window)==JFileChooser.APPROVE_OPTION) {
										targetLocation=dirChooser.getSelectedFile();
									}
								}
							}
							
							for(File selectedFile:selectedFiles) {
								Encrypt encryptObj = null;
								boolean notify=chkBox.isSelected();
								encryptObj=new Encrypt(notify, password, selectedFile, targetLocation);
								
								Thread encrypterThread=new Thread(encryptObj);
								encrypterThread.start();
								//TODO display progress bar
								encrypterThread.join();	
								progressScreen.updateFile();						
							}
							Window.switchPane(EncryptScreen.this, "Encrypt File");
							JOptionPane.showMessageDialog(Window.window, "Files Encrypted Successfully!");
						}
						catch(FileNotFoundException ex) {
							String error="Unable to read a file\n"
									+ "Make sure that the file really exists or try running the program as administrator.\n"
									+ "Techical Info:\n"
									+ ex.getMessage();
							
							JOptionPane.showMessageDialog(Window.window, error, "Unable To Read File", JOptionPane.ERROR_MESSAGE);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				};
				Window.switchPane(progressScreen, "Encrypting...");
				encryptQueuer.start();
			}
		}
	}
}
