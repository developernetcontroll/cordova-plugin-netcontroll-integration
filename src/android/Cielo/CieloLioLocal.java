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
import android.os.Build;

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
import cielo.orders.domain.DeviceModel;
import cielo.orders.domain.Item;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.ResultOrders;
import cielo.orders.domain.PrinterAttributes;
import cielo.orders.domain.Settings;

import cielo.sdk.order.OrderManager;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentListener;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.PrinterListener;
import cielo.sdk.printer.PrinterManager;
import cielo.sdk.info.InfoManager;


/**
 * This class echoes a string called from JavaScript.
 */
public class CieloLioLocal {

    //Cielo Package Sdk
    public OrderManager orderManager;
    public InfoManager infoManager;


    //Cielo Variables
    Credentials _credentials ;
    public boolean orderManagerServiceBinded = false;
    public Order order;
    public Payment payment;


    //Cielo Printer
    private PrinterManager printerManager;
    private PrinterListener printerListener;
    private HashMap<String, Integer> alignCenter = new HashMap<>();
    private HashMap<String, Integer> alignLeft = new HashMap<>();
    private HashMap<String, Integer> alignRight = new HashMap<>();

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
        // mContext = cordova.getActivity().getApplicationContext();
        mContext = this.webView.getContext();
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("ConfigureSdk")) {
            JSONObject params = args.getJSONObject(0);
            try {
                this.configSDK(params.getString("clientId"),params.getString("accessToken"), callbackContext);
                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal - ConfigureSdk: " + "Error Detail: " + error1);
            }
            return false;
        }
        if (action.equals("OrderManagerBind")) {
            try {
                this.orderManagerBind(callbackContext);
                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal - OrderManagerBind: " + "Error Detail: " + error1);
            }
            return false;
        }


        else if (action.equals("CreateDraftOrder")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.createDraftOrder(params.getString("orderReference"), callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal CreateDraftOrder: " + "Error Detail: " + error1);
            }
            return false;
        }
        else if (action.equals("AddOrderItem")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.addOrderItem(
                    params.getString("sku"), 
                    params.getString("name"), 
                    params.getInt("unitPrice"), 
                    params.getInt("quantity"), 
                    params.getString("unityOfMeasure"), 
                    callbackContext
                );
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal AddOrderItem: " + "Error Detail: " + error1);
            }
            return false;
        }
        else if (action.equals("MakePayment")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.makePayment(
                    params.getString("orderReference"),
                    params.getString("orderId"),
                    params.getInt("amount"),
                    params.getString("ec"),
                    params.getInt("installments"),
                    params.getString("email"),
                    params.getString("paymentCode"),
                    callbackContext
                );
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal MakePayment: " + "Error Detail: " + error1);
            }
            return false;
        }
        else if (action.equals("OrderManagerUnbind")) {
            try {
                orderManager.unbind();
                orderManagerServiceBinded = false;
                callbackContext.success("Unbind: Success");
                return true;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal Unbind: " + "Error Detail: " + error1);
            }
            return false;
        }





        else if (action.equals("ListOrders")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.listOrders(params.getInt("pageSize"), params.getInt("page"), callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal ListOrders: " + "Error Detail: " + error1);
            }
        }

        else if (action.equals("CancelPayment")) {
            JSONObject paramsObj = args.getJSONObject(0);
            try {
                boolean result = this.cancelPayment(paramsObj, callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal ListOrders: " + "Error Detail: " + error1);
            }
        }


        else if (action.equals("TerminalInformation")) {
            try {
                boolean result = this.terminalInformation(callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal ListOrders: " + "Error Detail: " + error1);
            }
        }


        else if (action.equals("PrintSimpleText")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.print_SimpleText(
                    params.getString("textToPrint"), 
                    params.getInt("textAlign"),
                    params.getInt("textSize"),
                    params.getInt("textTypeFace"),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    //params.getInt("textMarginLeft"),
                    //params.getInt("textMarginRight"),
                    //params.getInt("textMarginTop"),
                    //params.getInt("textMarginBotton"),
                    //params.getInt("textLineSpace"),
                    //params.getInt("textWeight"),
                    callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal PrintSimpleText: " + "Error Detail: " + error1);
            }
        }

        else if (action.equals("PrintQrCode")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.print_QrCode(
                    params.getString("textToPrint"), 
                    params.getInt("textAlign"),
                    params.getInt("size"),
                    callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal PrintQrCode: " + "Error Detail: " + error1);
            }
        }

         else if (action.equals("PrintBarCode")) {
            JSONObject params = args.getJSONObject(0);
            try {
                boolean result = this.print_BarCode(
                    params.getString("textToPrint"), 
                    params.getInt("textAlign"),
                    params.getInt("barcodeWidth"),
                    params.getInt("barcodeHeight"),
                    callbackContext);
                return result;
            } catch (Exception error1) {
                callbackContext.error("Result - Cielo - LioLocal PrintBarCode: " + "Error Detail: " + error1);
            }
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

    protected void configSDK(String clientId, String accessToken, CallbackContext callbackContext) {
        this._credentials = new Credentials(clientId, accessToken);
        this.orderManager = new OrderManager(this._credentials, this.mContext);
        callbackContext.success("configSDK: ok");
    }

    protected void orderManagerBind(CallbackContext callbackContext) {
        this.orderManager.bind(this.mContext, new ServiceBindListener() {
            @Override
            public void onServiceBoundError(Throwable throwable) {
                orderManagerServiceBinded = false;
                callbackContext.error(String.format("orderManagerBind: onServiceBoundError: Erro fazendo bind do serviço de ordem -> %s",
                                 throwable.getMessage()));
            }

            @Override
            public void onServiceBound() {
                orderManagerServiceBinded = true;
                callbackContext.success("orderManagerBind: onServiceBound: Success");
                // orderManager.createDraftOrder("REFERENCIA DA ORDEM");
                // Toast.makeText(mContext,String.format("configSDK: onServiceBound"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
                callbackContext.success("orderManagerBind: onServiceUnbound: Success");
                // Toast.makeText(mContext,String.format("configSDK: onServiceUnbound"), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected boolean createDraftOrder(String orderReference, CallbackContext callbackContext) {
        if (!orderManagerServiceBinded) {
            callbackContext.error("createDraftOrder: onServiceBoundError: No binded order manager");
            return false;
        }
        order = orderManager.createDraftOrder(orderReference);
        if (order != null) {
            callbackContext.success(this.parseObjToJsonSrtring(order));
            return true;
        }
        return false;
    }

    protected boolean addOrderItem(String sku, 
                                   String name, 
                                   int unitPrice, 
                                   int quantity, 
                                   String unityOfMeasure, 
                                   CallbackContext callbackContext) {
        if (!orderManagerServiceBinded) {
            callbackContext.error("createDraftOrder: onServiceBoundError: No binded order manager");
            return false;
        }
        order.addItem(sku, name, unitPrice, quantity, unityOfMeasure);
        callbackContext.success(this.parseObjToJsonSrtring(order));
        return true;
    }

    protected boolean makePayment(String orderReference,
                                  String orderId,
                                  long amount,
                                  String ec, 
                                  int installments,
                                  String email,
                                  String paymentCodeStr,
                                  CallbackContext callbackContext) {
        if (!orderManagerServiceBinded) {
            callbackContext.error("makePayment error:  No binded order manager");
            return false;
        }
        if (order == null) {
            callbackContext.error("makePayment error: No order found");
            return false;
        } else {
            if (!orderReference.equals(order.getReference())) {
                callbackContext.error("makePayment error: The order reference is not the same as the current order (Reference: " + orderReference + ") - (Current order Reference: " + order.getReference() + ")");
                return false;
            }
        }
        cielo.sdk.order.payment.PaymentCode paymentCode = GetPaymentCodeFromName(paymentCodeStr);
        if (paymentCode == null) {
            callbackContext.error("makePayment error: Invalid paymentCode (" + paymentCodeStr + ")");    
            return false;
        }
        orderManager.placeOrder(order);
        try 
        {
            CheckoutRequest.Builder requestBuilder = new CheckoutRequest.Builder()
                .orderId(orderId)
                // .orderId(order.getId())
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
                }

                @Override
                public void onPayment(Order paidOrder) {
                     order = paidOrder;
                     order.markAsPaid();
                     orderManager.updateOrder(order);
                     callbackContext.success(parseObjToJsonSrtring(order));
                     resetState();
                }

                @Override
                public void onCancel() {
                    callbackContext.error("{Result - Cielo - LioLocal MakePayment: checkoutOrder - onCancel");    
                    resetState();
                }
                
                @Override
                public void onError(@NonNull PaymentError paymentError) {
                    callbackContext.error("Result - Cielo - LioLocal MakePayment: checkoutOrder - onError");    
                    resetState();
                }
	
            });
        } catch (Exception error1) {
            callbackContext.error("makePayment " + "- Error Detail: " + error1);
            throw error1;
        }
        return true;
    }




    protected boolean listOrders(int pageSize, int page, CallbackContext callbackContext) {
        if (!orderManagerServiceBinded) {
            callbackContext.error("listOrders: onServiceBoundError: No binded order manager");
            return false;
        }
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(pageSize, page);
            if (resultOrders != null) {
                String resultOrdersStr = parseObjToJsonSrtring(resultOrders);
                callbackContext.success(resultOrdersStr);    
            } else {
                callbackContext.success("null");  
            }
            return true;
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - listOrders: " + "Error Detail: " + error1);
        }
        return false;
    }

    protected boolean cancelPayment(JSONObject orderObj, CallbackContext callbackContext) {
        if (!orderManagerServiceBinded) {
            callbackContext.error("cancelPayment: onServiceBoundError: No binded order manager");
            return false;
        }
        try {
                String jsonStr = orderObj.toString();
                Gson gson = new Gson();
                Order cancelOrder = gson.fromJson(jsonStr, Order.class);

                List<Payment> paymentList = cancelOrder.getPayments();
                payment = paymentList.get(0);


                if (cancelOrder != null && cancelOrder.getPayments().size() > 0) {
                    CancellationRequest request = new CancellationRequest.Builder()
                            .orderId(cancelOrder.getId())
                            .authCode(payment.getAuthCode())
                            .cieloCode(payment.getCieloCode())
                            .value(payment.getAmount())
                            //.ec("0000000000000003")
                            .build();

                    orderManager.cancelOrder(request, new CancellationListener() {

                        @Override
                        public void onSuccess(Order cancelledOrder) {
                            cancelledOrder.cancel();
                            orderManager.updateOrder(cancelledOrder);
                            order = cancelledOrder;
                            callbackContext.success(parseObjToJsonSrtring(order));
                        }

                        @Override
                        public void onCancel() {
                            callbackContext.success("cancelPayment - onCancel");
                        }

                        @Override
                        public void onError(PaymentError paymentError) {
                            callbackContext.success("cancelPayment - onError");
                        }
                    });
                }
                return true;
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - cancelPayment: " + "Error Detail: " + error1);
        }
        return false;
    }


    protected boolean terminalInformation(CallbackContext callbackContext) {
        try {
            infoManager = new InfoManager();
            Settings settings = infoManager.getSettings(this.mActivity);

            String logicNumber = settings.getLogicNumber();
            Float batteryLevel = infoManager.getBatteryLevel(this.mActivity);
            DeviceModel deviceModel = infoManager.getDeviceModel();


            JSONObject json = new JSONObject();
            json.put("logicNumber", logicNumber);
            json.put("batteryLevel", batteryLevel);
            json.put("deviceModel", deviceModel);



            json.put("serial", Build.SERIAL);
            json.put("model", Build.MODEL);
            json.put("id", Build.ID);
            json.put("manufacture", Build.MANUFACTURER);
            json.put("brand", Build.BRAND);
            json.put("type", Build.TYPE);
            json.put("user", Build.USER);
            json.put("base", Build.VERSION_CODES.BASE);
            json.put("device", Build.DEVICE);
            json.put("incremental", Build.VERSION.INCREMENTAL);
            json.put("sdk", Build.VERSION.SDK);
            json.put("board", Build.BOARD);
            json.put("brand", Build.BRAND);
            json.put("host", Build.HOST);
            json.put("fingerprint", Build.FINGERPRINT);
            json.put("versionCode",  Build.VERSION.RELEASE);
            json.put("hardware", Build.HARDWARE);
            callbackContext.success(json.toString());
            return true;
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - terminalInformation: " + "Error Detail: " + error1);
        }
        return false;
    }










    protected  boolean print_initialize() {
        setStyles();
        printerManager = new PrinterManager(this.mContext);
        printerListener = new PrinterListener() {
            @Override
            public void onWithoutPaper() {
                //Log.d("PrintSampleActivity", "printer without paper");
            }

            @Override
            public void onPrintSuccess() {
                //Log.d("PrintSampleActivity", "print success!");
            }

            @Override
            public void onError(Throwable throwable) {
                //Log.d("PrintSampleActivity",String.format("printer error -> %s", throwable.getMessage()));
            }
        };
        return true;
    }

    protected boolean print_SimpleText(String textToPrint, 
                                       int textAlign,
                                       int textSize,
                                       int textTypeFace,

                                       int textMarginLeft,
                                       int textMarginRight,
                                       int textMarginTop,
                                       int textMarginBotton,

                                       int textLineSpace,
                                       int textWeight,

                                       CallbackContext callbackContext) {
        try {
            print_initialize();
            HashMap<String, Integer> printStyle = new HashMap<>();

            // Alinhamento da impressão
            switch (textAlign) {
                case 1: // Left
                    printStyle.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
                    break;
                case 2: // Center
                    printStyle.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
                    break;
                case 3: // Right
                    printStyle.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
                    break;
                default:
                    printStyle.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
                    break;
            }
            
            // Tamanho do texto
            printStyle.put(PrinterAttributes.KEY_TEXT_SIZE, textSize);

            // Fonte do texto
            printStyle.put(PrinterAttributes.KEY_TYPEFACE, textTypeFace);

            // Margem 
            // printStyle.put(PrinterAttributes.KEY_MARGINLEFT, textMarginLeft);
            // printStyle.put(PrinterAttributes.KEY_MARGINRIGHT, textMarginRight);
            // printStyle.put(PrinterAttributes.KEY_MARGINTOP, textMarginTop);
            // printStyle.put(PrinterAttributes.KEY_MARGINBOTTOM, textMarginBotton);

            // LineSpace
            // printStyle.put(PrinterAttributes.KEY_LINESPACE, textLineSpace);
            
            // Varíavel utilizada quando se trbaalho com impressão de múltiplas colunas, para escolher o peso de cada coluna
            // printStyle.put(PrinterAttributes.KEY_WEIGHT, textWeight);
            
            printerManager.printText(textToPrint, printStyle, printerListener);
            callbackContext.success("Result - Cielo - LioLocal - print_SimpleText: OK");
            return true;
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - print_SimpleText: " + "Error Detail: " + error1);
        }
        return false;
    }

    protected boolean print_QrCode(String textToPrint, 
                                   int textAlign,
                                   int size,
                                   CallbackContext callbackContext) {
        try {
            print_initialize();

            // Alinhamento da impressão
            switch (textAlign) {
                case 1: // Left
                    printerManager.printQrCode(textToPrint, PrinterAttributes.VAL_ALIGN_LEFT, size, printerListener);
                    break;
                case 2: // Center
                    printerManager.printQrCode(textToPrint, PrinterAttributes.VAL_ALIGN_CENTER, size, printerListener);
                    break;
                case 3: // Right
                    printerManager.printQrCode(textToPrint, PrinterAttributes.VAL_ALIGN_RIGHT, size, printerListener);
                    break;
                default:
                    printerManager.printQrCode(textToPrint, PrinterAttributes.VAL_ALIGN_LEFT, size, printerListener);
                    break;
            }
            callbackContext.success("Result - Cielo - LioLocal - print_QrCode: OK");
            return true;
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - print_QrCode: " + "Error Detail: " + error1);
        }
        return false;
    }

    protected boolean print_BarCode(String textToPrint, 
                                    int textAlign,
                                    int barcodeWidth,
                                    int barcodeHeight,
                                    CallbackContext callbackContext) {
        try {
            print_initialize();

            // Alinhamento da impressão
            switch (textAlign) {
                case 1: // Left
                    printerManager.printBarCode(textToPrint, PrinterAttributes.VAL_ALIGN_LEFT, barcodeWidth, barcodeHeight, false, printerListener);
                    break;
                case 2: // Center
                    printerManager.printBarCode(textToPrint, PrinterAttributes.VAL_ALIGN_CENTER, barcodeWidth, barcodeHeight, false, printerListener);
                    break;
                case 3: // Right
                    printerManager.printBarCode(textToPrint, PrinterAttributes.VAL_ALIGN_RIGHT, barcodeWidth, barcodeHeight, false, printerListener);
                    break;
                default:
                    printerManager.printBarCode(textToPrint, PrinterAttributes.VAL_ALIGN_LEFT, barcodeWidth, barcodeHeight, false, printerListener);
                    break;
            }
            callbackContext.success("Result - Cielo - LioLocal - print_BarCode: OK");
            return true;
        } catch (Exception error1) {
            callbackContext.error("Result - Cielo - LioLocal - print_BarCode: " + "Error Detail: " + error1);
        }
        return false;
    }

    private void setStyles() {
        // alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
        // alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 0);
        // alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 30);

        // alignCenter.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
        // alignCenter.put(PrinterAttributes.KEY_TYPEFACE, 1);
        // alignCenter.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

        // alignRight.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
        // alignRight.put(PrinterAttributes.KEY_TYPEFACE, 2);
        // alignRight.put(PrinterAttributes.KEY_TEXT_SIZE, 10);
    }


    // Cielo Global
    protected String parseObjToJsonSrtring(Object obj) {
        Gson gson = new Gson();
        String result = gson.toJson(obj);
        return result;
    }

    protected void resetState() {
        order = null;
    }

}
