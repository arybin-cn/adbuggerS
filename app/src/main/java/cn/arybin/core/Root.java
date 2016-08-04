package cn.arybin.core;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Root {
    private static Root cachedRoot = null;

    private Listener listener = null;

    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private StringBuilder outputs = new StringBuilder();

    public interface Listener {
        public void receiveOuput(CharSequence raw);
    }


    private Root(Listener listener) {
        this.listener = listener;
    }

    public static Root getInstance() {
        return getInstance(null);
    }

    public static Root getInstance(Listener listener) {
        try {
            return tryToGetInstance(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Root tryToGetInstance(Listener listener) throws Exception {
        if (cachedRoot != null) {
            return cachedRoot;
        }
        cachedRoot = new Root(listener);
        Process suProcess = Runtime.getRuntime().exec("su");
        OutputStream os = suProcess.getOutputStream();
        InputStream is = suProcess.getInputStream();
        cachedRoot.reader = new BufferedReader(new InputStreamReader(is));
        cachedRoot.writer = new BufferedWriter(new OutputStreamWriter(os));
        cachedRoot.prepareOutputs();
        return cachedRoot;
    }

    private void prepareOutputs() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(300);
                        CharSequence tmp = null;
                        if ((tmp = Root.this.read()).length() > 0) {
                            listener.receiveOuput(tmp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    public boolean clearCache() {
        boolean flag = cachedRoot == null;
        cachedRoot = null;
        return flag;
    }

    public Root write(String... cmds) throws Exception {
        for (String cmd : cmds) {
            writer.write(cmd);
            writer.newLine();
        }
        writer.flush();
        return this;
    }

    public CharSequence read() throws Exception {
        synchronized (outputs) {
            String tmp = null;
            while((tmp = reader.readLine()) != null){
                outputs.append(tmp);
                System.out.println(tmp);
            }
            tmp = outputs.toString();
            outputs.setLength(0);
            return tmp;
        }
    }

}
