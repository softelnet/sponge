/* Generated by camel build tools - do NOT edit this file! */
package org.openksavi.sponge.camel;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.ExtendedPropertyConfigurerGetter;
import org.apache.camel.spi.PropertyConfigurerGetter;
import org.apache.camel.spi.ConfigurerStrategy;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.util.CaseInsensitiveMap;
import org.apache.camel.support.component.PropertyConfigurerSupport;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@SuppressWarnings("unchecked")
public class SpongeComponentConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(CamelContext camelContext, Object obj, String name, Object value, boolean ignoreCase) {
        SpongeComponent target = (SpongeComponent) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "action": target.setAction(property(camelContext, java.lang.String.class, value)); return true;
        case "autowiredenabled":
        case "autowiredEnabled": target.setAutowiredEnabled(property(camelContext, boolean.class, value)); return true;
        case "bridgeerrorhandler":
        case "bridgeErrorHandler": target.setBridgeErrorHandler(property(camelContext, boolean.class, value)); return true;
        case "engine": target.setEngine(property(camelContext, org.openksavi.sponge.engine.SpongeEngine.class, value)); return true;
        case "lazystartproducer":
        case "lazyStartProducer": target.setLazyStartProducer(property(camelContext, boolean.class, value)); return true;
        case "managed": target.setManaged(property(camelContext, java.lang.Boolean.class, value)); return true;
        default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "action": return java.lang.String.class;
        case "autowiredenabled":
        case "autowiredEnabled": return boolean.class;
        case "bridgeerrorhandler":
        case "bridgeErrorHandler": return boolean.class;
        case "engine": return org.openksavi.sponge.engine.SpongeEngine.class;
        case "lazystartproducer":
        case "lazyStartProducer": return boolean.class;
        case "managed": return java.lang.Boolean.class;
        default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        SpongeComponent target = (SpongeComponent) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "action": return target.getAction();
        case "autowiredenabled":
        case "autowiredEnabled": return target.isAutowiredEnabled();
        case "bridgeerrorhandler":
        case "bridgeErrorHandler": return target.isBridgeErrorHandler();
        case "engine": return target.getEngine();
        case "lazystartproducer":
        case "lazyStartProducer": return target.isLazyStartProducer();
        case "managed": return target.getManaged();
        default: return null;
        }
    }
}

