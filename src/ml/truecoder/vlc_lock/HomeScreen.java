package ml.truecoder.vlc_lock;

import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HomeScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public HomeScreen() {
		setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));
		setLayout(new BorderLayout(0, 0));
		
		JLabel heading = new JLabel("TCrypter");
		heading.setOpaque(true);
		heading.setBackground(Color.BLACK);
		heading.setForeground(Color.GREEN);
		heading.setFont(new Font("Tahoma", Font.PLAIN, 40));
		add(heading, BorderLayout.NORTH);
		
		JPanel sub_container = new JPanel();
		add(sub_container, BorderLayout.CENTER);
		sub_container.setLayout(new GridLayout(2, 1));
		
		JButton encryptBtn = new JButton("Encrypt Files");
		encryptBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Window.switchPane(new EncryptScreen(), "Encrypt File");
			}
		});
		encryptBtn.setFont(new Font("Calibri", Font.PLAIN, 25));
		sub_container.add(encryptBtn);
		
		JButton btnNewButton = new JButton("Decrypt Files");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Window.switchPane(new DecryptScreen(), "Decrypt File");
			}
		});
		btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 25));
		sub_container.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Made By: TrueCoder");
		lblNewLabel.setBackground(new Color(0, 0, 0));
		lblNewLabel.setForeground(new Color(255, 99, 71));
		lblNewLabel.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
		add(lblNewLabel, BorderLayout.SOUTH);
		lblNewLabel.setOpaque(true);
	}

}
