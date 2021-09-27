package paper.code.send_req;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class get_server implements Runnable{
    public String con_name = "app_mn1";
    
    double sim_time = 0;

    public get_server(double sim_time){
        this.sim_time = sim_time;
    }

    public void get_server1(){
        ArrayList<String> new_server = new ArrayList<>();
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service ps app_mn1";
        try {
            pr = run.exec(cmd);
            BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            String result = "";
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                if(line.indexOf(con_name)<0)
                    continue;
                String[] sp = line.split(" ");
                String tmp = sp[22];
                new_server.add(tmp);
                // System.out.println("server name " + tmp);
            }
            write(new_server);
            } catch (IOException e) {
                System.out.println(e);
        } 
    }

    public void write(ArrayList<String> new_server){
        try {
            
            FileWriter fw = new FileWriter("src/main/java/paper/code/warmup/server.txt");
            for(int i=0;i<new_server.size();i++){
                // System.out.println("server111 " + new_server.get(i));
                fw.write(new_server.get(i) + "\n");
            }
                
            // fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println("get server up");
        double startTime = System.nanoTime();
        while(true){
            double elapsed = System.nanoTime() - startTime;
            double roundtime = System.nanoTime();
            elapsed /= 1e9;
            if(elapsed > sim_time)
                break;
            get_server1();
            while((System.nanoTime() - roundtime) / 1e9 < 30);
        }
        System.out.println("get server exit");
    }
    
}
