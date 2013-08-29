package http.server;

import http.request.QueryStringRepository;
import http.server.logger.SystemLogger;
import http.server.logger.SystemLoggerFactory;
import http.server.serverSocket.HttpServerSocket;
import http.server.serverSocket.SystemHttpServerSocket;
import http.server.socket.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread implements Runnable {
  private Map<String, String> serverConfig;
  private SystemLogger logger;
  public HttpServerSocket httpServerSocket;
  private boolean closeRequested;

  public ServerThread(Map<String, String> serverConfig) throws IOException {
    this.serverConfig = serverConfig;
    this.logger = new SystemLoggerFactory().build(serverConfig.get("port"));
    closeRequested = false;
  }

  public void run() {
    try {
      int port = Integer.parseInt(serverConfig.get("port"));
      httpServerSocket = new SystemHttpServerSocket(port);

      logger.logMessage("Ninja Server is running in " + serverConfig.get("env").toString() + " mode on port " + serverConfig.get("port") + ".  WOOT!");
      logger.logMessage("Now serving files from: " + serverConfig.get("publicDirectoryPath").toString());
      logger.logMessage("Working directory: " + serverConfig.get("workingDirectoryPath"));
      logger.logMessage("Routes Filename: " + serverConfig.get("routesFilePath").toString());
      logger.logMessage(".htaccess Filename: " + serverConfig.get("htAccessFilePath").toString() + "\n");

      QueryStringRepository queryStringRepository = new QueryStringRepository();

      int cores = Runtime.getRuntime().availableProcessors();
      ExecutorService threadPool = Executors.newFixedThreadPool(cores);

      while (true) {
        WebSocket connectedSocket = httpServerSocket.accept();

        if (closeRequested==false)  {
          ServerRequestThread serverRequestThread = new ServerRequestThreadFactory().build(serverConfig, logger, connectedSocket, queryStringRepository);
          threadPool.submit((Runnable) serverRequestThread);
        }
        else  {
          break;
        }
      }
    } catch (IOException e) {
    }
  }

  public void closeServerSocket() throws IOException {
    closeRequested = true;
    httpServerSocket.close();
  }
}