import java.util.ArrayList;
import java.util.List;

public class MemoryEmObjectStorage implements EmObjectStorage {
    private List<EMObject> emobjects;

    public MemoryEmObjectStorage() {
        this.emobjects = new ArrayList<>();
    }

    @Override
    public void addEmObject(EMObject emobject) {
        emobjects.add(emobject);
    }

    @Override
    public boolean removeEmObject(EMObject emobject) {
        return emobjects.remove(emobject);
    }

    @Override
    public List<EMObject> getEmObjects() {
        return new ArrayList<>(emobjects);
    }
}
