package main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ANilsCustomBlockingQueue {
	BlockingQueue<Object> queue = new ArrayBlockingQueue<>(100000);
	String source;
	int numBlocked;
	
	public ANilsCustomBlockingQueue(String s) {
		setSource(s);
		setNumBlocked(0); // 0 by default but why not
	}
	
	public void setSource(String s) {
		source = s;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setNumBlocked(int x) {
		numBlocked = x;
	}
	
	public int getNumBlocked() {
		return numBlocked;
	}
	
	public void setQueue(BlockingQueue<Object> q) {
		queue = q;
	}
	
	public BlockingQueue<Object> getQueue(){
		return queue;
	}
	
	public void put(Object o) {
		try {
			getQueue().put(o);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Object take() {
		setNumBlocked(getNumBlocked() + 1);
		try {
			Object obj = getQueue().take(); // threads will block here until there is an obj to take
			setNumBlocked(getNumBlocked() - 1);
			return obj;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
