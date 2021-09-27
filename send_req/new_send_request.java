package paper.code.send_req;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.java.paper.code.send_req.send_req;
import paper.code.app_manager.stop;

public class new_send_request implements Runnable{
    public static ArrayList<Long> stop = new ArrayList<>();
    public static ArrayList<String> stage = new ArrayList<>();
    public static String filename = "";
    public static String output_filename = "";
    public static double sim_time = 0;
    public static int max_iter=0;
    public static int count = 0;
    public new_send_request(String filename,int max_iter){
        this.filename = filename;
        this.max_iter = max_iter;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println(filename);
        System.out.println("max_iter=" + max_iter);
        read();
        add_stage();
        send();
    }
    
    public static void main(String[]args){
        // filename = "send_req/input/exp_60.dat";
        read();
        add_stage();
        send();
        // System.out.println(stop.size());
    }

    public static void read(){
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            
            try {
                while ((line = r.readLine()) != null) {
                    stop.add(Long.parseLong(line));
                }
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static int read_iter(){
        FileReader fr;
        String filename = "iter.txt";
        double avg = 0.0;
        int iter = 0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    iter = Integer.parseInt(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            return 0;
        }
        return iter;
    }

    public static void send(){
        double st = System.nanoTime();
        double it = System.nanoTime();
        stop sto = new stop();
        ExecutorService es = Executors.newFixedThreadPool(40);
        for(int i=0;i<stop.size();i++){
            count ++;
            long t = stop.get(i);
            int ind = i % stage.size();
            String s = stage.get(ind);
            // System.out.println(s);
            es.execute(new send_req(s,count));
            Wait(t);

            if((System.nanoTime() - st) / 1e9 > 10){
                st = System.nanoTime();
                int signal = sto.read();
                if(signal != 0)
                    Wait(140000);

                int flag = read_iter();
                if(flag >= max_iter)
                    break;
            }
            

        }
        
        System.out.println("send over");
        Thread.currentThread().interrupt();
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
    public static void Wait(long time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
}
