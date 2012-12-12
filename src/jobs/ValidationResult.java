
package jobs;

/**
 *
 * @author alan
 */
public class ValidationResult {    
    public enum type { ERROR, WARNING, INFORMATION};
    
    private final type messageType;
    private final String message;
    
    public ValidationResult(type messageType, String message){
        this.messageType = messageType;
        this.message = message;
    }

    public type getType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
