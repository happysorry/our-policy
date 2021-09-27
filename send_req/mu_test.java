package paper.code.send_req;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;




public class mu_test implements Runnable {

    static long val = 0;
    static ArrayList<String> stage = new ArrayList<String>();// store every stages
    static int num = 0;
    static int cnt = 0;
    static int ip_cnt = 0;
    static ArrayList<Double> freq = new ArrayList<Double>();
    static double sim_time = 0.0;
    static double startTime = 0.0;
    static double changeTime = 0.0;
    static ArrayList<String> ip = new ArrayList<>();
    static int c_time = 30;

    public mu_test(double sim_time){
        this.sim_time = sim_time;
    }
    public static void main(String[] args) {
        add_stage();
        send_req(sim_time);

    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println("mu test up");
        add_freq();
        add_stage();
        startTime = System.nanoTime();
        changeTime = System.nanoTime();  
        read_ver();
        send_req(sim_time);
    }

    public void add_freq() {
        read_input r = new read_input();
        try{
            freq = r.read_use();
            // System.out.println("freq " + freq.size());
        }
        catch(FileNotFoundException e){
            
        }
    }
    

    public static long cal_send_time() {
        double elapsed = System.nanoTime() - startTime;
        elapsed /= 1e9;
        int ind = (int) elapsed;
        // System.out.println("ind " + ind);
        double lambda = freq.get(ind);
        double send_time = Math.log(1 - new Random().nextDouble()) / (-lambda);
        send_time *= 1e9;// change to nanosecond
        long s = (long) send_time;
        // System.out.println(s);
        return s;

    }

    
    public static void add_stage() {
        stage.add("RFID_Container_for_stage0");
        stage.add("RFID_Container_for_stage1");
        stage.add("Liquid_Level_Container");
        stage.add("RFID_Container_for_stage2");
        stage.add("Color_Container");
        stage.add("RFID_Container_for_stage3");
        stage.add("Contrast_Data_Container");
        stage.add("RFID_Container_for_stage4");
      }
    

    public static void stage1(String RFID,int cnt) {
        try{
            try {
                int val = Integer.parseInt(RFID);
                String con = "";
                con = "false";
                if ((val % 2) == 1)
                    con = "false";
                else
                    con = "true";
                String path = "http://" + ip.get(ip_cnt) + ":666/~/mn-cse/mn-name/AE1/" + stage.get(cnt);
                if(cnt == stage.size())
                    cnt = 0;
                URL url = new URL(path);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setDoOutput(true);
                // http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("X-M2M-Origin", "admin:admin");
                http.setRequestProperty("Content-Type", "application/json;ty=4");
                try {
                    try{
                    http.setRequestMethod("POST");
                    http.setConnectTimeout(1000);
                    http.setReadTimeout(1000);
                    http.connect();
                    DataOutputStream out = new DataOutputStream(http.getOutputStream());
                    String request = "{\"m2m:cin\": {\"con\": \"" + con
                    + "\", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\"" + RFID + "\"}}";
                    out.write(request.toString().getBytes("UTF-8"));
                    out.flush();
                    out.close();
                    int satus = http.getResponseCode();
                    // if(satus == 500|| satus == 503){
                    //     System.out.println("send 503");
                    //     ip.remove(ip_cnt);
                    // }
                    // if(satus == 404){
                    //     System.out.println("send 404");
                    //     ip.remove(ip_cnt);
                    // }
                        
                    // System.out.println(satus);
                    // print_response_code(satus);
                    }catch(SocketTimeoutException e){
                        System.out.println("send timeout");
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
            for(int i=0;i<ip.size();i++)
                System.out.print(ip.get(i) + " ");
            System.out.println("\n");
            System.out.println("ip cnt " + ip_cnt);
            System.out.println("IndexOutOfBound " + ip.size());
        }
    }

    public static String rand_RFID() {
        int val = (int) ((Math.random() * 899999) + 1);
        String RFID = String.valueOf(val);
        return RFID;
    }


    static void send_req(double simtime) {
        String RFID = rand_RFID();
        while (true) {
            long send_time = cal_send_time();
            long end = System.nanoTime() + send_time;
            double tmp = System.nanoTime();
            stage1(RFID,cnt);
            cnt ++;
            ip_cnt ++;

            if(ip.size() == 0)
                read_ver();
            
            if(ip_cnt >= ip.size())
                ip_cnt =0;
            if(cnt >= stage.size()){
                cnt = 0;
                RFID = rand_RFID();
            }
            while (System.nanoTime() < end) {
            }
            double ser_time = System.nanoTime() - changeTime;
            ser_time /= 1e9;
            if(ser_time > c_time){
                changeTime = System.nanoTime();
                read_ver();
            }
            double endtime = System.nanoTime();
            tmp = (endtime - startTime) / 1e9;
            if (tmp > simtime)
                break;
        }
        System.out.println("send exit");
    }


    static void Wait(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

    public static void read_ver(){
        ArrayList<String> server = new ArrayList<>();
        String filename = "src/main/java/paper/code/warmup/server.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            
            try {
                while ((line = r.readLine()) != null) {
                    server.add(line);
                }
                add_server(server);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            
        }
    }

    
    public static void add_server(ArrayList<String> server){
        ArrayList<String> ip1 = new ArrayList<>();
        for(int i=0;i<server.size();i++){
            String tmp = server.get(i);
            switch(tmp){
                case "default":
                    ip1.add("192.168.99.123");
                    break;
                case "worker":
                    ip1.add("192.168.99.119");
                    break;
                case "worker1":
                    ip1.add("192.168.99.120");
                    break;
                case "worker2":
                    ip1.add("192.168.99.121");
                    break;
                case "worker3":
                    ip1.add("192.168.99.122");
                    break;
            }
        }
        ip = ip1;
        for(int i=0;i<ip.size();i++)
            System.out.println("ip " + ip.get(i));
    }
}

