package org.paginalibre8.model;

public class Libro {

    private String isbn;
    private String titulo;
    private String autor;
    private String fechaPublicacion;
    private double precio;
    private int stock;
    private int stockMinimo;
    private int idCategoria;
    private String categoriaNombre;
    private String nitEditorial;
    private String editorialNombre;
    private boolean activo;

    public Libro() {
        this.stockMinimo = 5;
        this.activo = true;
    }

    public Libro(String isbn, String titulo, String autor, String fechaPublicacion,
                 double precio, int stock, int stockMinimo, int idCategoria, String nitEditorial) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.idCategoria = idCategoria;
        this.nitEditorial = nitEditorial;
        this.activo = true;
    }

    public Libro(String isbn, String titulo, String autor, String fechaPublicacion,
                 double precio, int stock, int idCategoria, String nitEditorial) {
        this(isbn, titulo, autor, fechaPublicacion, precio, stock, 5, idCategoria, nitEditorial);
    }

    public Libro(String isbn, String titulo, String fechaPublicacion,
                 double precio, int idCategoria, String nitEditorial) {
        this(isbn, titulo, "Desconocido", fechaPublicacion, precio, 0, 5, idCategoria, nitEditorial);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getNitEditorial() {
        return nitEditorial;
    }

    public void setNitEditorial(String nitEditorial) {
        this.nitEditorial = nitEditorial;
    }

    public String getEditorialNombre() {
        return editorialNombre;
    }

    public void setEditorialNombre(String editorialNombre) {
        this.editorialNombre = editorialNombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return titulo + " (ISBN: " + isbn + ")";
    }
}