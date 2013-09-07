package org.freecode.gmusic.plugin;

/**
 * Created with IntelliJ IDEA.
 * User: shivam
 * Date: 9/6/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class RubyPluginEngine extends PluginEngine {

    private final ScriptEngine engine;
    private List<MusicPlugin> plugins;

    public RubyPluginEngine() {
        engine = manager.getEngineByName("jruby");
        plugins = new LinkedList<MusicPlugin>();
        if (engine == null) {
            throw new UnsupportedOperationException("JRuby dependency not satisfied");
        }
    }

    public void loadScript(final URI uri) {
        try {
            final URL url = uri.toURL();
            InputStream in = url.openStream();
            Reader reader = new InputStreamReader(in);
            Object o = engine.eval(reader);
            if (o instanceof MusicPlugin) {
                plugins.add((MusicPlugin) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}