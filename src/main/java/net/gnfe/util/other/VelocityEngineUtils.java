package net.gnfe.util.other;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

public class VelocityEngineUtils {

	public static void merge(String path, Writer writer, Map<String, Object> model) {

		VelocityEngine engine = new VelocityEngine();
		VelocityContext context = new VelocityContext();

		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		if (model != null) {

			Set<String> keySet = model.keySet();
			for (String key : keySet) {

				Object value = model.get(key);
				context.put(key, value);
			}
		}

		Template template = engine.getTemplate(path, "UTF-8");
		template.merge(context, writer);
	}


	public static void mergeString(String template, Writer writer, Map<String, Object> model) {
		VelocityContext context = new VelocityContext();
		if (model != null) {
			Set<String> keySet = model.keySet();
			for (String key : keySet) {
				Object value = model.get(key);
				context.put(key, value);
			}
		}
		Velocity.evaluate(context, writer, "", template);
	}


}