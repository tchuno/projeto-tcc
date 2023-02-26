package net.gnfe.util.other;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import net.gnfe.util.DummyUtils;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class PDFCreator {

	private final String template;
	private final Map<String, Object> model;
	private HttpServletRequest request;
	private boolean portrait;
	private boolean textoDireto = false;

	public PDFCreator(String template, Map<String, Object> model) {
		this.template = template;
		this.model = model;
	}

	public PDFCreator(String template, boolean textoDireto) {
		this.model = null;
		this.template = template;
		this.textoDireto = textoDireto;
	}
	public PDFCreator(String template, Map<String, Object> model, boolean textoDireto) {
		this.model = model;
		this.template = template;
		this.textoDireto = textoDireto;
	}

	public byte[] toByteArray() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		render(baos);

		return baos.toByteArray();
	}

	public File toFile() {

		try {

			File file = File.createTempFile("topdf", ".pdf");
			DummyUtils.sleep(2500);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			render(bos);

			bos.flush();
			bos.close();

			return file;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected void render(OutputStream os) {

		StringBuilder html = new StringBuilder();

		html.append("\n<html>");
		html.append("\n<head>");
		html.append("\n	<style type='text/css'>");
		html.append("\n		@page {");
		if (isPortrait()) {
			html.append("\n		size: A4 landscape;");
		} else {
			html.append("\n		size: A4 portrait;");
		}
		html.append("\n			margin-right: 0;");
		html.append("\n			margin-left: 0;");
		html.append("\n			margin-top: 0;");
		html.append("\n			margin-bottom: 0;");
		html.append("\n			-fs-flow-top: \"header\";");
		html.append("\n			-fs-flow-bottom: \"footer\";");
		html.append("\n			-fs-flow-left: \"left\";");
		html.append("\n			-fs-flow-right: \"right\";");
		html.append("\n			padding: 0;");
		html.append("\n		}");
		html.append("\n	</style>");
		html.append("\n</head>");
		html.append("\n");

		if(request != null) {
			model.put("context", request.getContextPath());
		}

		if (textoDireto) {
//			String aux = DummyUtils.stringToHTML(template);
//			html.append("<div style='padding: 2em'>").append(aux).append("</div>");

			StringWriter writer = new StringWriter();
			VelocityEngineUtils.mergeString(template,writer, model);
			html.append(writer);

		}
		else {
			StringWriter writer = new StringWriter();
			VelocityEngineUtils.merge(template, writer, model);
			html.append(writer);
		}

		html.append("\n<html>");

		Tidy tidy = new Tidy();
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.setShowWarnings(false);
		tidy.setShowErrors(0);
		tidy.setQuiet(true);

		String htmlStr;
		try {
			htmlStr = new String(html.toString().getBytes(), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Document doc = tidy.parseDOM(new ByteArrayInputStream(htmlStr.getBytes()), null);
		DummyUtils.sleep(1500);

		ITextRenderer renderer = new ITextRenderer();

		SharedContext sharedContext = renderer.getSharedContext();
		sharedContext.setUserAgentCallback(new ImageFileITextUserAgent(renderer.getOutputDevice(), renderer.getSharedContext()));

		renderer.setDocument(doc, null);
		renderer.layout();

		try {
			renderer.createPDF(os);
		}
		catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * classe que trata a recupera��o de imagens que pode ser referencia para um arquivo e n�o para uma url, por exmplo:
	 * 
	 * <img src='C:\pasta\imagem.jpg'/>
	 * 
	 * caso a imagem seja gerada dinamicamente, ela pode ser enviada para um arquivo tempor�rio e usado como no exemplo acima
	 * 
	 * @author Felipe Maschio
	 * @created 12/12/2009
	 */
	private class ImageFileITextUserAgent extends ITextUserAgent {

		public ImageFileITextUserAgent(ITextOutputDevice outputDevice, SharedContext sharedContext) {
			super(outputDevice);

			setSharedContext(sharedContext);
		}

		@Override
		public ImageResource getImageResource(String uri) {

			String uriFile = uri.replace('/', File.separatorChar);
			File imagemFile = new File(uriFile);
			if(imagemFile.exists()) {

				try {
					byte[] file = FileUtils.readFileToByteArray(imagemFile);
					Image image = Image.getInstance(file);
					return new ImageResource(new ITextFSImage(image));
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			if(request != null) {

				try {
					ServletContext servletContext = request.getSession().getServletContext();
					URL resource = servletContext.getResource(uri);

					if(resource == null) {

						String contextPath = request.getContextPath();

						if(uri.startsWith(contextPath)) {
							resource = servletContext.getResource(uri.replaceFirst(contextPath, ""));
						}
					}

					if(resource != null) {

						return super.getImageResource(resource.toString());
					}
				}
				catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			return super.getImageResource(uri);
		}
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean isPortrait() {
		return portrait;
	}

	public void setPortrait(boolean portrait) {
		this.portrait = portrait;
	}
}
