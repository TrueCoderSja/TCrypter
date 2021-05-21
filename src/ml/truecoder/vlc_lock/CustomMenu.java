package ml.truecoder.vlc_lock;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class CustomMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	public CustomMenu(String name, String[] options, String[] screenNames, String titles[]) {
		super(name);
		for(int i=0;i<options.length;i++) {
			final int loopCount=i;
			JMenuItem item=new JMenuItem(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Class<?> classObj=Class.forName(screenNames[loopCount]);
						JPanel screen=(JPanel) classObj.getConstructor().newInstance();
						Window.switchPane(screen, titles[loopCount]);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
			});
			item.setText(options[i]);
			add(item);
		}
	}

}
