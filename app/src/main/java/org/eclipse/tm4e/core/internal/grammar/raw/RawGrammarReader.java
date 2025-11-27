
package org.eclipse.tm4e.core.internal.grammar.raw;

import java.util.List;
import org.eclipse.tm4e.core.internal.parser.PropertySettable;
import org.eclipse.tm4e.core.internal.parser.TMParser;
import org.eclipse.tm4e.core.internal.parser.TMParser.ObjectFactory;
import org.eclipse.tm4e.core.internal.parser.TMParserJSON;
import org.eclipse.tm4e.core.internal.parser.TMParserPList;
import org.eclipse.tm4e.core.internal.parser.TMParserYAML;
import org.eclipse.tm4e.core.registry.IGrammarSource;

public final class RawGrammarReader {
	public static final ObjectFactory<RawGrammar> OBJECT_FACTORY = new ObjectFactory<>() {
		@Override
		public RawGrammar createRoot() {
			return new RawGrammar();
		}
		@Override
		public PropertySettable<?> createChild(final TMParser.PropertyPath path, final Class<?> sourceType) {
			return switch (path.last().toString()) {
				case RawRule.REPOSITORY -> new RawRepository();
				case RawRule.BEGIN_CAPTURES, RawRule.CAPTURES, RawRule.END_CAPTURES, RawRule.WHILE_CAPTURES -> new RawCaptures();
				default -> List.class.isAssignableFrom(sourceType)
						? new PropertySettable.ArrayList<>()
						: new RawRule();
			};
		}
	};
	public static RawGrammar readGrammar(final IGrammarSource source) throws Exception {
		try (var reader = source.getReader()) {
			return switch (source.getContentType()) {
				case JSON -> TMParserJSON.INSTANCE.parse(reader, OBJECT_FACTORY);
				case YAML -> TMParserYAML.INSTANCE.parse(reader, OBJECT_FACTORY);
				default -> TMParserPList.INSTANCE.parse(reader, OBJECT_FACTORY);
			};
		}
	}
	private RawGrammarReader() {
	}
}
