package ml.truecoder.vlc_lock;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static JFrame window;
	
	public static final int WIDTH=500, HEIGHT=250;
	public Window() {
		setTitle("TCrypter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Insets insets=getInsets();
		setSize(new Dimension(insets.left+insets.right+WIDTH, insets.top+insets.bottom+HEIGHT));
		
		int screenWidth=(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight=(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		int x=(screenWidth-WIDTH)/2;
		int y=(screenHeight-HEIGHT)/2;
		
		//Add MenuBar
		JMenuBar menuBar=new JMenuBar();
		LinkedHashMap<String, JMenu> menus=new LinkedHashMap<String, JMenu>();
		menus.put("file", new CustomMenu("File", new String[] {"Encrypt File", "Decrypt File"}, new String[] {"ml.truecoder.vlc_lock.EncryptScreen", "ml.truecoder.vlc_lock.DecryptScreen"}, new String[] {"Encrypt File", "Decrypt File"}));
		menus.put("settings", new CustomMenu("Settings", new String[] {"Configure Piped Programs"}, new String[] {"ml.truecoder.vlc_lock.ProgramConfigureScreen"}, new String[] {"Edit Commands:"}));
		menus.put("about", new AboutMenu());
		
		for(JMenu menu:menus.values())
			menuBar.add(menu);
		setJMenuBar(menuBar);
		
		setLocation(x, y);
		add(new HomeScreen());
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					ObjectOutputStream fout=new ObjectOutputStream(new FileOutputStream(Constants.CONF_FILE));
					fout.writeObject(Constants.TABLE_DATA);
					fout.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Window.this, "Error sving data!", "Error:", JOptionPane.ERROR_MESSAGE, null);
				}
				
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		File confFile=Constants.CONF_FILE;
		try {	
			if(!confFile.exists()) {
				Constants.CONF_FILE_DIR.mkdirs();
				ObjectOutputStream fout=new ObjectOutputStream(new FileOutputStream(confFile));
				ArrayList<String[]> commandsData=new ArrayList<String[]>();
				fout.writeObject(commandsData);
				fout.close();
				Constants.TABLE_DATA=commandsData;
			}
			else {
				ObjectInputStream fin=new ObjectInputStream(new FileInputStream(Constants.CONF_FILE));
				Constants.TABLE_DATA=(List<String[]>) fin.readObject();
				fin.close();
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		window=new Window();
	}
	
	public static void switchPane(JPanel panel, String title) {
		window.setTitle(title);
		window.setContentPane(panel);
		window.revalidate();
	}
	
}

class MenuItemAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JPanel screen;
	private String title;
	MenuItemAction(String text, JPanel screen, String title){
		super("Text");
		this.screen=screen;
		this.title=title;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Window.switchPane(screen, title);
	}
	
}

class AboutMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	AboutMenu(){
		super("About");
		JMenuItem website=new JMenuItem(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.truecoder.ml"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
			
		});
		website.setText("Visit Website");
		
		JMenuItem jarProjects=new JMenuItem(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.truecoder.ml/jar_projects.php"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		jarProjects.setText("See more JAR projects");
		
		JMenuItem youtube=new JMenuItem(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.youtube.com/channel/UCEizLH-hCZkTIy_XlTQHerg?sub_confirmation=1"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		youtube.setText("View My Youtube Channel");
		
		add(website);
		add(jarProjects);
		add(youtube);
	}
}
