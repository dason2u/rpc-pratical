package rpc.ipc.client;

import rpc.io.Writable;
import rpc.ipc.util.RPCClientException;

import java.net.Socket;

public class ConnectionProxy implements Connection {
    private ConnectionPool connectionPool;
    private Connection realConnection;

    public ConnectionProxy(ConnectionPool connectionPool, Socket socket) throws RPCClientException {
        this.connectionPool = connectionPool;
        ConnectionImpl connection = new ConnectionImpl(socket);
        this.realConnection = connection;
    }

    public ConnectionProxy(ConnectionPool connectionPool, Connection connection) {
        this.connectionPool = connectionPool;
        this.realConnection = connection;
    }

    @Override
    public Writable remoteCall(Call call) throws RPCClientException {
        return realConnection.remoteCall(call);
    }

    public void close() throws RPCClientException {
        connectionPool.releaseConnection(realConnection);
    }
}
