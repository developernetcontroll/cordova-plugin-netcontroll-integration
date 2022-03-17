var exec = require('cordova/exec');

//Cielo Lio Local Functions

exports.Teste = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.Teste', [params]);
}

exports.ConfigureSdk = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.ConfigureSdk', [params]);
}

exports.PlaceOrder = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Cielo.LioLocal.PlaceOrder', [params]);
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


