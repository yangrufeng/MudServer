package com.yangrufeng.mud.mudserver.common;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取本地IP地址
 * @author Administrator
 *
 */
public class GetLocalIpAddress
{
	/**
	 * NetworkInterface 获取本机IP
	 * @return
	 * @throws SocketException
	 */
	public static String getLocalIpAdress()
	{
		/*
		 * 1、返回本机上所有的接口，物理和虚拟的
		 * 2、遍历每个接口的IP地址
		 * 3、去掉回送地址 127.0.0.1回送地址
		 * 4、返回IP地址
		 */

//		for(Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();en.hasMoreElements();)
//		{
//			NetworkInterface intf=en.nextElement();
//
//			for(Enumeration<InetAddress> enumIpAddr=intf.getInetAddresses();enumIpAddr.hasMoreElements();)
//			{
//				InetAddress inetAddress=enumIpAddr.nextElement();
//
//				if(!inetAddress.isLoopbackAddress())
//				{
//					return inetAddress.getHostAddress();
//				}
//			}
//		}
//
//		return null;

		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
//			Log.i("yao", "SocketException");
			e.printStackTrace();
		}
		return hostIp;
	}

}
