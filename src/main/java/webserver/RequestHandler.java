package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.UrlData;
import util.HttpRequestUtils;
import util.StringUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
		
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			String urlPath="";
			while(StringUtils.isNotEmpty(line)){
				if(line.contains(HttpRequestUtils.HTTP)){
					urlPath = line.split(" ")[1];
					log.debug(urlPath);
					break;
				}
				line = br.readLine();
			}
			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = getBody(urlPath);
			response200Header(dos, body.length, HttpRequestUtils.getContentType(urlPath));
			responseBody(dos, body);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private byte[] getBody(String urlPath) throws IOException {
		if(HttpRequestUtils.isResource(urlPath)){
			return Files.readAllBytes(new File("./webapp"+urlPath).toPath());
		}else{
			if("/".equals(urlPath)){
				return "Hello World".getBytes();
			}
			UrlData urlData = HttpRequestUtils.getUrlData(urlPath);
			if("user".equals(urlData.getModel())){
				if("create".equals(urlData.getMethod())){
					Service.addUser(new User(urlData.getParams()));
					return "user save success".getBytes();
				}else if("list".equals(urlData.getMethod())){
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					oos.writeObject(Service.findAll());
					return bos.toByteArray();
				}
			}
		}

		return "not request".getBytes();
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: "+contentType);
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
