import java.io.Serializable;

public class NodeInfo implements Serializable {
    private String logicalName = "";
    private String ip = "";
    private int portNum = -1;

    public boolean equals( Object compared ) {
        String comparedIP = ((NodeInfo) compared).getIP();
        int comparedPort = ((NodeInfo) compared).getPort();
        return comparedPort == this.portNum && comparedIP.equals(this.ip);
    }

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