package UTP.Zum.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reunion")
public class Reunion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reunion_id")
    private Integer reunionId;

    @Column(name = "titulo", length = 100, nullable = false)
    private String titulo;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "PROGRAMADA";

    @Column(name = "fecha_de_creacion")
    private LocalDateTime fechaDeCreacion = LocalDateTime.now();

    @Column(name = "fecha_de_inicio", nullable = false)
    private LocalDateTime fechaDeInicio;

    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;

    @Column(name = "codigo_acceso", length = 36, unique = true, nullable = false)
    private String codigoAcceso;

    @Column(name = "activa", nullable = false)
    private Boolean activa = false;

    @Column(name = "grabacion_habilitada", nullable = false)
    private Boolean grabacionHabilitada = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_creador", referencedColumnName = "usuario_id", nullable = false)
    private Usuario creador;

    @PrePersist
    public void prePersist() {
        if (this.codigoAcceso == null) {
            this.codigoAcceso = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.estado == null) {
            this.estado = "PROGRAMADA";
        }
        if (this.activa == null) {
            this.activa = false;
        }
        if (this.grabacionHabilitada == null) {
            this.grabacionHabilitada = false;
        }
        if (this.fechaDeCreacion == null) {
            this.fechaDeCreacion = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Integer getReunionId() { return reunionId; }
    public void setReunionId(Integer reunionId) { this.reunionId = reunionId; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaDeCreacion() { return fechaDeCreacion; }
    public void setFechaDeCreacion(LocalDateTime fechaDeCreacion) { this.fechaDeCreacion = fechaDeCreacion; }

    public LocalDateTime getFechaDeInicio() { return fechaDeInicio; }
    public void setFechaDeInicio(LocalDateTime fechaDeInicio) { this.fechaDeInicio = fechaDeInicio; }
    
    public Integer getDuracionMin() { return duracionMin; }
    public void setDuracionMin(Integer duracionMin) { this.duracionMin = duracionMin; }
    
    public String getCodigoAcceso() { return codigoAcceso; }
    public void setCodigoAcceso(String codigoAcceso) { this.codigoAcceso = codigoAcceso; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
    
    public Boolean getGrabacionHabilitada() { return grabacionHabilitada; }
    public void setGrabacionHabilitada(Boolean grabacionHabilitada) { this.grabacionHabilitada = grabacionHabilitada; }
    
    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }

    public String getDuracionFormateada() {
        int horas = duracionMin / 60;
        int mins = duracionMin % 60;
        if (horas > 0) {
            return horas + ":" + String.format("%02d", mins);
        }
        return mins + " min";
    }
    
    public java.time.LocalDate getFecha() {
        return fechaDeInicio != null ? fechaDeInicio.toLocalDate() : null;
    }
    
    public java.time.LocalTime getHoraInicio() {
        return fechaDeInicio != null ? fechaDeInicio.toLocalTime() : null;
    }
}
