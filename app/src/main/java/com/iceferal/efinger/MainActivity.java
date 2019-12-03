package com.iceferal.efinger;

import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


    if (!fingerprintManager.isHardwareDetected()) {
        Toast.makeText(this, "To urzadzenie nie posiada czytnika linii", Toast.LENGTH_SHORT).show();            }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Brak wymaganych pozwoleń", Toast.LENGTH_SHORT).show();            }

    if (!fingerprintManager.hasEnrolledFingerprints()) {
        Toast.makeText(this, "Brak zarejestrowanych odcisków", Toast.LENGTH_SHORT).show();            }

    if (!keyguardManager.isKeyguardSecure()) {
        Toast.makeText(this, "Weryfikacja odciskiem jest wyłączona", Toast.LENGTH_SHORT).show(); }
    else {
        try {
        generateKey(); }
        catch (FingerprintException e) { e.printStackTrace();         }

        if (initCipher()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler helper = new FingerprintHandler(this);
            helper.startAuth(fingerprintManager, cryptoObject);
        }  }      }

    private void generateKey() throws FingerprintException {
        try {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyStore.load(null);
        keyGenerator.init(new

        KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());

        keyGenerator.generateKey();

        } catch (KeyStoreException | NoSuchAlgorithmException
                | NoSuchProviderException | InvalidAlgorithmParameterException
                | CertificateException | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);     }
    }

    public boolean initCipher() {
        try {
        cipher = Cipher.getInstance(
        KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
        throw new RuntimeException("Failed to get Cipher", e); }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
        return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Błąd inicjalizacji Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
        super(e);      }
    }
}