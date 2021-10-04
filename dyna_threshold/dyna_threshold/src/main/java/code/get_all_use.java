package dyna_threshold.src.main.java.code;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;





public class get_all_use implements Runnable{
    ArrayList<String> cons = new ArrayList<>();
    ArrayList<String> machine = new ArrayList<>();
    double sim_time = 0;
    int iter = 0;
    public get_all_use(int iter){
        this.iter = iter;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println("all use up");
        add_cons();
        add_machine();
        double startTime = System.nanoTime();
        stop s = new stop();
        int iter_count = 0;
        while(true){
            iter_count ++;
            double endtime = System.nanoTime() - startTime;
            endtime /= 1e9;
            if(iter_count >= iter)
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
    public int read(){
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

                        double u = Double.parseDouble(ii);
                        // u /= 60;
                        // u *= 100;
                        String tmp = String.valueOf(u);
                        // System.out.println("ii" + ii);
                        switch(j){
                            case 0:
                                mn1.add(tmp);
                                break;
                            case 1:
                                mn2.add(tmp);
                                break;
                            case 2:
                                mnae1.add(tmp);
                                break;
                            case 3:
                                mnae2.add(tmp);
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
            //for record
            print_use(avg, "app_mn1");
            print_cons(mn1.size(), "app_mn1");
            //for qlearning
            print_use2(avg, "app_mn1");
            print_cons2(mn1.size(), "app_mn1");
////////////////////////////////////////
            avg = 0;
            for(i=0;i<mn2.size();i++){
                double tmp = Double.parseDouble(mn2.get(i));
                avg += tmp;
            }
            avg /= mn2.size();
            print_use(avg, "app_mn2");
            print_cons(mn2.size(), "app_mn2");
            print_use2(avg, "app_mn2");
            print_cons2(mn2.size(), "app_mn2");
/////////////////////////////////////////////
            avg = 0;
            for(i=0;i<mnae1.size();i++){
                double tmp = Double.parseDouble(mnae1.get(i));
                avg += tmp;
            }
            avg /= mnae1.size();
            print_use(avg, "app_mnae1");
            print_cons(mnae1.size(), "app_mnae1");
            print_use2(avg, "app_mnae1");
            print_cons2(mnae1.size(), "app_mnae1");
///////////////////////////////////////
            avg = 0;
            for(i=0;i<mnae2.size();i++){
                double tmp = Double.parseDouble(mnae2.get(i));
                avg += tmp;
            }
            avg /= mnae2.size();
            print_use(avg, "app_mnae2");
            print_cons(mnae2.size(), "app_mnae2");
            print_use2(avg, "app_mnae2");
            print_cons2(mnae2.size(), "app_mnae2");
        }
        catch(Exception e){

        }
    }

    public void print_use(double avg , String con_name) {
        try {
            String filename = "use/" + con_name + "_use.txt";
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
            String filename = "use/" + con_name + "_use2.txt";
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
            String filename = "con1/" + con_name + "_con1.txt";
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
            String filename = "con1/" + con_name + "_con2.txt";
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
