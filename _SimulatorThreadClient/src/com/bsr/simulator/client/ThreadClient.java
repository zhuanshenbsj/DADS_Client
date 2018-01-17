package com.bsr.simulator.client;

//import java.io.* ;
public class ThreadClient {
	public static void main(String args[]) throws Exception { // ?????????

		String deviceId = "0526";
		long start = System.currentTimeMillis();
		// 开启1000个线程，并发 发送数据包(1,2,3,4)
		for (int i = 0; i < 1; i++) {
			new Thread(new StepcountPackageThread(Integer.parseInt(deviceId + i))).start();
			// new Thread( new
			// BPDataPackageThread(Integer.parseInt(deviceId+i))).start();
		}
		long end = System.currentTimeMillis();
		System.out.println("消耗时间为" + (end - start));

		/*
		 * for(int i=4920;i<10000;i++) { new Thread(new
		 * StepcountPackageThread(i+1)).start() ; // ????????????????? }
		 */
		// ??????????????deviceId?????
		// for(int j =0;j<100;j++){
		// for(int i=60;i<62;i++)
		// new Thread(new StepcountPackageThread(0524)).start() ; //
		// ?????????????????

		// 测试BP血压包
		// new Thread(new BPDataPackageThread(0524)).start();

		// }
		//
		// }

		// for(int i=0;i<80;i++)
		// {
		// new Thread(new PacketHandleThread(i)).start() ; // ?????????????????
		// }
		// for(int i=0;i<80;i++)
		// {
		// new Thread(new PacketHandleThread(i)).start() ; // ?????????????????
		// }
		// for(int i=0;i<80;i++)
		// {
		// new Thread(new PacketHandleThread(i)).start() ; // ?????????????????
		// }
		// for(int i=0;i<80;i++)
		// {
		// new Thread(new PacketHandleThread(i)).start() ; // ?????????????????
		// }
	}
}
