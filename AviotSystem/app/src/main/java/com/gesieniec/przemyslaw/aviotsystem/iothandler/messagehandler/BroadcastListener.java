package com.gesieniec.przemyslaw.aviotsystem.iothandler.messagehandler;

import android.os.AsyncTask;
import android.util.Log;

import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by przem on 16.11.2017.
 */

public class BroadcastListener extends AsyncTask<String, List<String>, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        int port = 11000;
        try {
            DatagramSocket socket = new DatagramSocket(null);
            socket.bind(new InetSocketAddress(port));
            socket.setBroadcast(true);
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                socket.receive(packet);
                String msg = new String(buffer, 0, packet.getLength());
                if (msg.contains("AttachRequest")) {
                    publishProgress(stringToList(msg));
                }
                Log.d("BroadcastListener", packet.getAddress().getHostName() + ": " + msg);
                packet.setLength(buffer.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onProgressUpdate(List<String>... progress) {
        TaskDispatcher.newTask(TaskDispatcher.IoTTaskContext.ATTACH_REQUEST, progress[0]);
    }

    protected void onPostExecute(Void result) {

    }

    List<String> stringToList(String message) {
        return new ArrayList<>(Arrays.asList(message.split(";")));
    }
}