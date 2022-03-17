package cordova.plugin.netcontroll.integration;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
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

import java.util.List;

//Json Serialize
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


//Cielo Package Sdk

import cielo.orders.domain.CancellationRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Item;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.ResultOrders;

import cielo.sdk.order.OrderManager;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentListener;
import cielo.sdk.order.payment.PaymentError;


/**
 * This class echoes a string called from JavaScript.
 */
public class CieloLioLocal {

    //Cielo Package Sdk
    public OrderManager orderManager;



    //Cielo Variables
    Credentials _credentials ;
    public boolean orderManagerServiceBinded = false;
    public Order order;
    public Payment payment;



    //Cordova/Java Params
    private Activity mActivity;
    private Context mContext;
    private CordovaWebView webView;

    //Intents CODES
    private static int REQ_CODE_SELECAOIMAGEM = 1234;

    //CallbacksOnActivityResult
    private CallbackContext selecionarImagemCallbackContext;


    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        this.webView = webView;
        mActivity = cordova.getActivity();
        mContext = cordova.getActivity().getApplicationContext();
    }

    public String Teste() {
        return "Result - CieloLioLocalClass: ok";
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("Teste")) {
               int arg1 = args.getInt(0);
               if (arg1 == 0) {
                    Credentials credentials = new Credentials("M2vcq2fr0oWKbKd5bvpDoGZvHXQ8IneDLBJEulDjxNA59MOA2D/ EMOfYfErX7Y537JZgJvlgG0r805v5jYTxCgNiPtxm500bBUSxh", "NMgpwK4N5dcLCWnOr2kYbDIFcQdLPEn5M2ObKUTULEOdhxzFZu");
                    orderManager = new OrderManager(credentials, mContext);
                    if (credentials == null) {
                        callbackContext.success("Result - CieloLioLocalClass: Erro Credencial " + action);    
                        return false;
                    } else {
                        callbackContext.success("Result - CieloLioLocalClass: Tem Credencial " + action + " Token: " + credentials.getAccessToken().toString());    
                        return true;
                    }
               }
                else {
                    callbackContext.success("Result - CieloLioLocalClass: noOk " + action);
                    return false;
                }
        }
        else if (action.equals("ConfigureSdk")) {
            JSONObject params = args.getJSONObject(0);
            try {
                this.configSDK(params.getString("clientId"),params.getString("accessToken"));
                if (this.orderManager == null) {
                    callbackContext.error("Result - Cielo - LioLocal - ConfigureSdk: orderManager is null");
                    return false;
                } else {
                    callbackContext.success("Result - Cielo - LioLocal - ConfigureSdk: orderManager ok");
                    return true;
                }
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal - ConfigureSdk: " + "Error Detail: " + error1);
            }
            return false;
        }
        else if (action.equals("PlaceOrder")) {
            JSONObject params = args.getJSONObject(0);
            try {
                this.placeOrder(params.getString("productName"));
                // callbackContext.success("Result - Cielo - LioLocal PlaceOrder: ok ");    
                if (order != null)  {
                    callbackContext.success("Result - Cielo - LioLocal PlaceOrder - order: ok (" + order.getReference() + ")");    
                } else {
                    callbackContext.success("Result - Cielo - LioLocal PlaceOrder - order is null: Error ");    
                    return false;
                }
                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal PlaceOrder: " + "Error Detail: " + error1);
            }
            return false;
        }
        else if (action.equals("MakePayment")) {
            JSONObject params = args.getJSONObject(0);
            try {
                this.makePayment(
                    params.getString("orderId"),
                    params.getInt("amount"),
                    params.getString("ec"),
                    params.getInt("installments"),
                    params.getString("email"),
                    params.getString("paymentCode"),
                    callbackContext
                );
                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal MakePayment: " + "Error Detail: " + error1);
            }
            return false;
        }
        else if (action.equals("ListOrders")) {
            try {
                this.listOrders(callbackContext);
                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal ListOrders: " + "Error Detail: " + error1);
            }
        }
        else if (action.equals("CancelPayment")) {
            JSONObject paramsObj = args.getJSONObject(0);
            try {
                // JSONObject jobj = args.getJSONObject(0);
                // JSONObject jobj = new JSONObject(json);


                // // JSONObject params = args.getJSONObject(0);
                // // Order cancelOrder = 
                // // var testes = args.getJSONObject(0);
                // // JSONObject data = args.getJSONObject(0); 
                // // JSONObject data = args.getJSONObject(); 
                this.cancelPayment(paramsObj, callbackContext);
                // callbackContext.success(paramsObj);


                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal ListOrders: " + "Error Detail: " + error1);
            }


            // JSONObject params = args.getJSONObject(0);
            // JSONObject params = args.getJSONObject(0);
            // try {
            //     String idCancel = params.getString("id");
            //     // Toast.makeText(mContext,String.format("configSDK: onServiceUnbound"), Toast.LENGTH_LONG).show();
                
            //     this.cancelPayment(params.getString("id"), callbackContext);
            //     callbackContext.success("Result - Cielo - LioLocal CancelPayment: ok");
            //     return true;
            // } catch (Exception error1) {
            //     callbackContext.error("Result - Cielo - LioLocal ListOrders: " + "Error Detail: " + error1);
            // }
        }
        return false;
    }

    
    // Cielo Functions Helpers
    private cielo.sdk.order.payment.PaymentCode GetPaymentCodeFromName(String paymentCodeName) {
        cielo.sdk.order.payment.PaymentCode paymentCode = null;
        switch (paymentCodeName) {
            case "DEBITO_AVISTA":
                paymentCode = PaymentCode.DEBITO_AVISTA;
                break;
            case "DEBITO_PREDATADO":
                paymentCode = PaymentCode.DEBITO_PREDATADO;
                break;
            case "CREDITO_AVISTA":
                paymentCode = PaymentCode.CREDITO_AVISTA;
                break;
            case "CREDITO_PARCELADO_LOJA":
                paymentCode = PaymentCode.CREDITO_PARCELADO_LOJA;
                break;
            case "CREDITO_PARCELADO_ADM":
                paymentCode = PaymentCode.CREDITO_PARCELADO_ADM;
                break;
            case "CREDITO_PARCELADO_BNCO":
                paymentCode = PaymentCode.CREDITO_PARCELADO_BNCO;
                break;
            case "CARTAO_LOJA_AVISTA":
                paymentCode = PaymentCode.CARTAO_LOJA_AVISTA;
                break;
            case "CARTAO_LOJA_PARCELADO_LOJA":
                paymentCode = PaymentCode.CARTAO_LOJA_PARCELADO_LOJA;
                break;
            case "CARTAO_LOJA_PARCELADO":
                paymentCode = PaymentCode.CARTAO_LOJA_PARCELADO;
                break;
            case "PRE_AUTORIZACAO":
                paymentCode = PaymentCode.PRE_AUTORIZACAO;
                break;
            case "VOUCHER_ALIMENTACAO":
                paymentCode = PaymentCode.VOUCHER_ALIMENTACAO;
                break;
            case "VOUCHER_REFEICAO":
                paymentCode = PaymentCode.VOUCHER_REFEICAO;
                break;
            case "VISAVALE_REFEICAO":
                paymentCode = PaymentCode.VISAVALE_REFEICAO;
                break;
            case "VISAVALE_ALIMENTACAO":
                paymentCode = PaymentCode.VISAVALE_ALIMENTACAO;
                break;
            default:
                break;
        }
        return paymentCode;
    }


    // Cielo Functions
    protected void configSDK(String clientId, String accessToken) {
        this._credentials = new Credentials(clientId, accessToken);
        this.orderManager = new OrderManager(this._credentials, this.mContext);
        this.orderManager.bind(this.mContext, new ServiceBindListener() {

            @Override
            public void onServiceBoundError(Throwable throwable) {
                orderManagerServiceBinded = false;

                // Toast.makeText(mContext,
                //         String.format("configSDK: onServiceBoundError: Erro fazendo bind do serviço de ordem -> %s",
                //                 throwable.getMessage()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceBound() {
                orderManagerServiceBinded = true;
                // orderManager.createDraftOrder("REFERENCIA DA ORDEM");
                // Toast.makeText(mContext,String.format("configSDK: onServiceBound"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
                // Toast.makeText(mContext,String.format("configSDK: onServiceUnbound"), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void placeOrder(String productName) {
        if (!orderManagerServiceBinded) {
            // Toast.makeText(mContext, "Serviço de ordem ainda não recebeu retorno do método bind().\n"
            //         + "Verifique sua internet e tente novamente", Toast.LENGTH_LONG).show();
            return;
        }

        // placeOrderButton.setEnabled(false);
        order = orderManager.createDraftOrder("REFERENCIA DA ORDEM");
        order.addItem("8989", "PROD LCS", 100, 3, "EACH");
        orderManager.updateOrder(order);
    }

    protected void makePayment(String orderId,
                               long amount,
                               String ec, 
                               int installments,
                               String email,
                               String paymentCodeStr,
                               CallbackContext callbackContext) {
        cielo.sdk.order.payment.PaymentCode paymentCode = GetPaymentCodeFromName(paymentCodeStr);
        if (paymentCode == null) {
            callbackContext.error("Result - Cielo - LioLocal MakePayment: Invalid paymentCode (" + paymentCodeStr + ")");    
            return;
        }


        if (order != null) {
            orderManager.placeOrder(order);
            try {
                CheckoutRequest.Builder requestBuilder = new CheckoutRequest.Builder()
                    .orderId(order.getId())
                    .amount(amount)
                    .paymentCode(paymentCode)
                    .installments(installments);

                 if (!ec.equals(""))
                     requestBuilder.ec(ec);

                 if (!email.equals(""))
                     requestBuilder.email(email);
                
                CheckoutRequest request = requestBuilder.build();

                orderManager.checkoutOrder(request, new PaymentListener() {
                    @Override
                    public void onStart() {
                        // Toast.makeText(mContext,"Result - Cielo - LioLocal MakePayment: checkoutOrder - onStart", 0).show();
                        // callbackContext.success("Result - Cielo - LioLocal MakePayment: checkoutOrder - onStart");    
                            // Log.d(TAG, "ON START");
                    }

                    @Override
                    public void onPayment(Order paidOrder) {
                        order = paidOrder;
                        order.markAsPaid();
                        orderManager.updateOrder(order);
                        Gson gson = new Gson();
                        callbackContext.success(gson.toJson(order));    
                        resetState();
                    }

                    @Override
                    public void onCancel() {
                        // Toast.makeText(mContext,String.format("Result - Cielo - LioLocal MakePayment: onCancel"), Toast.LENGTH_LONG).show();
                        callbackContext.error("Result - Cielo - LioLocal MakePayment: checkoutOrder - onCancel");    
                        // Log.d(TAG, "ON CANCEL");
                        resetState();
                    }

                    @Override
                    public void onError(@NonNull PaymentError paymentError) {
                        // Toast.makeText(mContext,String.format("Result - Cielo - LioLocal MakePayment: onError"), Toast.LENGTH_LONG).show();
                        callbackContext.error("Result - Cielo - LioLocal MakePayment: checkoutOrder - onError");    
                        // Log.d(TAG, "ON ERROR");
                        resetState();
                    }

                });

            } catch (Exception error1) {
                callbackContext.error("makePayment " + "- Error Detail: " + error1);
                throw error1;
            }


            // if (!ec.equals(""))
            //     requestBuilder.ec(ec);

            // if (!userEmail.equals(""))
            //     requestBuilder.email(userEmail);

            // CheckoutRequest request = requestBuilder.build();
        }
    }

    protected void listOrders(CallbackContext callbackContext) {
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(200, 0);
            if (resultOrders != null) {
                Gson gson = new Gson();
                callbackContext.success(gson.toJson(resultOrders));    
            } else {
                callbackContext.success("null");  
            }
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - listOrders: " + "Error Detail: " + error1);
        }
    }

    protected void cancelPayment(JSONObject orderObj, CallbackContext callbackContext) {
        try {
                String jsonStr = orderObj.toString();
                Gson gson = new Gson();
                //Order objCliente = gson.fromJson(jsonStr, Order.class);
                order = gson.fromJson(jsonStr, Order.class);

                List<Payment> paymentList = order.getPayments();
                payment = paymentList.get(0);


                if (order != null && order.getPayments().size() > 0) {
                    CancellationRequest request = new CancellationRequest.Builder()
                            .orderId(order.getId())
                            .authCode(payment.getAuthCode())
                            .cieloCode(payment.getCieloCode())
                            .value(payment.getAmount())
                            //.ec("0000000000000003")
                            .build();

                    orderManager.cancelOrder(request, new CancellationListener() {

                        @Override
                        public void onSuccess(Order cancelledOrder) {
//                          Log.d(TAG, "ON SUCCESS");
                            cancelledOrder.cancel();
                            orderManager.updateOrder(cancelledOrder);
//
                            Toast.makeText(mContext, "CANCELAMENTO SUCESSO", Toast.LENGTH_SHORT).show();
                            order = cancelledOrder;
//                            resetState();
                            callbackContext.success(parseObjToJsonSrtring(order));
                        }

                        @Override
                        public void onCancel() {
                            callbackContext.success("cancelPayment - onCancel");
//                        Log.d(TAG, "ON CANCEL");
//
//                        Toast.makeText(CancelPaymentActivity.this, "CANCELADO", Toast.LENGTH_SHORT).show();
//                        resetUI();
                        }

                        @Override
                        public void onError(PaymentError paymentError) {
                            callbackContext.success("cancelPayment - onError");
//                        Log.d(TAG, "ON ERROR");
//
//                        Toast.makeText(CancelPaymentActivity.this, "ERRO", Toast.LENGTH_SHORT).show();
//                        resetUI();
                        }
                    });

//                callbackContext.success(jsonStr);
                }
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - listOrders: " + "Error Detail: " + error1);
        }
    }

    protected String parseObjToJsonSrtring(Object obj) {
        Gson gson = new Gson();
        String result = gson.toJson(obj);
        return result;
    }

    protected void resetState() {
        order = null;
        // configUi();
        // updatePaymentButton();
    }

}
