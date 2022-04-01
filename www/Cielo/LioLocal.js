var exec = require('cordova/exec');

//Cielo Lio Local Functions

exports.Teste = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.Teste', [params]);
}

exports.ConfigureSdk = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.ConfigureSdk', [params]);
}

exports.OrderManagerBind = function (success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.OrderManagerBind');
}

exports.OrderManagerUnbind = function (success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.OrderManagerUnbind');
}




exports.CreateDraftOrder = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.CreateDraftOrder', [params]);
}

exports.AddOrderItem = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.AddOrderItem', [params]);
}

exports.MakePayment = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.MakePayment', [params]);
}




exports.ListOrders = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.ListOrders', [params]);
}



exports.CancelPayment = function (param, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.CancelPayment', [param]);
}

exports.PrintSimpleText = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.PrintSimpleText', [params]);
}

exports.PrintQrCode = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.PrintQrCode', [params]);
}

exports.PrintBarCode = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.PrintBarCode', [params]);
}

// exports.PlaceOrder = function (params, success, error) {
//     exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.PlaceOrder', [params]);
// }




exports.TerminalInformation = function (success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.TerminalInformation');
}











