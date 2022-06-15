package net.gnfe.faces.converter;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.gnfe.util.ddd.Entity;

@FacesConverter(value="entity")
public class EntityConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

		if (value != null) {
			return this.getAttributesFrom(component).get(value);
		}

		return null;
	}

	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		if (value instanceof Entity) {

			Entity entity = (Entity) value;

			this.addAttribute(component, entity);

			Long codigo = entity.getId();
			if (codigo != null) {
				return String.valueOf(codigo);
			}
		}

		return String.valueOf(value);
	}

	protected void addAttribute(UIComponent component, Entity o) {
		String key = o.getId().toString();
		this.getAttributesFrom(component).put(key, o);
	}

	protected Map<String, Object> getAttributesFrom(UIComponent component) {
		return component.getAttributes();
	}
}