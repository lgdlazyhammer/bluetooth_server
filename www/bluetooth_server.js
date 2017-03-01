var exec = require('cordova/exec');

var MyPlugin = function() {};

MyPlugin.prototype.init = function(successCallback,failureCallback) {
	exec(successCallback, failureCallback, "BluetoothServer", "init", []);
}

MyPlugin.prototype.startService = function(successCallback,failureCallback) {
	exec(successCallback, failureCallback, "BluetoothServer", "startService", []);
}

MyPlugin.prototype.stopService = function(successCallback,failureCallback) {
	exec(successCallback, failureCallback, "BluetoothServer", "stopService", []);
}

MyPlugin.prototype.sendMessage = function(successCallback,failureCallback,str) {
	exec(successCallback, failureCallback, "BluetoothServer", "sendMessage", [str]);
}

MyPlugin.prototype.getServerState = function(successCallback,failureCallback) {
	
	/* ˢ�·�����״̬ */
	exec(successCallback, failureCallback, "BluetoothServer", "serverState", []);
}

MyPlugin.prototype.startGetServerState = function(successCallback,failureCallback,delay) {
	
	MyPlugin.prototype.serverStateInterId = setInterval(function(){
		/* ˢ�·�����״̬ */
		exec(successCallback, failureCallback, "BluetoothServer", "serverState", []);
	},delay);
}

MyPlugin.prototype.stopGetServerState = function() {
	
	if(MyPlugin.prototype.serverStateInterId){
		window.clearInterval(MyPlugin.prototype.serverStateInterId);
	}
}

MyPlugin.prototype.getInStreamByCharacter = function(successCallback,failureCallback) {
	exec(successCallback, failureCallback, "BluetoothServer", "getInStreamByCharacter", []);
}

MyPlugin.prototype.startGetInputStream = function(successCallback,failureCallback,delay) {
	/* ÿ��30�������У�ÿ��30����֡ */
	MyPlugin.prototype.inStreamInterId = setInterval(function(){
		/* ˢ�·�����״̬ */
		exec(successCallback, failureCallback, "BluetoothServer", "getInStreamByCharacter", []);
	},delay);
}

MyPlugin.prototype.stopGetInputStream = function() {
	/* cancel interval */
	if(MyPlugin.prototype.inStreamInterId){
		window.clearInterval(MyPlugin.prototype.inStreamInterId);
	}
}

var myplugin = new MyPlugin();
module.exports = myplugin;