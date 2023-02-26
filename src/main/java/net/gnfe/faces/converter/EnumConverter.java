package net.gnfe.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Map;

@FacesConverter(value="enumConverter")
public class EnumConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

		if (value != null) {
			return this.getAttributesFrom(component).get(value);
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		if (value instanceof Enum) {

			Enum e = (Enum) value;

			this.addAttribute(component, e);

			String enumName = e.name();
			if (enumName != null) {
				return String.valueOf(enumName);
			}
		}

		return String.valueOf(value);
	}

	@SuppressWarnings("rawtypes")
	protected void addAttribute(UIComponent component, Enum o) {
		String key = o.name();
		this.getAttributesFrom(component).put(key, o);
	}

	protected Map<String, Object> getAttributesFrom(UIComponent component) {
		return component.getAttributes();
	}
}