package paper.code.send_req;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class make_input {
    static ArrayList<Double> data = new ArrayList();
    public static void main(String[]args){
        read();
        cal();
    }

    public static void read(){
        try {
            FileReader fr = new FileReader("src/main/java/paper/code/send_req/profile.dat");
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            double tmp = 0.0;
            try {
                while ((line = r.readLine()) != null) {
                    tmp = Double.parseDouble(line);
                    data.add(tmp);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void cal(){
        double avg = 0;
        int ind = 0;
        while(ind<data.size()){
            avg = 0;
            for(int j=0;j<60;j++){
                avg += data.get(ind);
                ind ++;
            }  
            avg /= 60;
            write(avg);
        }
    }

    public static void write(double val){
        try {
            FileWriter fw = new FileWriter("src/main/java/paper/code/send_req/input.dat",true);
            for(int i=0;i<60;i++){
                fw.write(val + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
