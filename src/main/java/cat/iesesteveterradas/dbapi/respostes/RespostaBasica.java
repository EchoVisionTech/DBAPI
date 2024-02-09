package cat.iesesteveterradas.dbapi.respostes;

public class RespostaBasica {
    private String status;
    private String message;
    private String error;

    public RespostaBasica() {
    }

    public RespostaBasica(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters i setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Mètode toString per a representació en cadena de text
    @Override
    public String toString() {
        return "RespostaBasica{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
