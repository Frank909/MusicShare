package com.sms.musicshare.connectivity;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sms.musicshare.helper.Info;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicLibraryScanner;
import com.sms.musicshare.helper.customTaskPackage.CustomTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread{

    private Context context;
    private int mPort = 0;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private ArrayList<InfoTrack> mSharedTracks = new ArrayList<>();
    private ServerSocket serverSocket = null;

    private CustomTask<Void, Void, Object> createServerSocket = new CustomTask<>(getContext());
    private InjectOnPreExecute injectOnPreExecute;
    private InjectOnFinishTask injectOnFinishTask;
    private InjectOnTaskCancelled injectOnTaskCancelled;

    public ServerThread(Context context, int port, ArrayList<InfoTrack> tracks) throws IOException, InterruptedException {
        this.context = context;
        this.mPort = port;
        this.mSharedTracks.addAll(tracks);

        setPreCreateServerSocket();
        setCreateServerSocket();
        setFinishSocket();
        setCancelCreateSocket();
    }

    public void executeServer(){
        createServerSocket.execute();
    }

    private Context getContext(){
        return this.context;
    }

    private void setPreCreateServerSocket(){
        createServerSocket.setOnPreExecuteListener(new CustomTask.onPreExecuteListener() {
            @Override
            public void preExecuteTask() {
                if(injectOnPreExecute != null)
                    injectOnPreExecute.onPreExecuteTask();
            }
        });
    }

    public CustomTask<Void, Void, Object> getTask(){
        return this.createServerSocket;
    }

    private void setCreateServerSocket() throws IOException, InterruptedException {
        createServerSocket.setOnBackGroundListener(new CustomTask.onBackGroundListener<Void, Object>() {
            @Override
            public Object backgroundDoing(Void... params) throws InterruptedException, IOException {
                try {
                    serverSocket = new ServerSocket(mPort);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                if(serverSocket != null){
                    try {
                        while (true) {
                            socket = serverSocket.accept();
                            try {
                                in = new ObjectInputStream(socket.getInputStream());
                                String ok = (String) in.readObject();

                                if(ok.equals("OK")) {
                                    out = new ObjectOutputStream(socket.getOutputStream());

                                    int count = 0;
                                    for(InfoTrack track : mSharedTracks) {
                                        out.writeObject(track.getTitle());
                                        out.writeObject(track.getArtist().getName());
                                        out.writeObject(track.getPath());
                                        count++;
                                        if(count == mSharedTracks.size())
                                            out.writeObject(false);
                                        else
                                            out.writeObject(true);
                                        String ok2 = (String) in.readObject();
                                    }
                                    out.close();
                                }

                                in.close();
                            } catch (IOException e) {
                                socket.close();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        });
    }


    private void setFinishSocket(){
        createServerSocket.setOnFinishListener(new CustomTask.onFinishListener<Object>() {
            @Override
            public Object OnFinishTask(Object o) throws Exception {
                if(injectOnFinishTask != null)
                    injectOnFinishTask.onFinishTask(o);
                try {
                    if(socket != null)
                        socket.close();
                    if(serverSocket != null)
                        serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String OnFinishTaskException(Exception e) {
                if(injectOnFinishTask !=null)
                    injectOnFinishTask.onFinishTask(e);
                try {
                    if(socket != null)
                        socket.close();
                    if(serverSocket != null)
                        serverSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return null;
            }
        });
    }

    private void setCancelCreateSocket(){
        createServerSocket.setCancelledListener(new CustomTask.onCancelledListener<Object>() {
            @Override
            public void onTaskCancelled(Object o) {
            }

            @Override
            public void onTaskCancelled() {
                if(injectOnTaskCancelled != null)
                    injectOnTaskCancelled.onCancel();
            }
        });
    }

    public CustomTask.Status getTaskSocketStatus(){
        return createServerSocket.getStatus();
    }

    public ServerSocket getServerSocket(){
        return serverSocket;
    }

    public void setInjectOnFinishTask(InjectOnFinishTask injectOnFinishTask) {
        this.injectOnFinishTask = injectOnFinishTask;
    }

    public void setInjectOnTaskCancelled(InjectOnTaskCancelled injectOnTaskCancelled) {
        this.injectOnTaskCancelled = injectOnTaskCancelled;
    }

    public void setInjectOnPreExecute(InjectOnPreExecute injectOnPreExecute) {
        this.injectOnPreExecute = injectOnPreExecute;
    }


    /**
     * This interfaces allow external code blocks injection
     * **/
    public interface InjectOnFinishTask {
        void onFinishTask(Object o);

        void onFinishTask(Exception e);
    }

    public interface InjectOnPreExecute {
        void onPreExecuteTask();
    }

    public interface InjectOnTaskCancelled {
        void onCancel();
    }

    /*public void setDoInBackground(final int millis) {
        mCustomTask.setOnBackGroundListener(new CustomTask.onBackGroundListener() {
            @Override
            public Object backgroundDoing(Object[] params) throws InterruptedException {
                try {

                    /**
                     * Create a server socket and wait for client connections. This
                     * call blocks until a connection is accepted from a client
                     *
                    ServerSocket serverSocket = new ServerSocket(mPort);
                    Socket socket = serverSocket.accept();

                    in = new ObjectInputStream(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());

                    out.writeObject("prova");

                    /**
                     * If this code is reached, a client has connected and transferred data
                     * Save the input stream from the client as a JPEG file
                     */

                    /*
                    final File f = new File(Environment.getExternalStorageDirectory() + "/"
                            + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                            + ".jpg");

                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();
                    InputStream inputstream = client.getInputStream();
                    copyFile(inputstream, new FileOutputStream(f));
                    serverSocket.close();
                    return f.getAbsolutePath();*
                    return null;
                } catch (IOException e) {
                    return null;
                }
            }
        });
    }*/
}
