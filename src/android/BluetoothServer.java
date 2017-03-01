package org.apache.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
/**
* This class echoes a string called from JavaScript.
*/
public class BluetoothServer extends CordovaPlugin {

    // Debugging
    private static final String TAG = "econny";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
	
	//application context
	private Context context;

    private CallbackContext callbackContextServerState;// Keeps track of the JS callback context.

    /* save the inputstream content for js request */
    private StringBuffer messageHeap = new StringBuffer();

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                LOG.w(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
					/*Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();*/
                    break;
                case BluetoothChatService.STATE_CONNECTING:
					/*Toast.makeText(context, "connecting", Toast.LENGTH_SHORT).show();*/
                    break;
                case BluetoothChatService.STATE_LISTEN:
					/*Toast.makeText(context, "listening", Toast.LENGTH_SHORT).show();*/
                	break;
                case BluetoothChatService.STATE_NONE:
					/*Toast.makeText(context, "lost connection", Toast.LENGTH_SHORT).show();*/
                	/* service stopped */
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                /* display the read information as toast */
                /*Toast.makeText(context, "Me: \n" + writeMessage,
                Toast.LENGTH_SHORT).show();*/
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                /* save the input messages */
                messageHeap.append(readMessage);
                /* display the read information as toast */
                /*Toast.makeText(context, "Him: \n"+readMessage,
                Toast.LENGTH_SHORT).show();*/
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                break;
            case MESSAGE_TOAST:
                /*Toast.makeText(getApplicationContext(), "",
                               Toast.LENGTH_SHORT).show();*/
                break;
            }
        }
    };
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        /* initialize the values to use */
        context = this.cordova.getActivity().getApplicationContext();

		if(action.equals("init")){
			this.init(callbackContext);
			return true;
		}else if(action.equals("startService")){
			this.startService(callbackContext);
			return true;
		}else if(action.equals("stopService")){
			this.stopService(callbackContext);
			return true;
		}else if(action.equals("sendMessage")){
			String message = args.getString(0);
			this.sendMessage(message, callbackContext);
			return true;
		}else if(action.equals("serverState")){
            this.serverState(callbackContext);
            return true;
        }else if(action.equals("getInStreamByCharacter")){
            this.getInStreamByCharacter(callbackContext);
            return true;
        }
		return false;
	}

	private void init(CallbackContext callbackContext) {

        if (D) LOG.d(TAG, "init service");

		if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
			callbackContext.error("failed");
		}else{
			// Initialize the BluetoothChatService to perform bluetooth connections
			mChatService = new BluetoothChatService(context, mHandler);
			callbackContext.success("finished");
		}
	}
    
    /* 开启服务 */
    public void startService(CallbackContext callbackContext)
    {
        if (D) LOG.d(TAG, "start service");

		if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
	         /* 应用做服务端时，允许被设备发现,他将在不使你应用程序退出的情况下使你的设备能够被发现 */
	        /* Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			context.startActivity(discoverableIntent);*/

			if(mChatService.getState()==mChatService.STATE_NONE){
		        // start service listening
		        mChatService.start();
			}
		}else{

			if(mChatService.getState()==mChatService.STATE_NONE){
		        // start service listening
		        mChatService.start();
			}
		}
		callbackContext.success("finished");
    }
    
    /* 关闭服务 */
    public void stopService(CallbackContext callbackContext)
    {
        if (D) LOG.d(TAG, "stop service");
        // start service stop listening
        mChatService.stop();
		callbackContext.success("finished");
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message, CallbackContext callbackContext) {

        if (D) LOG.d(TAG, "send message");
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(context, "connection lost", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    /**
     * Sends a message.
     */
    private void serverState(CallbackContext callbackContext) {

        if (D) LOG.d(TAG, "server state");
        this.callbackContextServerState = callbackContext;
        /* 返回服务器状态 */
        responseServerState();
    }

    /* 返回服务器状态 */
    private void responseServerState(){
        // Success return object
        this.callbackContextServerState.success(this.getServerStateJSON());
    }

    private JSONObject getServerStateJSON() {
        JSONObject r = new JSONObject();
        try {
            r.put("state", mChatService.getState());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * Sends a message.
     */
    private void getInStreamByCharacter(CallbackContext callbackContext) {

        if(messageHeap.length()>0){
            if (D) LOG.d(TAG, "get input stream by character");
            if (D) LOG.d(TAG, "stack messages: " + messageHeap.toString());
            /* send first character and delete it */
            callbackContext.success(messageHeap.substring(0,1));
            messageHeap.deleteCharAt(0);
        }
    }
	
}

class BluetoothChatService {
    // Debugging
    private static final String TAG = "econny";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) LOG.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        /* 链接并输出内容--触发状态改变条件  */
        mHandler.obtainMessage(BluetoothServer.MESSAGE_STATE_CHANGE, state, -1, null)
                .sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) LOG.d(TAG, "start");

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) 
        {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }else{
        	if(mSecureAcceptThread.isAlive()){
        		/* do nothing */
        	}else{
            	mSecureAcceptThread.start();
        	}
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) LOG.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        if (D) LOG.d(TAG, "connected, Socket Type:");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) LOG.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                        MY_UUID_SECURE);
            } catch (IOException e) {
                LOG.e(TAG,"listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) LOG.d(TAG,
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread");

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    LOG.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                    	/* stop listening new connections -- to avoid two devices fight for the connection */
                    	/* if we want to let the client be able to request again when connection lost we should not close it */
                    	/*try {
    						mmServerSocket.close();
    					} catch (IOException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}*/
                    	
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                LOG.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) LOG.i(TAG, "END mAcceptThread");

        }

        public void cancel() {
            if (D) LOG.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                LOG.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
            } catch (IOException e) {
                LOG.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
            //set state
            setState(STATE_CONNECTING);
        }

        public void run() {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    LOG.e(TAG, " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LOG.e(TAG, "close() of connect failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

        	/* set state to connected */
            setState(STATE_CONNECTED);

            LOG.d(TAG, "create ConnectedThread ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                LOG.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            LOG.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(BluetoothServer.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    LOG.e(TAG, "disconnected", e);
                    // Start the service over to restart listening mode
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BluetoothServer.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                LOG.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LOG.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}