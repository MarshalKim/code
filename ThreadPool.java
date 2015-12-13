import java.awt.List;
import java.util.LinkedList;

import org.omg.CORBA.PUBLIC_MEMBER;

public final class ThreadPool{
	//�̳߳���Ĭ���߳���Ϊ5
	private static int worker_num=5;
	//�����߳� 
	private WorkThread[] workThread;
	//δ���������
	private static volatile int finished_num=0;
	//������У���Ϊһ������,list����ȫ
	private LinkedList<Runnable> taskQueue=new LinkedList<Runnable>();
	private static ThreadPool threadPool;
	
	//��������Ĭ���̸߳������̳߳�
	private ThreadPool(){
		this(5);
	}
	//�����̳߳أ�work_num���̳߳��й����̵߳ĸ���
	private ThreadPool(int work_num){
		ThreadPool.worker_num=work_num;
		workThread=new WorkThread[work_num];
		for(int i=0;i<work_num;i++){
			workThread[i]=new WorkThread();
			workThread[i].start();
		}
	}
	//��̬ģʽ�����һ��Ĭ���߳��������̳߳�
	public static ThreadPool getThreadPool(){
		return getThreadPool(worker_num);
	}
	//��̬ģʽ�����һ��ָ���̸߳������̳߳أ�work_num(>0)��Ϊ�̳߳��й����̵߳ĸ���
	//work_num(<0)����Ĭ�ϵĹ����߳��������̳߳�
	public static ThreadPool getThreadPool(int work_num){
		if(work_num<=0)
			work_num=ThreadPool.worker_num;
		if(threadPool==null)
			threadPool=new ThreadPool(work_num);
		return threadPool;		
	}
	//ִ������ֻ�ǰ�����ӵ�������У�ʲôʱ��ִ�����̹߳���������
	public void execute(Runnable task){
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
			
		}
	}
	//����ִ������
	public void execute(Runnable[] task){
		synchronized (taskQueue) {
			for(Runnable t:task)
				taskQueue.add(t);
			taskQueue.notify();
		}
	}
	//�����̳߳أ��÷�����֤������������ɵ�����²����������̣߳�����ȴ�������ɲ�����
	public void destroy(){
		while(!taskQueue.isEmpty()){
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		//�߳�ֹͣ��������Ϊnull
		for(int i=0;i<worker_num;i++){
			workThread[i].stopworker();
			workThread[i]=null;
		}
		threadPool=null;
		taskQueue.clear();
	}
	//�����̸߳���
	public int getWorkThreadNum(){
		return ThreadPool.worker_num;
	}
	//�������������ĸ���,������������ֻ����������е�������������ܸ�����û��ʵ��ִ�����  
	public int getFinshedTaskNum(){
		return ThreadPool.finished_num;
	}
	//���ض��г��ȣ�����û�д�����������
	public int getWaitTaskNum(){
		return taskQueue.size();
	}
	//����toString,�����̳߳���Ϣ
	public String toString(){
		return "WorkThread num"+worker_num+"finished task num"+finished_num+"wait task num"+taskQueue.size();
	}
	/**
	 * 
	 * @author Yixiong
	 * �ڲ��࣬�����߳�
	 *
	 */
	private class WorkThread extends Thread{
		//�ù����Ƿ���Ч�����ڽ����ù����߳�
		private boolean isRunning=true;
		/**
		 * �ؼ����ڣ�������в�Ϊ�գ���ȡ������ִ�У�������Ϊ�գ���ȴ�
		 */
		public void run(){
			Runnable r=null;
			while(isRunning){//ע�⣬���߳���Ч����Ȼ����run���������߳̾�û����
				synchronized (taskQueue) {
					while(isRunning&&taskQueue.isEmpty()){
						try {
							taskQueue.wait(20);
						} catch (InterruptedException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
					if(!taskQueue.isEmpty()){
						r=taskQueue.remove(0);//ȡ������
					}
					if(r!=null){
						r.run();
					}
					finished_num++;
					r=null;
				}
		
			}
			}
		public void stopworker(){
			isRunning=false;
		}		
	}
}
		
	
