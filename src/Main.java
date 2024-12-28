import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import org.apache.poi.xwpf.usermodel.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingApp().showScreen1());
    }
}

class BookingApp {
    private JFrame frame;
    private String fromTo;
    private String busClass;
    private String date;
    private String selectedSeats = "";
    private String name;
    private String nik;
    private final ArrayList<String> selectedSeatList = new ArrayList<>();
    private int totalPrice = 0;

    private static final String[] DESTINATIONS = {
            "Malang - Tangerang",
            "Malang - Solo",
            "Malang - Surabaya",
            "Malang - Jember"
    };

    private static final int[][] PRICES = {
            {400000, 650000},
            {250000, 400000},
            {50000, 125000},
            {120000, 200000}
    };

    private StringBuilder allBookings = new StringBuilder();
    private final HashMap<String, HashSet<String>> bookedSeatsByDate = new HashMap<>();

    public void showScreen1() {
        frame = new JFrame("Bus Ticket Booking - Screen 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblDestination = new JLabel("Select Destination:");
        JComboBox<String> cmbDestination = new JComboBox<>(DESTINATIONS);

        JLabel lblDate = new JLabel("Select Date:");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(100, 25));

        JLabel lblClass = new JLabel("Select Bus Class:");
        JComboBox<String> cmbClass = new JComboBox<>(new String[]{"Executive", "Sleeper"});

        JLabel lblPrice = new JLabel("Price: ");
        cmbDestination.addActionListener(e -> updatePrice(lblPrice, cmbDestination.getSelectedIndex(), cmbClass.getSelectedIndex()));
        cmbClass.addActionListener(e -> updatePrice(lblPrice, cmbDestination.getSelectedIndex(), cmbClass.getSelectedIndex()));

        JButton btnNext = new JButton("Next");
        btnNext.addActionListener(e -> {
            fromTo = (String) cmbDestination.getSelectedItem();
            date = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
            busClass = (String) cmbClass.getSelectedItem();

            if (validateScreen1(date)) {
                showScreen2();
            } else {
                JOptionPane.showMessageDialog(frame, "Please fill all fields correctly.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblDestination);
        panel.add(cmbDestination);
        panel.add(lblDate);
        panel.add(dateChooser);
        panel.add(lblClass);
        panel.add(cmbClass);
        panel.add(lblPrice);
        panel.add(btnNext);

        frame.add(panel);
        frame.setVisible(true);
    }

    private boolean validateScreen1(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        return true;
    }

    private void updatePrice(JLabel lblPrice, int destinationIndex, int classIndex) {
        lblPrice.setText("Price: " + PRICES[destinationIndex][classIndex]);
    }

    public void showScreen2() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        String[][] seatLayout = busClass.equals("Executive") ?
                new String[][] {
                        {"1A", "1B", "", "1C", "1D"},
                        {"2A", "2B", "", "2C", "2D"},
                        {"3A", "3B", "", "3C", "3D"},
                        {"4A", "4B", "", "4C", "4D"},
                        {"5A", "5B", "", "5C", "5D"},
                        {"6A", "6B", "", "6C", "6D"},
                        {"7A", "7B", "", "7C", "7D"},
                        {"8A", "8B", "", "TOILET"}
                } :
                new String[][] {
                        {"1A", "1B"},
                        {"2A", "2B"},
                        {"3A", "3B"},
                        {"4A", "4B"},
                        {"5A", "5B"},
                        {"6A", "TOILET"}
                };

        HashSet<String> bookedSeats = bookedSeatsByDate.getOrDefault(date, new HashSet<>());

        for (int i = 0; i < seatLayout.length; i++) {
            for (int j = 0; j < seatLayout[i].length; j++) {
                String seat = seatLayout[i][j];
                if (seat.equals("")) {
                    gbc.gridx = j;
                    gbc.gridy = i;
                    panel.add(Box.createGlue(), gbc);
                } else if (seat.equals("TOILET")) {
                    gbc.gridx = j;
                    gbc.gridy = i;
                    JLabel lblToilet = new JLabel("TOILET", SwingConstants.CENTER);
                    lblToilet.setOpaque(true);
                    lblToilet.setBackground(Color.LIGHT_GRAY);
                    panel.add(lblToilet, gbc);
                } else {
                    JButton seatButton = new JButton(seat);
                    if (bookedSeats.contains(seat)) {
                        seatButton.setEnabled(false);
                        seatButton.setBackground(Color.RED);
                    } else {
                        if (selectedSeatList.contains(seat)) {
                            seatButton.setBackground(Color.GRAY);
                        }
                        seatButton.addActionListener(e -> {
                            if (selectedSeatList.contains(seat)) {
                                selectedSeatList.remove(seat);
                                seatButton.setBackground(null);
                            } else {
                                selectedSeatList.add(seat);
                                seatButton.setBackground(Color.GRAY);
                            }
                            updateSelectedSeats();
                            updateTotalPrice();
                        });
                    }
                    gbc.gridx = j;
                    gbc.gridy = i;
                    panel.add(seatButton, gbc);
                }
            }
        }

        JButton btnNext = new JButton("Next");
        gbc.gridx = 0;
        gbc.gridy = seatLayout.length;
        gbc.gridwidth = 5;
        btnNext.addActionListener(e -> {
            if (selectedSeatList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select at least one seat.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } else {
                bookedSeats.addAll(selectedSeatList);
                bookedSeatsByDate.put(date, bookedSeats);
                showScreen3();
            }
        });
        panel.add(btnNext, gbc);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void updateSelectedSeats() {
        selectedSeats = String.join(", ", selectedSeatList);
    }

    private void updateTotalPrice() {
        int seatPrice = busClass.equals("Executive") ? PRICES[getDestinationIndex(fromTo)][0] : PRICES[getDestinationIndex(fromTo)][1];
        totalPrice = selectedSeatList.size() * seatPrice;
    }

    private int getDestinationIndex(String destination) {
        for (int i = 0; i < DESTINATIONS.length; i++) {
            if (DESTINATIONS[i].equals(destination)) {
                return i;
            }
        }
        return -1;
    }

    public void showScreen3() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel("Enter Name:");
        JTextField txtName = new JTextField(20);

        JLabel lblNIK = new JLabel("Enter NIK:");
        JTextField txtNIK = new JTextField(20);

        JButton btnNext = new JButton("Next");
        btnNext.addActionListener(e -> {
            name = txtName.getText();
            nik = txtNIK.getText();
            if (validateScreen3(name, nik)) {
                showScreen4();
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter valid Name and NIK.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblName);
        panel.add(txtName);
        panel.add(lblNIK);
        panel.add(txtNIK);
        panel.add(btnNext);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private boolean validateScreen3(String name, String nik) {
        if (name == null || name.isEmpty() || nik == null || nik.isEmpty()) {
            return false;
        }
        if (!Pattern.matches("\\d+", nik)) {
            return false;
        }
        return true;
    }

    public void showScreen4() {
        frame.getContentPane().removeAll();

        String summary = "Destination: " + fromTo + "\n" +
                "Date: " + date + "\n" +
                "Class: " + busClass + "\n" +
                "Seats: " + selectedSeats + "\n" +
                "Name: " + name + "\n" +
                "NIK: " + nik + "\n" +
                "Total Price: " + totalPrice;

        allBookings.append(summary).append("\n\n");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"Field", "Value"};
        String[][] data = {
                {"Destination", fromTo},
                {"Date", date},
                {"Class", busClass},
                {"Seats", selectedSeats},
                {"Name", name},
                {"NIK", nik},
                {"Total Price", String.valueOf(totalPrice)}
        };
        JTable table = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(table);

        JButton btnSave = new JButton("Save to Word File");
        btnSave.addActionListener(e -> saveAllBookingsToWord());

        JButton btnEdit = new JButton("Edit Booking");
        btnEdit.addActionListener(e -> showEditScreen());

        JButton btnDelete = new JButton("Delete Booking");
        btnDelete.addActionListener(e -> showDeleteScreen());

        JButton btnAddBooking = new JButton("Add Booking");
        btnAddBooking.addActionListener(e -> {
            resetBooking();
            showScreen1();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnAddBooking);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void saveAllBookingsToWord() {
        try (FileOutputStream out = new FileOutputStream("booking_summary.docx")) {
            XWPFDocument document = new XWPFDocument();

            // Add text content
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(allBookings.toString());

            // Add image based on class
            String busClass = "eksekutif"; // Contoh: Anda bisa mengganti nilai ini sesuai input pengguna
            String imagePath;

            // Tentukan gambar berdasarkan kelas bus
            if (busClass.equalsIgnoreCase("eksekutif")) {
                imagePath = "gambar/bus eksekutif.jpg";
            } else if (busClass.equalsIgnoreCase("sleeper")) {
                imagePath = "gambar/bus sleeper.jpg";
            } else {
                imagePath = ""; // Jika tidak ada kelas bus yang cocok
            }

            // Periksa apakah gambar ditemukan
            if (!imagePath.isEmpty() && new File(imagePath).exists()) {
                try (InputStream imageStream = new FileInputStream(imagePath)) {
                    XWPFParagraph imageParagraph = document.createParagraph();
                    XWPFRun imageRun = imageParagraph.createRun();
                    imageRun.addPicture(
                            imageStream,
                            XWPFDocument.PICTURE_TYPE_JPEG,
                            imagePath,
                            Units.toEMU(200), // Width
                            Units.toEMU(150)  // Height
                    );
                } catch (IOException | InvalidFormatException e) {
                    JOptionPane.showMessageDialog(frame, "Error adding image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Image file not found for class: " + busClass, "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Write document to file
            document.write(out);
            JOptionPane.showMessageDialog(frame, "All bookings saved to booking_summary.docx");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving to file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void showDeleteScreen() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel("Enter Name to Delete:");
        JTextField txtName = new JTextField(20);

        JLabel lblNIK = new JLabel("Enter NIK to Delete:");
        JTextField txtNIK = new JTextField(20);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> {
            String inputName = txtName.getText();
            String inputNIK = txtNIK.getText();

            if (inputName.equals(name) && inputNIK.equals(nik)) {
                resetBooking();
                saveDeletedToWord();
                JOptionPane.showMessageDialog(frame, "Booking deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                showScreen1();
            } else {
                JOptionPane.showMessageDialog(frame, "Name and NIK do not match. Cannot delete booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblName);
        panel.add(txtName);
        panel.add(lblNIK);
        panel.add(txtNIK);
        panel.add(btnDelete);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void saveDeletedToWord() {
        try (FileOutputStream out = new FileOutputStream("booking_summary.docx")) {
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Booking has been deleted.");

            document.write(out);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving to file: " + e.getMessage());
        }
    }

    private void showEditScreen() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblDestination = new JLabel("Edit Destination:");
        JComboBox<String> cmbDestination = new JComboBox<>(DESTINATIONS);
        cmbDestination.setSelectedItem(fromTo);

        JLabel lblDate = new JLabel("Edit Date:");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setText(date);

        JLabel lblClass = new JLabel("Edit Bus Class:");
        JComboBox<String> cmbClass = new JComboBox<>(new String[]{"Executive", "Slipper"});
        cmbClass.setSelectedItem(busClass);

        JLabel lblSeats = new JLabel("Edit Seats:");
        JTextArea txtSeats = new JTextArea(selectedSeats);
        txtSeats.setEditable(false);

        JButton btnChooseSeats = new JButton("Choose Seats");
        btnChooseSeats.addActionListener(e -> {
            selectedSeatList.clear();
            busClass = (String) cmbClass.getSelectedItem();
            showScreen2();
        });

        JButton btnSave = new JButton("Save Changes");
        btnSave.addActionListener(e -> {
            fromTo = (String) cmbDestination.getSelectedItem();
            date = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
            busClass = (String) cmbClass.getSelectedItem();
            selectedSeats = String.join(", ", selectedSeatList);
            updateTotalPrice();
            showScreen4();
        });

        panel.add(lblDestination);
        panel.add(cmbDestination);
        panel.add(lblDate);
        panel.add(dateChooser);
        panel.add(lblClass);
        panel.add(cmbClass);
        panel.add(lblSeats);
        panel.add(txtSeats);
        panel.add(btnChooseSeats);
        panel.add(btnSave);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void resetBooking() {
        fromTo = null;
        busClass = null;
        date = null;
        selectedSeats = "";
        name = null;
        nik = null;
        selectedSeatList.clear();
        totalPrice = 0;
    }
}