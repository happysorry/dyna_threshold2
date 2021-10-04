package main.java.code;

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

import dyna_threshold.src.main.java.code.service_restart;

public class health_check implements Runnable{
    public static double sim_time = 0;
    public static int flag = 0;
    static ArrayList<String> machine = new ArrayList<>();
    static ArrayList<String> c = new ArrayList<>();
    static int error_count = 0;
    // public int check_time = 30;
    public static int max_iter = 0;
    public health_check(int max_iter){
        this.max_iter = max_iter;
    }

    public static void main(String[]args){
        sim_time = 90;
        // check();
        // add_machine();
        // get_use1();
        add_c();
        check();
        write_error_count();
        // restart();
        Wait(10000);
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        add_c();
        check();
        write_error_count();
    }
    public static void add_c(){
        c.add("http://192.168.99.130:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0");
        c.add("http://192.168.99.130:777/~/mn-cse/mn-name/AE2/Control_Command_Container");
        c.add("http://192.168.99.130:1111/test");
        c.add("http://192.168.99.130:2222/test");
    }
    public static void add_machine(){
        machine.add("worker");
        machine.add("worker1");
        machine.add("worker2");
        machine.add("worker3");
    }
    public static void check(){
        double t = System.nanoTime();
        int iter = 0;
        add_machine();
        double st = System.nanoTime();
        while(true){
            // iter = read_iter();
            // if(iter >= max_iter)
            //     break;
            send();
            //send timeout
            int sig = read();
            if(sig != 0){
                error_count ++;
                // iter --;
                System.out.println("health check");
                restart();
                System.out.println("health iter=" + iter);
                // Wait(180000);
                System.out.println("healthy");
                write(0);
            }
            // System.out.println(iter);
            Wait(10000);
            if((System.nanoTime() - st)/1e9 > 30){
                iter ++;
                st = System.nanoTime();
            }
            if(iter >= max_iter)
                break;
        }
    }




    
    public static void send() {
        for(int i = 0;i<c.size();i++){
            int status = 0;
            try{
                try {
                    int val = (int) ((Math.random() * 899999) + 1);
                    String con = "";
                    con = "false";
                    if ((val % 2) == 1)
                        con = "false";
                    else
                        con = "true";
                    String path = c.get(i);
    
                    // String path = "http://192.168.99.123:666/~/mn-cse/mn-name/AE1/" + stage;
                    URL url = new URL(path);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setDoOutput(true);
                    // http.setRequestProperty("Accept", "application/json");
                    http.setRequestProperty("X-M2M-Origin", "admin:admin");
                    http.setRequestProperty("Content-Type", "application/json;ty=4");
                    try {
                        try{
                        http.setRequestMethod("POST");
                        http.setConnectTimeout(5000);
                        http.setReadTimeout(5000);
                        http.connect();
                        DataOutputStream out = new DataOutputStream(http.getOutputStream());
                        String request = "{\"m2m:cin\": {\"con\": \"" + con
                        + "\", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\"" + val + "\"}}";
                        out.write(request.toString().getBytes("UTF-8"));
                        out.flush();
                        out.close();
                        status = http.getResponseCode();
                        // System.out.println(status);
                        }catch(SocketTimeoutException e){
                            System.out.println("send timeout");
                            write(1);
                            // flag ++;
                            // timeout t = new timeout();
                            // t.restart();
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
            }
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
    public static int read(){
        String filename = "signal.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            int line = 0;
            try {
                line = Integer.parseInt(r.readLine());
            if(line==1){
                return 1;
            }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }


    public static void restart(){
        service_restart s = new service_restart();
        s.restart();
    }

    static void write_iter(int iter) {
        try {
            String filename = "iter.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(iter + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void write(int val) {
        try {
            String filename = "signal.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(val + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void write_error_count() {
        try {
            String filename = "error_count.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(error_count + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void Wait(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
   
}