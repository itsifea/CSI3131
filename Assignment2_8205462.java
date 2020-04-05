//Ifeoluwapo Agiri, 8295462 
//CSI 3131 A2
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Queue;

public class Assignment2 {
    public static void main(String[] args) {
        int numUsers = 15;
        int numPrinters = 5;
        int lenQueue = 10;
        // creating the pmc variable that will be used by
        // both users and printers
        PrintManagementCenter pmc = new PrintManagementCenter(lenQueue);

        // 1. Initialize Users and Printers +10
        User[] user = new User[numUsers];
        Printer[] printer = new Printer[numPrinters];

        for (int i = 0; i < numUsers; i++) {
            user[i] = new User(i + 1, pmc);
        }

        for (int i = 0; i < numPrinters; i++) {
            printer[i] = new Printer(i + 1, pmc);
        }

        // 2. Start Users and Prints Threads +10
        for (int i = 0; i < numUsers; i++) {
            user[i].start();
        }

        for (int i = 0; i < numPrinters; i++) {
            printer[i].start();
        }

        // Wait for the users and printers
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            System.out.println("Printer Interrupted");
        }

        // 3. Shutdown Users and Printers +10
        for (int i = 0; i < numUsers; i++) {
            user[i].interrupt();
        }
        for (int i = 0; i < numPrinters; i++) {
            printer[i].interrupt();
        }
    }
}

// No need to change UserFile
class UserFile {
    public int timeCost;
    public int ownerId;
    public int fileId;

    public UserFile(int ownerId, int fileId) {
        int minTimeCost = 1000;
        int maxTimeCost = 2000;
        Random random = new Random();
        this.timeCost = random.nextInt(maxTimeCost - minTimeCost + 1) + minTimeCost;
        this.ownerId = ownerId;
        this.fileId = fileId;

        // User will spend some time creating a UserFile
        int creationTime = random.nextInt(maxTimeCost - minTimeCost + 1) + minTimeCost;
        try {
            Thread.sleep(creationTime * 2);
        } catch (InterruptedException e) {

        }
    }

}

class User extends Thread {
    private int id;
    private PrintManagementCenter printManagementCenter;

    public User(int id, PrintManagementCenter printManagementCenter) {
        this.id = id;
        this.printManagementCenter = printManagementCenter;
    }

    @Override
    public void run() {
        // 4. User Loop +10
        // max loop is 10 because of the length of the queue is 10
        int maxLoop = 1;
        for (int i = 0; i < maxLoop; i++) {
            // int userid =
            UserFile userFile = new UserFile(id, i);
            printManagementCenter.Upload(userFile);
        }
        System.out.println("The queue is now full...");

    }

    public int getUserId() {
        return id;
    }
}

class PrintManagementCenter {
    private Semaphore sem;
    private Queue<UserFile> q;
    private Semaphore mutex;

    public PrintManagementCenter(int len){
        // 5. Create Semaphore, Mutex, and Queue +10
        sem = new Semaphore(len); 
        mutex = new Semaphore(1);
        q = new LinkedList<UserFile> () ;
        //q.size() = len; 
    }

    public void Upload(UserFile userFile) {
        // 6. Upload Function +20
      System.out.println("Steps to Upload " + userFile);
      System.out.println("Acquiring Semaphore...");
      try {
           sem.acquire();
      } catch (InterruptedException e) {
      }
      System.out.println("Acquiring Mutex...");
      try {
      mutex.acquire();
      } catch (InterruptedException e) {
      }
      boolean res = q.add(userFile);
      if (res == true) {
        System.out.println(userFile + " has been uploaded successfully to the Print Queue");
      }
      mutex.release();
    }

    public UserFile Download() {
        // 7. Download Function +20
      UserFile userFile = null;
      System.out.println("Steps to Download... ");
      try {
        mutex.acquire();
        if(!q.isEmpty()){
          // poll userfile from print queue
          userFile = q.poll();
          if (userFile != null) {
                sem.release();
          }
        }
      } catch (InterruptedException e) {
      }
        return userFile;

    }
}

class Printer extends Thread {
    private PrintManagementCenter printManagementCenter;
    private int id;

    public Printer(int id, PrintManagementCenter printManagementCenter) {
        this.id = id;
        this.printManagementCenter = printManagementCenter;
    }

    private void print(UserFile userFile) {
        if (userFile == null) {
            return;
        }
        System.out.println(
                "Printer#" + this.id + " is printing UserFile#" + userFile.fileId + " for User#" + userFile.ownerId);
        try {
            Thread.sleep(userFile.timeCost);
        } catch (InterruptedException e) {
            // System.out.println("Printer Interrupted");
        }
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        // 8. Printer Loop +10
        for (;;) { // Infinite Loop
            UserFile userf = printManagementCenter.Download();
            print(userf);
        }

    }
}
