package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = bufferedReader.readLine();
			if (line == null) {
				return;
			}
//			log.debug("request line : {}", line);
//			while (!line.equals("")) {
//				line = bufferedReader.readLine();
//				log.debug("header : {}", line);
//			}

			String url = HttpRequestUtils.getUrl(line);
			Map<String, String> heders = new HashMap<String, String>();
			while (!line.equals("")) {
				log.debug("header : {}", line);
				line = bufferedReader.readLine();
				String[] headerTokens = line.split(": ");
				if (headerTokens.length == 2) {
					heders.put(headerTokens[0], headerTokens[1]);
				}
			}

			log.debug("Content-Length : {}", heders.get("Content-Length"));

			// GET
//			if (url.startsWith("/create")) {
//				int index = url.indexOf("?");
//				String quersyString = url.substring(index + 1);
//				Map<String, String> params = HttpRequestUtils.parseQueryString(quersyString);
//				User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
//				log.debug("User : {}", user);
//
//				url = "/index.html";
//			}

			// POST
			if (url.startsWith("/create")) {
				String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(heders.get("Content-Length")));
				log.debug("Request Body : {}", requestBody);
				Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
				User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
				log.debug("User : {}", user);

				DataBase.addUser(user);

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos);
			} else if (url.startsWith("/login")) {
				String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(heders.get("Content-Length")));
				log.debug("Request Body : {}", requestBody);
				Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
				log.debug("userId : {}, password : {}", params.get("userId"), params.get("password"));

				User user = DataBase.findUserById(params.get("userId"));
				if (user == null) {
					log.debug("User Not Found!!");
					DataOutputStream dos = new DataOutputStream(out);
					response302Header(dos);
				} else if (user.getPassword().equals(params.get("password"))) {
					log.debug("login success!!");
					DataOutputStream dos = new DataOutputStream(out);
					response302HeaderWithCookie(dos, "logined=true");
				} else {
					log.debug("Password Mismatch");
					DataOutputStream dos = new DataOutputStream(out);
					response302Header(dos);
				}

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos);
			} else if (url.endsWith(".css")) {
				DataOutputStream dos = new DataOutputStream(out);
				byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
				response200HeaderWithCss(dos, body.length);
				responseBody(dos, body);
			} else {
				DataOutputStream dos = new DataOutputStream(out);
				byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
				response200Header(dos, body.length);
				responseBody(dos, body);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302HeaderWithCookie(DataOutputStream dos, String cookie) {
		try {
			dos.writeBytes("HTTP/1.1 302 OK \r\n");
			dos.writeBytes("Location: /index.html\r\n");
			dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 OK \r\n");
			dos.writeBytes("Location: /index.html\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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
