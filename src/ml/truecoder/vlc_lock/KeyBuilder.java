package ml.truecoder.vlc_lock;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyBuilder {
	public static SecretKey buildKey(String password, byte[] saltBytes) throws KeyBuildError {
		try {
			String salt=new String(saltBytes);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
			return secret;
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new KeyBuildError();
		}
	}
	
	static class KeyBuildError extends Exception {
		private static final long serialVersionUID = 1L;
		public KeyBuildError() {
			super("Error building the key!");
		}
	}
}
