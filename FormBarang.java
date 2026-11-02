
package view;

import dao.BarangDAO;
import model.Barang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class FormBarang extends JFrame {

    private JTextField txtKode;
    private JTextField txtNama;
    private JTextField txtStok;

    private JComboBox<String> cmbKategori;

    private JButton btnSimpan;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnBersihkan;

    private JTable tabel;
    private DefaultTableModel modelTabel;

    private BarangDAO dao = new BarangDAO();

    public FormBarang() {

        setTitle("Aplikasi Inventori Barang Laboratorium");
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buatKomponen();
        tampilkanData();
    }

    private void buatKomponen() {

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Judul
        JLabel judul = new JLabel(
            "DATA INVENTORI BARANG LABORATORIUM",
            JLabel.CENTER
        );

        judul.setFont(new Font("Arial", Font.BOLD, 18));

        panelUtama.add(judul, BorderLayout.NORTH);

        // Panel form
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 10));

        txtKode = new JTextField();
        txtNama = new JTextField();
        txtStok = new JTextField();

        cmbKategori = new JComboBox<>(
            new String[]{
                "Komputer",
                "Jaringan",
                "Alat Ukur"
            }
        );

        panelForm.add(new JLabel("Kode Barang"));
        panelForm.add(txtKode);

        panelForm.add(new JLabel("Nama Barang"));
        panelForm.add(txtNama);

        panelForm.add(new JLabel("Kategori"));
        panelForm.add(cmbKategori);

        panelForm.add(new JLabel("Stok"));
        panelForm.add(txtStok);

        // Tombol
        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnBersihkan = new JButton("Bersihkan");

        JPanel panelTombol = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        panelTombol.add(btnSimpan);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersihkan);

        // Panel atas
        JPanel panelAtas = new JPanel(new BorderLayout(10, 10));

        panelAtas.add(panelForm, BorderLayout.CENTER);
        panelAtas.add(panelTombol, BorderLayout.SOUTH);

        // Tabel
        modelTabel = new DefaultTableModel(
            new Object[]{
                "Kode Barang",
                "Nama Barang",
                "Kategori",
                "Stok"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(modelTabel);

        JScrollPane scroll = new JScrollPane(tabel);

        JPanel panelIsi = new JPanel(new BorderLayout(10, 10));

        panelIsi.add(panelAtas, BorderLayout.NORTH);
        panelIsi.add(scroll, BorderLayout.CENTER);

        panelUtama.add(panelIsi, BorderLayout.CENTER);

        add(panelUtama);

        // Event tombol
        btnSimpan.addActionListener(e -> simpanData());

        btnUbah.addActionListener(e -> ubahData());

        btnHapus.addActionListener(e -> hapusData());

        btnBersihkan.addActionListener(e -> bersihkan());

        // Klik dua kali pada tabel
        tabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int baris = tabel.getSelectedRow();

                    if (baris != -1) {

                        txtKode.setText(
                            modelTabel.getValueAt(baris, 0).toString()
                        );

                        txtNama.setText(
                            modelTabel.getValueAt(baris, 1).toString()
                        );

                        cmbKategori.setSelectedItem(
                            modelTabel.getValueAt(baris, 2).toString()
                        );

                        txtStok.setText(
                            modelTabel.getValueAt(baris, 3).toString()
                        );

                        txtKode.setEditable(false);
                    }
                }
            }
        });
    }

    // Memuat data ke JTable
    private void tampilkanData() {

        modelTabel.setRowCount(0);

        for (Barang b : dao.tampilkan()) {

            modelTabel.addRow(new Object[]{
                b.getKodeBarang(),
                b.getNamaBarang(),
                b.getKategori(),
                b.getStok()
            });
        }
    }

    // Validasi input
    private boolean validasi() {

        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String stok = txtStok.getText().trim();

        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Kode barang tidak boleh kosong!"
            );
            txtKode.requestFocus();
            return false;
        }

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Nama barang tidak boleh kosong!"
            );
            txtNama.requestFocus();
            return false;
        }

        try {

            int jumlah = Integer.parseInt(stok);

            if (jumlah < 0) {
                JOptionPane.showMessageDialog(
                    this, "Stok tidak boleh kurang dari 0!"
                );
                txtStok.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this, "Stok harus berupa angka!"
            );

            txtStok.requestFocus();
            return false;
        }

        return true;
    }

    // Menyimpan data
    private void simpanData() {

        if (!validasi()) return;

        Barang b = new Barang(
            txtKode.getText().trim(),
            txtNama.getText().trim(),
            cmbKategori.getSelectedItem().toString(),
            Integer.parseInt(txtStok.getText().trim())
        );

        if (dao.simpan(b)) {

            JOptionPane.showMessageDialog(
                this, "Data berhasil disimpan!"
            );

            tampilkanData();
            bersihkan();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Data gagal disimpan! Periksa kode barang "
                + "atau koneksi database."
            );
        }
    }

    // Mengubah data
    private void ubahData() {

        if (!validasi()) return;

        Barang b = new Barang(
            txtKode.getText().trim(),
            txtNama.getText().trim(),
            cmbKategori.getSelectedItem().toString(),
            Integer.parseInt(txtStok.getText().trim())
        );

        if (dao.ubah(b)) {

            JOptionPane.showMessageDialog(
                this, "Data berhasil diubah!"
            );

            tampilkanData();
            bersihkan();

        } else {

            JOptionPane.showMessageDialog(
                this, "Data gagal diubah!"
            );
        }
    }

    // Menghapus data
    private void hapusData() {

        String kode = txtKode.getText().trim();

        if (kode.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Pilih barang yang akan dihapus!"
            );

            return;
        }

        int pilihan = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus barang dengan kode "
            + kode + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (pilihan == JOptionPane.YES_OPTION) {

            if (dao.hapus(kode)) {

                JOptionPane.showMessageDialog(
                    this, "Data berhasil dihapus!"
                );

                tampilkanData();
                bersihkan();

            } else {

                JOptionPane.showMessageDialog(
                    this, "Data gagal dihapus!"
                );
            }
        }
    }

    // Membersihkan form
    private void bersihkan() {

        txtKode.setText("");
        txtNama.setText("");
        txtStok.setText("");

        cmbKategori.setSelectedIndex(0);

        txtKode.setEditable(true);

        tabel.clearSelection();

        txtKode.requestFocus();
    }
}
