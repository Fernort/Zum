package UTP.Zum.Dto;

public class RegistroRequest {
    private String nombre;
    private String correo;
    private String contrasenia;
    private String rol;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}