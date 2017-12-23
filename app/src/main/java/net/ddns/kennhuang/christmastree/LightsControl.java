package net.ddns.kennhuang.christmastree;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by user on 12/13/2017.
 */

public class LightsControl implements Runnable {
    private ServerSocket server;
    private Socket socket;
    private JSONObject data = new JSONObject();
    private String mode;

    private final Object sync = new Object();

    public LightsControl(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mode = "0,0,0,0";
    }

    @Override
    public void run() {
        OutputStream os = null;
        while (true) {
            try {
                socket = server.accept();
                os = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Client " + socket.getRemoteSocketAddress() + " connected.");
            String prevString = "";
            while (true) {
                String[] str;
                str = mode.split(",");

                String out = "";
                for (int i = 0; i < str.length; i++) {
                    out += str[i];
                }
                if (!out.equals(prevString)) {
                    try {
                        os.write(out.getBytes("UTF-8"));
                        prevString = out;
                    } catch (IOException e) {
                        if (e.getMessage().contains("Broken pipe")) {
                            System.out.println("Client disconnected");
                            socket = null;
                            os = null;
                            break;
                        } else {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLightsMode(String mode) {
        this.mode = mode;
    }
}
