package PropertyManager.common;

/**
 * Created by Jozef Živčic on 30. 3. 2015.
 */
public class IllegalEntityException extends RuntimeException {

    public IllegalEntityException() {}
    public IllegalEntityException(String msg) {
        super(msg);
    }
    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }

}
