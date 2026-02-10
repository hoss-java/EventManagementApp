import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileEMObjectStorage implements EMObjectStorage {
    private File file;

    public FileEMObjectStorage(File file) {
        this.file = file;
    }

    @Override
    public void addEMObject(EMObject emobject) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(emobject.toString()); // Customize serialization as needed
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeEMObject(EMObject emobject) {
        // Implement logic to remove an emobject from the file
        // This typically involves reading the file, modifying the list, and writing back
        throw new UnsupportedOperationException("File removal not implemented yet.");
    }

    @Override
    public List<EMObject> getEMObjects() {
        List<EMObject> emobjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Deserialize line into EMObject, customize accordingly
                EMObject emobject = new EMObject(line); // Assume EMObject has a constructor that takes a string
                emobjects.add(emobject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emobjects;
    }
}
