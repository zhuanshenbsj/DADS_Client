package com.bsr.simulator.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

import com.bsr.simulator.utils.ByteUtil;

public class BPDataPackageThread implements Runnable {

	private int deviceId;
	private String device;

	public BPDataPackageThread(int deviceId) {
		this.deviceId = deviceId;
		//		this.device = ConfUtil.deviceStr[deviceId];
		this.device = "0" + deviceId;
	}

	@Override
	public void run() {
		byte[] serverIp = new byte[4];
		serverIp[0] = (byte) 127;
		serverIp[1] = (byte) 0;
		serverIp[2] = (byte) 0;
		serverIp[3] = (byte) 1;

		byte[] b = new byte[1024];

		InputStream in = null;
		OutputStream out = null;
		try {
			InetAddress address = InetAddress.getByAddress(serverIp);
			Socket client = new Socket(address, 8891);

			in = client.getInputStream();
			out = client.getOutputStream();

			//发送login包
			sentBPLoginPackage(out);
			Thread.sleep(2000);

			//发送四号包
			//sendBPPackage4(out, in);
			//Thread.sleep(2000);
			//in.read(b);
			//System.out.println("4" + Arrays.toString(b));

			//发送8号包
			//sentBPPackage8(out);
			//Thread.sleep(20000);
			//in.read(b);
			//System.out.println("8" + Arrays.toString(b));

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���͵�½��
	 * @param out
	 * @throws IOException
	 */
	private void sentBPLoginPackage(OutputStream out) throws IOException {
		System.out.println("sentBPLoginPackage... " + device);
		byte[] sendData = null;
		//1�����͵�½��
		sendData = new byte[44];
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		//The length of all package(big endian)
		ByteUtil.putIntByLarge(sendData, 44, 4);
		sendData[8] = 1;
		sendData[9] = 16;
		String deviceIdStr = this.device;

		char[] array = deviceIdStr.toCharArray();
		for (int i = 0; i < array.length; i++) {
			sendData[10 + i] = (byte) array[i];
		}
		//"123456".getBytes(0,16,sendData,26);
		//crc
		sendData[42] = 0;
		sendData[43] = 0;
		out.write(sendData);
		out.flush();
	}

	private void sendBPPackage4(OutputStream out, InputStream in) throws IOException {
		System.out.println("sendBPPackage4... " + device);
		byte[] sendData = null;
		sendData = new byte[12];
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		ByteUtil.putIntByLarge(sendData, 12, 4);
		sendData[8] = 4;
		sendData[9] = 0;
		//crc
		sendData[10] = 0;
		sendData[11] = 0;
		out.write(sendData);
		in.read(sendData);
		out.flush();

	}

	/**
	 * ����8�Ű�
	 * @param out
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void sentBPPackage8(OutputStream out) throws IOException, InterruptedException {
		System.out.println("sentBPPackage8... " + device);
		byte[] sendData = new byte[47];
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		//The length of all package(big endian)
		ByteUtil.putIntByLarge(sendData, 47, 4);
		sendData[8] = 8;
		sendData[9] = 4;
		String deviceIdStr = this.device;

		Calendar c = Calendar.getInstance();//
		int year = c.get(Calendar.YEAR);

		char[] array = deviceIdStr.toCharArray();
		for (int i = 0; i < array.length; i++) {
			sendData[10 + i] = (byte) array[i];
		}
		ByteUtil.putShortByLarge(sendData, (short) year, 26);
		sendData[28] = (byte) (c.get(Calendar.MONTH) + 1);//month
		sendData[29] = (byte) c.get(Calendar.DATE);//day
		sendData[30] = (byte) c.get(Calendar.HOUR_OF_DAY);//Hour
		sendData[31] = (byte) c.get(Calendar.MINUTE);//Minute
		sendData[32] = (byte) c.get(Calendar.SECOND);//Second

		ByteUtil.putShortByLarge(sendData, (short) 99, 33);
		ByteUtil.putShortByLarge(sendData, (short) 77, 35);
		ByteUtil.putShortByLarge(sendData, (short) 88, 37);
		ByteUtil.putShortByLarge(sendData, (short) 66, 39);
		//crc
		sendData[45] = 0;
		sendData[46] = 0;
		out.write(sendData);
		out.flush();
	}
}
