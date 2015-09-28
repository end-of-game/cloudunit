/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.config;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Custom encoder for CloudUnit.
 * We could not use any standard because we need a bijectiv algorith.
 * We need to decrypt password from database to inject them into containers and shell scripts.
 */
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
