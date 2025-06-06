package com.mast.readup.services;

import com.mast.readup.services.SfidaDAO;
import com.mast.readup.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SfidaDAOImpl implements SfidaDAO {

    @Override
    public void aggiungiSfida(int codiceSfida, String nome, String descrizione, Date inizio, Date fine) {
        String sql = "INSERT INTO sfida (codice_sfida, nome_sfida, descrizione_sfida, data_inizio, data_fine, num_partecipanti) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codiceSfida);
            stmt.setString(2, nome);
            stmt.setString(3, descrizione);
            stmt.setDate(4, inizio);
            stmt.setDate(5, fine);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rimuoviSfida(int codiceSfida) {
        String sql = "DELETE FROM sfida WHERE codice_sfida = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codiceSfida);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void aggiornaSfida(int codiceSfida, String nome, String descrizione, Date inizio, Date fine) {
        String sql = "UPDATE sfida SET nome_sfida = ?, descrizione_sfida = ?, data_inizio = ?, data_fine = ? WHERE codice_sfida = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, descrizione);
            stmt.setDate(3, inizio);
            stmt.setDate(4, fine);
            stmt.setInt(5, codiceSfida);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void partecipaSfida(int codiceSfida, int idUtente) {
        String insertSql = "INSERT INTO partecipanti_sfida (codice_sfida, id_utente) VALUES (?, ?)";
        String updateSql = "UPDATE sfida SET num_partecipanti = num_partecipanti + 1 WHERE codice_sfida = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, codiceSfida);
                insertStmt.setInt(2, idUtente);
                insertStmt.executeUpdate();
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, codiceSfida);
                updateStmt.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void annullaPartecipaSfida(int codiceSfida, int idUtente) {
        String deleteSql = "DELETE FROM partecipanti_sfida WHERE codice_sfida = ? AND id_utente = ?";
        String updateSql = "UPDATE sfida SET num_partecipanti = num_partecipanti - 1 WHERE codice_sfida = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, codiceSfida);
                deleteStmt.setInt(2, idUtente);
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, codiceSfida);
                updateStmt.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getDatiSfida(int codiceSfida) {
        List<String> dati = new ArrayList<>();
        String sql = "SELECT * FROM sfida WHERE codice_sfida = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codiceSfida);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dati.add("Nome: " + rs.getString("nome_sfida"));
                dati.add("Descrizione: " + rs.getString("descrizione_sfida"));
                dati.add("Partecipanti: " + rs.getInt("num_partecipanti"));
                dati.add("Data inizio: " + rs.getDate("data_inizio"));
                dati.add("Data fine: " + rs.getDate("data_fine"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dati;
    }
}
