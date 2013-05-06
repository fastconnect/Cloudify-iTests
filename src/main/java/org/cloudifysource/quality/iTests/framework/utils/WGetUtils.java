package org.cloudifysource.quality.iTests.framework.utils;

import java.io.IOException;

import iTests.framework.utils.ScriptUtils;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * @author Sagi Bernstein
 *
 */
public class WGetUtils {


	public static void wget(String url) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException{  
		ScriptUtils.runGroovy("src/test/utils/wget.groovy", "wget", url);
	}

	public static void wget(String url, String destFolder) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException{  
		ScriptUtils.runGroovy("src/test/utils/wget.groovy", "wget", url, destFolder);
	}

	public static void wget(String url, String destFolder, String destFileName) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException{  
		ScriptUtils.runGroovy("src/test/utils/wget.groovy", "wget", url, destFolder, destFileName);
	}
	
	
	
}