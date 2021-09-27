package paper.code.ql3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class test{
    public static void main(String[]args){
        get_cons();
    }
    public static void get_cons(){
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
                if(line.indexOf("app_mn1")<0)
                    continue;
                String[] sp = line.split(" ");
                String tmp = sp[22];
                System.out.println(tmp);
            }
            } catch (IOException e) {
                System.out.println(e);
        }  
    }
}