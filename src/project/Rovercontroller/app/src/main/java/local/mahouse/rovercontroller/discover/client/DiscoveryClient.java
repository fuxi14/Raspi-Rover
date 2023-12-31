package local.mahouse.rovercontroller.discover.client;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*; // For Callable, ExecutorServer, Executors, FutureTask
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * THIS CODE IS NOT MINE, I have grabbed from a github repo
 *
 * It has been modified a little but
 * @see https://github.com/jbrucker/server-discovery/tree/master
 */

import static local.mahouse.rovercontroller.discover.client.DiscoveryConfig.*;

/**
 * A client that tries to discover a service by sending a UDP broadcast on a
 * known port, containing a given request string. Waits for a reply from the
 * server.  It resends the broadcast every TIMEOUT milliseonds (default 1 sec)
 * until a response is received.
 *
 * I implemented it as a Callable<InetAddress> so it can be run in a separate
 * thread.
 *
 * Some issues that may affect this: 1. Server/client on different LAN or VLAN.
 * Router generally does not forward broadcasts. 2. On a wireless network with
 * "Wifi Isolation" enabled, which prevents direct connections between Wifi
 * clients. 3. Client has more than one LAN IP address (at KU, machines have
 * both IPv4 and IPv6 addresses). Might broadcast on the wrong address. This can
 * be fixed by getting all IP addresses except localhost and loopback. See
 * NetworkUtil class for example.
 *
 * @see <a href="https://demey.io/network-discovery-using-udp-broadcast/">...</a>
 */
public class DiscoveryClient implements Callable<String> {
	private static final int MAX_PACKET_SIZE = 2048;
	/** maximum time to wait for a reply, in milliseconds. */
	private static final int TIMEOUT = 2000; // milliseonds
	private static final Logger logger;

	public DatagramSocket socket;

	public static String server;
	public String result = "";
	static Object lock = new Object();

	/* Set an environment variable for for 1-line log messages. */
	static {
		// %1=datetime %2=methodname %3=loggername %4=level %5=message
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tF %1$tT %3$s %4$-7s %5$s%n");
		logger = Logger.getLogger("DiscoveryClient");
	}

	public static String main() { //The code now returns the address to the caller
		DiscoveryClient client = new DiscoveryClient();

		// run it here.  This will hang until a response is received
			server = client.call();
			logger.info("The recieved IP is: " + server);
			return server; //I retorem
		
		// run it in a separate thread, since it could take a while
//		FutureTask<String> task = new FutureTask<>(client);
//		ExecutorService exec = Executors.newSingleThreadExecutor();
//		exec.submit(task);

		// demo how to wait for Future
//		while( ! task.isDone() ) {
//			System.out.println("waiting for client");
//			try { Thread.sleep(1000); } catch (InterruptedException ie) { break; }
//		}
//		System.out.println("Client done. Result is " + task.get());
	}

	/**
	 * Create a UDP socket on the service discovery broadcast port.
	 * 
	 * @return open DatagramSocket if successful
	 * @throws RuntimeException
	 *             if cannot create the socket
	 */
	public DatagramSocket createSocket() {
		// Create a Datagram (UDP) socket on any available port
		socket = null;
		// Create a socket for sending UDP broadcast packets
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			// use a timeout and resend broadcasts instead of waiting forever
			socket.setSoTimeout(TIMEOUT);
		} catch (SocketException sex) {
			logger.severe("SocketException creating broadcast socket: "
					+ sex.getMessage());
			throw new RuntimeException(sex);
		}
		return socket;
	}

	/**
	 * Send broadcast packets with service request string until a response
	 * is received.  Return the response as String (even though it should 
	 * contain an internet address).
	 * @return String received from server. Should be server IP address.
	 *    Returns empty string if failed to get valid reply.
	 */
	public String call() {

		//new Thread(() -> {
			synchronized (lock) {
				// Packet for receiving response from server
				byte[] receiveBuffer = new byte[MAX_PACKET_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer,
						receiveBuffer.length);

				DatagramSocket socket = createSocket();

				// send a known request string (server checks this)
				// TODO is this correct or do we need to apply network byte order
				// function?
				byte[] packetData = DISCOVERY_REQUEST.getBytes();
				// try the widest broadcast address first
				InetAddress broadcastAddress = null;
				try {
					broadcastAddress = InetAddress.getByName("255.255.255.255");
				} catch (UnknownHostException e) { /* This should never happen! */ }
				int servicePort = DISCOVERY_PORT;
				DatagramPacket packet = new DatagramPacket(packetData,
						packetData.length, broadcastAddress, servicePort);
				// use a loop so we can resend broadcast after timeout
				for (int i = 0; i < 10; i++) { //No el faig córrer per sempre
					try {
						socket.send(packet);
						logger.info(String.format("Sent packet to %s:%d",
								broadcastAddress.getHostAddress(), servicePort));

						// wait for reply
						socket.receive(receivePacket);
						logger.info("Received reply from "
								+ receivePacket.getAddress().getHostAddress());
						String reply = new String(receivePacket.getData());
						logger.info("Reply data: " + reply);
						// Does is match?
						logger.info("Do I get here?");
						int k = reply.indexOf(DISCOVERY_REPLY);
						if (k < 0) {
							logger.warning("Reply does not contain prefix " + DISCOVERY_REPLY);
							break;
						}
						k += DISCOVERY_REPLY.length(); // skip prefix
						result = reply.substring(k).trim();
						break;
					} catch (SocketTimeoutException ste) {
						// time-out while waiting for reply.  Send the broadcast again.
					} catch (IOException ioe) {
						logger.log(Level.SEVERE, "IOException during socket operation ", ioe);
						break;
					} /*finally {
						lock.notify();
					}*/
				}
			}
		//}).start();

		/*synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}*/
		logger.info("This should be the last");
		// should close the socket before returning
		if (socket != null) socket.close();
		return result;
	}
}
