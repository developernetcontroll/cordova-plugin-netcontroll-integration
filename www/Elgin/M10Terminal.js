var exec = require('cordova/exec');

//Printer Functions

exports.AbreConexaoImpressora = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.AbreConexaoImpressora', [params]);
}

exports.FechaConexaoImpressora = function (success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.FechaConexaoImpressora');
}

exports.AvancaPapel = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.AvancaPapel', [params]);
}

exports.Corte = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.Corte', [params]);
}

exports.ImpressaoTexto = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.ImpressaoTexto', [params]);
}

exports.ImpressaoCodigoBarras = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.ImpressaoCodigoBarras', [params]);
}

exports.DefinePosicao = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.DefinePosicao', [params]);
}

exports.ImpressaoQRCode = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.ImpressaoQRCode', [params]);
}

exports.ImprimeXMLNFCe = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.ImprimeXMLNFCe', [params]);
}

exports.ImprimeXMLSAT = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.ImprimeXMLSAT', [params]);
}

exports.AbreGavetaElgin = function (success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.AbreGavetaElgin');
}

exports.StatusImpressora = function (params, success, error) {
    exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.StatusImpressora', [params]);
}


// //CUSTOM METHOD
// exports.imprimeImagem = function (params, success, error) {
//     exec(success, error, 'NetControllCordovaPluginsIntegration', 'Elgin.M10Terminal.ImprimeImagem', [params]);
// }



