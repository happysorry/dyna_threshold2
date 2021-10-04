package dyna_threshold.src.main.java.code;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class service_restart {
    public static void main(String[]args){
        add_cons();
        restart();
    }

    public static ArrayList<String> cons = new ArrayList<>();

    public service_restart(){
        add_cons();
    }

    public static void restart(){
        System.out.println("service restart");
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker stack rm app";
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
        Wait(20000);

        cmd = "sudo docker-machine ssh default docker stack deploy --compose-file docker-compose.yml app";
        //docker stack deploy --compose-file docker-compose.yml app
        //docker stack deploy --compose-file docker-compose.yml app
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
        Wait(60000);
        recovery();
        Wait(60000);
        write();
    }

    public  static void write() {
        try {
            String filename = "signal.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(0 + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void add_cons(){
        cons.add("app_mn1");
        cons.add("app_mn2");
        cons.add("app_mnae1");
        cons.add("app_mnae2");
    }

    public static int get_cons(String con_name){
        String filename = "con1/" + con_name + "_con2.txt";
        int replicas = 0;
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    replicas = (int) Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return replicas;
    }

    public static void recovery(){
        for(int i = 0;i<cons.size();i++){
            int replicas = get_cons(cons.get(i));
            Runtime run = Runtime.getRuntime();
            Process pr;
            String cmd = "sudo docker-machine ssh default docker service scale " + cons.get(i) + "=" + replicas;
            // System.out.println(cmd);
            try {
                pr = run.exec(cmd);
            }catch(IOException e){

            }
        }
    }

    public static void recovery(int replicas){
        for(int i = 0;i<cons.size();i++){
            Runtime run = Runtime.getRuntime();
            Process pr;
            String cmd = "sudo docker-machine ssh default docker service scale " + cons.get(i) + "=" + replicas;
            // System.out.println(cmd);
            try {
                pr = run.exec(cmd);
            }catch(IOException e){

            }
        }
    }

    public static void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
