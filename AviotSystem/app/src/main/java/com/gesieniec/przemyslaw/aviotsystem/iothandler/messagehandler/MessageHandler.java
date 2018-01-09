package com.gesieniec.przemyslaw.aviotsystem.iothandler.messagehandler;

import android.os.AsyncTask;
import android.util.Log;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by przem on 09.11.2017.
 */

public class MessageHandler {

    private DatagramSocket socket;

    public void sendAndReceiveUDPMessage(String message, InetAddress address) {
        byte[] buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 2390);
        new SendAndReceive().execute(packet);
    }

    private class SendAndReceive extends AsyncTask<DatagramPacket, Void, String> {

        @Override
        protected String doInBackground(DatagramPacket... datagramPackets) {
            String enchantedCaps = "";
            try {
                /**
                 * send message
                 */
                socket = new DatagramSocket();
                socket.send(datagramPackets[0]);
                /**
                 * w8 4 message from device
                 */

                byte[] buf = new byte[512];
                DatagramPacket receivedPacket = new DatagramPacket(buf,buf.length);
                socket.setSoTimeout(300);
                socket.receive(receivedPacket);
                String receivedMessage = new String(buf,0,receivedPacket.getLength());
                enchantedCaps = receivedMessage;
                socket.close();
            } catch (SocketException e) {
                Log.d("SocketException"," message timeout");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return enchantedCaps;
        }
        protected void onProgressUpdate(Integer... progress) {

        }
        protected void onPostExecute(String messageBack) {
            if(messageBack.contains("stateupdate")){
                TaskDispatcher.newTask(TaskDispatcher.IoTTaskContext.UPDATE_DEVICE_DATA,new DeviceCapabilities(messageBack));
            }
            else if(messageBack.contains("capabilities")){
                TaskDispatcher.newTask(TaskDispatcher.IoTTaskContext.ATTACH_COMPLETE,messageBack);
            }
            else if(messageBack.contains("connectioncfm")){
                TaskDispatcher.newTask(TaskDispatcher.IoTTaskContext.CONSISTENCY_MESSGE_RECEIVED,new DeviceCapabilities(messageBack));
            }
        }
    }
}
