package channels;

import java.io.IOException;
import java.net.*;

/**
 * Created by ei10117 on 16/03/2017.
 */
public class MulticastChannel extends Thread{
    protected MulticastSocket mc_socket;
    protected InetAddress mc_addr;
    protected int mc_port;
    protected int MAX_SIZE= 65536;

    public MulticastChannel(String addr, int port/*, String type*/) {


        try {
            mc_addr = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mc_port = port;
        try {
            mc_socket = new MulticastSocket(mc_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mc_socket.joinGroup(mc_addr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mc_socket.setTimeToLive(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mc_socket.setLoopbackMode(false);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        //System.out.println("channel :" + type + "    on addr: " + mc_addr.getHostName() + "  port:" + mc_port);
    }


    public MulticastSocket getMc_socket(){
        return mc_socket;
    }

    public int getPort(){
        return mc_port;
    }

    public InetAddress getAdress(){
        return mc_addr;
    }

    public void sendMessage(DatagramPacket packet) throws IOException { mc_socket.send(packet);}

}
