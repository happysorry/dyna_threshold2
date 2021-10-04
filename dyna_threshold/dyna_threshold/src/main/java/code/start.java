package dyna_threshold.src.main.java.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.java.code.globe;
import main.java.code.health_check;

public class start {
    public static void main(String[]args){
        int iter = 1;
        double sim_time = 3600;
        int max_iter = 121; 
        String input_file = "input/exp(7200~10800).dat"; 
        ExecutorService es = Executors.newCachedThreadPool();
        restart r = new restart();
        for(int i=0;i<iter;i++){
            r.res();
            Wait(30000);
            es.execute(new get_all_use(max_iter));
            es.execute(new get_all_res(max_iter));
            es.execute(new new_send_request(input_file));
            es.execute(new health_check(max_iter));
            es.execute(new service("app_mn1", max_iter));
            es.execute(new service("app_mn2", max_iter));
            es.execute(new service("app_mnae1", max_iter));
            es.execute(new service("app_mnae2", max_iter));
            // es.execute(new globe());
            try {
                es.awaitTermination(60, TimeUnit.SECONDS);//wait until thread terminate
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Wait((long) (sim_time * 1000));
        }
        System.out.println("process over");
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
