package com.iceferal.efinger;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context mContext) {
        context = mContext;    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Toast.makeText(context, "Błąd autoryzacji!", Toast.LENGTH_LONG).show();    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Autoryzacja się nie powiodła!", Toast.LENGTH_LONG).show();    }


    @Override
    public void onAuthenticationSucceeded(
        FingerprintManager.AuthenticationResult result) {
        context.startActivity(new Intent(context, SuccessActivity.class));
        Toast.makeText(context, "Pomyślnie zalogowano!", Toast.LENGTH_LONG).show();    }
}
