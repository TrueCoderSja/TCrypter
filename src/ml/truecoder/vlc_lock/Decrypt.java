package ml.truecoder.vlc_lock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class Decrypt implements Runnable {
	
	private FileInputStream fin;
	private OutputStream fout;
	private byte[] salt, iv;
	private File tmpFile, targetFile;
	private File encryptedFile;
	private byte[] hashedPass;
	
	public Decrypt(String password, File encryptedFile, File targetFile) throws WrongPasswordException, IOException {
		this.targetFile=targetFile;
		this.encryptedFile=encryptedFile;
		fin=new FileInputStream(encryptedFile);
		
		if(targetFile==null) {
			String[] command = null;
			for(String[] row:Constants.TABLE_DATA) {
				if(row[0].equals(Session.retrieve("pipeProgram")))
					command=row;
			}
			Process process=Runtime.getRuntime().exec(command[1]+" "+command[2]);
			fout=process.getOutputStream();
		}
		else {
			Constants.TMP_FOLDER_NAME.mkdirs();
			tmpFile=new File(Constants.TMP_FOLDER_NAME+File.separator+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date()));
			fout=new FileOutputStream(tmpFile);
		}
		
		try {
			byte[] notifyByte=new byte[1];
			fin.read(notifyByte);
			Session.store(Constants.TO_NOTIFY, notifyByte[0]+"");
			
			salt=new byte[32];
			fin.read(salt);
			
			iv=new byte[16];
			fin.read(iv);
			
			MessageDigest hasher=MessageDigest.getInstance("MD5");
			hashedPass=hasher.digest(password.getBytes());
			
			
			if(notifyByte[0]==1) {
				byte[] realHash=new byte[32];
				fin.read(realHash);
				
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(hashedPass);
				if(!Arrays.equals(realHash, hash)) {
					fout.close();
					fin.close();
					throw new WrongPasswordException();
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		try {			
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, KeyBuilder.buildKey(Base64.getEncoder().encodeToString(hashedPass), salt), new IvParameterSpec(iv));
			
			CipherInputStream reader=new CipherInputStream(fin, cipher);
			long totalRead=0, max=encryptedFile.length();
			int bytesRead;
			byte[] buffer=new byte[4096];
			while((bytesRead=reader.read(buffer))>0) {
				fout.write(buffer, 0, bytesRead);
				totalRead+=bytesRead;
				DecryptScreen.getProgressScreenObj().updateCurrent(totalRead, max, encryptedFile.getName());
			}
			reader.close();
			fout.close();
			
			if(targetFile!=null) {
				if(targetFile.isDirectory()) {
					targetFile=checkFileExistence(new File(targetFile+File.separator+encryptedFile.getName()));
					Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				else
					Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException | KeyBuilder.KeyBuildError | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(tmpFile!=null)
				tmpFile.delete();
		}
		
	}
	
	private File checkFileExistence(File file) {
		if(!file.exists())
			return file;
		for(int i=1;;i++) {
			String filePath=file.getAbsolutePath();
			String newName=file.getName()+"("+i+")";
			file=new File(filePath.substring(0, filePath.lastIndexOf(File.separator+1))+newName);
			if(!file.exists())
				return file;
		}
	}

	class WrongPasswordException extends Exception {
		private static final long serialVersionUID = 1L;
		
		WrongPasswordException() {
			super("Incorrect Password!");
		}
	}
}
