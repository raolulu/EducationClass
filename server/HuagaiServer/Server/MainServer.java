package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainServer{
    private ServerSocket serverSocket = null;
    private ThreadPoolExecutor threadPoolExecutor = null;
    private ServerSocket  surveillanceServer = null;
    public MainServer(){
        try {
            serverSocket = new ServerSocket(9947);
            surveillanceServer = new ServerSocket(9948);
            threadPoolExecutor = newCachedThreadPool();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ThreadPoolExecutor newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }

    public void startServer(){
        while(true){
            try {
                // new Thread(new WorkThread(serverSocket.accept())).start();
                threadPoolExecutor.execute(new WorkThread(serverSocket.accept(),surveillanceServer.accept()));
                System.out.println("线程池中线程数目：" + threadPoolExecutor.getPoolSize());
                System.out.println("线程池中队列的线程数目：" + threadPoolExecutor.getQueue().size());
                System.out.println("线程池中完成线程数目：" + threadPoolExecutor.getCompletedTaskCount());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
    public class WorkThread implements Runnable{
        private Socket clientScoket = null;
        private RequestTest requestTest;
        private Socket surveClient = null;
        public WorkThread(final Socket socket,final Socket sure){
            clientScoket = socket;
            surveClient = sure;
            requestTest = new RequestTest(clientScoket,surveClient);
        }
        @Override
        public void run() {
            try {
                requestTest.handleRequest();
                requestTest.requestRelease();
            } catch (IOException e) {
                e.printStackTrace();
            }     
        }
    }
}