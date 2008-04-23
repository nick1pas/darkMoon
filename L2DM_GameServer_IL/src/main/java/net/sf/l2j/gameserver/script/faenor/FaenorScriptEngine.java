/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.script.faenor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.script.Parser;
import net.sf.l2j.gameserver.script.ParserNotCreatedException;
import net.sf.l2j.gameserver.script.ScriptDocument;
import net.sf.l2j.gameserver.script.ScriptEngine;
import net.sf.l2j.gameserver.script.ScriptPackage;
import net.sf.l2j.gameserver.services.PathFindingService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * @author Luis Arias
 *
 */
public class FaenorScriptEngine extends ScriptEngine
{
    private static Log _log = LogFactory.getLog(GameServer.class.getName());
  //L2EMU_EDIT
    public static String PACKAGE_DIRECTORY = PathFindingService.SCRIPT_ENGINE_PATH;
  //L2EMU_EDIT
    private static FaenorScriptEngine _instance;

    private LinkedList<ScriptDocument> _scripts;

    public static FaenorScriptEngine getInstance()
    {
        if (_instance == null)
        {
        	//L2EMU_ADD
        	_log.info("GameServer: Initializing Script Event Engine.");
        	//L2EMU_ADD
            _instance = new FaenorScriptEngine();
        }

        return _instance;
    }

    private FaenorScriptEngine()
    {
        _scripts = new LinkedList<ScriptDocument>();
        loadPackages();
        parsePackages();

    }

    public void reloadPackages()
    {
        _scripts.clear();
        _scripts = new LinkedList<ScriptDocument>();
        parsePackages();
    }

    private void loadPackages()
    {
        File packDirectory = new File(Config.DATAPACK_ROOT, PACKAGE_DIRECTORY);

        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file)
            {
                return file.getName().endsWith(".zip");
            }
        };

        File[] files = packDirectory.listFiles(fileFilter);
        if (files == null) return;
        ZipFile zipPack;

        for (File element : files) {
            try
            {
                zipPack = new ZipFile(element);
            }
            catch (ZipException e)
            {
                _log.error(e.getMessage(),e);
                continue;
            }
            catch (IOException e)
            {
                _log.error(e.getMessage(),e);
                continue;
            }

            ScriptPackage module = new ScriptPackage(zipPack);

            List<ScriptDocument> scripts = module.getScriptFiles(); 
            for (ScriptDocument script : scripts)
            {
                _scripts.add(script);
            }

        }
    }

    public void parsePackages()
    {
        for (ScriptDocument script : _scripts)
        {
            parseScript(script);
        }
    }

    public void parseScript(ScriptDocument script)
    {
        if (_log.isDebugEnabled()) _log.debug("Parsing Script: " + script.getName());

        Node node = script.getDocument().getFirstChild();
        String parserClass = "faenor.Faenor" + node.getNodeName() + "Parser";

        Parser parser = null;
        try
        {
            parser = createParser(parserClass);
        }
        catch (ParserNotCreatedException e)
        {
            _log.warn("ERROR: No parser registered for Script: " + parserClass,e);
        }

        if (parser == null)
        {
            _log.warn("Unknown Script Type: " + script.getName());
            return;
        }

        try
        {
            parser.parseScript(node);
            if (_log.isDebugEnabled())_log.debug(script.getName() + "Script Successfully Parsed.");
        }
        catch (Exception e)
        {
            _log.warn("Script Parsing Failed.",e);
        }
    }

    @Override
    public String toString()
    {
        if (_scripts.isEmpty()) return "No Packages Loaded.";

        String out = "Script Packages currently loaded:\n";

        for (ScriptDocument script : _scripts)
        {
            out += script;
        }
        return out;
    }
}
