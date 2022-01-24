public class NodeInfo {
    private String logicalName = "";
    private String ip = "";
    private int portNum = -1;

    public NodeInfo( String name, String ip, int port ) {
        this.logicalName = name;
        this.ip = ip;
        this.portNum = port;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return portNum;
    }

    public String getName() {
        return logicalName;
    }

}
