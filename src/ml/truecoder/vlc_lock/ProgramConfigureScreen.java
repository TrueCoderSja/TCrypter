package ml.truecoder.vlc_lock;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Insets;
import java.io.IOException;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProgramConfigureScreen extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;	
	private JButton removeBtn = null;
	private int selectedRow;
	private boolean removed, insert;

	public ProgramConfigureScreen() throws ClassNotFoundException, IOException {
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		
		
		
		table = new JTable();
		DefaultTableModel tableModel=new DefaultTableModel(new String[] {"Name", "Executable","Arguments"}, 0);
		table.setModel(tableModel);
		scrollPane.setViewportView(table);
		
		for(String[] row:Constants.TABLE_DATA) 
			tableModel.addRow(row);
		
		tableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if(!removed && !insert)
					Constants.TABLE_DATA.get(e.getFirstRow())[e.getColumn()]=(String) tableModel.getValueAt(e.getFirstRow(), e.getColumn());
				else if(removed) {
					removed=false;
					Constants.TABLE_DATA.remove(table.getSelectedRow());
				}
				else if(insert)
					insert=false;
			}
			
		});
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedRow=table.getSelectedRow();
				System.out.println(selectedRow);
				removeBtn.setEnabled(true);				
			}
			
		});
		
		JButton newBtn = new JButton("Add new Entry");
		newBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insert=true;
				JPanel panel=new JPanel(new GridLayout(6, 1));
				JTextField nameField = new JTextField(), exeField=new JTextField(), argField=new JTextField();
				panel.add(new JLabel("Name:"));
				panel.add(nameField);
				panel.add(new JLabel("Path to executable"));
				panel.add(exeField);
				panel.add(new JLabel("Arguments"));
				panel.add(argField);
				
				int choice=JOptionPane.showConfirmDialog(Window.window, panel, "New Command:", JOptionPane.OK_CANCEL_OPTION);
				
				if(choice==JOptionPane.OK_OPTION) {
					String[] row=new String[] {
							nameField.getText(),
							exeField.getText(),
							argField.getText()
					};
					tableModel.addRow(row);
					Constants.TABLE_DATA.add(row);
				}
			}
		});
		newBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_newBtn = new GridBagConstraints();
		gbc_newBtn.fill = GridBagConstraints.HORIZONTAL;
		gbc_newBtn.insets = new Insets(0, 0, 5, 0);
		gbc_newBtn.gridx = 0;
		gbc_newBtn.gridy = 1;
		add(newBtn, gbc_newBtn);
		
		removeBtn = new JButton("Remove Entry");
		removeBtn.setEnabled(false);
		removeBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_removeBtn = new GridBagConstraints();
		gbc_removeBtn.fill = GridBagConstraints.HORIZONTAL;
		gbc_removeBtn.gridx = 0;
		gbc_removeBtn.gridy = 2;
		add(removeBtn, gbc_removeBtn);
		
		removeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removed=true;
				tableModel.removeRow(selectedRow);
			}
			
		});
		
	}

}
