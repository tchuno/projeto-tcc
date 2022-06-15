package net.gnfe.util.rewrite;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.List;

@XStreamAlias("rewrite")
public class RewriteTag {

	@XStreamAsAttribute private List<JoinTag> joins;

	public List<JoinTag> getJoins() {
		return joins;
	}

	public void setJoins(List<JoinTag> joins) {
		this.joins = joins;
	}
}
