package main.java.code;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class globe implements Runnable{
    /////tmax = vm * tmax
    double phi = 0.1;
    public ArrayList<String> name = new ArrayList();
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }


    public void calculate_tmax(){
        add_name();
        for(int i=0;i<name.size();i++){
            String con_name = name.get(i);
            double tmax = read_tmax(con_name);
            double resp = response_time(con_name);
            // double vm = 
        }

    }

    public void add_name(){
        name.add("app_mn1");
        name.add("app_mn2");
        name.add("app_mnae1");
        name.add("app_mnae2");
    }
    public double read_tmax(String con_name){
        FileReader fr;
        String filename = "tmax/" + con_name + "_tmax.txt";
        double tmax = 0.0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    tmax = Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {// give a initial tmax
            System.out.println("initial tmax");
            switch(con_name){
                case("app_mn1"):
                    tmax = 25;
                    write_tmax(con_name, tmax);
                    break;
                case("app_mn2"):
                    tmax = 15;
                    write_tmax(con_name, tmax);
                    break;
                default:
                    tmax = 5;
                    write_tmax(con_name, tmax);
                    break;
            }
            return tmax;
        }
        return tmax;
    }

    public void write_tmax(String con_name,double tmax) {
        try {
            String filename = "tmax/" + con_name + "_tmax.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(tmax + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * get response time(ms)
     */
    public double response_time(String con_name) {
        FileReader fr;
        String filename = "resp/" + con_name + "_response_time2.txt";
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
        return avg;
    }

}
