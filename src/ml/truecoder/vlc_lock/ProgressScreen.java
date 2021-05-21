package ml.truecoder.vlc_lock;

import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JProgressBar;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class ProgressScreen extends JPanel {
	public static final int ENCRYPT_MODE=1;
	public static final int DECRYPT_MODE=2;
	
	private static final long serialVersionUID = 1L;
	private JLabel currentLabel;
	private JLabel overall_Label;
	private JProgressBar overAll_ProgressBar;
	private JProgressBar current_ProgressBar;
	private int currentFile;
	private String prefix;
	private int totalFiles;

	/**
	 * Create the panel.
	 */
	public ProgressScreen(int totalFiles, int mode) {
		setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));
		setLayout(new GridLayout(2, 1));
		
		if(mode==ENCRYPT_MODE)
			prefix="Encrypting ";
		else if(mode==DECRYPT_MODE)
			prefix="Decrpyting ";
		else
			throw new RuntimeException("Illegal Mode Specified!");
		
		this.totalFiles=totalFiles;
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		overAll_ProgressBar = new JProgressBar();
		overAll_ProgressBar.setMaximum(totalFiles);
		panel.add(overAll_ProgressBar, BorderLayout.CENTER);
		
		overall_Label = new JLabel(prefix+" file "+currentFile+" of "+totalFiles+" | "+(int)((double)currentFile/(double)totalFiles*100.0)+"% complete");
		overall_Label.setFont(new Font("Tahoma", Font.PLAIN, 16));
		overall_Label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(overall_Label, BorderLayout.SOUTH);
		
		JLabel lblNewLabel_2 = new JLabel("Overall Progress:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panel.add(lblNewLabel_2, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		current_ProgressBar = new JProgressBar();
		panel_1.add(current_ProgressBar, BorderLayout.CENTER);
		
		currentLabel = new JLabel("-");
		currentLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		currentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(currentLabel, BorderLayout.SOUTH);
		
		JLabel lblNewLabel_3 = new JLabel("Current Progress:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_1.add(lblNewLabel_3, BorderLayout.NORTH);
	}
	
	public void updateFile() {
		currentFile++;
		overAll_ProgressBar.setValue(currentFile);
		overall_Label.setText(prefix+" file "+currentFile+" of "+totalFiles+" | "+(int)((double)currentFile/(double)totalFiles*100.0)+"% complete");
	}
	
	public void updateCurrent(long current, long max, String fileName) {
		int percent=(int)((double)current/(double)max*100.0);
		current_ProgressBar.setMaximum(100);
		current_ProgressBar.setValue(percent);
		if(fileName.length()>8) {
			fileName=fileName.substring(0, 7)+"..";
		}
		currentLabel.setText(prefix+" file: \""+fileName+"\" | "+percent+"% complete");
	}
}
