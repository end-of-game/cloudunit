package fr.treeptik.cloudunit.config;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

	private static final String ALGO = "AES";
	private static final byte[] keyValue = new byte[] { 'C', 'l', 'O', 'u',
			'D', 'U', 'n', 'I', 't', '2', '0', '1', '5', '0', '0', '0' };

	@Override
	public String encode(CharSequence sequence) {
		Cipher cipher;
		String encryptedString = null;
		try {
			cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, generateKey());
			encryptedString = Base64.encodeBase64String(cipher.doFinal(sequence
					.toString().getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedString;
	}

	public String decode(CharSequence pass) {
		Cipher cipher;
		String decryptString = null;
		try {
			byte[] encryptText = Base64.decodeBase64(pass.toString());
			cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, generateKey());
			decryptString = new String(cipher.doFinal(encryptText));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptString;
	}

	@Override
	public boolean matches(CharSequence sequence, String toMatch) {
		return this.encode(sequence).equalsIgnoreCase(toMatch);
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}

}
