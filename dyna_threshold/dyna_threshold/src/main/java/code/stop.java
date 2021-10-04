package dyna_threshold.src.main.java.code;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class stop{

    public stop(){
        // read();
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


    void write() {
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
    
    void Wait(long time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

    // @Override
    // public void run() {
    //     // TODO Auto-generated method stub
    //     read();
    //     Wait(10000);
    // }
}
