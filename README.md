# bluetooth_server
bluetooth plugin of cordova
# this plugin works together with it's customer side plugin. two mobile device can be connected with eachother and transfer data. before using the plugin. device must be paired with eachother and bluetooth must be set open. 

# initialize service, which is necessary before using plugin
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
# return data: 0-initialized, 1-listening, 2-connecting, 3-connected
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



# for example

start listening for incomming connection ------ 
mobile.bluetoothServer.init(function(data){
			},function(error){
			});
#     
mobile.bluetoothServer.startService(function(data){
},function(error){
});
#
monitor the connection state ------ 
mobile.bluetoothServer.startGetServerState(function(data){
    console.log(data);
},function(error){
},1000);
#
stop monitor ------ 
mobile.bluetoothServer.stopGetServerState();
#
get remote message ------ 
mobile.bluetoothServer.startGetInputStream(function(data){
    console.log(data);
},function(error){
},delay);
#
stop getting remote message ------ 
mobile.bluetoothServer.stopGetInputStream();
#
send message ------ 
mobile.bluetoothServer.sendMessage(function(data){
},function(error){
},"hello there");
#
stop connection ------ 
mobile.bluetoothServer.stopService(function(data){
},function(error){
});
