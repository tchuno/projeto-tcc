package net.gnfe.util.rewrite;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("join")
public class JoinTag {

	@XStreamAsAttribute private String path;
	@XStreamAsAttribute private String view;

	public String getPath() {
		return path;
	}

	public String getView() {
		return view;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setView(String view) {
		this.view = view;
	}
}
