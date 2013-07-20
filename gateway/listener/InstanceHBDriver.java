package gateway.listener;

public class InstanceHBDriver
{

    public static void main(String args[])
    {
        String name = args[0];
        int port = Integer.parseInt(args[1]);

        System.out.println("name: " + name + " Port Number: " + port);

        new InstanceHBThread(port).start();
    }
}
