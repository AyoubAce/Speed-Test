package com.ex.mihawk.startingsp;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity{
private Handler mHandler=new Handler();
private ProgressBar progressBar;
private Button btn;


int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inside);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        new Thread(myWorker).start();

    }

    private final Runnable myWorker=new Runnable(){
        final DecimalFormat mDecimalFormater=new DecimalFormat("##.##");

        @Override
        public void run() {

            final TextView uplink=(TextView)findViewById(R.id.upload);
            try {
                final TextView txt=(TextView)findViewById(R.id.ping);
                final TextView downlink=(TextView)findViewById(R.id.download);

                try {

                    long startCon=System.currentTimeMillis();
                    Socket s= new Socket("192.168.1.41", 4000);
                    final long Latency=System.currentTimeMillis()-startCon;
                    txt.post(new Runnable() {
                        @Override
                        public void run() {
                            txt.setText(Long.toString(Latency)+" ms");
                        }
                    });

                    long start=System.currentTimeMillis();
                    ObjectInputStream obj= new ObjectInputStream(s.getInputStream());
                    final byte [] buffer=(byte[]) obj.readObject();
                    FileOutputStream fous=new FileOutputStream("/storage/emulated/0/Download/8.txt");
                    fous.write(buffer);
                    final long duration=System.currentTimeMillis()-start;
                    final int fileSize= buffer.length;
                    for(count=100;count>0;count-=4){
                        android.os.SystemClock.sleep(100);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(count);
                            }
                        });  }

                    obj.close();
                    fous.close();
                    s.close();
                    //progressBar.setVisibility(View.VISIBLE);




                    //calculate(duration,fileSize);  mDecimalFormater.format(calculate(duration,fileSize))
                    // mDecimalFormater.format(calculate(duration,fileSize)) Long.toString(calculate(duration,fileSize)
                    downlink.post(new Runnable() {
                        @Override
                        public void run() {
                            downlink.setText(mDecimalFormater.format(calculate(duration,fileSize))+"\n Mbps");
                        }
                    });
                 /*   mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            layout6.setVisibility(layout5.GONE);
                        }
                    });*/

                    // Great solution

                    //  txt.setText(Long.toString(Latency));


                               /* try{
                                    wait(5000);
                                }c♠atch (InterruptedException f){
                                    f.printStackTrace();
      ♠                          }*/

                }catch (ClassNotFoundException e){
                    e.printStackTrace();

                }

            }catch (IOException e){
                e.printStackTrace();
            }
            catch (NumberFormatException e){
                e.printStackTrace();

            }
            try{
                long upStart=System.currentTimeMillis();
                //Thread.sleep(5000);
                Socket soc= new Socket("192.168.1.41", 4000);
                FileInputStream fis=new FileInputStream("/storage/emulated/0/Download/8.txt");
                byte [] buff=new byte[fis.available()];
                fis.read(buff);
                final int size=buff.length;
                ObjectOutputStream oos=new ObjectOutputStream(soc.getOutputStream());
                oos.writeObject(buff);
                final long upDuration=System.currentTimeMillis()-upStart;
                for(count=0;count<100;count+=4){
                    android.os.SystemClock.sleep(100);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(count);
                        }
                    });  }
                oos.close();
                fis.close();
                soc.close();
                //mDecimalFormater.format(calculate(upDuration,size))


                uplink.post(new Runnable() {
                    @Override
                    public void run() {
                        uplink.setText(mDecimalFormater.format(calculate(upDuration,size))+" Mbps");
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
            }

               /* File dir = getFilesDir();
                File file = new File(dir, "OSvize.rtf");
                boolean deleted = file.delete();
                */

           /* btn.post(new Runnable() {
                @Override
                public void run() {
                    btn.setEnabled(true);
                }
            });*/
        }

    };

    private double calculate(final long duration,int fileSize){
        double bytesPerSecond=1000*(8*fileSize/duration);
        double megabitsPerSecond= bytesPerSecond/(1024*1024);
        return megabitsPerSecond;

    }
}
