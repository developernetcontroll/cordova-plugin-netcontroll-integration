package cordova.plugin.netcontroll.integration;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.net.Uri;
import android.content.Intent;
import android.provider.MediaStore;

import static android.app.Activity.RESULT_OK;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

/**
 * This class echoes a string called from JavaScript.
 */
public class NetControllCordovaPluginsIntegration extends CordovaPlugin {
    //Elgin
    public ElginM10Terminal elginM10Terminal;

    //Cielo
    public CieloLioLocal cieloLioLocal;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private void initializeElginM10Terminal() {
        if (this.elginM10Terminal == null) {
            this.elginM10Terminal = new ElginM10Terminal();
            this.elginM10Terminal.initialize(this.cordova, this.webView);
        }
    }

    private void initializeCieloLioLocal() {
        if (this.cieloLioLocal == null) {
            this.cieloLioLocal = new CieloLioLocal();
            this.cieloLioLocal.initialize(this.cordova, this.webView);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try { 
            String result = "Sem resultado";
            String[] actionProviderList = action.split("\\.");
            String provider = actionProviderList[0];

            switch (provider.toLowerCase()) {
                case "elgin":
                    String elginProduct = actionProviderList[1];
                    switch (elginProduct.toLowerCase()) {
                        case "m10terminal":
                            this.initializeElginM10Terminal();
                            String elginAction = actionProviderList[2];
                            boolean resultElginExecute = this.elginM10Terminal.execute(elginAction, args, callbackContext);
                            return resultElginExecute;
                        default:
                            break;
                    }
                case "cielo":
                    String cieloProduct = actionProviderList[1];
                    switch (cieloProduct.toLowerCase()) {
                        case "liolocal":
                            this.initializeCieloLioLocal();
                            String cieloAction = actionProviderList[2];
                            boolean resultCieloExecute = this.cieloLioLocal.execute(cieloAction, args, callbackContext);
                            return resultCieloExecute;
                        default:
                            break;
                    }
                default:
                    break;
            }
            callbackContext.success(result);
            return true;
        } catch (Exception e) {
            callbackContext.error("NetControllCordovaPluginsIntegration excute error: " + e.toString());
        }
        return false;
    }
}
