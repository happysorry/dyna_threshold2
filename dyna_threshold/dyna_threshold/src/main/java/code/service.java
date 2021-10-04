package dyna_threshold.src.main.java.code;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class service implements Runnable {
    public static double sim_time = 0;
    public String con_name = "";
    public int iter = 0;
    public int states = 100;
    public int actions = 3;
    public int container = 4;
    public double alpha = 0.01;
    public double gamma = 0.9;
    public double threshold = 0;
    public double[][] Q; // [state][action]
    ArrayList<String> machine_id = new ArrayList<String>(); // docker machine id
    State s = new State();

    public service(String con_name, int iter) {
        this.con_name = con_name;
        this.iter = iter;
    }

    public void run() {
        add_machine();
        read_qtable();
        init_state();
        for (int i = 0; i < iter; i++) {
            // System.out.println("iter =" + i);
            double st = System.nanoTime();
            calculateQ();
            while ((System.nanoTime() - st) / 1e9 < 30)
                ;
            st = System.nanoTime();
            stop s = new stop();
            int sig = s.read();
            if(sig != 0)
                Wait(140000);
        }
        print_qtable();
        System.out.println("service over");
    }

    /////////////////////////////////
    /**
     * initial threshold = 0.5(4) initial threshold = 0(0)
     */
    void init_state() {
        s.threshold = 4;
        s.use = 0;
    }

    public void add_machine() {
        machine_id.add("worker");
        machine_id.add("worker1");
        machine_id.add("worker2");
        machine_id.add("worker3");
    }

    /**
     * initialize q table which is 5 actions -1 means impossible states
     */
    void init_5() {
        // Q = new double[states][actions];// Reward lookup
        for (int i = 0; i < states; i++) {
            for (int j = 0; j < actions; j++) {
                Q[i][j] = 0;
            }
        }
    }

    /**
     * read file as qtable
     */
    void read_qtable() {
        Q = new double[states][actions];
        String filename = "qtable/" + con_name + "_qtable.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            int ind = 0;
            try {
                while ((line = r.readLine()) != null) {
                    String s[] = line.split(" ");
                    for (int i = 0; i < 3; i++)
                        Q[ind][i] = Double.parseDouble(s[i]);
                    ind++;
                }
                System.out.println("read qtable");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();

            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("not read");
            init_5();
        }

    }

    /**
     * print out q table
     */
    void print_qtable() {
        // System.out.println("print q table");
        FileWriter fw;
        try {
            String filename = "qtable/" + con_name + "_qtable.txt";
            fw = new FileWriter(filename);
            for (int i = 0; i < states; i++) {
                for (int j = 0; j < actions; j++) {
                    fw.write(Q[i][j] + " ");
                }
                fw.write("\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    void print_threshold() {
        try {
            String filename = "threshold/" + con_name + "_threshold.txt";
            FileWriter fw = new FileWriter(filename, true);
            // System.out.println(filename);
            fw.write(threshold + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    int cla_state(State st) {
        // 50% < threshold < 90%
        if (st.threshold < 4)
            st.threshold = 4;
        if (st.threshold > 8)
            st.threshold = 8;

        if (st.use < 0)
            st.use = 0;
        if (st.use > 9)
            st.use = 9;

        int ans = st.threshold * 10 + st.use;
        return ans;
    }

    /**
     * return all possible actions
     */
    State[] possibleActionsFromState(State st) {
        State[] res1 = new State[5];
        State tmp = new State();
        State tmp1 = new State();
        int ans = cla_state(st);
        res1[0] = st;
        //////////////////////////////////////////////////////////////// - threshold
        tmp.threshold = st.threshold - 1;
        tmp.use = st.use;
        if (tmp.threshold < 4)
            ;
        else
            res1[1] = tmp;
        /////////////////////////////////////////////////////////////// + threshold
        tmp1.threshold = st.threshold + 1;
        tmp1.use = st.use;
        if (tmp1.threshold > 8)
            ;
        else
            res1[2] = tmp1;
        return res1;
        // return res.toArray(new State[res.size()]);
    }

    void print_state_action(String action) {
        try {
            String filename = "step/" + con_name + "_state_action.txt";
            FileWriter fw = new FileWriter(filename, true);
            // System.out.println(filename);
            fw.write(cla_state(s) + " " + action + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double read_tmax(){
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
                    write_tmax(tmax);
                    break;
                case("app_mn2"):
                    tmax = 15;
                    write_tmax(tmax);
                    break;
                default:
                    tmax = 5;
                    write_tmax(tmax);
                    break;
            }
            return tmax;
        }
        return tmax;
    }

    public void write_tmax(double tmax) {
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

    double cost() {
        double wperf = 0.5;
        double wres = 0.5;
        double cperf = 0;
        double cres = 0;
        double cost = 0;
        double resp_time = response_time();
        ////////////////////////////////// cperf
        double tmax = read_tmax();
        if (resp_time > tmax)
            cperf = 1;
        else if (resp_time <= tmax) {
            double tmp = resp_time - tmax;
            tmp /= tmax;
            double E = 10;
            tmp *= E;
            cperf = Math.exp(tmp);
        }
        //////////////////////////////// cres
        double theta = (s.threshold + 1) * 0.1;
        cres = 1 - theta;
        ////////////////////////////////
        cost = wperf * cperf + wres * cres;
        return cost;
    }

    /**
     * get response time(ms)
     */
    double response_time() {
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

    void calculateQ() {
        try {
            // epison greedy parameter
            int epison = 1;
            int r = 0;
            r = (int) (Math.random() * 100);
            if (r < 10)
                epison = 0;
            // get_state1();
            ////////////////////////////////////////////// get state(cpu utilization)
            double now_use = get_use2();
            int tmp1 = (int) (now_use / 10);
            if (tmp1 > 9)
                tmp1 = 9;
            if (tmp1 < 0)
                tmp1 = 0;
            s.use = tmp1;
            /////////////////////////////////////////////
            // get_cons();
            int state = cla_state(s);
            System.out.println("state " + state);
            State[] actionsFromCurrentState = possibleActionsFromState(s); // get all possible states. Return type
                                                                           // <State>
            int nextState = 0;
            State next = new State(); // next state s'
            next = s;
            State nexenext = new State();
            int action = 0;
            // choose action normally
            if (epison == 1) {
                double max = -1e9;
                ArrayList<Double> ran = new ArrayList<Double>();
                ArrayList<Integer> ind = new ArrayList<Integer>();
                double[] sss = new double[actions];// action q value
                for (int i = 0; i < actions; i++) {
                    sss[i] = 1e9;
                }
                /**
                 * get next state
                 */
                for (int i = 0; i < actionsFromCurrentState.length; i++) {
                    if (actionsFromCurrentState[i] == null)
                        continue;
                    State tmp = actionsFromCurrentState[i];

                    double q = Q[state][i];
                    ran.add(q);
                    sss[i] = q;
                    if (q > max) {
                        action = i;
                        next = tmp;
                        max = q;
                    }

                }
                for (int i = 0; i < actions; i++) {
                    if (sss[i] == max)
                        ind.add(i);
                }
                r = (int) (Math.random() * ind.size());
                action = ind.get(r);
                next = actionsFromCurrentState[action];

            } else {// choose action randomly
                r = (int) (Math.random() * 5);
                while (actionsFromCurrentState[r] == null) {
                    r = (int) (Math.random() * 5);
                }
                State tmp = actionsFromCurrentState[r];
                action = r;
                next = tmp;
            }
            /////////////////////////////////////////////////////
            // update service state
            nextState = cla_state(next);
            if (state == nextState) {
                print_state_action("same");
                System.out.println("same");
                // Wait(20000);
            } else if (state < nextState) {
                print_state_action("+r");
                System.out.println("+r");
            } else {
                print_state_action("-r");
                System.out.println("-r");
            }
            // update threshold
            /////////////////////////////////////////// check threshold
            threshold = next.threshold +1;
            threshold *= 10;
            print_threshold();
            if(now_use > threshold){
                int replicas = get_cons();
                if(replicas < 4){
                    replicas ++;
                    add_cons(replicas);
                }
            }
            else if(now_use < 10){
                int replicas = get_cons();
                if(replicas > 1){
                    replicas --;
                    add_cons(replicas);
                }
            }
            Wait(25000);
            /////////////////////////////////////////////////
            // es.execute(new health_check(sim_time));
            /**
             * calculate cost
             */
            double ci = cost();
            ////////////////////////////////////////////////
            /**
             * update q table
             */
            double q = Q[state][action]; // state before update

            // calculate minQ
            State[] actionsFromCurrentState2 = possibleActionsFromState(next); // get all possible states. Return type
            double max = -1e9;
            int action2 = 0;
            /**
             * get next state from next state
             */
            for (int i = 0; i < actionsFromCurrentState2.length; i++) {
                if (actionsFromCurrentState2[i] == null)
                    continue;
                State tmp = actionsFromCurrentState2[i];
                double qq = Q[nextState][i];
                if (qq > max) {
                    action2 = i;
                    max = qq;
                }
            }
            double minQ = Q[nextState][action2]; // next state action
            double value = (1 - alpha) * q + alpha * (-ci + gamma * minQ); // calculate value
            Q[state][action] = value; // update q table

            print_cost(-ci);

            // stage change
            s = next;
            state = nextState;
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    void print_cost(double qvalue) {
        try {
            String filename = "cost/" + con_name + "_qvalue.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(qvalue + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double get_use2() {
        FileReader fr;
        String filename = "use/" + con_name + "_use2.txt";
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
    public int get_cons(){
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

    public  void add_cons(int replicas){
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service scale " + con_name + "=" + replicas;
        System.out.println(cmd);
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }

    public static void Wait(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
