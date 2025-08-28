import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
// Main Class
public class TimeTableBuilder {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage());
    }
}

// --------------------------- Theme Class ---------------------------
class Theme {
    public static final Color BACKGROUND = new Color(22, 23, 47);
    public static final Color FOREGROUND = Color.WHITE;
   
    public static Font font(int size, boolean bold) {
    return new Font("Tahoma", bold ? Font.BOLD : Font.PLAIN, size);
    }

    public static JButton roundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFont(font(14, true));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        return btn;
    }

    public static JTextField roundedField(int cols) {
        JTextField field = new JTextField(cols) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        field.setFont(font(13, false));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return field;
    }
}

// --------------------------- DataManager Class ---------------------------
class DataManager {
    private static final String CLASSROOM_FILE = "classrooms.csv";
    private static final String INSTRUCTOR_FILE = "instructors.csv";
    private static final String COURSE_FILE = "courses.csv";

    // ------------------ Classroom Methods ------------------
    public static void saveClassroom(String roomNo, int capacity, int computers, String type, boolean hasMic, boolean hasProjector) {
        // Check if the classroom already exists
        java.util.List<String[]> classrooms = loadClassrooms();
        for (String[] entry : classrooms) {
            if (entry[0].trim().equalsIgnoreCase(roomNo.trim())) {
                JOptionPane.showMessageDialog(null, "Classroom with room number '" + roomNo + "' already exists.");
                return;
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLASSROOM_FILE, true))) {
            String record = String.format("%s,%d,%d,%s,%b,%b", roomNo, capacity, computers, type, hasMic, hasProjector);
            bw.write(record);
            bw.newLine();
            JOptionPane.showMessageDialog(null, "Classroom saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving classroom: " + e.getMessage());
        }
    }

    public static java.util.List<String[]> loadClassrooms() {
        java.util.List<String[]> classrooms = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CLASSROOM_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                classrooms.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("No classroom data found. Creating new file.");
        }
        return classrooms;
    }

    public static void updateClassroom(String roomNo, int capacity, int computers, String type, boolean hasMic, boolean hasProjector) {
        java.util.List<String[]> classrooms = loadClassrooms();
        boolean updated = false;
        for (int i = 0; i < classrooms.size(); i++) {
            String[] data = classrooms.get(i);
            if (data[0].trim().equalsIgnoreCase(roomNo.trim())) {
                classrooms.set(i, new String[] {
                    roomNo,
                    String.valueOf(capacity),
                    String.valueOf(computers),
                    type,
                    String.valueOf(hasMic),
                    String.valueOf(hasProjector)
                });
                updated = true;
                break;
            }
        }
        if (!updated) {
            JOptionPane.showMessageDialog(null, "Classroom not found.");
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLASSROOM_FILE))) {
            for (String[] entry : classrooms) {
                bw.write(String.join(",", entry));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error updating classroom: " + e.getMessage());
        }
    }

    public static void deleteClassroom(String roomNo) {
        java.util.List<String[]> classrooms = loadClassrooms();
        boolean deleted = false;
        Iterator<String[]> iterator = classrooms.iterator();
        while (iterator.hasNext()) {
            String[] data = iterator.next();
            if (data[0].trim().equalsIgnoreCase(roomNo.trim())) {
                iterator.remove();
                deleted = true;
                break;
            }
        }
        if (!deleted) {
            JOptionPane.showMessageDialog(null, "Classroom not found: " + roomNo);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLASSROOM_FILE))) {
            for (String[] entry : classrooms) {
                bw.write(String.join(",", entry));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null, "Classroom deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error deleting classroom: " + e.getMessage());
        }
    }

    // ------------------ Instructor Methods ------------------
    public static void saveInstructor(String name, String id, String courses, String availability) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INSTRUCTOR_FILE, true))) {
            String record = String.format("%s,%s,%s,%s", name, id, courses, availability);
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving instructor: " + e.getMessage());
        }
    }

    public static java.util.List<String[]> loadInstructors() {
        java.util.List<String[]> instructors = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INSTRUCTOR_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                instructors.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("No instructor data found. Creating new file.");
        }
        return instructors;
    }

    public static void updateInstructor(String name, String id, String courses, String availability) {
        java.util.List<String[]> instructors = loadInstructors();
        boolean updated = false;
        for (int i = 0; i < instructors.size(); i++) {
            String[] entry = instructors.get(i);
            if (entry[1].trim().equalsIgnoreCase(id.trim())) { // ID is unique
                instructors.set(i, new String[]{name, id, courses, availability});
                updated = true;
                break;
            }
        }
        if (!updated) {
            JOptionPane.showMessageDialog(null, "Instructor not found with ID: " + id);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INSTRUCTOR_FILE))) {
            for (String[] entry : instructors) {
                bw.write(String.join(",", entry));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null, "Instructor updated successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error updating instructor: " + e.getMessage());
        }
    }

    public static void deleteInstructor(String id) {
        java.util.List<String[]> instructors = loadInstructors();
        boolean deleted = false;
        Iterator<String[]> iterator = instructors.iterator();
        while (iterator.hasNext()) {
            String[] entry = iterator.next();
            if (entry[1].trim().equalsIgnoreCase(id.trim())) { // ID is unique
                iterator.remove();
                deleted = true;
                break;
            }
        }
        if (!deleted) {
            JOptionPane.showMessageDialog(null, "Instructor not found with ID: " + id);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INSTRUCTOR_FILE))) {
            for (String[] entry : instructors) {
                bw.write(String.join(",", entry));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null, "Instructor deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error deleting instructor: " + e.getMessage());
        }
    }

    // ------------------ Course Methods ------------------
    public static void saveCourse(String id, String name, int lectureHours, int labHours, String instructor) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COURSE_FILE, true))) {
            String record = String.format("%s,%s,%d,%d,%s", id, name, lectureHours, labHours, instructor);
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving course: " + e.getMessage());
        }
    }

    public static java.util.List<String[]> loadCourses() {
        java.util.List<String[]> courses = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(COURSE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Ensure that each course row has exactly 5 fields.
                if (parts.length < 5) {
                    String[] fixed = new String[5];
                    for (int i = 0; i < 5; i++) {
                        fixed[i] = i < parts.length ? parts[i] : "";
                    }
                    courses.add(fixed);
                } else {
                    courses.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("No course data found. Creating new file.");
        }
        return courses;
    }

    public static void updateCourse(String id, String name, int lectureHours, int labHours, String instructor) {
        java.util.List<String[]> courses = loadCourses();
        boolean updated = false;
        for (int i = 0; i < courses.size(); i++) {
            String[] entry = courses.get(i);
            if (entry[0].trim().equalsIgnoreCase(id.trim())) { // ID unique
                courses.set(i, new String[]{id, name, String.valueOf(lectureHours), String.valueOf(labHours), instructor});
                updated = true;
                break;
            }
        }
        if (!updated) {
            JOptionPane.showMessageDialog(null, "Course not found with ID: " + id);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COURSE_FILE))) {
            for (String[] entry : courses) {
                bw.write(String.join(",", entry));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null, "Course updated successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error updating course: " + e.getMessage());
        }
    }

    public static void deleteCourse(String id) {
        java.util.List<String[]> courses = loadCourses();
        boolean deleted = false;
        Iterator<String[]> iterator = courses.iterator();
        while (iterator.hasNext()) {
            String[] entry = iterator.next();
            if (entry[0].trim().equalsIgnoreCase(id.trim())) { // ID unique
                iterator.remove();
                deleted = true;
                break;
            }
        }
        if (!deleted) {
            JOptionPane.showMessageDialog(null, "Course not found with ID: " + id);
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COURSE_FILE))) {
            for (String[] entry : courses) {
                bw.write(String.join(",", entry));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null, "Course deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error deleting course: " + e.getMessage());
        }
    }

    // ------------------ Timetable Methods ------------------
    public static void saveTimetable(Map<String, Map<String, String>> timetable, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Map<String, String>> entry : timetable.entrySet()) {
                String key = entry.getKey();
                Map<String, String> data = entry.getValue();
                String line = key + "," + data.get("classroom") + "," + data.get("instructor") + "," + data.get("course");
                if (data.containsKey("type")) {
                    line += "," + data.get("type");
                }
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving timetable: " + e.getMessage());
        }
    }

    public static Map<String, Map<String, String>> loadTimetable(String filename) {
        Map<String, Map<String, String>> timetable = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String key = parts[0];
                    Map<String, String> slotData = new HashMap<>();
                    slotData.put("classroom", parts[1]);
                    slotData.put("instructor", parts[2]);
                    slotData.put("course", parts[3]);
                    if (parts.length >= 5) {
                        slotData.put("type", parts[4]);
                    }
                    timetable.put(key, slotData);
                }
            }
        } catch (IOException e) {
            System.out.println("No timetable data found. Creating new timetable.");
        }
        return timetable;
    }
}

// --------------------------- Home Page ---------------------------
class HomePage extends JFrame {
    public HomePage() {
        setTitle("Home Page");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Time Table Builder");
        title.setFont(Theme.font(28, true));
        title.setForeground(Theme.FOREGROUND);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Automate and Manage Schedules");
        sub.setFont(Theme.font(16, false));
        sub.setForeground(Theme.FOREGROUND);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startBtn = Theme.roundedButton("Get Started");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(sub);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startBtn);
        add(Box.createVerticalGlue());

        setVisible(true);
    }
}

// --------------------------- Main Menu ---------------------------
class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Main Menu");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new GridLayout(6, 1, 12, 12));

        String[] buttons = {
            "Add Classroom", "Add Instructor", "Add Course",
            "Manual Timetable Display", "Auto Scheduler", "Exit"
        };

        for (String text : buttons) {
            JButton btn = Theme.roundedButton(text);
            btn.setBackground(Theme.BACKGROUND);
            btn.setForeground(Theme.FOREGROUND);

            switch (text) {
                case "Add Classroom":
                    btn.addActionListener(e -> new AddClassroomForm());
                    break;
                case "Add Instructor":
                    btn.addActionListener(e -> new AddInstructorForm());
                    break;
                case "Add Course":
                    btn.addActionListener(e -> new AddCourseForm());
                    break;
                case "Manual Timetable Display":
                    btn.addActionListener(e -> new TimetableDisplay());
                    break;
                case "Auto Scheduler":
                    btn.addActionListener(e -> new AutoScheduler());
                    break;
                case "Exit":
                    btn.addActionListener(e -> System.exit(0));
                    break;
            }
            add(btn);
        }

        setVisible(true);
    }
}

// --------------------------- Add Classroom Form ---------------------------
class AddClassroomForm extends JFrame {
    private JTextField roomField, capField, compField;
    private JComboBox<String> roomType;
    private JCheckBox mic, proj;

    public AddClassroomForm() {
        setTitle("Add Classroom Details");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new java.awt.BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel roomLbl = new JLabel("Room No");
        JLabel capLbl = new JLabel("Capacity");
        JLabel compLbl = new JLabel("No of Computers");
        JLabel typeLbl = new JLabel("Type of Room");
        JLabel avLbl = new JLabel("AV Requirements");

        roomField = Theme.roundedField(15);
        capField = Theme.roundedField(15);
        compField = Theme.roundedField(15);
        roomType = new JComboBox<>(new String[]{"Lecture", "Lab"});

        mic = new JCheckBox("Mic");
        proj = new JCheckBox("Projector");
        for (JLabel lbl : new JLabel[]{roomLbl, capLbl, compLbl, typeLbl, avLbl}) {
            lbl.setForeground(Theme.FOREGROUND);
        }
        mic.setForeground(Theme.FOREGROUND);
        proj.setForeground(Theme.FOREGROUND);
        mic.setOpaque(false);
        proj.setOpaque(false);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(roomLbl, gbc);
        gbc.gridx = 1; formPanel.add(roomField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(capLbl, gbc);
        gbc.gridx = 1; formPanel.add(capField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(compLbl, gbc);
        gbc.gridx = 1; formPanel.add(compField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(typeLbl, gbc);
        gbc.gridx = 1; formPanel.add(roomType, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(avLbl, gbc);
        JPanel avPanel = new JPanel();
        avPanel.setOpaque(false);
        avPanel.add(mic);
        avPanel.add(proj);
        gbc.gridx = 1; formPanel.add(avPanel, gbc);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JPanel viewPanel = new JPanel();
        viewPanel.setOpaque(false);
        JButton viewBtn = Theme.roundedButton("View All");
        viewBtn.addActionListener(e -> new ClassroomListView());
        viewPanel.add(viewBtn);

        JPanel bottomRow = new JPanel();
        bottomRow.setOpaque(false);
        JButton editBtn = Theme.roundedButton("Edit");
        JButton saveBtn = Theme.roundedButton("Save");
        JButton delBtn = Theme.roundedButton("Del");

        saveBtn.addActionListener(e -> {
            try {
                String roomNo = roomField.getText();
                int capacity = Integer.parseInt(capField.getText());
                int computers = Integer.parseInt(compField.getText());
                String type = (String) roomType.getSelectedItem();

                DataManager.saveClassroom(roomNo, capacity, computers, type,
                        mic.isSelected(), proj.isSelected());
                roomField.setText("");
                capField.setText("");
                compField.setText("");
                mic.setSelected(false);
                proj.setSelected(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for capacity and computers");
            }
        });

        editBtn.addActionListener(e -> {
            try {
                String roomNo = roomField.getText();
                int capacity = Integer.parseInt(capField.getText());
                int computers = Integer.parseInt(compField.getText());
                String type = (String) roomType.getSelectedItem();

                DataManager.updateClassroom(roomNo, capacity, computers, type,
                        mic.isSelected(), proj.isSelected());
                roomField.setText("");
                capField.setText("");
                compField.setText("");
                mic.setSelected(false);
                proj.setSelected(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for capacity and computers");
            }
        });

        delBtn.addActionListener(e -> {
            String roomNo = roomField.getText().trim();
            if (roomNo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a room number to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null,
                 "Are you sure you want to delete classroom " + roomNo + "?", "Confirm Delete",
                 JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataManager.deleteClassroom(roomNo);
                roomField.setText("");
                capField.setText("");
                compField.setText("");
                mic.setSelected(false);
                proj.setSelected(false);
            }
        });

        bottomRow.add(editBtn);
        bottomRow.add(saveBtn);
        bottomRow.add(delBtn);

        buttonsPanel.add(viewPanel);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(bottomRow);

        add(formPanel, java.awt.BorderLayout.CENTER);
        add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        setVisible(true);
    }
}

// --------------------------- Classroom List View ---------------------------
class ClassroomListView extends JFrame {
    public ClassroomListView() {
        setTitle("All Classrooms");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);

        java.util.List<String[]> classrooms = DataManager.loadClassrooms();

        String[] columns = {"Room No", "Capacity", "Computers", "Type", "Mic", "Projector"};
        Object[][] data = new Object[classrooms.size()][6];

        for (int i = 0; i < classrooms.size(); i++) {
            String[] classroom = classrooms.get(i);
            data[i] = classroom;
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setVisible(true);
    }
}

// --------------------------- Add Instructor Form ---------------------------
class AddInstructorForm extends JFrame {
    private JTextField nameField, idField, courseField, availField;

    public AddInstructorForm() {
        setTitle("Add Instructor Details");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new java.awt.BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLbl = new JLabel("Name");
        JLabel idLbl = new JLabel("ID");
        JLabel courseLbl = new JLabel("Assigned Courses");
        JLabel availLbl = new JLabel("Availability");

        nameField = Theme.roundedField(15);
        idField = Theme.roundedField(15);
        courseField = Theme.roundedField(15);
        availField = Theme.roundedField(15);

        for (JLabel lbl : new JLabel[]{nameLbl, idLbl, courseLbl, availLbl}) {
            lbl.setForeground(Theme.FOREGROUND);
        }

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(nameLbl, gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(idLbl, gbc);
        gbc.gridx = 1; formPanel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(courseLbl, gbc);
        gbc.gridx = 1; formPanel.add(courseField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(availLbl, gbc);
        gbc.gridx = 1; formPanel.add(availField, gbc);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JPanel viewPanel = new JPanel();
        viewPanel.setOpaque(false);
        JButton viewBtn = Theme.roundedButton("View All");
        viewBtn.addActionListener(e -> new InstructorListView());
        viewPanel.add(viewBtn);

        JPanel bottomRow = new JPanel();
        bottomRow.setOpaque(false);
        JButton editBtn = Theme.roundedButton("Edit");
        JButton saveBtn = Theme.roundedButton("Save");
        JButton delBtn = Theme.roundedButton("Del");

        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            String courses = courseField.getText();
            String availability = availField.getText();

            if (name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and ID are required fields");
                return;
            }
            DataManager.saveInstructor(name, id, courses, availability);
            nameField.setText("");
            idField.setText("");
            courseField.setText("");
            availField.setText("");
        });

        editBtn.addActionListener(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            String courses = courseField.getText();
            String availability = availField.getText();

            if (name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and ID are required fields");
                return;
            }
            DataManager.updateInstructor(name, id, courses, availability);
            nameField.setText("");
            idField.setText("");
            courseField.setText("");
            availField.setText("");
        });

        delBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the instructor ID to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the instructor with ID: " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataManager.deleteInstructor(id);
                nameField.setText("");
                idField.setText("");
                courseField.setText("");
                availField.setText("");
            }
        });

        bottomRow.add(editBtn);
        bottomRow.add(saveBtn);
        bottomRow.add(delBtn);

        buttonsPanel.add(viewPanel);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(bottomRow);

        add(formPanel, java.awt.BorderLayout.CENTER);
        add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        setVisible(true);
    }
}

// --------------------------- Instructor List View ---------------------------
class InstructorListView extends JFrame {
    public InstructorListView() {
        setTitle("All Instructors");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);

        java.util.List<String[]> instructors = DataManager.loadInstructors();

        String[] columns = {"Name", "ID", "Assigned Courses", "Availability"};
        Object[][] data = new Object[instructors.size()][4];

        for (int i = 0; i < instructors.size(); i++) {
            String[] instructor = instructors.get(i);
            data[i] = instructor;
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setVisible(true);
    }
}

// --------------------------- Add Course Form ---------------------------
class AddCourseForm extends JFrame {
    private JTextField idField, nameField, lecField, labField, instField;
    private JComboBox<String> lecDaysBox;

    public AddCourseForm() {
        setTitle("Add Course Details");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new java.awt.BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel idLbl = new JLabel("Course ID");
        JLabel nameLbl = new JLabel("Course Name");
        JLabel lecDaysLbl = new JLabel("Lecture Days");
        JLabel lecLbl = new JLabel("Lecture Hours");
        JLabel labLbl = new JLabel("Lab Hours");
        JLabel instLbl = new JLabel("Instructor Details");

        idField = Theme.roundedField(15);
        nameField = Theme.roundedField(15);
        lecDaysBox = new JComboBox<>(new String[]{"M, W, F", "T, Th, Sat"});
        lecDaysBox.setPreferredSize(new Dimension(150, 25));
        lecField = Theme.roundedField(15);
        labField = Theme.roundedField(15);
        instField = Theme.roundedField(15);

        for (JLabel lbl : new JLabel[]{idLbl, nameLbl, lecDaysLbl, lecLbl, labLbl, instLbl}) {
            lbl.setForeground(Theme.FOREGROUND);
        }

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(idLbl, gbc);
        gbc.gridx = 1; formPanel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(nameLbl, gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lecDaysLbl, gbc);
        gbc.gridx = 1; formPanel.add(lecDaysBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lecLbl, gbc);
        gbc.gridx = 1; formPanel.add(lecField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(labLbl, gbc);
        gbc.gridx = 1; formPanel.add(labField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(instLbl, gbc);
        gbc.gridx = 1; formPanel.add(instField, gbc);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JPanel viewPanel = new JPanel();
        viewPanel.setOpaque(false);
        JButton viewBtn = Theme.roundedButton("View All");
        viewBtn.addActionListener(e -> new CourseListView());
        viewPanel.add(viewBtn);

        JPanel bottomRow = new JPanel();
        bottomRow.setOpaque(false);
        JButton editBtn = Theme.roundedButton("Edit");
        JButton saveBtn = Theme.roundedButton("Save");
        JButton delBtn = Theme.roundedButton("Del");

        saveBtn.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                // lecDays is currently not stored, but can be used if desired
                int lectureHours = Integer.parseInt(lecField.getText());
                int labHours = Integer.parseInt(labField.getText());
                String instructor = instField.getText();

                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Course ID and Name are required fields");
                    return;
                }

                DataManager.saveCourse(id, name, lectureHours, labHours, instructor);
                idField.setText("");
                nameField.setText("");
                lecField.setText("");
                labField.setText("");
                instField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for lecture and lab hours");
            }
        });

        editBtn.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                int lectureHours = Integer.parseInt(lecField.getText());
                int labHours = Integer.parseInt(labField.getText());
                String instructor = instField.getText();

                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Course ID and Name are required fields");
                    return;
                }

                DataManager.updateCourse(id, name, lectureHours, labHours, instructor);
                idField.setText("");
                nameField.setText("");
                lecField.setText("");
                labField.setText("");
                instField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for lecture and lab hours");
            }
        });

        delBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the course ID to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the course with ID: " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataManager.deleteCourse(id);
                idField.setText("");
                nameField.setText("");
                lecField.setText("");
                labField.setText("");
                instField.setText("");
            }
        });

        bottomRow.add(editBtn);
        bottomRow.add(saveBtn);
        bottomRow.add(delBtn);

        buttonsPanel.add(viewPanel);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(bottomRow);

        add(formPanel, java.awt.BorderLayout.CENTER);
        add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        setVisible(true);
    }
}

// --------------------------- Course List View ---------------------------
class CourseListView extends JFrame {
    public CourseListView() {
        setTitle("All Courses");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);

        java.util.List<String[]> courses = DataManager.loadCourses();
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No course data found. Please add some courses first.");
            return;
        }
        String[] columns = {"Course ID", "Course Name", "Lecture Hours", "Lab Hours", "Instructor"};
        Object[][] data = new Object[courses.size()][5];
        for (int i = 0; i < courses.size(); i++) {
            String[] course = courses.get(i);
            // Ensure we always have exactly 5 fields.
            if (course.length < 5) {
                String[] fixed = new String[5];
                for (int j = 0; j < 5; j++) {
                    fixed[j] = j < course.length ? course[j] : "";
                }
                data[i] = fixed;
            } else {
                data[i] = course;
            }
        }
        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        setVisible(true);
    }
}

// --------------------------- Manual Timetable Display ---------------------------
class TimetableDisplay extends JFrame {
    private Map<String, Map<String, String>> timetable = new HashMap<>();
    private JComboBox<String> instBox, courseBox, classBox, slotBox, dayBox;

    public TimetableDisplay() {
        setTitle("Timetable Display");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new java.awt.BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel instLbl = new JLabel("Select Instructor");
        JLabel courseLbl = new JLabel("Select Course");
        JLabel classLbl = new JLabel("Select Classroom");
        JLabel slotLbl = new JLabel("Select Time Slot");
        JLabel dayLbl = new JLabel("Select Day");

        instBox = new JComboBox<>();
        courseBox = new JComboBox<>();
        classBox = new JComboBox<>();
        slotBox = new JComboBox<>(new String[]{"8-9", "9-10", "10-11", "11-12", "2-3", "3-4", "4-5"});
        dayBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});

        Dimension comboSize = new Dimension(180, 30);
        @SuppressWarnings("unchecked")
        JComboBox<String>[] boxes = new JComboBox[]{instBox, courseBox, classBox, slotBox, dayBox};
        for (JComboBox<String> box : boxes) {
            box.setPreferredSize(comboSize);
        }
        for (JLabel lbl : new JLabel[]{instLbl, courseLbl, classLbl, slotLbl, dayLbl}) {
            lbl.setForeground(Theme.FOREGROUND);
        }

        loadInstructors();
        loadCourses();
        loadClassrooms();
        loadTimetableFromFile();

        // Auto-fill instructor from selected course.
        courseBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) courseBox.getSelectedItem();
                if (selected != null) {
                    // Assuming format "CourseID - CourseName"
                    String courseID = selected.split(" - ")[0];
                    for (String[] crs : DataManager.loadCourses()) {
                        if (crs[0].equals(courseID)) {
                            // crs[4] is the instructor.
                            instBox.setSelectedItem(crs[4]);
                            break;
                        }
                    }
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(instLbl, gbc);
        gbc.gridx = 1; formPanel.add(instBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(courseLbl, gbc);
        gbc.gridx = 1; formPanel.add(courseBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(classLbl, gbc);
        gbc.gridx = 1; formPanel.add(classBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(dayLbl, gbc);
        gbc.gridx = 1; formPanel.add(dayBox, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(slotLbl, gbc);
        gbc.gridx = 1; formPanel.add(slotBox, gbc);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JPanel top = new JPanel();
        top.setOpaque(false);
        JButton checkBtn = Theme.roundedButton("Check Conflict");
        checkBtn.addActionListener(e -> {
            String day = (String) dayBox.getSelectedItem();
            String slot = (String) slotBox.getSelectedItem();
            String classroom = (String) classBox.getSelectedItem();
            String instructor = (String) instBox.getSelectedItem();
            if (checkConflict(day, slot, classroom, instructor)) {
                JOptionPane.showMessageDialog(this, "Conflict detected! This slot is already occupied.");
            } else {
                JOptionPane.showMessageDialog(this, "No conflicts found. You can schedule this slot.");
            }
        });
        top.add(checkBtn);

        JButton viewBtn = Theme.roundedButton("View Timetable");
        viewBtn.addActionListener(e -> new TimetableGridView(timetable));
        top.add(viewBtn);

        JPanel bottomRow = new JPanel();
        bottomRow.setOpaque(false);
        JButton saveBtn = Theme.roundedButton("Save");
        saveBtn.addActionListener(e -> {
            String day = (String) dayBox.getSelectedItem();
            String slot = (String) slotBox.getSelectedItem();
            String classroom = (String) classBox.getSelectedItem();
            String instructor = (String) instBox.getSelectedItem();
            String course = (String) courseBox.getSelectedItem();
            if (!checkConflict(day, slot, classroom, instructor)) {
                saveScheduleEntry(day, slot, classroom, instructor, course);
                JOptionPane.showMessageDialog(this, "Schedule entry saved!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot save due to conflict!");
            }
        });
        bottomRow.add(saveBtn);

        buttonsPanel.add(top);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(bottomRow);

        add(formPanel, java.awt.BorderLayout.CENTER);
        add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        setVisible(true);
    }

    private boolean checkConflict(String day, String slot, String classroom, String instructor) {
        String key = day + "-" + slot;
        if (timetable.containsKey(key)) {
            Map<String, String> slotData = timetable.get(key);
            if (slotData.containsKey("classroom") && slotData.get("classroom").equals(classroom)) {
                return true;
            }
            if (slotData.containsKey("instructor") && slotData.get("instructor").equals(instructor)) {
                return true;
            }
        }
        return false;
    }

    private void saveScheduleEntry(String day, String slot, String classroom, String instructor, String course) {
        String key = day + "-" + slot;
        Map<String, String> slotData = new HashMap<>();
        slotData.put("classroom", classroom);
        slotData.put("instructor", instructor);
        slotData.put("course", course);
        timetable.put(key, slotData);
        DataManager.saveTimetable(timetable, "timetable.ttb");
    }

    private void loadTimetableFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("timetable.ttb"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String key = parts[0];
                    Map<String, String> slotData = new HashMap<>();
                    slotData.put("classroom", parts[1]);
                    slotData.put("instructor", parts[2]);
                    slotData.put("course", parts[3]);
                    timetable.put(key, slotData);
                }
            }
        } catch (IOException e) {
            System.out.println("No timetable data found. Creating new timetable.");
        }
    }

    private void loadInstructors() {
        java.util.List<String[]> instructors = DataManager.loadInstructors();
        for (String[] instructor : instructors) {
            instBox.addItem(instructor[0]); // assuming first column is name
        }
    }

    private void loadCourses() {
        java.util.List<String[]> courses = DataManager.loadCourses();
        for (String[] course : courses) {
            courseBox.addItem(course[0] + " - " + course[1]);
        }
    }

    private void loadClassrooms() {
        java.util.List<String[]> classrooms = DataManager.loadClassrooms();
        for (String[] classroom : classrooms) {
            classBox.addItem(classroom[0]);
        }
    }
}

// --------------------------- Timetable Grid View ---------------------------
class TimetableGridView extends JFrame {
    public TimetableGridView(Map<String, Map<String, String>> timetable) {
        setTitle("Timetable Grid View");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] slots = {"8-9", "9-10", "10-11", "11-12", "2-3", "3-4", "4-5"};

        JPanel gridPanel = new JPanel(new GridLayout(days.length + 1, slots.length + 1));
        gridPanel.setBackground(Theme.BACKGROUND);

        gridPanel.add(new JLabel("")); // corner cell
        for (String slot : slots) {
            JLabel lbl = new JLabel(slot, SwingConstants.CENTER);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            gridPanel.add(lbl);
        }
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            gridPanel.add(dayLabel);
            for (String slot : slots) {
                String key = day + "-" + slot;
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                cell.setForeground(Color.WHITE);
                if (timetable.containsKey(key)) {
                    Map<String, String> data = timetable.get(key);
                    String courseInfo = data.get("course");
                    String roomInfo = data.get("classroom");
                    String instInfo = data.get("instructor");
                    cell.setText("<html><center>" + courseInfo + "<br>Room: " + roomInfo + "<br>Inst: " + instInfo + "</center></html>");
                }
                gridPanel.add(cell);
            }
        }
        add(new JScrollPane(gridPanel));
        setVisible(true);
    }
}

// --------------------------- Auto Scheduler ---------------------------
// --------------------------- Auto Scheduler (Fixed) ---------------------------
class AutoScheduler extends JFrame {
    private java.util.List<Map<String, Map<String, String>>> suggestions = new java.util.ArrayList<>();
    private int currentSuggestion = 0;
    private JPanel gridPanel;
    private java.util.List<String[]> courses;
    private java.util.List<String[]> instructors;
    private java.util.List<String[]> classrooms;

    public AutoScheduler() {
        setTitle("Auto Scheduler");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        courses = DataManager.loadCourses();
        instructors = DataManager.loadInstructors();
        classrooms = DataManager.loadClassrooms();

        gridPanel = new JPanel(new GridLayout(7, 8));
        gridPanel.setBackground(Theme.BACKGROUND);

        // Generate initial suggestion and show it.
        generateSuggestion();
        displayCurrentSuggestion();

        JPanel nav = new JPanel();
        nav.setOpaque(false);

        JButton prevBtn = Theme.roundedButton("<");
        prevBtn.addActionListener(e -> {
            if (currentSuggestion > 0) {
                currentSuggestion--;
                displayCurrentSuggestion();
            }
        });

        JButton saveBtn = Theme.roundedButton("Save");
        saveBtn.addActionListener(e -> {
            if (!suggestions.isEmpty()) {
                saveSuggestionToFile(suggestions.get(currentSuggestion));
                JOptionPane.showMessageDialog(this, "Timetable saved successfully!");
            }
        });

        JButton nextBtn = Theme.roundedButton(">");
        nextBtn.addActionListener(e -> {
            if (currentSuggestion < suggestions.size() - 1) {
                currentSuggestion++;
                displayCurrentSuggestion();
            } else {
                generateSuggestion();
                currentSuggestion = suggestions.size() - 1;
                displayCurrentSuggestion();
            }
        });

        nav.add(prevBtn);
        nav.add(saveBtn);
        nav.add(nextBtn);

        add(gridPanel, BorderLayout.CENTER);
        add(nav, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void generateSuggestion() {
        Map<String, Map<String, String>> suggestion = new HashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] slots = {"8-9", "9-10", "10-11", "11-12", "2-3", "3-4", "4-5"};

        // Build maps to track scheduled slots.
        Map<String, java.util.Set<String>> instructorSlots = new HashMap<>();
        Map<String, java.util.Set<String>> classroomSlots = new HashMap<>();
        Map<String, Integer> courseLectureCount = new HashMap<>();
        Map<String, Integer> courseLabCount = new HashMap<>();
        Map<String, java.util.List<String>> courseDays = new HashMap<>();

        // Populate instructorSlots using the instructor's name (assumed to be in column 0).
        for (String[] instructor : instructors) {
            if (instructor.length > 0) {
                instructorSlots.put(instructor[0].trim(), new HashSet<>());
            }
        }
        // Populate classroomSlots.
        for (String[] classroom : classrooms) {
            if (classroom.length > 0) {
                classroomSlots.put(classroom[0].trim(), new HashSet<>());
            }
        }
        // Populate course lecture and lab counts.
        for (String[] course : courses) {
            if (course.length >= 4) { // Ensure you have room for course id, name, lecture, lab hours
                String courseId = course[0].trim();
                int lectureHours = 0;
                int labHours = 0;
                try {
                    lectureHours = Integer.parseInt(course[2].trim());
                    labHours = Integer.parseInt(course[3].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing hours for course " + courseId + ". Skipping course.");
                    continue;
                }
                courseLectureCount.put(courseId, lectureHours);
                courseLabCount.put(courseId, labHours);
                courseDays.put(courseId, new java.util.ArrayList<>());
            }
        }

        // Assign lectures ensuring a day gap.
        for (String[] course : courses) {
            if (course.length < 5) {
                // Skip courses that do not contain instructor info.
                System.out.println("Course " + (course.length > 0 ? course[0] : "") + 
                                   " missing instructor details. Skipping.");
                continue;
            }
            String courseId = course[0].trim();
            String courseName = course[1].trim();
            String instructorName = course[4].trim();
            int lectureHours = courseLectureCount.getOrDefault(courseId, 0);
            if (lectureHours > 0) {
                if (!instructorSlots.containsKey(instructorName)) {
                    System.out.println("Instructor " + instructorName + " for course " + courseId +
                                       " not found. Skipping lecture assignment.");
                    continue;
                }
                java.util.List<Integer> availableDays = new java.util.ArrayList<>();
                for (int i = 0; i < days.length; i++) {
                    availableDays.add(i);
                }
                java.util.Collections.shuffle(availableDays);
                int assignedLectures = 0;
                while (assignedLectures < lectureHours && !availableDays.isEmpty()) {
                    int dayIndex = availableDays.remove(0);
                    String day = days[dayIndex];
                    // Check for adjacent day conflict.
                    if (hasAdjacentDay(courseDays.get(courseId), day)) {
                        continue;
                    }
                    java.util.List<String> preferredSlots = java.util.Arrays.asList(slots[0], slots[1], slots[2], slots[3]);
                    java.util.Collections.shuffle(preferredSlots);
                    boolean slotAssigned = false;
                    for (String slot : preferredSlots) {
                        String timeKey = day + "-" + slot;
                        String selectedClassroom = null;
                        // Look for a classroom of type "Lecture" that is available.
                        for (String[] classroom : classrooms) {
                            if (classroom.length < 4)
                                continue;
                            String roomNo = classroom[0].trim();
                            String roomType = classroom[3].trim();
                            if (roomType.equalsIgnoreCase("Lecture") && !classroomSlots.get(roomNo).contains(timeKey)) {
                                selectedClassroom = roomNo;
                                break;
                            }
                        }
                        if (selectedClassroom != null && !instructorSlots.get(instructorName).contains(timeKey)) {
                            Map<String, String> slotData = new HashMap<>();
                            slotData.put("classroom", selectedClassroom);
                            slotData.put("instructor", instructorName);
                            slotData.put("course", courseId + " - " + courseName);
                            slotData.put("type", "Lecture");
                            suggestion.put(timeKey, slotData);
                            classroomSlots.get(selectedClassroom).add(timeKey);
                            instructorSlots.get(instructorName).add(timeKey);
                            courseDays.get(courseId).add(day);
                            assignedLectures++;
                            slotAssigned = true;
                            break; // Move to next lecture hour.
                        }
                    }
                    // If no slot is assigned for this day, continue with the next available day.
                }
            }
        }

        // Assign labs (preferably in afternoon slots).
        for (String[] course : courses) {
            if (course.length < 5)
                continue;
            String courseId = course[0].trim();
            String courseName = course[1].trim();
            String instructorName = course[4].trim();
            int labHours = courseLabCount.getOrDefault(courseId, 0);
            if (labHours > 0) {
                if (!instructorSlots.containsKey(instructorName)) {
                    System.out.println("Instructor " + instructorName + " for course " + courseId +
                                       " not found. Skipping lab assignment.");
                    continue;
                }
                for (int i = 0; i < days.length && labHours > 0; i++) {
                    String day = days[i];
                    java.util.List<String> preferredSlots = java.util.Arrays.asList(slots[4], slots[5], slots[6]);
                    java.util.Collections.shuffle(preferredSlots);
                    for (String slot : preferredSlots) {
                        String timeKey = day + "-" + slot;
                        String selectedClassroom = null;
                        for (String[] classroom : classrooms) {
                            if (classroom.length < 4)
                                continue;
                            String roomNo = classroom[0].trim();
                            String roomType = classroom[3].trim();
                            if (roomType.equalsIgnoreCase("Lab") && !classroomSlots.get(roomNo).contains(timeKey)) {
                                selectedClassroom = roomNo;
                                break;
                            }
                        }
                        if (selectedClassroom != null && !instructorSlots.get(instructorName).contains(timeKey)) {
                            Map<String, String> slotData = new HashMap<>();
                            slotData.put("classroom", selectedClassroom);
                            slotData.put("instructor", instructorName);
                            slotData.put("course", courseId + " - " + courseName);
                            slotData.put("type", "Lab");
                            suggestion.put(timeKey, slotData);
                            classroomSlots.get(selectedClassroom).add(timeKey);
                            instructorSlots.get(instructorName).add(timeKey);
                            labHours--;
                            break; // Proceed to assign the next lab hour.
                        }
                    }
                }
            }
        }
        suggestions.add(suggestion);
    }

    private boolean hasAdjacentDay(java.util.List<String> assignedDays, String newDay) {
        int newDayIndex = getDayIndex(newDay);
        for (String day : assignedDays) {
            int dayIndex = getDayIndex(day);
            if (Math.abs(dayIndex - newDayIndex) == 1) {
                return true;
            }
        }
        return false;
    }

    private int getDayIndex(String day) {
        switch (day) {
            case "Monday": return 0;
            case "Tuesday": return 1;
            case "Wednesday": return 2;
            case "Thursday": return 3;
            case "Friday": return 4;
            case "Saturday": return 5;
            default: return -1;
        }
    }

    private void displayCurrentSuggestion() {
        gridPanel.removeAll();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] slots = {"8-9", "9-10", "10-11", "11-12", "2-3", "3-4", "4-5"};

        gridPanel.add(new JLabel("")); // Empty top left corner.
        for (String slot : slots) {
            JLabel lbl = new JLabel(slot, SwingConstants.CENTER);
            lbl.setForeground(Color.WHITE);
            gridPanel.add(lbl);
        }

        Map<String, Map<String, String>> currentSuggestionData = suggestions.get(currentSuggestion);
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            gridPanel.add(dayLabel);
            for (String slot : slots) {
                String timeKey = day + "-" + slot;
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                cell.setForeground(Color.WHITE);
                if (currentSuggestionData.containsKey(timeKey)) {
                    Map<String, String> slotData = currentSuggestionData.get(timeKey);
                    String courseInfo = slotData.get("course");
                    String type = slotData.get("type");
                    String roomInfo = slotData.get("classroom");
                    cell.setText("<html><center>" + courseInfo + "<br>" + type + "<br>Room: " + roomInfo + "</center></html>");
                }
                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
        setTitle("Auto Scheduler - Suggestion " + (currentSuggestion + 1) + " of " + suggestions.size());
    }

    private void saveSuggestionToFile(Map<String, Map<String, String>> suggestion) {
        try (FileWriter fw = new FileWriter("auto_timetable.ttb")) {
            for (Map.Entry<String, Map<String, String>> entry : suggestion.entrySet()) {
                String key = entry.getKey();
                Map<String, String> data = entry.getValue();
                String line = key + "," + data.get("classroom") + "," +
                              data.get("instructor") + "," + data.get("course") +
                              "," + data.get("type");
                fw.write(line + "\n");
            }
            JOptionPane.showMessageDialog(this, "Timetable saved successfully to auto_timetable.ttb");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving timetable: " + e.getMessage());
        }
    }
}
