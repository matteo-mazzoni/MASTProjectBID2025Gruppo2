package com.mast.readup.services;

import com.mast.readup.services.UtenteDAO;
import com.mast.readup.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UtenteDAOImpl implements UtenteDAO {

    @Override
    public void aggiungiUtente(String nickname, String password, String email, String citta) {
        String sql = "INSERT INTO utente (nickname, password, email, citta, logged_in) VALUES (?, ?, ?, ?, false)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nickname);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, citta);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificaPassword(int idUtente, String nuovaPassword) {
        aggiornaCampo("password", nuovaPassword, idUtente);
    }

    @Override
    public void modificaMail(int idUtente, String nuovaEmail) {
        aggiornaCampo("email", nuovaEmail, idUtente);
    }

    @Override
    public void modificaNickname(int idUtente, String nuovoNickname) {
        aggiornaCampo("nickname", nuovoNickname, idUtente);
    }

    @Override
    public void modificaCitta(int idUtente, String nuovaCitta) {
        aggiornaCampo("citta", nuovaCitta, idUtente);
    }

    @Override
    public void cambiaStatusLogin(int idUtente, boolean loggedIn) {
        String sql = "UPDATE utente SET logged_in = ? WHERE id_utente = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, loggedIn);
            stmt.setInt(2, idUtente);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaUtente(int idUtente) {
        String sql = "DELETE FROM utente WHERE id_utente = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtente);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aggiornaCampo(String campo, String valore, int idUtente) {
        String sql = "UPDATE utente SET " + campo + " = ? WHERE id_utente = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, valore);
            stmt.setInt(2, idUtente);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
