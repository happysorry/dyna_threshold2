package dyna_threshold.src.main.java.code;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;


public class get_all_res implements Runnable {
    ArrayList<String> cons = new ArrayList<>();
    ArrayList<String> machine = new ArrayList<>();
    double sim_time = 0;
    int iter = 0;

    public get_all_res(int iter) {
        this.iter = iter;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println("all res up");
        add_cons();
        add_machine();
        stop s = new stop();
        int iter_count = 0;
        double startTime = System.nanoTime();
        while (true) {
            iter_count ++;
            double endtime = System.nanoTime() - startTime;
            endtime /= 1e9;
            if(iter_count >= iter)
                break;
            endtime = System.nanoTime();
            response_time();

            int signal = s.read();
            if(signal != 0){
                Wait(140000);
                // sim_time += 180;
            }
            while (((System.nanoTime() - endtime) / 1e9) < 30)
                ;

        }
        Thread.currentThread().interrupt();
    }

    public void add_cons() {
        cons.add("app_mn1");
        cons.add("app_mn2");
        cons.add("app_mnae1");
        cons.add("app_mnae2");
    }

    public void add_machine() {
        machine.add("worker");
        machine.add("worker1");
        machine.add("worker2");
        machine.add("worker3");
    }

    public void response_time() {
        for (int j = 0; j < cons.size(); j++) {
            double[] res = new double[3];
            double elapsed = 0.0;
            for (int i = 0; i < 3; i++) {
                double startTime = System.nanoTime();
                int status = send_request(cons.get(j));
                elapsed = System.nanoTime() - startTime;
                elapsed /= 1e6;
                res[i] = elapsed;
                if (status != 201)
                    i--;
                // Wait(3000);
            }
            elapsed = 0;
            for (int i = 0; i < 3; i++) {
                elapsed += res[i];
            }
            elapsed /= 3;
            if(elapsed > 50)
                elapsed = 50;
            print_res_time(cons.get(j), elapsed);
            print_res_time2(cons.get(j), elapsed);
        }

    }

    public int send_request(String con_name) {
        int status = 0;
        try {
            try {
                int val = (int) ((Math.random() * 899999) + 100000);
                String con = "";
                if ((val % 2) == 1)
                    con = "false";
                else
                    con = "true";
                String path = "";
                switch (con_name) {
                    case "app_mn1":
                        path = "http://192.168.99.130:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0";
                        break;
                    case "app_mn2":
                        path = "http://192.168.99.130:777/~/mn-cse/mn-name/AE2/Control_Command_Container";
                        break;
                    case "app_mnae1":
                        path = "http://192.168.99.130:1111/test";
                        break;
                    case "app_mnae2":
                        path = "http://192.168.99.130:2222/test";
                }

                URL url = new URL(path);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setReadTimeout(2000);
                http.setConnectTimeout(1000);
                http.setDoOutput(true);
                // http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("X-M2M-Origin", "admin:admin");
                http.setRequestProperty("Content-Type", "application/json;ty=4");
                try {
                    http.setRequestMethod("POST");
                    http.connect();
                    DataOutputStream out = new DataOutputStream(http.getOutputStream());

                    String request = "{\"m2m:cin\": {\"con\": \" default \", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\""
                            + val + "\"}}";
                    // '{"m2m:cin": {"con": "EXAMPLE_VALUE", "cnf": "text/plain:0"}}'
                    out.write(request.toString().getBytes("UTF-8"));
                    out.flush();
                    out.close();
                    status = http.getResponseCode();// error place

                } catch (IOException e) {
                }
            } catch (SocketTimeoutException e) {
                // recovery();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

        }
        return status;
    }

    void print_res_time(String con_name, double elapsed) {
        try {
            String filename = "resp/" + con_name + "_response_time.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(elapsed + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    void print_res_time2(String con_name, double elapsed) {
        try {
            String filename = "resp/" + con_name + "_response_time2.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(elapsed + "\n");
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
