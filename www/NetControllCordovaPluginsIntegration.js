var exec = require('cordova/exec');

exports.TesteRoot = function (arg0, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Teste', [arg0]);
};

exports.coolMethod2 = function (arg0, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'coolMethod2', [arg0]);
};

