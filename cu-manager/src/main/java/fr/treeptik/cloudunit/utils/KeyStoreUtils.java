/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by guillaume on 29/10/15.
 */
public class KeyStoreUtils {

    public static KeyStore createDockerKeyStore(String certPath) throws IOException, GeneralSecurityException{
        PrivateKey privKey = loadPrivateKey(certPath + "/key.pem");
        Certificate[] certs = loadCertificates(certPath + "/cert.pem");

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);

        keyStore.setKeyEntry("docker", privKey, "docker".toCharArray(), certs);
        addCA(keyStore, certPath + "/ca.pem");
        return keyStore;
    }

    public static PrivateKey loadPrivateKey(String keyPath) throws IOException, GeneralSecurityException {
        PEMKeyPair keyPair = loadPEM(keyPath);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivateKeyInfo().getEncoded());
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    private static <T> T loadPEM(String keyPath) throws IOException {
        PEMParser parser = new PEMParser(new BufferedReader(new FileReader(keyPath)));
        return (T) parser.readObject();
    }

    private static void addCA(KeyStore keyStore, String caPath) throws KeyStoreException, FileNotFoundException, CertificateException {
        for (Certificate cert : loadCertificates(caPath)) {
            X509Certificate crt = (X509Certificate) cert;
            String alias = crt.getSubjectX500Principal().getName();
            keyStore.setCertificateEntry(alias, crt);
        }
    }

    private static Certificate[] loadCertificates(String certPath) throws FileNotFoundException, CertificateException {
        InputStream is = new FileInputStream(certPath);
        Collection<? extends Certificate> certs = CertificateFactory.getInstance("X509").generateCertificates(is);
        return new ArrayList<>(certs).toArray(new Certificate[certs.size()]);
    }
}
