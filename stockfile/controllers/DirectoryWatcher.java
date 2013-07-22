package stockfile.controllers;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import stockfile.models.FileList;
import stockfile.models.StockFile;
import stockfile.security.StockFileSession;

/**
 * Directory watcher is based on the java online example of WatchService and
 * sets up notifications on the directory inbetween synchronization periods. If
 * a file changes, it updates the singleton FileList by incrementing the version
 * number and sets the inSync flag to false.
 *
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class DirectoryWatcher implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> pathsToWatch;
    private boolean trace = true;
    private final String HOME_DIR = StockFileSession.getInstance().getCurrentClient().getFullDir();

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void registerDir(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = pathsToWatch.get(key);
            if (prev == null) {
                //System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    //System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        pathsToWatch.put(key, dir);
    }

    private void registerHome() throws IOException {
        File f = new File(HOME_DIR);

        if (!f.exists()) {
            if (!(new File(HOME_DIR)).mkdirs()) {
                System.err.println("Home directory could not be created.");
            }
        }

        Files.walkFileTree(Paths.get(HOME_DIR), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                registerDir(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                registerDir(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public DirectoryWatcher() throws IOException {

        this.watchService = FileSystems.getDefault().newWatchService();
        this.pathsToWatch = new HashMap<WatchKey, Path>();
        registerHome();

        // enable trace after initial registration
        this.trace = true;
    }

    public void monitorDirectories() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = pathsToWatch.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path filePath = dir.resolve(name);

                StockFile thisFile = new StockFile(filePath.toString(), null);
                String fileKey = thisFile.getRelativePath();

                if (kind == ENTRY_DELETE) {
                    System.out.println(fileKey + " was deleted from the directory.");
                    FileList.getInstance().getManifest().getFile(fileKey).setRemoveMarker(true);
                }

                if (kind == ENTRY_MODIFY) {
                    System.out.println(fileKey + " was modified.");
                    FileList.getInstance().getManifest().getFile(fileKey).incrementVersion();
                }

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if ((kind == ENTRY_CREATE)) {
                    System.out.println(thisFile.getRelativePath() + " was created.");
                    FileList.getInstance().getManifest().insertFile(thisFile.getRelativePath(), thisFile);
                    try {
                        if (Files.isDirectory(filePath, NOFOLLOW_LINKS)) {
                            registerAll(filePath);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            System.out.println(FileList.getInstance().getManifest());

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                pathsToWatch.remove(key);

                // all directories are inaccessible
                if (pathsToWatch.isEmpty()) {
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        monitorDirectories();
    }
}
