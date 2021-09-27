package main.java.paper.code.send_req;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import paper.code.app_manager.restart;

public class timeout {


    public void read(){
        String filename = "send_req.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            
            try {
                line = r.readLine();
                int tmp = Integer.parseInt(line);
                if(tmp >= 5){
                    restart();
                    tmp = 0;
                }
                tmp ++;
                write(tmp);
                    
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            write(0);
        }
    }

    public void write(int val){
        String filename = "send_req/timeout.txt";
        // System.out.println(val);
        // String filename = "exp_30_60s.txt";
            FileWriter fw1;
            try {
                fw1 = new FileWriter(filename);
                fw1.write(val + "\n");
                fw1.flush();
                fw1.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
    }

    public void restart(){
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service update --force --update-parallelism 1 app_mn1";
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }
}
