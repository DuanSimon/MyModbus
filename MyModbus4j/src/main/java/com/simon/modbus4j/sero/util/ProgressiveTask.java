package com.simon.modbus4j.sero.util;

abstract public class ProgressiveTask implements Runnable {
    private boolean cancelled = false;
    protected boolean completed = false;
    private ProgressiveTaskListener listener;

    public ProgressiveTask(){

    }

    public ProgressiveTask(ProgressiveTaskListener l){
        listener = l;
    }

    public void cancel(){
        cancelled = true;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public final void run(){
        while(true){
            if(isCancelled()){
                declareFinished(true);
                break;
            }

            runImpl();

            if(isCompleted()){
                declareFinished(false);
                break;
            }
            completed = true;
        }
    }

    protected void declareProgress(float progress){
        ProgressiveTaskListener l = listener;
        if(l != null){
            l.progressUpdate(progress);
        }
    }

    private void declareFinished(boolean cancelled){
        ProgressiveTaskListener l = listener;
        if(l != null){
            if(cancelled){
                l.taskCancelled();
            }else{
                l.taskCompleted();
            }
        }
    }

    abstract protected void runImpl();
}
