package org.openksavi.sponge.core.kb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseScriptProvider;

public class KnowledgeBaseScriptProviderMapping {

    private Map<Class<? extends KnowledgeBaseScript>,
            BiFunction<SpongeEngine, ? extends KnowledgeBaseScript, ? extends KnowledgeBaseScriptProvider<?>>> map =
                    Collections.synchronizedMap(new HashMap<>());

    private SpongeEngine engine;

    public KnowledgeBaseScriptProviderMapping(SpongeEngine engine) {
        this.engine = engine;

        map.put(FileKnowledgeBaseScript.class, (eng, script) -> new FileKnowledgeBaseScriptProvider(eng, (FileKnowledgeBaseScript) script));
        map.put(StringKnowledgeBaseScript.class,
                (eng, script) -> new StringKnowledgeBaseScriptProvider(eng, (StringKnowledgeBaseScript) script));
    }

    @SuppressWarnings("unchecked")
    public <T extends KnowledgeBaseScript> KnowledgeBaseScriptProvider<T> getProvider(T script) {
        BiFunction<SpongeEngine, T, ? extends KnowledgeBaseScriptProvider<T>> function =
                (BiFunction<SpongeEngine, T, ? extends KnowledgeBaseScriptProvider<T>>) map.get(script.getClass());
        if (function == null) {
            throw new SpongeException("Unsupported knowledge base script type: " + script.getClass());
        }

        return function.apply(engine, script);
    }
}
