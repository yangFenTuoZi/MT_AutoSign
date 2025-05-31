package stand.ronghui;

import java.io.BufferedReader;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class HttpRequest {
	public String cookies = "";
	public String text = "";

	public static HttpRequest get(String url, String[][] params, String[][] headers) {
		return get(buildParams(url, params), headers);
	}

	public static HttpRequest get(String url, String[][] headers) {
		HttpRequest request = new HttpRequest();
		StringBuilder result = new StringBuilder();
		BufferedReader in = null;
		try {
			URL realUrl = URI.create(url).toURL();
			//拼接Header请求信息
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			buildHeader(connection, headers);
			connection.connect();
			//获取cookie
			request.cookies = getCookies(connection);
			//获取网页内容
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line).append("\n");
				}
				request.text = result.toString();
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace(System.out);
			}
		}
		return request;
	}

	public static HttpRequest post(String url, String data, String[][] headers) {
		HttpRequest request = new HttpRequest();
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = URI.create(url).toURL();
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			buildHeader(conn, headers);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
			out.print(data);
			out.flush();
			request.cookies = getCookies(conn);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
				request.text = result.toString();
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace(System.out);
			}
		}
		return request;
	}

	private static void buildHeader(HttpURLConnection connection, String[][] headers) {
        for (String[] header : headers) {
            for (int j = 0; j < header.length; j += 2) {
                connection.setRequestProperty(header[j], header[j + 1]);
            }
        }
	}

	private static String buildParams(String url, String[][] parms) {
		StringBuilder sb = new StringBuilder(url);
		sb.append("?");
        for (String[] parm : parms) {
            for (int j = 0; j < parm.length; j += 2) {
                sb.append(parm[j]).append("=").append(parm[j + 1]);
            }
            sb.append("&");
        }
		return sb.toString();
	}

	private static String getCookies(HttpURLConnection connection) {
		Map<String, List<String>> map = connection.getHeaderFields();
		List<String> cookiesList=map.get("Set-Cookie");
		StringBuilder newcookie= new StringBuilder();
		for (String cook : cookiesList) {
			newcookie.append(cook.split(";")[0]).append("; ");
		}
		if (!newcookie.isEmpty()) {
			return newcookie.toString();
		}
		return "";
	}
}
