# bluetooth_server
bluetooth plugin of cordova
# this plugin works together with it's customer side plugin
# two mobile device can connected with eachother and transfer data
# before use the plugin, device must be paired with eachother and bluetooth must be set open

# initialize service, which is necessary before useing plugin
mobile.bluetoothServer.init(function(data){
			},function(error){
			});

# start listening for incomming request
mobile.bluetoothServer.startService(function(data){
},function(error){
});

# stop connected socket and return to listening mode
mobile.bluetoothServer.stopService(function(data){
},function(error){
});

# send message to remote device
var message = document.getElementById("message").value;
mobile.bluetoothServer.sendMessage(function(data){
},function(error){
},message);

# get server state constantly after some millis second delay
# 0-initialized, 1-listening, 2-connecting, 3-connected
mobile.bluetoothServer.startGetServerState(function(data){
},function(error){
},1000);

# get remote device sended message constantly after some milli seconds delay
mobile.bluetoothServer.startGetInputStream(function(data){
},function(error){
},30);

# stop get server state loop
mobile.bluetoothServer.stopGetServerState();

# stop get remote device sended message loop
mobile.bluetoothServer.stopGetInputStream();
