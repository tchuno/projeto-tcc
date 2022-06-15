package net.gnfe.util.rest.jackson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateDeserializer extends JsonDeserializer<Date> {

	private enum DateFormat {

		DATE_TIME0("dd/MM/yyyy HH:mm:ss"),
		ISO_8601("yyyy-MM-dd'T'HH:mm:ssz"),
		DATE_TIME1("dd/MM/yyyy HH:mm"),
		DATE_TIME2("yyyy-MM-dd HH:mm:ss"),
		DATE_TIME3("dd/MM/yyyy HH:mm:SSS");

		private String pattern;

		DateFormat(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}
	}

	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		String text = parser.getText();
		if (StringUtils.isBlank(text)) {
			return null;
		}
		else {

			Exception exception = null;
			DateFormat[] values = DateFormat.values();
			for (int i = 0; i < values.length; i++) {

				DateFormat df = values[i];
				SimpleDateFormat sdf = new SimpleDateFormat(df.getPattern());
				try {
					Date parse = sdf.parse(text);
					return parse;
				}
				catch (ParseException e) {
					exception = e;
				}
			}

			if(exception != null) {
				exception.printStackTrace();
			}

			throw new IOException("data em formato inválido: " + text);
		}
	}
}