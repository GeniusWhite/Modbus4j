package com.bestwaiting.modbus.test;

import java.nio.ByteBuffer;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import com.serotonin.util.queue.ByteQueue;

public class Test {
	static ModbusFactory modbusFactory;
	static {
		if (modbusFactory == null) {
			modbusFactory = new ModbusFactory();
		}
	}

	/**
	 * �õ� WriteRegistersRequest
	 * 
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param start
	 * @param values
	 */
	public static WriteRegistersRequest getWriteRegistersRequest(int slaveId,
			int start, short[] values) {
			WriteRegistersRequest request = null;
			try {
				request = new WriteRegistersRequest(slaveId, start, values);
			} catch (ModbusTransportException e) {
				e.printStackTrace();
			}
		return request;
	}

	/**
	 * �õ� WriteRegistersRequest
	 * 
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param start
	 * @param values
	 */
	public static WriteRegistersResponse getWriteRegistersResponse(ModbusMaster tcpMaster, WriteRegistersRequest request) {
		WriteRegistersResponse response = null;
		try {
			response = (WriteRegistersResponse) tcpMaster.send(request);
		} catch (ModbusTransportException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * д��
	 * 
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param start
	 * @param values
	 */
	public static int modbusWTCP(String ip, int port, int slaveId, int start,short[] values) {
		ModbusMaster tcpMaster = getTcpMaster(ip, port, slaveId);
		if (tcpMaster == null) {System.out.println("tcpMaster is null ");return 0;}
		tcpMaster = initTcpMaster(tcpMaster);
		WriteRegistersRequest request = getWriteRegistersRequest(slaveId,start, values);

		WriteRegistersResponse response = getWriteRegistersResponse(tcpMaster,request);
		if (response.isException()) {
			return 0;
		} else {
			return 1;
		}
	}

	
	/**
	 * ��ʼ��?tcpMaster
	 * 
	 * @param tcpMaster
	 * @return
	 */
	public static ModbusMaster initTcpMaster(ModbusMaster tcpMaster) {
		if (tcpMaster == null)return null;
		try {
			tcpMaster.init();
			return tcpMaster;
		} catch (ModbusInitException e) {
			return null;
		}
	}

	/**
	 * �õ� ModbusRequest
	 * 
	 * @param salveId
	 * @param start
	 * @param readLenth
	 * @param tcpMaster
	 */
	public static ModbusRequest getRequest(int salveId, int start,int readLenth, ModbusMaster tcpMaster) {
		ModbusRequest modbusRequest = null;
		try {
			modbusRequest = new ReadHoldingRegistersRequest(salveId, start,readLenth); 
			return modbusRequest;
		} catch (ModbusTransportException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * �õ� ModbusResponse
	 * 
	 * @param salveId
	 * @param start
	 * @param readLenth
	 * @param tcpMaster
	 */
	public static ModbusResponse getModbusResponse(ModbusMaster tcpMaster,ModbusRequest request) {
		ModbusResponse modbusResponse = null;
		try {
			modbusResponse = tcpMaster.send(request);
			return modbusResponse;
		} catch (ModbusTransportException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ��ȡ �豸����
	 * @param ip
	 *            ��Ϣ����ַip
	 * @param port
	 *            �˿� Ĭ�� �˿�502
	 * @param salveId
	 *            ��վ��ַ
	 * @param start
	 *            ���ݱ��� �� ��ʼ λ��
	 * @param readLenth
	 *            ��ȡ�ĳ���
	 * @return ���
	 */
	public static ByteQueue modbusRTCP(String ip, int port, int salveId,int start, int readLenth) {
		ModbusMaster tcpMaster = getTcpMaster(ip, port, salveId);// �õ�tcpMaster
		if (tcpMaster == null) {System.out.println("tcpMaster is null ");return null;
		}
		return modbusRTCP0(ip, port, salveId, start, readLenth, tcpMaster);
	}

	/**
	 * ��ȡ tcp master
	 * 
	 * @param ip
	 * @param port
	 * @param salveId
	 */
	public static ModbusMaster getTcpMaster(String ip, int port, int salveId) {
		IpParameters params = new IpParameters();
		params.setHost(ip);// ����ip
		if (port == 0)params.setPort(502);// ���ö˿ڣ�Ĭ��Ϊ502
		else params.setPort(port);
		ModbusMaster tcpMaster = modbusFactory.createTcpMaster(params, true);// ��ȡModbusMaster����
		return tcpMaster;
	}

	/**
	 * modbus ��ȡ
	 * 
	 * @param ip
	 * @param port
	 * @param salveId
	 * @param start
	 * @param readLenth
	 * @param tcpMaster
	 * @return
	 */
	public static ByteQueue modbusRTCP0(String ip, int port, int salveId,int start, int readLenth, ModbusMaster tcpMaster) {
		if (tcpMaster == null) {System.out.println("tcpMaster is null");return null;}
		tcpMaster = initTcpMaster(tcpMaster);// ��ʼ��tcpmaster

		if (tcpMaster == null) {System.out.println("tcpMaster is null");return null;
		}
		ModbusRequest modbusRequest = getRequest(salveId, start, readLenth,tcpMaster);// �õ�requst ����
		
		if (modbusRequest == null) {System.out.println("request is null");return null;}
		ModbusResponse response = getModbusResponse(tcpMaster, modbusRequest);// �������󣬵õ�Response

		ByteQueue byteQueue = new ByteQueue(12);
		response.write(byteQueue);
		System.out.println("����" + modbusRequest.getFunctionCode());
		System.out.println("��վ��ַ:" + modbusRequest.getSlaveId());
		System.out.println("�յ�����Ӧ��Ϣ��С" + byteQueue.size());
		System.out.println("�յ�����Ӧ��ϢС:" + byteQueue);
		return byteQueue;
	}

	/* *
	 * Convert byte[] to hex
	 * string.�������ǿ��Խ�byteת����int��Ȼ������Integer.toHexString(int)��ת����16�����ַ���
	 * @param src byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * ***************************************************
	 * ��ʼλ��15,��Ӧ���ݣ���վ|data�����Ĵ���������|data length|data*
	 * ***************************************************
	 * 
	 * @param bq
	 */
	public static void ansisByteQueue(ByteQueue bq) {
		byte[] result = bq.peekAll();
		System.out.println("��վ��ַ===" + result[0]);
		System.out.println("data ����===" + result[1]);
		System.out.println("data ����===" + result[2]);
		byte[] temp = null;
		ByteBuffer buffer = ByteBuffer.wrap(result, 3, result.length - 3);//ֱ�ӻ�ȡ data
		while (buffer.hasRemaining()) {
			temp = new byte[2];
			buffer.get(temp, 0, temp.length);
			System.out.print(Integer.parseInt(bytesToHexString(temp), 16)+" ");
		}

	}

	public static void main(String[] args) {
		ByteQueue result = Test.modbusRTCP("169.254.48.188", 502, 2,15, 3);
		ansisByteQueue(result);
		short[] shor = new short[1];
		shor[0] = 0x33;
		Test.modbusWTCP("169.254.48.188", 502, 2, 15, shor);

	}
}
