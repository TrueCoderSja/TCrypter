package ml.truecoder.vlc_lock;

import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;

import ml.truecoder.vlc_lock.Decrypt.WrongPasswordException;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class DecryptScreen extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPasswordField passwordField;
	private File[] selectedFiles;
	private JButton pipeBtn;
	private static ProgressScreen progressScreen;

	/**
	 * Create the panel.
	 */
	public DecryptScreen() {
		setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));
		GridLayout gridLayout = new GridLayout(6,1);
		setLayout(gridLayout);
		
		JLabel lblNewLabel = new JLabel("Encrypted File:");
		add(lblNewLabel);
		
		JButton chooseBtn = new JButton("Choose:");
		chooseBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setMultiSelectionEnabled(true);
				if(fileChooser.showOpenDialog(Window.window)==JFileChooser.APPROVE_OPTION) {
					selectedFiles=fileChooser.getSelectedFiles();
					if(selectedFiles.length>1)
						pipeBtn.setEnabled(false);
					else
						pipeBtn.setEnabled(true);
				}
			}
		});
		add(chooseBtn);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		add(lblNewLabel_1);
		
		passwordField = new JPasswordField();
		add(passwordField);
		
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton decryptBtn = new JButton("Decrypt File");
		panel.add(decryptBtn);
		
		JButton copy_decryptBtn = new JButton("Make a copy and then Decrypt");	
		panel.add(copy_decryptBtn);
		
		pipeBtn = new JButton("Pipe to Command");
		add(pipeBtn);
		
		ActionListener btnClickListener=new ButtonClickAction(decryptBtn, copy_decryptBtn, pipeBtn);
		decryptBtn.addActionListener(btnClickListener);
		copy_decryptBtn.addActionListener(btnClickListener);
		pipeBtn.addActionListener(btnClickListener);
	}
	
	
	class ButtonClickAction implements ActionListener {
		private JButton decryptBtn, copyDecryptBtn, pipeBtn;
		
		ButtonClickAction(JButton decryptBtn, JButton copyDecryptBtn, JButton pipeBtn) {		
			this.decryptBtn=decryptBtn;
			this.copyDecryptBtn=copyDecryptBtn;
			this.pipeBtn=pipeBtn;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String password=new String(passwordField.getPassword());
			
			if(selectedFiles==null) {
				JOptionPane.showMessageDialog(Window.window, "Please select a file!");
			}
			else if(password.length()==0) {
				JOptionPane.showMessageDialog(Window.window, "Password cannot be empty!");
			}
			else {
				Thread decryptQueuer=new Thread() {
					@Override
					public void run() {
						boolean decryptSuccessfull=false;
						boolean continueConsent=false;
						boolean stopped=false;
						List<String> decryptedFiles=new ArrayList<String>();						

						File targetLocation = null;
						
						
						if(e.getSource()==copyDecryptBtn) {
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
						else if(e.getSource()==pipeBtn) {
							targetLocation=null;
							String[] commands=new String[Constants.TABLE_DATA.size()];
							for(int i=0;i<commands.length;i++)
								commands[i]=Constants.TABLE_DATA.get(i)[0];
							String choice=(String) JOptionPane.showInputDialog(Window.window, "Select the command to pipe:", "Select Command:", JOptionPane.QUESTION_MESSAGE, null, commands, null);
							if(choice==null)
								return;
							Session.store("pipeProgram", choice);
						}
						
						for(File selectedFile:selectedFiles) {
							try {
								Decrypt decryptObj = null;
								if(e.getSource()==decryptBtn)
									decryptObj=new Decrypt(password, selectedFile, selectedFile);
								else if(e.getSource()==copyDecryptBtn) {
									if(targetLocation==null)
										break;
									decryptObj=new Decrypt(password, selectedFile, targetLocation);
								}
								else if(e.getSource()==pipeBtn)
									decryptObj=new Decrypt(password, selectedFile, null);
								boolean toNotify=(Integer.parseInt(Session.retrieve(Constants.TO_NOTIFY))==1)?true:false;
								if(!toNotify && !continueConsent) {
									String message="<html>You have chosen to decrypt the file in place but the "
											+ "notify attribute of the selected file(s) was disabled at the encrption time<br>"
											+ "If you entered wrong password then the file may corrupt and you may lose the file(s) forever<br>"
											+ "It is advised to copy and decrpt for un-notified file(s)<br><br>"
											+ "Continue decrypting at you own risk. Continue?<br>";
									
									if(decryptedFiles.size()>0) {
										message+="However, some files have been decrypted successfully<br>"
												+ "The files that were decrypted successfully are:<br>";
										for(String fileName:decryptedFiles)
											message+="*| "+fileName+"<br>";
									}
									
									message+="</html>";
									int choice=JOptionPane.showConfirmDialog(Window.window, message, "Continue at your own risk", JOptionPane.YES_NO_OPTION);
									if(choice==JOptionPane.NO_OPTION)
										break;
									else if(!continueConsent)
										continueConsent=true;
								}
								
								Thread decryptThread=new Thread(decryptObj);
								decryptThread.start();
								//TODO display progress bar
								decryptThread.join();
								
								if(toNotify) {
									decryptSuccessfull=true;
									decryptedFiles.add(selectedFile.getName());
								}
								
							}
							catch(WrongPasswordException ex) {
								stopped=true;
								if(!decryptSuccessfull) {
									JOptionPane.showMessageDialog(Window.window, "Wrong Password!");
									break;
								}
								else {
									String message="<html>The password is different for different files:<br>"
											+ "Select only the files which have common password<br><br>"
											+ "However, some files has been decrypted successfully<br>"
											+ "The files that were decrypted successfully are:<br>";
									for(String fileName:decryptedFiles)
										message+="*| "+fileName+"<br>";
									message+="</html>";
									JOptionPane.showMessageDialog(Window.window, message);
									break;
								}								
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							progressScreen.updateFile();
						}
						
						Window.switchPane(DecryptScreen.this, "Decrypt File");
						if(!stopped)
							JOptionPane.showMessageDialog(Window.window, "All files decrypted successfully!");
					}
				};
				
				progressScreen=new ProgressScreen(selectedFiles.length, ProgressScreen.DECRYPT_MODE);
				Window.switchPane(progressScreen, "Decrypting...");
				decryptQueuer.start();
			}
		}
	
	}
	
	public static ProgressScreen getProgressScreenObj() {
		return progressScreen;
	}
}
