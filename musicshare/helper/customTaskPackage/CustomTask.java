package com.sms.musicshare.helper.customTaskPackage;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.io.IOException;

@SuppressWarnings("unchecked") public class CustomTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{

    private Context context;

    public CustomTask(Context context) {
        super();
        this.context = context;
    }

    private onPreExecuteListener preExecuteListener;
    private onBackGroundListener<Params,Result> backGroundListener;
    private onFinishListener<Result> finishListener;
    private onProgressTaskListener<Progress> progressTaskListener;
    private onCancelledListener<Result> cancelledListener;


    public void setCancelledListener(onCancelledListener<Result> cancelledListener) {
        this.cancelledListener = cancelledListener;
    }


    public void setOnPreExecuteListener(onPreExecuteListener L){
        this.preExecuteListener = L;
    }

    public void setOnBackGroundListener(onBackGroundListener<Params,Result> L){
        this.backGroundListener = L;
    }

    public void setOnFinishListener(onFinishListener<Result> L){
        this.finishListener = L;
    }

    public void setProgressTaskListener(onProgressTaskListener<Progress> progressTaskListener) {
        this.progressTaskListener = progressTaskListener;
    }

    @Override
    protected void onPreExecute() {
        if(preExecuteListener != null)
            preExecuteListener.preExecuteTask();
        else
            super.onPreExecute();
    }

    public void DoProgress(Progress... values){
        publishProgress(values);
    }

    @Override
    protected Result doInBackground(Params[] params) {
        try {
            Result result = backGroundListener.backgroundDoing(params);
            if (result != null)
                return result;
            else
                return null;
        } catch (InterruptedException e) {
            return (Result) e.toString();
        } catch (IOException e) {
            return (Result) e.toString();
        }
    }

    private Context getContext(){
        return this.context;
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        if (progressTaskListener != null)
            progressTaskListener.onUpdateProgress(values);
        else
            super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Result result) {
        try {
            finishListener.OnFinishTask(result);
        }catch (Exception e){
            finishListener.OnFinishTaskException(e);
        }
    }

    @Override
    protected void onCancelled(Result result) {
        if(cancelledListener != null)
            cancelledListener.onTaskCancelled(result);
        super.onCancelled(result);
    }


    @Override
    protected void onCancelled() {
        if(cancelledListener != null)
            cancelledListener.onTaskCancelled();
        super.onCancelled();
    }


    public interface onBackGroundListener<Params,Result>{
        Result backgroundDoing(Params... params) throws InterruptedException, IOException;
    }

    public interface onFinishListener<Result>{
        Result OnFinishTask(Result result) throws Exception;
        String OnFinishTaskException(Exception e);
    }

    public interface onPreExecuteListener{
        void preExecuteTask();
    }

    public interface onProgressTaskListener<Progress>{
        void onUpdateProgress(Progress... values);
    }

    public interface onCancelledListener<Result>{
        void onTaskCancelled(Result result);
        void onTaskCancelled();
    }
}
