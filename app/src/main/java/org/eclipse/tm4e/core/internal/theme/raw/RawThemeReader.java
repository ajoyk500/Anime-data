
package org.eclipse.tm4e.core.internal.theme.raw;

import java.util.List;
import org.eclipse.tm4e.core.internal.parser.PropertySettable;
import org.eclipse.tm4e.core.internal.parser.TMParser;
import org.eclipse.tm4e.core.internal.parser.TMParser.ObjectFactory;
import org.eclipse.tm4e.core.internal.parser.TMParserJSON;
import org.eclipse.tm4e.core.internal.parser.TMParserPList;
import org.eclipse.tm4e.core.internal.parser.TMParserYAML;
import org.eclipse.tm4e.core.registry.IThemeSource;

public final class RawThemeReader {
	public static final ObjectFactory<RawTheme> OBJECT_FACTORY = new ObjectFactory<>() {
		@Override
		public RawTheme createRoot() {
			return new RawTheme();
		}
		@Override
		public PropertySettable<?> createChild(final TMParser.PropertyPath path, final Class<?> sourceType) {
			return List.class.isAssignableFrom(sourceType)
					? new PropertySettable.ArrayList<>()
					: new RawTheme();
		}
	};
	public static IRawTheme readTheme(final IThemeSource source) throws Exception {
		try (var reader = source.getReader()) {
			return switch (source.getContentType()) {
				case JSON -> TMParserJSON.INSTANCE.parse(reader, OBJECT_FACTORY);
				case YAML -> TMParserYAML.INSTANCE.parse(reader, OBJECT_FACTORY);
				default -> TMParserPList.INSTANCE.parse(reader, OBJECT_FACTORY);
			};
		}
	}
	private RawThemeReader() {
	}
}
