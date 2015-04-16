package network;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import logger.VCSLogger;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class SimpleWebServer extends NanoHTTPD {
	/**
	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
	 */
	private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
		{
			put("css", "text/css");
			put("htm", "text/html");
			put("html", "text/html");
			put("xml", "text/xml");
			put("java", "text/x-java-source, text/java");
			put("txt", "text/plain");
			put("asc", "text/plain");
			put("gif", "image/gif");
			put("jpg", "image/jpeg");
			put("jpeg", "image/jpeg");
			put("png", "image/png");
			put("mp3", "audio/mpeg");
			put("m3u", "audio/mpeg-url");
			put("mp4", "video/mp4");
			put("ogv", "video/ogg");
			put("flv", "video/x-flv");
			put("mov", "video/quicktime");
			put("swf", "application/x-shockwave-flash");
			put("js", "application/javascript");
			put("pdf", "application/pdf");
			put("doc", "application/msword");
			put("ogg", "application/x-ogg");
			put("zip", "application/octet-stream");
			put("exe", "application/octet-stream");
			put("class", "application/octet-stream");
		}
	};
	private NetworkOps ops;

	public SimpleWebServer(String host, int port, NetworkOps ops) {
		super(host, port);
		this.ops = ops;
	}

	@Override
	public Response serve(String uri, Method method,
			Map<String, String> header, Map<String, String> parms,
			Map<String, String> files) {

		String msg = "Hello";
		if (uri.contains(".vcs")) {
			uri = uri.substring(1, uri.length());
			String request = parms.get("REQUEST");
			VCSLogger.infoLogToCmd("SERVER RECV." + request);
			//Check the ?REQUEST parameter for value: CLONE
			if (request != null) {
				if (parms.get("REQUEST").equals("CLONE")) {
					System.out.println("URI = " + uri); // Example: URI = abcd.vcs 
					File repoRoot = ops.CloneRepository(uri); //Operation in NetworkOps
					//File f = new File("/home/shubham/Downloads/");
					//return serveFile("/views.py", header, f);
					return serveFile("", header, repoRoot);
				}
			}
		}
		return new NanoHTTPD.Response(msg);
	}

	Response serveFile(String uri, Map<String, String> header, File homeDir) {
		File f = homeDir;
		// System.out.println(homeDir+uri);
		Response res = null;
		if (f.exists()) {
			VCSLogger.infoLogToCmd("FLE EXISTS!!");
			//System.out.println("\n\nexists\n\n");
			try {
				if (res == null) {
					// Get MIME type from file name extension, if possible
					String mime = null;
					int dot = f.getCanonicalPath().lastIndexOf('.');
					if (dot >= 0) {
						mime = MIME_TYPES.get(f.getCanonicalPath()
								.substring(dot + 1).toLowerCase());
					}
					if (mime == null) {
						// mime = NanoHTTPD.MIME_DEFAULT_BINARY;
					}

					// Calculate etag
					String etag = Integer.toHexString((f.getAbsolutePath()
							+ f.lastModified() + "" + f.length()).hashCode());

					// Support (simple) skipping:
					long startFrom = 0;
					long endAt = -1;
					String range = header.get("range");
					if (range != null) {
						if (range.startsWith("bytes=")) {
							range = range.substring("bytes=".length());
							int minus = range.indexOf('-');
							try {
								if (minus > 0) {
									startFrom = Long.parseLong(range.substring(
											0, minus));
									endAt = Long.parseLong(range
											.substring(minus + 1));
								}
							} catch (NumberFormatException ignored) {
							}
						}
					}

					// Change return code and add Content-Range header when
					// skipping is requested
					long fileLen = f.length();
					// System.out.println(fileLen);
					if (range != null && startFrom >= 0) {

						// System.out.println("First");
						if (startFrom >= fileLen) {
							res = new Response(
									Response.Status.RANGE_NOT_SATISFIABLE,
									NanoHTTPD.MIME_PLAINTEXT, "");
							res.addHeader("Content-Range", "bytes 0-0/"
									+ fileLen);
							res.addHeader("ETag", etag);
						} else {
							if (endAt < 0) {
								endAt = fileLen - 1;
							}
							long newLen = endAt - startFrom + 1;
							if (newLen < 0) {
								newLen = 0;
							}

							final long dataLen = newLen;
							FileInputStream fis = new FileInputStream(f) {
								@Override
								public int available() throws IOException {
									return (int) dataLen;
								}
							};
							fis.skip(startFrom);

							res = new Response(Response.Status.PARTIAL_CONTENT,
									mime, fis);
							res.addHeader("Content-Length", "" + dataLen);
							res.addHeader("Content-Range", "bytes " + startFrom
									+ "-" + endAt + "/" + fileLen);
							res.addHeader("ETag", etag);
						}
					} else {
						if (etag.equals(header.get("if-none-match"))) {
							// System.out.println("Second");
							res = new Response(Response.Status.NOT_MODIFIED,
									mime, "");
						} else {
							// System.out.println("Third");
							res = new Response(Response.Status.OK, mime,
									new FileInputStream(f));
							res.addHeader("Content-Length", "" + fileLen);
							res.addHeader("ETag", etag);
						}
					}
				}
			} catch (IOException ioe) {
				res = new Response(Response.Status.FORBIDDEN,
						NanoHTTPD.MIME_PLAINTEXT,
						"FORBIDDEN: Reading file failed.");
			}

			res.addHeader("Accept-Ranges", "bytes");
		}
		return res;
	}
}
