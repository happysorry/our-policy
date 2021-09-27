package paper.code.ql3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import main.java.paper.code.app_manager.service_restart;
import paper.code.app_manager.stop;

public class get_all_use implements Runnable{
    ArrayList<String> cons = new ArrayList<>();
    ArrayList<String> machine = new ArrayList<>();
    double sim_time = 0;
    stop s = new stop();
    service_restart ss = new service_restart();
    double max_iter = 0;
    public get_all_use(double max_iter){
        this.max_iter = max_iter;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println("all use up");
        add_cons();
        add_machine();
        double startTime = System.nanoTime();
        int iter=0;
        while(true){
            iter = read_iter();
            if(iter > max_iter)
                break;
            double endtime = System.nanoTime() - startTime;
            endtime /= 1e9;
            if(iter >= max_iter)
                break;

            endtime = System.nanoTime();
            get_use1();
            while(((System.nanoTime() - endtime) / 1e9 )< 30) ;
            int signal = s.read();
            if(signal != 0){
                Wait(140000);
                // sim_time += 180;
            }
        }
        Thread.currentThread().interrupt();
    }
    public int read_iter(){
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


    public void add_cons(){
        cons.add("app_mn1");
        cons.add("app_mn2");
        cons.add("app_mnae1");
        cons.add("app_mnae2");
    }

    public void add_machine(){
        machine.add("worker");
        machine.add("worker1");
        machine.add("worker2");
        machine.add("worker3");
    }

    

    public void get_use1() {
        double mn11[] = new double [3];
        double mn22[] = new double [3];
        double mnae11[] = new double [3];
        double mnae22[] = new double [3];
        int mn1_cons = 0;
        int mn2_cons = 0;
        int mnae1_cons = 0;
        int mnae2_cons = 0;
        for(int k = 0;k<3;k++){
            try{
                ArrayList<String> mn1 = new ArrayList(); // store all replicas' container id
                ArrayList<String> mn2 = new ArrayList<>();
                ArrayList<String> mnae1 = new ArrayList<>();
                ArrayList<String> mnae2 = new ArrayList<>();
                int replicas = 0; // replicas of target container
                double use = 0.0;// calculate average cpu utilization
                int i = 0;
                /**
                 * // * get cpu utilization,replicas
                 */
                Runtime run = Runtime.getRuntime();
                Process pr;
                for (i = 0; i < machine.size(); i++) {
                    String cmd = "sudo docker-machine ssh " + machine.get(i) + " docker stats --no-stream";
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
                            int flag = 0;
                            int j = 0;
                            /**
                             * 
                             */
                            for(j=0;j<cons.size();j++){
                                if(line.indexOf(cons.get(j)) != -1){
                                    flag = 1;
                                    break;
                                }
                            }
                            if (flag == 0) {
                                continue;
                            }
                            // System.out.println(machine_id.get(i));
                            String[] sp = line.split("%");
                            String[] sp2 = sp[0].split(" ");
                            String ii = sp2[sp2.length - 1];
                            // System.out.println("ii" + ii);
                            switch(j){
                                case 0:
                                    mn1.add(ii);
                                    break;
                                case 1:
                                    mn2.add(ii);
                                    break;
                                case 2:
                                    mnae1.add(ii);
                                    break;
                                case 3:
                                    mnae2.add(ii);
                                    break;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
                double avg = 0;
                for (i = 0; i < mn1.size(); i++) {
                    double tmp = Double.parseDouble(mn1.get(i));
                    avg += tmp;
                }
                avg /= mn1.size();
                mn1_cons = mn1.size();
                mn11[k] = avg;
                if(mn1_cons < 1){
                    mn1_cons = 1;
                    ss.recovery(mn1_cons);
                }
                    
                //for record
                // print_use(avg, "app_mn1");
                // print_cons(mn1.size(), "app_mn1");
                //for qlearning
                
                // print_use2(avg, "app_mn1");
                // print_cons2(mn1.size(), "app_mn1");
    ////////////////////////////////////////
                avg = 0;
                for(i=0;i<mn2.size();i++){
                    double tmp = Double.parseDouble(mn2.get(i));
                    avg += tmp;
                }
                avg /= mn2.size();
                mn22[k] = avg;
                
                mn2_cons = mn2.size();
                if(mn2_cons < 1){
                    mn2_cons = 1;
                    ss.recovery(mn2_cons);
                }
                    
    /////////////////////////////////////////////
                avg = 0;
                for(i=0;i<mnae1.size();i++){
                    double tmp = Double.parseDouble(mnae1.get(i));
                    avg += tmp;
                }
                avg /= mnae1.size();
                mnae11[k] = avg;
                mnae1_cons = mnae1.size();
                if(mnae1_cons < 1){
                    mnae1_cons = 1;
                    ss.recovery(mnae1_cons);
                }
                
    ///////////////////////////////////////
                avg = 0;
                for(i=0;i<mnae2.size();i++){
                    double tmp = Double.parseDouble(mnae2.get(i));
                    avg += tmp;
                }
                avg /= mnae2.size();
                mnae22[k] = avg;
                mnae2_cons = mnae2.size();
                if(mnae2_cons < 1){
                    mnae2_cons = 1;
                    ss.recovery(mnae2_cons);
                }
                    
            }
            catch(Exception e){
    
            }
        }
        /////////////////////////////////////////
        double avg = 0;
        for(int i = 0;i<3;i++){
            avg += mn11[i];
        }
        avg /= 3;
        print_cons(mn1_cons, "app_mn1");
        print_cons2(mn1_cons, "app_mn1");
        // print_use(avg, "app_mn1");
        print_use2(avg, "app_mn1");
        /////////////////////////////////////////
        avg = 0;
        for(int i = 0;i<3;i++)
            avg += mn22[i];
        avg /=3;
        print_cons(mn2_cons, "app_mn2");
        print_cons2(mn2_cons, "app_mn2");
        // print_use(avg, "app_mn2");
        print_use2(avg, "app_mn2");
        /////////////////////////////////////////
        avg = 0;
        for(int i = 0;i<3;i++)
            avg += mnae11[i];
        avg /= 3;
        print_cons(mnae1_cons, "app_mnae1");
        print_cons2(mnae1_cons, "app_mnae1");
        // print_use(avg, "app_mnae1");
        print_use2(avg, "app_mnae1");
        /////////////////////////////////////////
        avg = 0;
        for(int i = 0;i<3;i++)
        avg += mnae22[i];
        avg /= 3;
        print_cons(mnae2_cons, "app_mnae2");
        print_cons2(mnae2_cons, "app_mnae2");
        // print_use(avg, "app_mnae2");
        print_use2(avg, "app_mnae2");
    }

    public void print_use(double avg , String con_name) {
        try {
            String filename = "ql3/" + con_name + "/" + con_name + "_use.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(avg + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void print_use2(double avg , String con_name) {
        try {
            String filename = "ql3/" + con_name + "/" + con_name + "_use2.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(avg + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void print_cons(double cons,String con_name){
        try {
            String filename = "ql3/" + con_name + "/" + con_name + "_con1.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(cons + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void print_cons2(double cons,String con_name){
        try {
            String filename = "ql3/" + con_name + "/" + con_name + "_con2.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(cons + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void Wait(long time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
}
