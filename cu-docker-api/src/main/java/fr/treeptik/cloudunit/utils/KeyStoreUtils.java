/*
    * Copyright 2014 Roland Huss
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *       http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
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

public class KeyStoreUtils {

    public static KeyStore createDockerKeyStore(String certPath) throws IOException, GeneralSecurityException{
        File file = new File(certPath + "/key.pem");

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
