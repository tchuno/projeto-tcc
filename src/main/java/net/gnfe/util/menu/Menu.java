package net.gnfe.util.menu;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import net.gnfe.util.faces.FacesUtil;
import net.gnfe.util.rewrite.RewriteConfigurationProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XStreamAlias("menu")
public class Menu {

	@XStreamAsAttribute private List<MenuItem> arvoreMenuItens;
	@XStreamAsAttribute private List<Item> todosItens;
	@XStreamAsAttribute private List<Item> todasFuncionalidades;

	public List<MenuItem> getArvoreMenuItens() {
		return arvoreMenuItens;
	}

	public void setArvoreMenuItens(List<MenuItem> menuItens) {
		this.arvoreMenuItens = menuItens;
	}

	public void processaParents() {

		for (Item menuItem : arvoreMenuItens) {

			addParent(menuItem);
		}
	}

	private void addParent(Item menuItem) {

		List<MenuItem> menuItens = menuItem.getMenuItens();
		if(menuItens != null) {
			for (Item menuItem2 : menuItens) {
				menuItem2.setParent(menuItem);
				addParent(menuItem2);
			}
		}

		List<SubItem> associados = menuItem.getSubItens();
		if(associados != null) {
			for (SubItem associado : associados) {
				associado.setParent(menuItem);
				addParent(associado);
			}
		}
	}

	public Item getActiveItem() {

		Map<String, String> viewToPathMap = RewriteConfigurationProvider.getViewToPathMap();

		String viewId = FacesUtil.getViewId();
		String url = viewToPathMap.get(viewId);
		url = url == null ? viewId : url;
		url = url.replaceAll("\\{.*\\}", "");

		Item activeItem = null;

		List<Item> todosItens = getTodosItens();

		for (Item item : todosItens) {

			String url2 = item.getUrl();
			if(url.equals(url2)) {

				activeItem = item;
				return activeItem;
			}
		}

		return activeItem;
	}

	/** MenuItens + SubItens */
	public List<Item> getTodosItens() {

		if(todosItens == null) {

			todosItens = new ArrayList<>();

			for (Item menuItem : arvoreMenuItens) {

				addToTodosItens(menuItem);
			}
		}

		return todosItens;
	}

	private void addToTodosItens(Item menuItem) {

		todosItens.add(menuItem);

		List<MenuItem> menuItens = menuItem.getMenuItens();
		if(menuItens != null) {
			for (Item menuItem2 : menuItens) {
				addToTodosItens(menuItem2);
			}
		}

		List<SubItem> subItens = menuItem.getSubItens();
		if(subItens != null) {
			for (SubItem associado : subItens) {
				addToTodosItens(associado);
			}
		}
	}

	/** Apenas MenuItens */
	public List<Item> getTodasFuncionalidades() {

		if(todasFuncionalidades == null) {

			todasFuncionalidades = new ArrayList<>();

			for (MenuItem menuItem : arvoreMenuItens) {

				addToTodasFuncionalidades(menuItem);
			}
		}

		return todasFuncionalidades;
	}

	private void addToTodasFuncionalidades(Item menuItem) {

		String id = menuItem.getId();
		if(StringUtils.isNotBlank(id)) {
			todasFuncionalidades.add(menuItem);
		}

		List<MenuItem> menuItens = menuItem.getMenuItens();
		if(menuItens != null) {
			for (Item menuItem2 : menuItens) {
				addToTodasFuncionalidades(menuItem2);
			}
		}
	}
}
