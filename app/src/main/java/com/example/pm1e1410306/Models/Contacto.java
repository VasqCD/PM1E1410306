package com.example.pm1e1410306.Models;

public class Contacto {
    private Integer id;
    private String nombres;
    private String telefono;
    private String pais;
    private String nota;
    private String foto;

    public Contacto() {
    }

    public Contacto(Integer id, String nombres, String telefono, String pais, String nota, String foto) {
        this.id = id;
        this.nombres = nombres;
        this.telefono = telefono;
        this.pais = pais;
        this.nota = nota;
        this.foto = foto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
