package net.gnfe.util.menu;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import net.gnfe.bin.domain.enumeration.Funcionalidade;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {

	@XStreamAsAttribute private String id;
	@XStreamAsAttribute private String url;
	@XStreamAsAttribute private String labelKey;
	@XStreamAsAttribute private List<MenuItem> menuItens;
	@XStreamAsAttribute private List<SubItem> subItens;

	private Item parent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	public Item getParent() {
		return parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	public List<MenuItem> getMenuItens() {
		return menuItens;
	}

	public void setMenuItens(List<MenuItem> menuItens) {
		this.menuItens = menuItens;
	}

	public List<SubItem> getSubItens() {
		return subItens;
	}

	public void setSubItens(List<SubItem> subItens) {
		this.subItens = subItens;
	}

	public boolean hasParent(Item item) {

		Item parent = getParent();
		while(parent != null) {

			if(parent.equals(item)) {
				return true;
			}

			parent = parent.getParent();
		}

		return false;
	}

	public Funcionalidade getFuncionalidade() {

		String id = getId();
		if(StringUtils.isBlank(id)) {
			return null;
		}

		Funcionalidade funcionalidade = Funcionalidade.valueOf(id);
		return funcionalidade;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, "menuItens", "subItens", "parent");
	}

	@Override
	public String toString() {
		return super.toString() + url;
	}
}
