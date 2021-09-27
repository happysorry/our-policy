package main.java.paper.code.send_req;

import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import main.java.paper.code.app_manager.health_check;
import main.java.paper.code.app_manager.service_restart;


public class send_req implements Runnable{
    public static String output_filename = "";
    public static String stage = "";
    public static int count = 0;
    public send_req(String stage,int count){
        this.stage = stage;
        this.count = count;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        String RFID = rand_RFID();
        // double startTime = System.nanoTime();
        int status = send(RFID,stage);
        // double endtime = (System.nanoTime() - startTime) / 1e6;
        // if(status == 201)
        //     write(endtime);
        Thread.currentThread().interrupt();
    }

    public static void main(String[]args){
        String RFID = rand_RFID();
        // double startTime = System.nanoTime();
        send(RFID,stage);
        // double endtime = (System.nanoTime() - startTime) / 1e6;
        // write(endtime);
    }

    public static String rand_RFID() {
        int val = (int) ((Math.random() * 899999) + 1);
        String RFID = String.valueOf(val);
        return RFID;
    }

    public static int send(String RFID,String stage) {
        int status = 0;
        try{
            try {
                int val = Integer.parseInt(RFID);
                String con = "";
                con = "false";
                if ((count % 5) == 0)
                    con = "false";
                else
                    con = "true";
                // String path = "http://192.168.99.123:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0";
                String path = "http://192.168.99.130:666/~/mn-cse/mn-name/AE1/" + stage;
                URL url = new URL(path);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setDoOutput(true);
                // http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("X-M2M-Origin", "admin:admin");
                http.setRequestProperty("Content-Type", "application/json;ty=4");
                try {
                    try{
                    http.setRequestMethod("POST");
                    http.setConnectTimeout(5000);
                    http.setReadTimeout(5000);
                    http.connect();
                    DataOutputStream out = new DataOutputStream(http.getOutputStream());
                    String request = "{\"m2m:cin\": {\"con\": \"" + con
                    + "\", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\"" + RFID + "\"}}";
                    out.write(request.toString().getBytes("UTF-8"));
                    out.flush();
                    out.close();
                    status = http.getResponseCode();
                    // System.out.println(status);
                    }catch(SocketTimeoutException e){
                        System.out.println("send timeout");
                        re();
                        // timeout t = new timeout();
                        // t.restart();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
            }
        }
        catch(IndexOutOfBoundsException e){
        }
        return status;
    }

    public static void re(){
        // write();
        // service_restart s = new service_restart();
        // s.restart();
        
    }
    public static void write() {
        try {
            String filename = "signal.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(1 + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
}
