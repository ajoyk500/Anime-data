
package org.eclipse.tm4e.core.internal.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public final class TMParserYAML extends TMParserJSON {
	public static final TMParserYAML INSTANCE = new TMParserYAML();
	private static final LoadSettings LOAD_SETTINGS = LoadSettings.builder()
			.setDefaultList(ArrayList::new)
			.setDefaultMap(HashMap::new)
			.setDefaultSet(HashSet::new)
			.build();
	@Override
	@SuppressWarnings({ "null", "unchecked" })
	protected Map<String, Object> loadRaw(final Reader source) {
		return (Map<String, Object>) new Load(LOAD_SETTINGS).loadFromReader(source);
	}
}
