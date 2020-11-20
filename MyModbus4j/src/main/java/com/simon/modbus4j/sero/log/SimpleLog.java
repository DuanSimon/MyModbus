package com.simon.modbus4j.sero.log;

public class SimpleLog {
    private final PrintWriter out;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss.SSS");
    private final StringBuilder sb = new StringBuilder();
    private final Date date = new Date();

    public SimpleLog(){
        this(new PrintWriter(System.out));
    }

    public SimpleLog(PrintWriter out){
        this.out = out;
    }

    public void out(String message){
        out(message, null);
    }

    public void out(Trowable t){
        out(null, t);
    }

    public void out(Object o){
        if(o instanceof Throwable){
            out(null, (Throwable) o);
        }else if(o == null){
            out(null, null);
        }else{
            out(o.toString(), null);
        }
    }

    public void close(){
        out.close();
    }

    public synchronized void out(String message, Throwable t){
        sb.delete(0, sb.length());
        date.setTime(system.currentTimeMillis());
        sb.append(sdf.format(date)).append(" ");
        if(message != null){
            sb.append(message);
        }
        if(t != null){
            if(t.getMessage() != null){
                sb.append(" - ").append(t.getMessage());
            }
            out.println(sb.toString());
            t.printStackTrace();
        }else{
            out.println(sb.toString());
        }
        out.flush();
    }

}
