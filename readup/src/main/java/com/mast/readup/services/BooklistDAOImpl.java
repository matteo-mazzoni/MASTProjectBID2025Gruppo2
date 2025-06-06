package com.mast.readup.services;

import com.mast.readup.services.BooklistDAO;
import com.mast.readup.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class BooklistDAOImpl implements BooklistDAO {

    @Override
    public void creaBooklist(String nome, String descrizione, int idUtenteCreatore) {
        String sql = "INSERT INTO booklist (nome, descrizione, id_utente_creatore) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, descrizione);
            stmt.setInt(3, idUtenteCreatore);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificadatiBooklist(int idBooklist, String nuovoNome, String nuovaDescrizione) {
        String sql = "UPDATE booklist SET nome = ?, descrizione = ? WHERE id_booklist = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuovoNome);
            stmt.setString(2, nuovaDescrizione);
            stmt.setInt(3, idBooklist);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificaLibriBooklist(int idBooklist, List<String> nuoviLibri) {
        String deleteSql = "DELETE FROM libri_booklist WHERE id_booklist = ?";
        String insertSql = "INSERT INTO libri_booklist (id_booklist, titolo_libro) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, idBooklist);
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (String libro : nuoviLibri) {
                    insertStmt.setInt(1, idBooklist);
                    insertStmt.setString(2, libro);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaBooklist(int idBooklist) {
        String sql = "DELETE FROM booklist WHERE id_booklist = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBooklist);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
