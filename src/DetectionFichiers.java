import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

public class DetectionFichiers extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton btnBrowse;
    private JButton btnDetect;
    private JFileChooser fileChooser;
    private JPanel jPanel;
    private JLabel labeltitre;

    public DetectionFichiers() {
        super("Détecteur de double fichiers");
        setLayout(new BorderLayout());

        // la creation d'un panel
        jPanel = new JPanel(new BorderLayout());
        jPanel.setBackground(new Color(51, 51, 91));
        jPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        labeltitre = new JLabel("Fichiers Double");
        labeltitre.setForeground(Color.WHITE);
        labeltitre.setFont(new Font("Arial", Font.BOLD, 24));
        jPanel.add(labeltitre, BorderLayout.WEST);

        btnBrowse = new JButton("Parcourir un repertoire");
        btnBrowse.addActionListener(this);
        jPanel.add(btnBrowse, BorderLayout.EAST);

        btnDetect = new JButton("Detecter");
        btnDetect.addActionListener(this);
        jPanel.add(btnDetect, BorderLayout.CENTER);

        add(jPanel, BorderLayout.NORTH);

        // Create text area for displaying results
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Repertiores", "folder"));

        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBrowse) {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                textArea.setText("");
                textArea.append("Repertiore sélectionné: " + fileChooser.getSelectedFile().getAbsolutePath() + "\n");
            }
        } else if (e.getSource() == btnDetect) {
            if (fileChooser.getSelectedFile() != null) {
                File folder = fileChooser.getSelectedFile();
                List<File> files = Arrays.asList(folder.listFiles());
                Map<String, List<File>> fileMap = new HashMap<>();

                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            String hash = getHash(file);
                            if (!fileMap.containsKey(hash)) {
                                fileMap.put(hash, new ArrayList<>());
                            }
                            fileMap.get(hash).add(file);
                        } catch (NoSuchAlgorithmException | IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                textArea.append("\n\nles fichiers double:\n");

                boolean est_double = false;
                for (List<File> fileList : fileMap.values()) {
                    if (fileList.size() > 1) {
                        for (File file : fileList) {
                            textArea.append(file.getAbsolutePath() + "\n");
                        }
                        textArea.append("\n");
                        est_double = true;
                    }
                }

                if (!est_double) {
                    textArea.append("Aucun fichier double dans le dossier sélectionné.\n");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un dossier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        byte[] mdBytes = md.digest();

        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < mdBytes.length; i++) {
            stringBuilder.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        fis.close();

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        new DetectionFichiers();
    }}
