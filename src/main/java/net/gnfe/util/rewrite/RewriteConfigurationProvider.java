package net.gnfe.util.rewrite;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

import com.thoughtworks.xstream.XStream;

@RewriteConfiguration
public class RewriteConfigurationProvider extends HttpConfigurationProvider {

	private final static Map<String, String> viewToPathMap = new HashMap<>();
	private static final Pattern PATTERN_PARAMETRO = Pattern.compile("\\{(.*)\\}");

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public Configuration getConfiguration(ServletContext context) {

		System.out.println("RewriteConfigurationProvider.getConfiguration()");

		try {
			ConfigurationBuilder builder = ConfigurationBuilder.begin();
			InputStream inputStream = context.getResourceAsStream("/WEB-INF/rewrite.xml");

			if (inputStream != null) {

				XStream xStream = new XStream();
				xStream.processAnnotations(RewriteTag.class);
				RewriteTag rewrite = (RewriteTag) xStream.fromXML(inputStream);

				if (rewrite != null) {

					List<JoinTag> joins = rewrite.getJoins();
					for (JoinTag join : joins) {

						String path = join.getPath();
						String view = join.getView();

						Join join2 = Join.path(path).to(view);

						Matcher matcher = PATTERN_PARAMETRO.matcher(path);
						if(matcher.find()) {

							String parameterName = matcher.group(1);

							DefaultParameterStore store = new DefaultParameterStore();
							DefaultParameter value = new DefaultParameter(parameterName);
							value.validatedBy(new Validator<String>() {

								@Override
								public boolean isValid(Rewrite paramRewrite, EvaluationContext paramEvaluationContext, String paramT) {
									return StringUtils.isNotBlank(paramT);
								}
							});
							store.store(value);

							join2.setParameterStore(store);
						}

						builder.addRule(join2);

						viewToPathMap.put(view, path);
					}
				}
			}

			return builder;
		}
		catch(Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public static Map<String, String> getViewToPathMap() {
		return viewToPathMap;
	}
}
