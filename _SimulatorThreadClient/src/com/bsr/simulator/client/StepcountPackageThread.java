package com.bsr.simulator.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.bsr.simulator.utils.ByteUtil;
import com.bsr.simulator.utils.MLinkCRC;

public class StepcountPackageThread implements Runnable {
	private static Logger log = Logger.getLogger(StepcountPackageThread.class);
	private int deviceId;
	private String device;

	public StepcountPackageThread(int deviceId) {
		this.deviceId = deviceId;
		//		this.device = C3p0Utils.executeQuery("select deviceID from usertbl where deviceID="+Integer.toString(deviceId)).get(0).get("deviceID");
		device = "0" + Integer.toString(deviceId);
		System.out.println(this.device);
	}

	@Override
	public void run() {
		byte[] serverIp = new byte[4];
		/*serverIp[0] = (byte)127;
		serverIp[1] = (byte)0;
		serverIp[2] = (byte)0;
		serverIp[3] = (byte)1;*/

		serverIp[0] = (byte) 127;
		serverIp[1] = (byte) 0;
		serverIp[2] = (byte) 0;
		serverIp[3] = (byte) 1;

		InputStream in = null;
		OutputStream out = null;

		byte[] b = new byte[1024];
		try {
			InetAddress address = InetAddress.getByAddress(serverIp);
			Socket client = new Socket(address, 8891);
			in = client.getInputStream();
			out = client.getOutputStream();
			client.setKeepAlive(true);

			//发送登录包
			sentLoginPackage(out);
			Thread.sleep(3000);
			in.read(b);
			log.info("login: " + Arrays.toString(b));

			//发送8号包数据
			//sendPackage8Two(out);
			//Thread.sleep(1000);
			//in.read(b);
			//log.info("2: " + Arrays.toString(b));
			//System.out.println("2: " + Arrays.toString(b));

			//发送三号包数据
			sendPackage8Three(out);
			Thread.sleep(3000);
			in.read(b);
			log.info("3: " + Arrays.toString(b));
			System.out.println("3:" + Arrays.toString(b));

			//发送四号包数据
			//sendPackage8Four(out);
			//Thread.sleep(3000);
			//in.read(b);
			//System.out.print("4"+Arrays.toString(b));

			//发送登出包
			//			sentLogoutPackage(out);
			//			Thread.sleep(1000);
			//			in.read(b);
			//			log.info("logout: " + Arrays.toString(b));

			out.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		try{
		//			InetAddress address = InetAddress.getByAddress(serverIp);
		//			Socket client = new Socket(address,ConfUtil.port);
		//			in = client.getInputStream();
		//			out = client.getOutputStream();
		//			client.setKeepAlive(true);

		//			//���͵�½�����ACK��
		//			sentLoginPackage(out);
		//			Thread.sleep(3000);
		//			
		//			in.read(b);
		//			log.info("login: "+Arrays.toString(b));
		//		
		//			//����8�Ű�ʽ��
		//			sendPackage8Three(out);
		//			Thread.sleep(5000);
		//			in.read(b);
		//			log.info("3: "+Arrays.toString(b));
		//			
		//			out.close();
		//			client.close();
		//		}catch(Exception e)
		//		{
		//			e.printStackTrace();
		//		}
	}

	/**
	 * ���͵�½��
	 * @param out
	 * @throws IOException
	 */
	private void sentLoginPackage(OutputStream out) throws IOException {
		log.info("sentLoginPackage... " + device);
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
		sendData[9] = -128;
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

	/**
	 * ����8�Ű�  8-1
	 * @param out
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void sentPackage8One(OutputStream out) throws IOException, InterruptedException {
		log.info("sentPackage8One... " + device);
		byte[] sendData = null;
		sendData = new byte[68];
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		ByteUtil.putIntByLarge(sendData, 68, 4);
		sendData[8] = 8;
		sendData[9] = 1;
		sendData[10] = 1;//��ݰ����ͣ��Զ�|����

		Calendar c = Calendar.getInstance();
		sendData[11] = (byte) (c.get(Calendar.YEAR) - 2000);
		sendData[12] = (byte) (c.get(Calendar.MONTH) + 1);
		sendData[13] = (byte) c.get(Calendar.DATE);

		sendData[18] = 3;
		sendData[19] = 5;
		sendData[20] = 0;
		sendData[21] = 3;

		sendData[22] = 60;//����
		sendData[23] = 70;
		sendData[24] = 70;

		ByteUtil.putIntByLarge(sendData, (this.deviceId + 1) * 1000, 25);
		ByteUtil.putIntByLarge(sendData, (this.deviceId + 1) * 1000, 29);
		ByteUtil.putIntByLarge(sendData, (this.deviceId + 1) * (int) (10 * Math.random()), 33);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 37);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 39);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 41);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 43);

		sendData[45] = 'D';
		sendData[46] = 'E';
		sendData[47] = 'V';
		sendData[48] = 'I';
		sendData[49] = 'D';
		String deviceIdStr = this.device;
		char[] array = deviceIdStr.toCharArray();
		for (int i = 0; i < array.length; i++) {
			sendData[50 + i] = (byte) array[i];
		}
		//crc
		sendData[66] = 0;
		sendData[67] = 0;

		out.write(sendData);
		out.flush();

	}

	/**
	 * ����8�Ű���ϸ���: ��8Сʱ���   8-2
	 * @param out
	 * @throws IOException
	 */
	private void sendPackage8Two(OutputStream out) throws IOException {
		log.info("sendPackage8Two..." + device);
		System.out.println("sendPackage8Two..." + device);
		byte[] sendData = null;
		int hours = 8;
		sendData = new byte[33 + 114 * hours];
		//Header(4)
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		//Length(4)
		ByteUtil.putIntByLarge(sendData, 33 + 114 * hours, 4);
		//Type(2)
		sendData[8] = 8;
		sendData[9] = 2;
		//USEDATA(114/h)
		Calendar c = Calendar.getInstance();

		for (int j = 0; j < hours; j++) {
			//Year(2)
			ByteUtil.putShortByLarge(sendData, (short) (c.get(Calendar.YEAR)), j * 114 + 10);
			sendData[j * 114 + 12] = 0;
			sendData[j * 114 + 13] = (byte) (c.get(Calendar.MONTH) + 1);
			sendData[j * 114 + 14] = (byte) c.get(Calendar.DATE);
			sendData[j * 114 + 15] = (byte) j;

			//ÿ5�����ڵĲ���
			for (int i = 0; i < 12; i++) {
				ByteUtil.putShortByLarge(sendData, (short) (i * 10 + 3), j * 114 + 16 + i * 2);
			}
			//ÿ5�����ڵĿ�·��
			for (int i = 0; i < 12; i++) {
				ByteUtil.putShortByLarge(sendData, (short) (i * 100 + 3), j * 114 + 39 + i * 2);
			}
			//ÿ5�����˶�ǿ�ȵȼ�2
			for (int i = 0; i < 12; i++) {
				sendData[j * 114 + 63 + i] = 5;
			}
		}
		//deviceID
		String deviceIdStr = this.device;

		char[] array = deviceIdStr.toCharArray();
		for (int i = 0; i < array.length; i++) {
			sendData[15 + 114 * hours + i] = (byte) array[i];
		}
		sendData[145] = 0;
		sendData[146] = 0;
		out.write(sendData);
		out.flush();
	}

	/**
	 * ����8�Ű� 8-3
	 * @param out
	 * @throws IOException
	 */
	private void sendPackage8Three(OutputStream out) throws IOException {
		log.info("sendPackage8Three..." + device);
		byte[] sendData = null;
		sendData = new byte[88];
		//Header(4)
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		//Length(4)
		ByteUtil.putIntByLarge(sendData, 88, 4);
		//Type(2)
		sendData[8] = 8;
		sendData[9] = 3;
		//USEDATA
		sendData[10] = 1;//��ݰ����ͣ��Զ�|����

		Calendar c = Calendar.getInstance();
		sendData[11] = (byte) (c.get(Calendar.YEAR) - 2000);
		sendData[12] = (byte) (c.get(Calendar.MONTH) + 1);
		sendData[13] = (byte) c.get(Calendar.DATE);

		sendData[18] = 3;
		sendData[19] = 5;
		sendData[20] = 0;
		sendData[21] = 3;

		sendData[22] = 60;//����
		sendData[23] = 70;//����
		sendData[24] = 70;//����

		ByteUtil.putIntByLarge(sendData, (this.deviceId + 1) * 1000, 25);
		ByteUtil.putIntByLarge(sendData, (this.deviceId + 1) * 1000, 29);
		ByteUtil.putIntByLarge(sendData, (this.deviceId + 1) * (int) (10 * Math.random()), 33);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 37);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 39);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 41);
		ByteUtil.putShortByLarge(sendData, (short) ((this.deviceId + 1) * 10), 43);

		String deviceIdStr = this.device;
		sendData[65] = 'D';
		sendData[66] = 'E';
		sendData[67] = 'V';
		sendData[68] = 'I';
		sendData[69] = 'D';
		char[] array = deviceIdStr.toCharArray();
		for (int i = 0; i < array.length; i++) {
			sendData[70 + i] = (byte) array[i];
		}
		//crc
		sendData[86] = 0;
		sendData[87] = 0;
		out.write(sendData);
		out.flush();
	}

	/**
	 * ����8�Ű�ʽ�� ��8Сʱ���
	 * @param out
	 * @throws IOException
	 */
	private void sendPackage8Four(OutputStream out) throws IOException {
		log.info("sendPackage8Four..." + device);
		byte[] sendData = null;
		int hours = 8;
		sendData = new byte[33 + 114 * hours];
		//Header(4)
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		//Length(4)
		ByteUtil.putIntByLarge(sendData, 33 + 114 * hours, 4);
		//Type(2)
		sendData[8] = 8;
		sendData[9] = 4;
		//USEDATA(114/h)
		Calendar c = Calendar.getInstance();

		for (int j = 0; j < hours; j++) {
			//Year(2)
			ByteUtil.putShortByLarge(sendData, (short) (c.get(Calendar.YEAR)), j * 114 + 10);
			sendData[j * 114 + 12] = 0;
			sendData[j * 114 + 13] = (byte) (c.get(Calendar.MONTH) + 1);
			sendData[j * 114 + 14] = (byte) c.get(Calendar.DATE);
			sendData[j * 114 + 15] = (byte) j;

			//ÿ5�����ڵĲ���
			for (int i = 0; i < 12; i++) {
				ByteUtil.putShortByLarge(sendData, (short) (i * 10 + 3), j * 114 + 16 + i * 2);
			}
			//ÿ5�����ڵĿ�·��
			for (int i = 0; i < 12; i++) {
				ByteUtil.putShortByLarge(sendData, (short) (i * 100 + 3), j * 114 + 39 + i * 2);
			}
			//ÿ5�����˶�ǿ�ȵȼ�2
			for (int i = 0; i < 12; i++) {
				sendData[j * 114 + 63 + i] = 5;
			}
		}
		//deviceID
		String deviceIdStr = this.device;

		char[] array = deviceIdStr.toCharArray();
		for (int i = 0; i < array.length; i++) {
			sendData[15 + 114 * hours + i] = (byte) array[i];
		}
		//		sendData[145] = 0;
		//		sendData[146] = 0;
		out.write(sendData);
		out.flush();
	}

	/**
	 * �����˳���
	 * @param out
	 * @throws IOException
	 */
	private void sentLogoutPackage(OutputStream out) throws IOException {
		log.info("sentLogoutPackage... ");
		byte[] sendData = null;
		//1�������˳���
		sendData = new byte[12];
		byte[] crc_c = new byte[2];
		sendData[0] = -89;
		sendData[1] = -72;
		sendData[2] = 0;
		sendData[3] = 1;
		//The length of all package(big endian)
		ByteUtil.putIntByLarge(sendData, 12, 4);
		sendData[8] = 1;
		sendData[9] = 3;
		crc_c = MLinkCRC.crc16(sendData);
		//crc
		sendData[10] = crc_c[0];
		sendData[11] = crc_c[1];
		out.write(sendData);
		out.flush();
	}
}
