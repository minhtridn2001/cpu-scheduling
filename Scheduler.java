
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.*;

public class SchedulerCopy {
    /*
     * author Tri
     * GUI Based Scheduler
     *
     */

    static class MainWindow extends JPanel implements ActionListener {

        JButton burstButton, exitButton, FCFSButton, SJFButton, RRButton, SRJFButton;
        JLabel lblContextSW, lblQuantum;
        JTextField txtContextSw, txtQuantum;
        JFrame frame;
        Graphics G;
        int yscale = 1;                 //adjust to screen size
        boolean drawProcs = false;
        int choice = 0;
        long[] pArrTime = new long[100];       //Process Arrival time (Job submission time)
        int[] plen = new int[100];            //Task length (burst size)
        int[] prem = new int[100];            //remaining job (for RR and SRJF only)
        int[] pstat = new int[100];           //Status: 0 = ready; -1 = completed.  If completed, should be removed from queue
        int QTM = 50;                    //quantum
        final int jobSize = 10;                //Number of jobs/processes to be scheduled
        int contextSw = 0;
        LinkedList procs = new LinkedList();

        public MainWindow(JFrame frame) {
            this.frame = frame;
            burstButton = new JButton("     Show Burst");
            exitButton = new JButton("Exit Scheduler");
            FCFSButton = new JButton("Show FCFS");
            SJFButton = new JButton("Show SJF");
            RRButton = new JButton("Show RR");
            SRJFButton = new JButton("Show SRJF");
            this.add(burstButton);
            burstButton.addActionListener(this);
            this.add(FCFSButton);
            FCFSButton.addActionListener(this);
            this.add(SJFButton);
            SJFButton.addActionListener(this);
            this.add(RRButton);
            RRButton.addActionListener(this);
            this.add(SRJFButton);
            SRJFButton.addActionListener(this);
            this.add(exitButton);
            exitButton.addActionListener(this);
            yscale = (int) (600 / jobSize);
            lblContextSW = new JLabel("Context Switch");
            txtContextSw = new JTextField(3);
            txtContextSw.setText("0");
            this.add(lblContextSW); this.add(txtContextSw);
            lblQuantum = new JLabel("Quantum Time");
            txtQuantum = new JTextField(3);
            txtQuantum.setText("50");
            this.add(lblQuantum); this.add(txtQuantum);
            QTM = Integer.parseInt(txtQuantum.getText());
        }
        public void paintComponent(Graphics g) {
            G = g;
            if (choice == 0) {
                createProcesses();
            }
            if (choice == 1) {
                drawBurst();
            }
            choice = 2;
        }

        public void actionPerformed(ActionEvent e) {
            String s = "";
            if (e.getSource() == burstButton) {
                choice = 1;
                drawBurst();
                drawProcs = true;
            }

            if (e.getSource() == FCFSButton) {
                if (!drawProcs) {
                    return;
                }
                System.out.println("\nCalculating FCFS Schedule...");
                calcFCFS();
            }
            if (e.getSource() == SJFButton) {
                if (!drawProcs) {
                    return;
                }
                System.out.println("\n\nCalculating SJF Schedule...");
                calcSJF();
            }
            if (e.getSource() == RRButton) {
                if (!drawProcs) {
                    return;
                }
                System.out.println("\n\nCalculating RR Schedule...");
                calcRR();
            }
            if (e.getSource() == SRJFButton) {
                if (!drawProcs) {
                    return;
                }
                System.out.println("\n\nCalculating SRJF Schedule...");
                calcSRJF();
            }
            if (e.getSource() == exitButton) {
                System.exit(0);
            }
        }

        private long poissonDF(double lambda) {
            double L, p;
            long k = 0;
            L = Math.exp(-lambda);
            p = 1;
            while (p > L) {
                k = k + 1;
                p = p * Math.random();
            }
            return k;
        }

        private void calcFCFS() {
            int[] JST = new int[100];       //Job Start Time
            int[] RSPT = new int[100];      //Response Time
            int[] WT = new int[100];        //Wait Time
            int[] TART = new int[100];      //Turn around time
            double avgJST, avgRSPT, avgWT, avgTART;  //Average

            avgJST = 0;
            avgRSPT = 0;
            avgWT = 0;
            avgTART = 0;

            /*
            Write your algorithm implementation code here...
             */
            // Cal JST
            // The first process startes immediately, the other processes start after it arrived and its before process terminated.
            // If its before process terminated after its arrival time, the process will start when its before terminated.
            // If not(the process arrive after its before terminated), it will start immediately when it arrive.
            contextSw = Integer.parseInt(txtContextSw.getText());
            JST[0] = (int) pArrTime[0];
            for (int i = 1; i < jobSize; i++) {
                if ((JST[i - 1] + plen[i - 1]) > pArrTime[i]) {
                    JST[i] = (JST[i - 1] + plen[i - 1]) + contextSw;
                } else {
                    JST[i] = (int) pArrTime[i] + contextSw;
                }
            }
            // Calculate resp time, turnaround time and waiting time.
            for (int i = 0; i < jobSize; i++) {
                RSPT[i] = (int) (JST[i] - pArrTime[i]); // Resp time equals start time minus arrival time.
                TART[i] = (int) (JST[i] + plen[i] - pArrTime[i]); // Turnaround time = start time + job's length - arrival time - In FCFS only.
                WT[i] = TART[i] - plen[i]; // Waiting time = Turnaround time - Job's length.
            }
            System.out.println("Proc\t  Arriv.\tLeng \tStart\tWait\tResp.\tTurn Ar.");
            for (int i = 0; i < jobSize; i++) {
                avgJST = avgJST + JST[i];
                avgRSPT = avgRSPT + RSPT[i];
                avgWT = avgWT + WT[i];
                avgTART = avgTART + TART[i];
                System.out.format("%5d\t", i);      //\t%10d\t%10d, plen[i], JST[i]]);
                System.out.format("%5d\t", pArrTime[i]);
                System.out.format("%10d\t", plen[i]);
                System.out.format("%5d\t", JST[i]);
                System.out.format("%5d\t", WT[i]);
                System.out.format("%5d\t", RSPT[i]);
                System.out.format("%5d\t\n", TART[i]);
            }
            String avg;
            avg = "\nAverage Response Time " + (avgRSPT / jobSize);
            System.out.println(avg);
            avg = "Average Wait Time " + (avgWT / jobSize);
            System.out.println(avg);
            avg = "Average Turn Arround Time " + (avgTART / jobSize);
            System.out.println(avg);
        }

        private void calcRR() {
            resetProcess();
            //See above... and write your code for RR here...
            int[] WT = new int[100];        //Wait Time
            int[] TART = new int[100];      //Turn around time
            double avgWT, avgTART;  //Average

            // Parse quantum time
            QTM = Integer.parseInt(txtQuantum.getText());
            avgWT = 0;
            avgTART = 0;
            // Declare variables
            // doneP : number of finished processes.
            // curTime : the currently time.
            // curP : the process to be excute.
            // Vector process represent queue.
            int doneP = 0;
            int curTime = 0;
            int curP = 0;
            Vector<Integer> proces = new Vector<Integer>();
            proces.add(0);

            // Repeat when finished job is less than number of job

            while (doneP < jobSize) {
                // If there is no job in queue, add the proximate job to queue.
                if (proces.size() == 0) {
                    for (int i = 0; i < jobSize; i++) {
                        if (pstat[i] != -1) {
                            proces.add(i);
                            curTime = (int) pArrTime[i]; // re-set the currently time.
                            break;
                        }
                    }
                }
                // Pick the first process of Queue to excute, remove it from queue.
                curP = proces.get(0);
                proces.remove(0);
                // Time to switch between processes
                contextSw = Integer.parseInt(txtContextSw.getText());
                curTime += contextSw;

                // If the remaining time of currently process is smaller than quantum, the process will finished in next interrupt
                // set the status of process to -1, calculate turnaround time. increase doneP
                if (prem[curP] <= QTM) {
                    curTime += prem[curP];
                    prem[curP] = 0;
                    pstat[curP] = -1;
                    doneP++;
                    TART[curP] = (int) (curTime - pArrTime[curP]);
                } else {
                    // If the remaining time of currently process is greater than quantum, it run normally
                    // Increase curTime and decrease remaining time of this process.
                    curTime += QTM;
                    prem[curP] = prem[curP] - QTM;
                }
                // Check if there is a new process arrive. If there's a new process, add it to queue.
                for (int i = 1; i < jobSize; i++) {
                    if (!proces.contains(i) && pArrTime[i] <= curTime && pstat[i] != -1 && i != curP) {
                        proces.add(i);
                    }
                }
                // Append the currently process to the end of Queue if it wasn't finished.
                if (pstat[curP] != -1) {
                    proces.add(curP);
                }
            }
            System.out.println("Proc\t  Arriv.\tLeng \tWait \tTurn Ar.");
            for (int i = 0; i < jobSize; i++) {
                WT[i] = TART[i] - plen[i];
                avgWT = avgWT + WT[i];
                avgTART = avgTART + TART[i];
                System.out.format("%3d\t ", i);      //\t%10d\t%10d, plen[i], JST[i]]);
                System.out.format("%5d\t ", pArrTime[i]);
                System.out.format("%10d\t ", plen[i]);
                System.out.format("%4d\t ", WT[i]);
                System.out.format("%5d\t\n", TART[i]);
            }
            String avg;
            avg = "Average Wait Time " + (avgWT / jobSize);
            System.out.println(avg);
            avg = "Average Turn Arround Time " + (avgTART / jobSize);
            System.out.println(avg);
        }

        private void calcSJF() {
            resetProcess();
            //see above... and write your code for SJF here...
            int[] JST = new int[100];       //Job Start Time
            int[] RSPT = new int[100];      //Response Time
            int[] WT = new int[100];        //Wait Time
            int[] TART = new int[100];      //Turn around time
            double avgJST, avgRSPT, avgWT, avgTART;  //Average

            avgJST = 0;
            avgRSPT = 0;
            avgWT = 0;
            avgTART = 0;

            // First job startes immediately and excute continously until terminate.
            JST[0] = (int) pArrTime[0];
            TART[0] = plen[0];
            pstat[0] = -1;
            int nextP = 0;
            int curTime = plen[0], doneP = 1;
            while (doneP < jobSize) {
                // Calculate number of ready processes in Queue.
                int inQue = 0;
                for (int i = 0; i < jobSize; i++) {
                    if (pArrTime[i] < curTime && pstat[i] == 0) {
                        inQue++;
                    }
                }
                // If queue is empty, add the process that have minimun arrival time and set it to excute.
                if (inQue == 0) {
                    for (int i = 0; i < jobSize; i++) {
                        if (pstat[i] != -1) {
                            nextP = i;
                            inQue++;
                            break;
                        }
                    }
                } else {
                    // If queue is not empty, find the shortest process
                    int jLength = 99999;
                    for (int i = 0; i < jobSize; i++) {
                        if ((pArrTime[i] < curTime && pstat[i] == 0 && plen[i] < jLength)) {
                            jLength = plen[i];
                            nextP = i;
                        }
                    }
                }
                // Execute next process
                contextSw = Integer.parseInt(txtContextSw.getText());
                curTime+= contextSw;
                if (curTime < pArrTime[nextP]) {
                    curTime = (int) pArrTime[nextP];
                }
                pstat[nextP] = -1;
                JST[nextP] = curTime;
                RSPT[nextP] = (int) (JST[nextP] - pArrTime[nextP]);
                TART[nextP] = (int) (JST[nextP] + plen[nextP] - pArrTime[nextP]);
                doneP++;
                curTime += plen[nextP];
            }

            System.out.println("Proc\t  Arriv.\tLeng \tStart\tWait\tResp.\tTurn Ar.");
            for (int i = 0; i < jobSize; i++) {
                avgJST = avgJST + JST[i];
                avgRSPT = avgRSPT + RSPT[i];
                WT[i] = TART[i] - plen[i];
                avgWT = avgWT + WT[i];
                avgTART = avgTART + TART[i];
                System.out.format("%5d\t", i);      //\t%10d\t%10d, plen[i], JST[i]]);
                System.out.format("%5d\t", pArrTime[i]);
                System.out.format("%10d\t", plen[i]);
                System.out.format("%5d\t", JST[i]);
                System.out.format("%5d\t", WT[i]);
                System.out.format("%5d\t", RSPT[i]);
                System.out.format("%5d\t\n", TART[i]);
            }
            String avg;
            avg = "\nAverage Response Time " + (avgRSPT / jobSize);
            System.out.println(avg);
            avg = "Average Wait Time " + (avgWT / jobSize);
            System.out.println(avg);
            avg = "Average Turn Arround Time " + (avgTART / jobSize);
            System.out.println(avg);
        }

        private void calcSRJF() {
            //See above and write your code for FCFS here...
            resetProcess();
            //see above... and write your code for SJF here...
            int[] WT = new int[100];        //Wait Time
            int[] TART = new int[100];      //Turn around time
            double avgWT, avgTART;  //Average

            avgWT = 0;
            avgTART = 0;

            int curTime = 0;
            int doneP = 0;
            int curP = 0;
            contextSw = Integer.parseInt(txtContextSw.getText());
            while (doneP < jobSize) {
                // Select process and execute
                curP = selectProcessSRJF(curTime);
                if (curP == -1) {
                    curTime++;
                } else {
                    curTime++;
                    prem[curP] = prem[curP] - 1;
                    if (prem[curP] == 0) {
                        doneP++;
                        pstat[curP] = -1;
                        TART[curP] = (int) (curTime - pArrTime[curP]);
                        curTime+= contextSw;
                    }
                }
            }
            System.out.println("Proc\t  Arriv.\tLeng \tWait \tTurn Ar.");
            for (int i = 0; i < jobSize; i++) {
                WT[i] = TART[i] - plen[i];
                avgWT = avgWT + WT[i];
                avgTART = avgTART + TART[i];
                System.out.format("%3d\t ", i);      //\t%10d\t%10d, plen[i], JST[i]]);
                System.out.format("%5d\t ", pArrTime[i]);
                System.out.format("%10d\t ", plen[i]);
                System.out.format("%4d\t ", WT[i]);
                System.out.format("%5d\t\n", TART[i]);
            }
            String avg;
            avg = "Average Wait Time " + (avgWT / jobSize);
            System.out.println(avg);
            avg = "Average Turn Arround Time " + (avgTART / jobSize);
            System.out.println(avg);
        }

        int selectProcessSRJF(int curTime) {
            // Find the process have minimum remaining time in queue
            // return -1 if can find process.
            int min = 999999, minid = -1, i;
            for (i = 0; i < jobSize; i++) {
                if (prem[i] < min && pstat[i] != -1 && pArrTime[i] <= curTime) {
                    min = prem[i];
                    minid = i;
                }
            }
            return minid;
        }

        private void resetProcess() {
            System.arraycopy(plen, 0, prem, 0, jobSize);
            for (int i = 0; i < jobSize; i++) {
                pstat[i] = 0;
            }
        }

        private void drawBurst() {
            int[] cpuT = new int[15];
            int[] ioT = new int[15];
            String s;
            int burstNo, burstSize, cpuBurst, x1, x2, y;    //Plot CPU and IO burst data
            G.setColor(Color.WHITE);
            G.fillRect(1, 1, 800, 800);
            G.setColor(Color.RED);

            //Draw Legend and axes
            x1 = 550;
            x2 = 600;
            y = 70;
            G.drawLine(x1, y, x2, y);
            G.drawLine(x1, y + 1, x2, y + 1);
            G.drawString("CPU Burst", x2 + 5, y + 5);
            G.setColor(Color.BLUE);
            y = 90;
            G.fillRect(x1, y, x2 - x1, 5);
            G.drawString("IO Burst", x2 + 5, y + 5);
            G.drawLine(700, 35, 750, 35);
            G.drawString("> Time", 750, 40);

            //Draw time axis
            G.setColor(Color.RED);
            G.drawLine(10, 60, 800, 60);
            G.setColor(Color.BLACK);
            G.drawLine(10, 1, 10, 800);
            G.drawLine(700, 10, 750, 10);
            G.drawString("> Time", 750, 15);

            //Caliberate time axis
            for (int i = 0; i < 10; i++) {
                G.drawLine(i * 50 + 10, 55, i * 50 + 10, 800);
            }

            //draw  labels /legends/
            for (int i = 0; i < 5; i++) {
                s = i * 100 + "";
                G.drawString(s, i * 100, 50);
            }

            //Generate IO and CPU bursts for each process and plot them
            for (int i = 0; i < jobSize; i++) {
                if (plen[i] > 100) //long process, more bursts short process has less
                {
                    burstNo = 3 + (int) (3 * Math.random());
                } else {
                    burstNo = (int) (3 * Math.random());
                }
                for (int j = 0; j < burstNo; j++) {
                    burstSize = (int) (plen[i] / (2 * burstNo));
                    cpuBurst = (int) (burstSize * (2 * Math.random()));
                    cpuT[j] = cpuBurst;     //Total CPU burst length for the process
                    ioT[j] = (2 * burstSize) - cpuBurst;  //Total IO burst length
                }
                //Draw the COU and IO burst line graph now
                G.setColor(Color.RED);
                x1 = 10 + (int) pArrTime[i];
                x2 = x1 + cpuT[0];
                y = 100 + i * yscale;
                G.fillRect(x1, y, x2 - x1, 5);
                G.drawLine(x1, y + 1, x2, y + 1);
                G.setColor(Color.BLUE);
                for (int j = 0; j < burstNo; j++) {  //Interleave IO and CPU bursts
                    G.setColor(Color.BLUE);
                    x1 = x2;
                    x2 = x1 + ioT[j];
                    y = 100 + i * yscale;
                    G.fillRect(x1, y, (x2 - x1), 5);

                    G.setColor(Color.RED);
                    x1 = x2;
                    x2 = x1 + cpuT[j + 1];
                    y = 100 + i * yscale;
                    G.fillRect(x1, y, (x2 - x1), 5);
                }
            }
            repaint();
        }

        private void createProcesses() {
            double lambda = 8;     //Used as a parameter for the poisson distribution function to generate random arrival times

            pArrTime[0] = 0;
            for (int i = 1; i < jobSize; i++) { //Generate arrival times
                pArrTime[i] = pArrTime[i - 1] + 3 * poissonDF(lambda);
                pstat[i] = 0;
            }

            for (int i = 0; i < jobSize; i++) {
                plen[i] = (int) (10 + 200 * Math.random()); //The process size is at least 10, atmost 210
            }

            /*
             * The next lines initialize process arrival and burst arrays if you want to use a known test case 
             * instead of randomly generated processes.  Uncomment the next two lines if you want to check your hand calculated results
            It is recommended to evalaute the correctness of your algorithm by hand using the following cases.
             * The first set works for jobSize=5 and the next one is for jobSize=10;
             * Uncomment the
             */
//5 processes
//            plen[0] = 28; plen[1] = 6; plen[2] = 199; plen[3] = 183; plen[4] = 132;
//            pArrTime[0] = 0; pArrTime[1] = 48; pArrTime[2] = 69; pArrTime[3] = 84; pArrTime[4] = 108;
//10 processes

            pArrTime[0] = 0; pArrTime[1] = 30; pArrTime[2] = 54; pArrTime[3] = 93; pArrTime[4] = 135; pArrTime[5] = 153; pArrTime[6] = 177; pArrTime[7] = 198; pArrTime[8] = 237; pArrTime[9] = 264;
            plen[0] = 71; plen[1] = 184; plen[2] = 151; plen[3] = 115; plen[4] = 101; plen[5] = 161; plen[6] = 162; plen[7] = 151; plen[8] = 166; plen[9] = 169;

            for (int i = 0; i < jobSize; i++) {
                prem[i] = plen[i];        //Initialize the 'remianing job' array
            }
            /*Another possibility is to use a linked list of type Process (the class we created below) 
            instead of an array, to store all attributes of a process as follows
            Process p;
            for (int i=0; i<jobSize; i++) {
            p = new Process();
            p.pid = i;
            p.arrivalTime = pArrTime[i];
            p.length = plen[i];
            p.remainingTime = prem[i];
            ...
            procs.add(p);    //Add to the linked list
            }
            
            
            
             */
            //Print the above
            System.out.println("Process\tArrival Time\tLength ");
            for (int i = 0; i < jobSize; i++) {
                System.out.format("%5d\t", i);
                System.out.format("%5d\t\t", pArrTime[i]);
                System.out.format("%5d\n", plen[i]);
            }

        }
    }

    public static void main(String[] args) throws java.io.IOException {
        JFrame frame = new JFrame("CPU Scheduling");
        MainWindow mWind = new MainWindow(frame);
        frame.getContentPane().add(mWind);
        frame.setSize(1000, 600);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private static class Process {		//Process attributes can be stored here.

        public Process() {
        }
        public int pid;
        public int arrivalTime;
        public int length;
        public int remainingTime;
        public int stat;
        public int startTime;
        public int waitTime;
        public int responseTime;
        public int turnAroundTime;
    }
}
