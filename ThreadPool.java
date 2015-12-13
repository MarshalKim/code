import java.awt.List;
import java.util.LinkedList;

import org.omg.CORBA.PUBLIC_MEMBER;

public final class ThreadPool{
	//线程池中默认线程数为5
	private static int worker_num=5;
	//工作线程 
	private WorkThread[] workThread;
	//未处理的任务
	private static volatile int finished_num=0;
	//任务队列，作为一个缓冲,list不安全
	private LinkedList<Runnable> taskQueue=new LinkedList<Runnable>();
	private static ThreadPool threadPool;
	
	//创建具有默认线程个数的线程池
	private ThreadPool(){
		this(5);
	}
	//创建线程池，work_num是线程池中工作线程的个数
	private ThreadPool(int work_num){
		ThreadPool.worker_num=work_num;
		workThread=new WorkThread[work_num];
		for(int i=0;i<work_num;i++){
			workThread[i]=new WorkThread();
			workThread[i].start();
		}
	}
	//单态模式，获得一个默认线程数量的线程池
	public static ThreadPool getThreadPool(){
		return getThreadPool(worker_num);
	}
	//单态模式，获得一个指定线程个数的线程池，work_num(>0)是为线程池中工作线程的个数
	//work_num(<0)创建默认的工作线程数量的线程池
	public static ThreadPool getThreadPool(int work_num){
		if(work_num<=0)
			work_num=ThreadPool.worker_num;
		if(threadPool==null)
			threadPool=new ThreadPool(work_num);
		return threadPool;		
	}
	//执行任务，只是把任务加到任务队列，什么时候执行由线程管理器决定
	public void execute(Runnable task){
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
			
		}
	}
	//批量执行任务
	public void execute(Runnable[] task){
		synchronized (taskQueue) {
			for(Runnable t:task)
				taskQueue.add(t);
			taskQueue.notify();
		}
	}
	//销毁线程池，该方法保证在所有任务都完成的情况下才销毁所有线程，否则等待任务完成才销毁
	public void destroy(){
		while(!taskQueue.isEmpty()){
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		//线程停止工作且置为null
		for(int i=0;i<worker_num;i++){
			workThread[i].stopworker();
			workThread[i]=null;
		}
		threadPool=null;
		taskQueue.clear();
	}
	//返回线程个数
	public int getWorkThreadNum(){
		return ThreadPool.worker_num;
	}
	//返回已完成任务的个数,这里的已完成是只出了任务队列的任务个数，可能该任务并没有实际执行完成  
	public int getFinshedTaskNum(){
		return ThreadPool.finished_num;
	}
	//返回队列长度，即还没有处理的任务个数
	public int getWaitTaskNum(){
		return taskQueue.size();
	}
	//覆盖toString,返回线程池信息
	public String toString(){
		return "WorkThread num"+worker_num+"finished task num"+finished_num+"wait task num"+taskQueue.size();
	}
	/**
	 * 
	 * @author Yixiong
	 * 内部类，工作线程
	 *
	 */
	private class WorkThread extends Thread{
		//该工作是否有效，用于结束该工作线程
		private boolean isRunning=true;
		/**
		 * 关键所在，如果队列不为空，则取出任务执行，若队列为空，则等待
		 */
		public void run(){
			Runnable r=null;
			while(isRunning){//注意，若线程无效则自然结束run方法，该线程就没用了
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
						r=taskQueue.remove(0);//取出任务
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
		
	
