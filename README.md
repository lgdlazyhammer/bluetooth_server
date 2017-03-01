# bluetooth_server
bluetooth plugin of cordova
# this plugin works together with it's customer side plugin https://github.com/lgdlazyhammer/bluetooth_client. two mobile device can be connected with eachother and transfer data. before using the plugin. device must be paired with eachother and bluetooth must be set open. 

# methods
<b>initialize service, which is necessary before using plugin</b><br></br>
mobile.bluetoothServer.init(function(data){
			},function(error){
			});

<b>start listening for incomming request</b><br></br>
mobile.bluetoothServer.startService(function(data){
},function(error){
});

<b>stop connected socket and return to listening mode</b><br></br>
mobile.bluetoothServer.stopService(function(data){
},function(error){
});

<b>send message to remote device</b><br></br>
var message = document.getElementById("message").value;
mobile.bluetoothServer.sendMessage(function(data){
},function(error){
},message);

<b>get server state constantly after some millis second delay</b><br></br>
<b>return data: 0-initialized, 1-listening, 2-connecting, 3-connected</b><br></br>
mobile.bluetoothServer.startGetServerState(function(data){
},function(error){
},1000);

<b>get remote device sended message constantly after some milli seconds delay</b><br></br>
mobile.bluetoothServer.startGetInputStream(function(data){
},function(error){
},30);

<b>stop get server state loop</b><br></br>
mobile.bluetoothServer.stopGetServerState();

<b>stop get remote device sended message loop</b><br></br>
mobile.bluetoothServer.stopGetInputStream();



# for example

<b>start listening for incomming connection</b>
<br></br>
mobile.bluetoothServer.init(function(data){
			},function(error){
			});
<br></br>
mobile.bluetoothServer.startService(function(data){
},function(error){
});
<br></br>
<b>monitor the connection state</b><br></br> 
mobile.bluetoothServer.startGetServerState(function(data){
    console.log(data);
},function(error){
},1000);
<br></br>
<b>stop monitor</b><br></br> 
mobile.bluetoothServer.stopGetServerState();
<br></br>
<b>get remote message</b> <br></br>
mobile.bluetoothServer.startGetInputStream(function(data){
    console.log(data);
},function(error){
},delay);
<br></br>
<b>stop getting remote message</b><br></br>  
mobile.bluetoothServer.stopGetInputStream();
<br></br>
<b>send message</b><br></br>
mobile.bluetoothServer.sendMessage(function(data){
},function(error){
},"hello there");
<br></br>
<b>stop connection</b> <br></br>
mobile.bluetoothServer.stopService(function(data){
},function(error){
});
