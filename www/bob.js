var alice = require('cordova-plugin-netcontroll-integration.alice');

function bob(){
    console.log("Hello World From Bob! Are you there Alice?");
    alice();
}

// bob();

module.exports = bob;