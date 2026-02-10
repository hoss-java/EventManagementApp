import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseEMObjectStorage implements EMObjectStorage {
    private Connection connection;

    public DatabaseEMObjectStorage(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addEMObject(EMObject embject) {
        String sql = "INSERT INTO embject (data) VALUES (?)"; // Customize as needed
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, embject.toString()); // Customize serialization
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeEMObject(EMObject embject) {
        // Implement logic to remove an embject from the database
        throw new UnsupportedOperationException("Database removal not implemented yet.");
    }

    @Override
    public List<EMObject> getEMObjects() {
        List<EMObject> embjects = new ArrayList<>();
        String sql = "SELECT * FROM embjects"; // Customize as needed
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EMObject embject = new EMObject(rs.getString("data")); // Customize deserialization
                embjects.add(embject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return embjects;
    }
}
