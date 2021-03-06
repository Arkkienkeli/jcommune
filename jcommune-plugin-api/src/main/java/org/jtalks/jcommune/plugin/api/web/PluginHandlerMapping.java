/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.plugin.api.web;

import com.google.common.annotations.VisibleForTesting;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.WebControllerPlugin;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.HandlerMethodSelector;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Custom handler mapping. Needed to map plugin handlers separately from application handlers. It's necessary to allow
 * update handlers without application restart.
 *
 * @author Mikhail Stryzhonok
 */
public class PluginHandlerMapping extends RequestMappingHandlerMapping {

    private static final PluginHandlerMapping INSTANCE = new PluginHandlerMapping();
    private final Map<String, HandlerMethod> pluginHandlerMethods = new HashMap<>();
    private PluginLoader pluginLoader;

    private PluginHandlerMapping() {

    }

    public static PluginHandlerMapping getInstance() {
        return INSTANCE;
    }

    // need to run tests without context
    @Override
    protected boolean isContextRequired() {
        return false;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        if (PluginController.class.isAssignableFrom(method.getDeclaringClass())) {
            registerPluginHandlerMethod((PluginController)handler, method, mapping);
        } else {
            super.registerHandlerMethod(handler, method, mapping);
        }
    }

    /**
     * Registers new plugin handler or updates existence
     *
     * @param handler controller object
     * @param method method to be registered
     * @param mapping information about request
     */
    @VisibleForTesting
    void registerPluginHandlerMethod(PluginController handler, Method method, RequestMappingInfo mapping) {
        handler.setApiPath(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        Set<String> patterns = getMappingPathPatterns(mapping);
        if (patterns.size() != 1) {
            throw new IllegalStateException("Controller method " + method.getName() + " mapped to " + patterns.size()
                    + " urls. Expected 1 url");
        }
        pluginHandlerMethods.put(getUniformUrl(patterns.iterator().next()), createHandlerMethod(handler, method));
    }


    /**
     * Adds handlers from controller to handler mapping
     * Note: class should be annotated with {@link org.springframework.stereotype.Controller} annotation and all
     * handler method should be annotated with {@link org.springframework.web.bind.annotation.RequestMapping} annotation
     *
     * @param controller controller object to map
     */
    public void addController(PluginController controller) {
        INSTANCE.detectHandlerMethods(controller);
        if (controller instanceof ApplicationContextAware) {
            ((ApplicationContextAware) controller).setApplicationContext(getApplicationContext());
        }
    }

    /**
     * Disables handlers from specified controller
     *
     * @param controller controller bean to disable handlers
     */
    public void deactivateController(PluginController controller) {
        List<String> urls = getPluginControllerUrls(controller);
        for (String url : urls) {
            pluginHandlerMethods.remove(url);
        }
    }

    /**
     * Get list of URLs which can be handled by controller
     *
     * @param controller controller object
     * @return list of URLs
     */
    private List<String> getPluginControllerUrls(PluginController controller) {
        List<String> urls = new ArrayList<>();
        final Class controllerType = controller.getClass();
        Set<Method> methods = HandlerMethodSelector.selectMethods(controllerType, new ReflectionUtils.MethodFilter() {
            public boolean matches(Method method) {
                return getMappingForMethod(method, controllerType) != null;
            }
        });

        for (Method method : methods) {
            RequestMappingInfo mapping = getMappingForMethod(method, controllerType);
            //Method have {@link RequestMapping} annotation
            if (mapping != null) {
                Set<String> patterns = getMappingPathPatterns(mapping);
                if (patterns.size() != 1) {
                    throw new IllegalStateException("Controller method " + method.getName() + " mapped to "
                            + patterns.size() + " urls. Expected 1 url");
                }
                urls.add(getUniformUrl(patterns.iterator().next()));
            }
        }

        return urls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        //We should clear map in case if plugin version was changed
        pluginHandlerMethods.clear();
        //We should update Web plugins before resolving handler
        pluginLoader.reloadPlugins(new TypeFilter(WebControllerPlugin.class));
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        HandlerMethod handlerMethod = pluginHandlerMethods.get(getUniformUrl(lookupPath));
        if (handlerMethod != null) {
            return handlerMethod;
        } else {
            return super.getHandlerInternal(request);
        }
    }

    /**
     * Adds "/" to the end of url if it necessary. Needed to support optional "/" at the end
     */
    private String getUniformUrl(String url) {
        if (url.endsWith("/")) {
            return url;
        }
        return url + "/";
    }

    //Needed for tests only
    @VisibleForTesting
    Map<String, HandlerMethod> getPluginHandlerMethods() {
        return pluginHandlerMethods;
    }

    public PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }
}
