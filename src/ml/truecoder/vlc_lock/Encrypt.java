package ml.truecoder.vlc_lock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Encrypt implements Runnable {
	private boolean notify;
	private String password;
	private FileInputStream fin;
	private FileOutputStream fout;
	private File srcFile, encryptedFile, targetFile;
	
	public Encrypt(boolean notify, String password, File srcFile, File targetFile) throws FileNotFoundException {
		this.notify=notify;
		this.password=password;
		this.srcFile=srcFile;
		if(targetFile==null)
			this.targetFile=srcFile;
		else
			this.targetFile=targetFile;
		
		fin=new FileInputStream(srcFile);
		
		Constants.TMP_FOLDER_NAME.mkdirs();
		encryptedFile=new File(Constants.TMP_FOLDER_NAME+File.separator+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date()));
				
		fout=new FileOutputStream(encryptedFile);
	}
	
	@Override
	public void run() {
		
		try {
			byte[] salt=new byte[32];
			new SecureRandom().nextBytes(salt);
			
			MessageDigest hasher=MessageDigest.getInstance("MD5");
			byte[] hashedPass=hasher.digest(password.getBytes());
			
			SecretKey key=KeyBuilder.buildKey(Base64.getEncoder().encodeToString(hashedPass), salt);
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			fout.write(new byte[] {(byte) ((notify)?1:0)});
			fout.write(salt);
			fout.write(cipher.getIV());
			if(notify) {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(hashedPass);
				fout.write(hash);
			}
			
			CipherOutputStream writer=new CipherOutputStream(fout, cipher);
			
			int bytesRead;
			long totalRead=0, max=srcFile.length();
			byte[] buffer=new byte[4096];
			while((bytesRead=fin.read(buffer))>0) {
				writer.write(buffer, 0, bytesRead);
				totalRead+=bytesRead;
				EncryptScreen.getProgressScreenObject().updateCurrent(totalRead, max, srcFile.getName());
			}
			writer.close();
			fin.close();
			
			
			
			//Copy Encrypted File
			if(targetFile.isDirectory()) {
				targetFile=checkFileExistence(new File(targetFile+File.separator+srcFile.getName()));
				Files.move(encryptedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				Files.move(encryptedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch(KeyBuilder.KeyBuildError | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			encryptedFile.delete();
		}
	}
	
	private File checkFileExistence(File file) {
		if(!file.exists())
			return file;
		for(int i=1;;i++) {
			String filePath=file.getAbsolutePath();
			String newName=file.getName().substring(0, file.getName().lastIndexOf('.'))+"("+i+")"+file.getName().substring(file.getName().indexOf('.'));
			file=new File(filePath.substring(0, filePath.lastIndexOf(File.separator)+1)+newName);
			if(!file.exists())
				return file;
		}
	}
}
