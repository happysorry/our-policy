package paper.code.ql3;

import java.io.FileWriter;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;


import paper.code.app_manager.stop;

public class start_ql3 implements Runnable{
    String con_name;
    int iter = 0;
    double sim_time = 0;
    double max_iter = 0;
    public start_ql3(String con_name,double max_iter){
        this.con_name = con_name;
        this.max_iter = max_iter;
    }

    public void run(){
        ql3 ql = new ql3(con_name);
        ql.get_machine_id();
        ql.read_qtable();
        ql.init_state();
        double startTime = System.nanoTime();
        int iter = 0;
        stop s = new stop();
        int flag = 0;
        while(true){
            // Wait(10000);
            double roundtime = System.nanoTime();
            
            iter = read_iter();
            System.out.println("iteration " + iter);
            if(iter >= max_iter)
                break;
            try {
                ql.learn();
                while((System.nanoTime() - roundtime )/1e9< 30);
                int signal = s.read();
                if(signal != 0){
                    flag = 1;
                    write(iter);
                    write_last();
                    // delete_last();
                    // iter --;
                    Wait(140000);
                    // flag =0;
                }
                if(flag == 1){ //means it just came back from timeout
                    flag =0;
                    // delete_last();
                    iter --;
                }
                // if(flag == 2){ // delete the record after timeout
                //     delete_last();
                //     flag = 0;
                //     iter --;
                // }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ql.print();
        System.out.println("ql exit");
        double avg_qvalue = ql.avg_qvalue();
        System.out.println("avg_qvalue = " + avg_qvalue);
        Thread.currentThread().interrupt();
        // ql.print_tmax_vio();
    }
    
    void Wait(long time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
    void write(int iter) {
        try {
            String filename = "error.txt";
            FileWriter fw1 = new FileWriter(filename,true);
            fw1.write(iter + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    int read_iter(){
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

    void delete_last(){
        ArrayList<String> fn = new ArrayList<>();
        fn.add("ql3/" + con_name + "/" + con_name + "_con1.txt");
        fn.add("ql3/" + con_name + "/" + con_name + "_cpus.txt");
        fn.add("ql3/" + con_name + "/" + con_name + "_response_time.txt");
        fn.add("ql3/" + con_name + "/" + con_name + "_use.txt");
        for(int i = 0;i<fn.size();i++){
            String filename = fn.get(i);
        
            try {
                RandomAccessFile f;
                f = new RandomAccessFile(filename, "rw");
                long length;
                try {
                    length = f.length() - 2;
                    byte b;
                    do {                     
                        length -= 1;
                        f.seek(length);
                        b = f.readByte();
                    } while(b != 10);
                    f.setLength(length+1);
                    f.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
    }

    void write_last(){
        FileReader fr;
        String filename = "ql3/" + con_name + "/" + con_name + "_use2.txt";
        double avg = 0.0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    avg = Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            filename = "error_use.txt";
            FileWriter fw1 = new FileWriter(filename,true);
            fw1.write(con_name + " " + avg + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


