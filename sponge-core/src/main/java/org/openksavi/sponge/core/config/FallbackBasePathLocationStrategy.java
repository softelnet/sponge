package org.openksavi.sponge.core.config;

import java.net.URL;

import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;

public class FallbackBasePathLocationStrategy implements FileLocationStrategy {

    private FileLocationStrategy baseStrategy;

    private String fallbackBasePath;

    private URL locatedUrl;

    public FallbackBasePathLocationStrategy(FileLocationStrategy baseStrategy, String fallbackBasePath) {
        this.baseStrategy = baseStrategy;
        this.fallbackBasePath = fallbackBasePath;
    }

    @Override
    public URL locate(FileSystem fileSystem, FileLocator locator) {
        locatedUrl = baseStrategy.locate(fileSystem, locator);
        if (locatedUrl != null) {
            return locatedUrl;
        }

        locatedUrl = fallbackBasePath != null ? FileLocatorUtils.DEFAULT_LOCATION_STRATEGY.locate(fileSystem,
                FileLocatorUtils.fileLocator(locator).basePath(fallbackBasePath).create()) : null;

        return locatedUrl;
    }

    public URL getLocatedUrl() {
        return locatedUrl;
    }
}
