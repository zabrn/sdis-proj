/**
 * @author ojoaofernandes
 * @created March 23th, 2017
 */
package filesystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemManager {

    private final int id;
    private Path filesystem;
    private Path originals;
    private Path backups;
    private Path restored;

    public FileSystemManager(int id) {
        this.id = id;
        this.initialize();
    }

    private boolean initialize() {
        String home = System.getProperty("user.home");

        Path filesystemPath = Paths.get(home, "DBS", "peer" + this.id);
        Path originalsPath = Paths.get(filesystemPath.toString(), "originals");
        Path restoredPath = Paths.get(filesystemPath.toString(), "restored");
        Path backupsPath = Paths.get(filesystemPath.toString(), "backups");

        try {
            Files.createDirectories(originalsPath);
            Files.createDirectories(restoredPath);
            Files.createDirectories(backupsPath);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return false;
        }

        this.filesystem = filesystemPath;
        this.originals = originalsPath;
        this.restored = restoredPath;
        this.backups = backupsPath;

        return true;
    }

    public String getPathToFilesystem() {
        return this.filesystem.toString();
    }

    public String getPathToOriginals() {
        return this.originals.toString();
    }

    public String getPathToBackups() {
        return this.backups.toString();
    }

    public String getPathToRestored() {
        return this.restored.toString();
    }

    public boolean createDir(String dirname) {
        Path newDir = Paths.get(this.backups.toString(), dirname);

        try {
            Files.createDirectories(newDir);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return false;
        }

        return true;
    }

    public File getOriginalFile(String filename) {
        File file = new File(this.getPathToOriginals() + "/" + filename);
        return (file.exists()) ? file : null;
    }

    public boolean deleteOriginalFile(String filename) {
        File file = this.getOriginalFile(filename);
        return file.exists() && file.delete();
    }

    public boolean saveRestoredFile(byte[] buffer, String filename) {
        File newFile = new File(this.getPathToRestored()
                + "/" + filename
        );

        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(buffer, 0, buffer.length);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean saveChunk(Chunk chunk) {
        this.createDir(chunk.getFileId());

        File newFile = new File(this.getPathToBackups()
                + "/"
                + chunk.getFileId()
                + "/"
                + chunk.getNumber()
                + ".chunk"
        );

        try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(chunk.getContent(), 0, chunk.getContent().length);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public Chunk getChunk(String fileId, int chunkNumber) {
        File file = new File(this.getPathToBackups()
                + "/"
                + fileId
                + "/"
                + chunkNumber
                + ".chunk"
        );

        if (!file.exists()) {
            return null;
        }

        byte[] buffer = new byte[(int)file.length()];
        Chunk chunk = new Chunk(chunkNumber, fileId, buffer);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bis.read(buffer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return chunk;
    }

    public boolean deleteChunk(String fileId, int chunkNumber) {
        File file = new File(this.getPathToBackups()
                + "/"
                + fileId
                + "/"
                + chunkNumber
                + ".chunk"
        );

        return file.exists() && file.delete();
    }

    public boolean deleteBackupFile(String fileID) {
        File dir = new File(this.getPathToBackups(), fileID);

        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
            dir.delete();
        }

        return true;
    }

}
