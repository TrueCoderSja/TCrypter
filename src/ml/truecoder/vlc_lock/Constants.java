package ml.truecoder.vlc_lock;

import java.io.File;
import java.util.List;

public class Constants {
	public static final File TMP_FOLDER_NAME=new File(System.getProperty("java.io.tmpdir")+File.separator+"VLC_LOCK");
	public static final String FILE="file";
	public static final String TO_NOTIFY="toNotify";
	public static final File CONF_FILE_DIR=new File(System.getenv("LOCALAPPDATA")+File.separator+"TCrypter");
	public static final File CONF_FILE=new File(System.getenv("LOCALAPPDATA")+File.separator+"TCrypter"+File.separator+"tcrypt.conf");
	public static List<String[]> TABLE_DATA;
}
