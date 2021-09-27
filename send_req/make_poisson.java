package main.java.paper.code.send_req;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class make_poisson {
    static ArrayList<Double> s = new ArrayList<>();
    public static int div = 10;
    
    public static void main(String[]args){
        read();
        // for(int i=0;i<600;i++)
        //     cal(40);
    }

    public static void read(){
        String filename = "send_req/input/input(7200~10800).dat";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            
            try {
                while ((line = r.readLine()) != null) {
                    double lambda = Double.parseDouble(line);
                    lambda /= div;
                    cal(lambda);
                    write((long) lambda);
                }
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void cal(double lambda){
        long t = 0;
        while(true){
            if(t > 1000)
                break;
            double send_time = Math.log(1 - new Random().nextDouble()) / (-lambda); //exponential distribution
            // double send_time = 1 / lambda;//constant interval
            send_time *= 1e3;// change to ms
            long s = (long) send_time;
            write(s);
            t += s;
        }
        
    }

    public static void write(long val){
        String filename = "send_req/input/exp(7200~10800).dat";
        // String filename = "send_req/input/fixed20.dat";
            FileWriter fw1;
            try {
                fw1 = new FileWriter(filename, true);
                fw1.write(val + "\n");
                fw1.flush();
                fw1.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
    }
}
