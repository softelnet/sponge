import java.util.Map

import java.util.concurrent.atomic.AtomicBoolean

import javax.inject.Inject

import org.apache.camel.CamelContext

import org.openksavi.sponge.core.util.Utils
import org.openksavi.sponge.engine.Engine

public class CamelService {

    @Inject
    private Engine engine;

    @Inject
    private CamelContext camelContext;

    public void stopSourceRoutes() {
        engine.getOperations().getVariable("rssSources").each { source, url ->
            println("Stopping " + source)
            camelContext.stopRoute(source);
        }

        engine.getOperations().setVariable("stoppedSources", new AtomicBoolean(true));
    }
}

beans {
    xmlns([context:'http://www.springframework.org/schema/context'])

    camelService(CamelService)
}