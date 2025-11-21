package UTP.Zum.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "nom_usuario", length = 50, nullable = false)
    private String nomUsuario;

    @Column(name = "correo_usuario", length = 50, nullable = false, unique = true)
    private String correoUsuario;

    @Column(name = "contrasenia_usuario", length = 255, nullable = false)
    private String contraseniaUsuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_usuario", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaDeCreacion = LocalDateTime.now();


    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getNomUsuario() { return nomUsuario; }
    public void setNomUsuario(String nomUsuario) { this.nomUsuario = nomUsuario; }
    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
    public String getContraseniaUsuario() { return contraseniaUsuario; }
    public void setContraseniaUsuario(String contraseniaUsuario) { this.contraseniaUsuario = contraseniaUsuario; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public LocalDateTime getFechaDeCreacion() { return fechaDeCreacion; }
    public void setFechaDeCreacion(LocalDateTime fechaDeCreacion) { this.fechaDeCreacion = fechaDeCreacion; }
}