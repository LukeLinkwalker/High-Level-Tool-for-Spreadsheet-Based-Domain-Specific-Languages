package com.github.lukelinkwalker.orchestrator.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class LanguageLoader {
	public LanguageLoader() {
		Path langDirectory = Paths.get(Paths.get("").toAbsolutePath().toString(), "/languages");
		if(!Files.exists(langDirectory)) {
			langDirectory.toFile().mkdir();
		}
	}
}