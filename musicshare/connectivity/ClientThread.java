package com.sms.musicshare.connectivity;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.Toast;

import com.sms.musicshare.helper.InfoTrack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends AsyncTask<Void, Void, Void>{

    private Context mContext;
    private InetAddress mHostAddress;
    private int mPort = 0, len;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ArrayList<InfoTrack> infoTracks = new ArrayList<>();

    public ClientThread(Context context, InetAddress address, int port) throws IOException{
        this.mContext = context;
        this.mHostAddress = address;
        this.mPort = port;
    }

    @Override
    protected Void doInBackground(Void[] objects) {
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            this.socket = new Socket(mHostAddress, mPort);
            infoTracks.clear();

            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("OK");

            in = new ObjectInputStream(socket.getInputStream());



            final ArrayList<TempTrack> tempTracks = new ArrayList<>();
                new Thread() {
                    @Override
                    public void run() {
                        boolean counting = true;

                        try {
                            while (counting) {
                                String name = (String) in.readObject();
                                String artist = (String) in.readObject();
                                String path = (String) in.readObject();
                                counting = in.readBoolean();

                                tempTracks.add(new TempTrack(name, artist, path));
                                out.writeObject("OK2");
                            }
                        }catch (IOException e){

                        }catch (ClassNotFoundException class_ex){

                        }
                    }
                }.run();


            in.close();
            out.close();
            /*OutputStream outputStream = socket.getOutputStream();
            ContentResolver mContentResolver = mContext.getContentResolver();
            InputStream inputStream = mContentResolver.openInputStream(Uri.parse("path/to/picture.jpg"));
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();*/


        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
            e.printStackTrace();
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return null;
    }

    public Context getContext() {
        return mContext;
    }

    public class TempTrack{
        private String name;
        private String artist;
        private String path;

        public TempTrack(String name, String artist, String path){
            this.name = name;
            this.artist = artist;
            this.path = path;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getArtist() {
            return this.artist;
        }

        public String getName() {
            return this.name;
        }

        public String getPath() {
            return this.path;
        }
    }
}
